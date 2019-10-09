package com.blog.macgyver.yb.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blog.macgyver.yb.Activities.CommentsActivity;
import com.blog.macgyver.yb.Activities.NewPostActivity;
import com.blog.macgyver.yb.Model.BlogPost;
import com.blog.macgyver.yb.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter  extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public  List<BlogPost> blog_list;
    public Context context;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<BlogPost> blog_list){

        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_list_item,viewGroup,false);
        context = viewGroup.getContext();
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        viewHolder.setIsRecyclable(false);

        final String blogPostId = blog_list.get(i).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String desc_data = blog_list.get(i).getTitle();
        viewHolder.setDescText(desc_data);

        String imageUir = blog_list.get(i).getImage();
        viewHolder.setImageBlog(imageUir);

        String user_id = blog_list.get(i).getUser();

        if (user_id.equals(currentUserId)){

            viewHolder.deleteButton.setEnabled(true);
            viewHolder.deleteButton.setVisibility(View.VISIBLE);
        }

        firestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    viewHolder.setUserData(userName, userImage);
                }

            }
        });


        try {
            long milliseconds = blog_list.get(i).getTimestamp().getTime();
            String dateString = DateFormat.format("dd/MM/yyyy", new Date(milliseconds)).toString();
            viewHolder.setDate(dateString);
        }

        catch(Exception e){
            Toast.makeText(context, "Exception : "  + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Get likes feature
        firestore.collection("Posts/" + blogPostId + "/Likes")
                .document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {

                    viewHolder.blog_like_btn.setImageDrawable(context.getDrawable(R.drawable.action_like_accent));
                }

                else {

                    viewHolder.blog_like_btn.setImageDrawable(context.getDrawable(R.drawable.action_llike));
                }
            }
        });


        //Like Count
        firestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()){

                    int count = queryDocumentSnapshots.size();

                    viewHolder.updateLikeCount(count);
                }

                else {

                    viewHolder.updateLikeCount(0);
                }

            }
        });

        //Likes feature
        viewHolder.blog_like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firestore.collection("Posts/" + blogPostId + "/Likes")
                        .document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        //here we check if like do exist, then if likes dont exist we add them.
                        if (!task.getResult().exists()){

                            Map<String,Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firestore.collection("Posts/" + blogPostId + "/Likes")
                                    .document(currentUserId).set(likesMap);

                        }

                        else{

                            firestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();

                        }
                    }
                });
            }
        });


        //Comments intent
        viewHolder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);

            }
        });

        //Like Count
        firestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()){

                    int count = queryDocumentSnapshots.size();

                    viewHolder.updateCommentCount(count);
                }

                else {

                    viewHolder.updateCommentCount(0);
                }

            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firestore.collection("Posts").document(blogPostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        blog_list.remove(i);
                        notifyDataSetChanged();
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView descView, BloguserName, blog_like_count,blog_comment_count;
        private ImageView blog_like_btn;
        private ImageView comment_btn, deleteButton;
        private CircleImageView BloguserImage;
        private TextView blogDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            blog_like_btn = mView.findViewById(R.id.blog_like);
            comment_btn = mView.findViewById(R.id.blog_comment_btn);
            deleteButton = mView.findViewById(R.id.deleteButton);
        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.blog_description);
            descView.setText(descText);
        }

        public void setImageBlog(String imageUri){

            ImageView blog_image = mView.findViewById(R.id.blog_post_image);

            RequestOptions placeHolderOption = new RequestOptions();
            placeHolderOption.placeholder(R.drawable.image_palceholder);

            Glide.with(context).applyDefaultRequestOptions(placeHolderOption).load(imageUri).into(blog_image);
        }

        public void setUserData(String name, String image){

            BloguserName = mView.findViewById(R.id.blog_userName);
            BloguserImage = mView.findViewById(R.id.blog_image_profile);

            RequestOptions placeHolderOption = new RequestOptions();
            placeHolderOption.placeholder(R.drawable.user_profile_small);

            BloguserName.setText(name);
            Glide.with(context).applyDefaultRequestOptions(placeHolderOption).load(image).into(BloguserImage);
        }

        public void setDate(String date){

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }

        public void updateLikeCount(int Count){

            blog_like_count = mView.findViewById(R.id.blog_like_count);
            blog_like_count.setText(Count + " Likes");

        }


        public void updateCommentCount(int Count){

            blog_comment_count = mView.findViewById(R.id.blog_comment_count);
            blog_comment_count.setText(Count + " Comment");

        }

    }
}
