package com.gprovs.earnestly;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar commentsToolBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView comment_field;
    private Button comment_post_btn;

    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<CommentsClass> commentsList;

    private String blog_post_id;
    private String current_user_id;

    private ProgressBar progressBar;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentsToolBar = findViewById(R.id.comments_toolbar);
        setSupportActionBar(commentsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.commenting_progress);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        blog_post_id = getIntent().getStringExtra("blog_post_id");

        comment_field = findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);
        comment_list = findViewById(R.id.comment_list);

        //Recycler view firebase list

        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);

        if (firebaseAuth.getCurrentUser() != null) {

            Query commentsQuery = firebaseFirestore.collection("posts/" + blog_post_id + "/comments").orderBy("timestamp", Query.Direction.DESCENDING);

            commentsQuery.addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String commentId = doc.getDocument().getId();
                                CommentsClass comments = doc.getDocument().toObject(CommentsClass.class);
                                commentsList.add(comments);
                                commentsRecyclerAdapter.notifyDataSetChanged();


                            }
                        }

                    }

                }
            });


            comment_post_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String comment_message = comment_field.getText().toString();

                    progressBar.setVisibility(View.VISIBLE);
                    if (!comment_message.isEmpty()) {

                        Map<String, Object> commentsMap = new HashMap<>();
                        commentsMap.put("message", comment_message);
                        commentsMap.put("user_id", current_user_id);
                        commentsMap.put("timestamp", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("posts/" + blog_post_id + "/comments").add(commentsMap)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                        progressBar.setVisibility(View.INVISIBLE);
                                        if (!task.isSuccessful()) {

                                            Toast.makeText(CommentsActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        } else {

                                            comment_field.setText(comment_message);
                                            Toast.makeText(CommentsActivity.this, "Comment added", Toast.LENGTH_LONG).show();


                                        }
                                    }
                                });

                    } else {

                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CommentsActivity.this, "Nothing to comment", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
}
