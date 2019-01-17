package com.gprovs.earnestly;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    private ProgressDialog progressDialog;

    public FirebaseAuth firebaseAuth;
    public FirebaseFirestore firebaseFirestore;

    public List<CommentsClass> commentsList;
    public Context context;

    public CommentsRecyclerAdapter(List<CommentsClass> commentsList){

        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewTypes) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item,parent,false);
        context = parent.getContext();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsRecyclerAdapter.ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);

        String user_id = commentsList.get(position).getUser_id();

        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        final String comment_user_id = commentsList.get(position).getUser_id();

//        if (comment_user_id.equals(currentUserId)){
//
//            holder.commentDeleteButton.setVisibility(View.VISIBLE);
//        }

        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

        //set time
        long milliseconds = commentsList.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("MM/dd/yy*HH:mm:ss", new Date(milliseconds)).toString();
        holder.setTime(dateString);

        //deleting a comment
        holder.commentDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();
                firebaseFirestore.collection("comments").document().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        commentsList.remove(position);
                        progressDialog.dismiss();
                        context.startActivity(new Intent(context,CommentsActivity.class));

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

        if (commentsList != null){

            return commentsList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView comment_message,blogUserName,blogCommentCount,commentDeleteButton,commentDate;
        private CircleImageView blogUserImage;

        public ViewHolder(View itemView){
            super(itemView);

            mView = itemView;
            commentDeleteButton = mView.findViewById(R.id.tv_delete_comment);

        }

        public void setComment_message(String message){

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
        }

        public void updateCommentCount(int count){

            blogCommentCount = mView.findViewById(R.id.blog_comments_count);

            blogCommentCount.setText(count + "Comments");

        }

        public void setUserData(String name,String image){

            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);


            RequestOptions placeholderoption = new RequestOptions();
            placeholderoption.placeholder(R.drawable.ic_bloglist_profile_pic);

            Glide.with(context).applyDefaultRequestOptions(placeholderoption).load(image).into(blogUserImage);
            blogUserName.setText(name);
        }

        public void setTime(String dateString) {

            commentDate =mView.findViewById(R.id.comment_date);
            commentDate.setText(dateString);
        }
    }
}
