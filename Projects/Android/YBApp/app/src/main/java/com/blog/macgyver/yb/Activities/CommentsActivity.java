package com.blog.macgyver.yb.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blog.macgyver.yb.Adapter.CommentsRecyclerAdapter;
import com.blog.macgyver.yb.Model.BlogPost;
import com.blog.macgyver.yb.Model.Comments;
import com.blog.macgyver.yb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;

    private EditText comment_input;
    private ImageButton comment_btn;

    private String blog_post_id;
    private String current_user_id;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private ProgressBar progressBar;

    private RecyclerView comments_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        blog_post_id = getIntent().getStringExtra("blog_post_id");
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        comment_input = findViewById(R.id.comment_input);
        comment_btn = findViewById(R.id.comment_btn);

        comments_list = findViewById(R.id.comments_list);

        //RecylerView Firebaseb List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comments_list.setHasFixedSize(true);
        comments_list.setLayoutManager(new LinearLayoutManager(this));
        comments_list.setAdapter(commentsRecyclerAdapter);

        comment_btn.setOnClickListener(this);


        progressBar = findViewById(R.id.progressBar);

        //retrieving comments
        firestore.collection("Posts/" + blog_post_id + "/Comments").addSnapshotListener(
                CommentsActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String comment_id = doc.getDocument().getId();

                            Comments comments = doc.getDocument().toObject(Comments.class);
                            commentsList.add(comments);
                            commentsRecyclerAdapter.notifyDataSetChanged();

                         }
                    }
                }

            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.comment_btn:
                write_comments();
        }

    }

    private void write_comments() {

        String comment_message = comment_input.getText().toString();

        if (!comment_message.isEmpty()){

            Map<String,Object> commentsMap = new HashMap<>();

            commentsMap.put("comments", comment_message);
            commentsMap.put("user_id", current_user_id);
            commentsMap.put("timestamp", FieldValue.serverTimestamp());

            firestore.collection("Posts/" + blog_post_id + "/Comments")
                    .add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (!task.isSuccessful()){

                        Toast.makeText(CommentsActivity.this,
                                "Error Comment Posting" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }

}
