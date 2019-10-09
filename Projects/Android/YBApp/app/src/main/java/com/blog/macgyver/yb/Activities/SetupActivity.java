package com.blog.macgyver.yb.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blog.macgyver.yb.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    //reference views
    private Toolbar toolbar;
    private CircleImageView imageView;
    private EditText name_text;
    private Button save_button;

    //reference uri
    private Uri mainImageURI = null;

    //prgressDialog
    private ProgressDialog mDialog;

    //reference string
    private String user_id;


    private boolean isChanged = false;

    //reference firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore cloud_database;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //initializing views
        toolbar = findViewById(R.id.toolbar_setup);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Settings");

        imageView = findViewById(R.id.select_image_id);
        name_text = findViewById(R.id.setup_name_id);
        save_button = findViewById(R.id.save_button_setup_id);

        //initializing progressDialog
        mDialog = new ProgressDialog(this);

        //initializing firebase
        mAuth = FirebaseAuth.getInstance();
        cloud_database = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        //get user_id
        user_id = mAuth.getCurrentUser().getUid();

        imageView.setOnClickListener(this);
        save_button.setOnClickListener(this);


        showProgressDialog();
        save_button.setEnabled(false);

        //here we are retrieving user's information like name of a user and a profile image of a user
        cloud_database.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                try {
                    if (task.isSuccessful()) {

                        //checks if "Users" collection exist or not
                        if (task.getResult().exists()) {

                            String name = task.getResult().getString("name");
                            String image = task.getResult().getString("image");

                            mainImageURI = Uri.parse(image);

                            name_text.setText(name);

                            RequestOptions placeHolderRequest = new RequestOptions();
                            Glide.with(SetupActivity.this)
                                    .setDefaultRequestOptions(placeHolderRequest).load(image).into(imageView);

                        }

                    } else {

                        String Error = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "FIREBASE Retrieve ERROR " + Error, Toast.LENGTH_SHORT).show();
                    }
                    hideProgressDialog();
                    save_button.setEnabled(true);
                }

                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    //This method checks if the user is signed in or not.
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        try {
            //if user is not signed in, they will be redirected to Login Screen.
            if (currentUser == null) {
                updateUItoLogin();
            }

        }

        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.select_image_id:
                select_image();
                break;
                
            case R.id.save_button_setup_id:
                save();
        }
    }

    private void save() {

        final String user_name = name_text.getText().toString().trim();
        if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {
            showProgressDialog();
            if (isChanged) {

                final StorageReference file_path = mStorage.child("profile_pictures").child(user_id + ".jpg");
                file_path.putFile(mainImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()) {
                            task.getException();

                        }

                        return file_path.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            storeFirestore(task, user_name);
                        }

                        else {
                        String Error = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "IMAGE ERROR " + Error, Toast.LENGTH_SHORT).show();

                        }
                        hideProgressDialog();
                    }
                });
            }

            else {

                storeFirestore(null,user_name);
            }

        }

    }

    private void storeFirestore(Task<Uri> task, String user_name) {

        Uri download_uri;

        if (task != null) {
            download_uri = task.getResult();
        }

        else {
            download_uri = mainImageURI;
        }

        Map<String,String> userMap = new HashMap<>();

        userMap.put("name", user_name);
        userMap.put("image",download_uri.toString());
        cloud_database.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (task.isSuccessful()){

                    updateUItoMain();
                    Toast.makeText(SetupActivity.this, "Account Updated", Toast.LENGTH_SHORT).show();

                }

                else {

                    String Error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "CLOUD DATABASE ERROR " + Error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void select_image(){

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission
                        .READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {

                    imagePicker();
                }
            } else {
                imagePicker();
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void imagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                imageView.setImageURI(mainImageURI);

                isChanged = true;

            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }

    }


    //Sends user to Login Screen.
    public void updateUItoLogin(){

        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    public void updateUItoMain(){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void showProgressDialog(){

        mDialog.setMessage("Loading User Information...");
        mDialog.show();
    }

    public void hideProgressDialog(){

        mDialog.dismiss();
    }
}
