package com.gprovs.earnestly;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private ProgressDialog progressDialog;
    public List<BlogPostGettersSetters> blog_list;
    public Context context;

    public FirebaseAuth firebaseAuth;

    public FirebaseFirestore firebaseFirestore;
    public BlogRecyclerAdapter(List<BlogPostGettersSetters> blog_list){

        this.blog_list = blog_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);

        context = parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String desc_data = blog_list.get(position).getDescription();
        holder.setDescText(desc_data);

//        String image_url = blog_list.get(position).getImage_url();
//        String thumb_url = blog_list.get(position).getThumb_url();
//        holder.setBlogImage(image_url,thumb_url);

        String blog_title = blog_list.get(position).getTitle();
        holder.setBlogTitle(blog_title);

        String blog_user_id = blog_list.get(position).getUser_id();
        if (blog_user_id.equals(currentUserId)){

            holder.blogDeleteBtn.setEnabled(true);
            holder.blogDeleteBtn.setVisibility(View.VISIBLE);
        }

        //fetching users
        firebaseFirestore.collection("users").document(blog_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()) {
                    if (task.isSuccessful()) {

                        String userName = task.getResult().getString("name");
                        String userImage = task.getResult().getString("image");

                        holder.setUserData(userName, userImage);
                    } else {

                    }
                }
            }
        });

        long milliseconds = blog_list.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("MM/dd/yy*HH:mm:ss", new Date(milliseconds)).toString();
        holder.setTime(dateString);

        //Get comments count
        firebaseFirestore.collection("posts/" + blogPostId + "/comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NewApi")
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.blogCommentBtn.setImageDrawable(context.getDrawable(R.drawable.ic_mode_comment_jungle_24dp));
                    holder.updateCommentsCount(count);


                }else{


                    holder.blogCommentBtn.setImageDrawable(context.getDrawable(R.drawable.ic_mode_comment_black_24dp));
                    holder.updateCommentsCount(0);

                }

            }
        });


        //Get likes count
        firebaseFirestore.collection("posts/" + blogPostId + "/likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.updateLikesCount(count);


                }else{

                    holder.updateLikesCount(0);

                }

            }
        });

        //Get likes using realtime i.e SnapshotListener
        firebaseFirestore.collection("posts/" + blogPostId + "/likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()){

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_thumb_up_jungle_24dp));


                }else{

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_thumb_up_black_black_24dp));

                }

            }
        });

        //like feature
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("posts/" + blogPostId + "/likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                        if (!task.getResult().exists()){
                            Map<String,Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("posts/" + blogPostId + "/likes").document(currentUserId).set(likesMap);
                        }else {

                            firebaseFirestore.collection("posts/" + blogPostId + "/likes").document(currentUserId).delete();
                        }

                    }
                });

            }
        });

        //to the comments activity
        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context,CommentsActivity.class);
                commentIntent.putExtra("blog_post_id",blogPostId);
                context.startActivity(commentIntent);
            }
        });

        //deleting an entire blog
        holder.blogDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();
                firebaseFirestore.collection("posts").document(blogPostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        blog_list.remove(position);
                        progressDialog.dismiss();
                        context.startActivity(new Intent(context,BlogViewMain.class));

                    }
                });

//                firebaseFirestore.collection("posts").document(blogPostId).delete().addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        Toast.makeText(context, "Error : " + e, Toast.LENGTH_LONG).show();
//                    }
//                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView descView,blogUserName,blogTitle,blogDate,blogLikeCount,blogCommentCount;
        private ImageView blogImageView, blogLikeBtn,blogCommentBtn;
        private Button blogDeleteBtn;

        private CircleImageView blogUserImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogCommentBtn =mView.findViewById(R.id.to_comments_btn);
            blogDeleteBtn = mView.findViewById(R.id.blog_delete_btn);
        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.blog_description);
            descView.setText(descText);

        }

//        public void setBlogImage(String downloadUri, String thumbUrl){
//
//            blogImageView = mView.findViewById(R.id.blog_image);
//
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.drawable.blogviewblogimage);
//
//            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri)
//                    .thumbnail(Glide.with(context).load(thumbUrl)).into(blogImageView);
//
//        }

        public void setBlogTitle(String title){

            blogTitle = mView.findViewById(R.id.blog_title);
            blogTitle.setText(title);
        }

        public void setTime(String date){

            blogDate =mView.findViewById(R.id.blog_date);
            blogDate.setText(date);

        }

        public void setUserData(String name,String image){

            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);


            RequestOptions placeholderoption = new RequestOptions();
            placeholderoption.placeholder(R.drawable.ic_bloglist_profile_pic);

            Glide.with(context).applyDefaultRequestOptions(placeholderoption).load(image).into(blogUserImage);
            blogUserName.setText(name);
        }

        public void updateLikesCount(int count){

            blogLikeCount = mView.findViewById(R.id.blog_like_count);

            blogLikeCount.setText(count + "Likes");

        }

        public void updateCommentsCount(int count) {

            blogCommentCount = mView.findViewById(R.id.blog_comments_count);
            blogCommentCount.setText(count + "Comments");
        }
    }


}
