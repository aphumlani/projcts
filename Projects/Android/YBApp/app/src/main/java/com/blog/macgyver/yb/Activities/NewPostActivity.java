package com.blog.macgyver.yb.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blog.macgyver.yb.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {

    //reference views
    private Toolbar toolbar;
    private ImageView select_image;
    private EditText mTitle;
    private Button post_button;

    //reference uri
    private Uri postURI = null;

    //reference string
    private String current_user_id;

    //reference progressDialog
    private ProgressDialog mDialog;

    private Bitmap compressedImageFile;

    //reference firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseCloud;
    private StorageReference mStorage;
    private Object Timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //initializing views
        toolbar = findViewById(R.id.toolbar_new_post);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add new post");

        select_image = findViewById(R.id.add_new_post_image);
        mTitle = findViewById(R.id.new_post_title_id);
        post_button = findViewById(R.id.post_button_setup_id);

        //initializing progressDialog
        mDialog = new ProgressDialog(this);

        //initializing firebase
        mAuth = FirebaseAuth.getInstance();
        databaseCloud = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        //get user_id
        current_user_id = mAuth.getCurrentUser().getUid();

        Timestamp = new Timestamp(new Date());

        select_image.setOnClickListener(this);
        post_button.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        try {
            //if user is not signed in, they will be redirected to Login Screen.
            if (currentUser == null) {
                updateUItoLogin();
            }

            else{

                databaseCloud.collection("Users").document(current_user_id).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                try {

                                    if (task.isSuccessful()){
                                        if (!task.getResult().exists()){

                                            updateUItoSetup();

                                        }

                                        else {
                                            assert false;
                                            String Error = task.getException().getMessage();
                                            Toast.makeText(NewPostActivity.this, "ERROR" + Error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });

            }

        }

        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.add_new_post_image:
                selectImageToPost();
                break;

            case R.id.post_button_setup_id:
                postToblog();
        }
    }

    private void selectImageToPost(){

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512,512)
                .setAspectRatio(1,1)
                .start(NewPostActivity.this);
    }



    private void postToblog() {

        final String desc = mTitle.getText().toString().trim();

        if (!TextUtils.isEmpty(desc) && postURI != null) {

            showProgressDialog();

            final String random_name = UUID.randomUUID().toString();

            final StorageReference post_file_path = mStorage.child("posts_image").child(random_name + ".jpg");
            post_file_path.putFile(postURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return post_file_path.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull final Task<Uri> task) {

                    final Uri post_download_uri, thumbimage;

                        if (task.isSuccessful()) {

                            File newImageFile = new File(postURI.getPath());

                            try {
                                compressedImageFile = new Compressor(NewPostActivity.this)
                                        .setMaxWidth(100)
                                        .setMaxHeight(100)
                                        .setQuality(2)
                                        .compressToBitmap(newImageFile);
                            }

                            catch (Exception e){
                                e.printStackTrace();
                            }

                            if (task != null){

                                post_download_uri = task.getResult();

                            }

                            else {

                                post_download_uri = postURI;
                            }



                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask thumbs_file_path = mStorage.child("posts_image/thumbs").child(random_name + ".jpg").putBytes(data);

                            thumbs_file_path.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    post_file_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final Uri downlaodThum = uri;

                                            Map<String, Object> new_post = new HashMap<>();

                                            new_post.put("title", desc);
                                            new_post.put("image", post_download_uri.toString());
                                            new_post.put("user", current_user_id);
                                            new_post.put("timestamp", Timestamp);
                                            new_post.put("thumbnail", downlaodThum.toString());



                                            databaseCloud.collection("Posts").add(new_post).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                                    if (task.isSuccessful()){
                                                        Toast.makeText(NewPostActivity.this, "Post is successfully", Toast.LENGTH_SHORT).show();
                                                        updateUItoMain();
                                                    }

                                                    else{

                                                        String Error = task.getException().getMessage();
                                                        Toast.makeText(NewPostActivity.this, "CLOUD DATABASE ERROR " + Error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        } else {
                            String Error = task.getException().getMessage();
                            Toast.makeText(NewPostActivity.this, "IMAGE ERROR " + Error, Toast.LENGTH_SHORT).show();
                        }
                    hideProgressDialog();
                }
            });
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postURI = result.getUri();
                select_image.setImageURI(postURI);

            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "ERROR " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void updateUItoMain(){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
    //Sends user to Login Screen.
    public void updateUItoLogin(){

        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    public void updateUItoSetup(){

        startActivity(new Intent(getApplicationContext(),SetupActivity.class));
        finish();
    }

    public void showProgressDialog() {

        mDialog.setMessage("Posting. Please wait...");
        mDialog.show();
    }

    public void hideProgressDialog(){

        mDialog.dismiss();
    }
}
