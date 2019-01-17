package com.gprovs.earnestly;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

import static com.google.firebase.auth.FirebaseAuth.*;

public class BlogViewMain extends AppCompatActivity {

//    from home fragment starts
    private BlogRecyclerAdapter blogRecyclerAdapter;



    public RecyclerView blog_list_view;
    public List<BlogPostGettersSetters> blog_list;
//    from home ends here

    //from blogview
    private EditText blogTitle,blogDescription;
    private Button postingBtn;
//blogview ends

    private ProgressDialog progressDialog;
    public FirebaseAuth firebaseAuth;
    public FirebaseFirestore firebaseFirestore;
    private String current_user_id;

    private Toolbar mainToolBar;
    private BottomNavigationView mainBottomNav;

//    private HomeFragment homeFragment;
//    private AccountFragment accountFragment;
//    private NotificationsFragment notificationsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_view);

        mainToolBar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        //    from home fragment start

        blog_list = new ArrayList<>();
        blog_list_view = findViewById(R.id.blog_list_view);

        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(this));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);
        //    from home ends here


//        postingBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (current_user_id != null) {
//
//
//                    blogTitle.setError(null);
//                    blogDescription.setError(null);
//                    View focusView = null;
//                    Boolean cancel = false;
//                    final String title = blogTitle.getText().toString();
//                    final String desc = blogDescription.getText().toString();
//
//                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc)) {//had  && postImageUri != null
//
//                        progressDialog.show();
//                        Map<String, Object> postMap = new HashMap<>();
////                                            postMap.put("image_url", downloadUri);
////                                            postMap.put("thumb_url", downloadThumbUri);
//                        postMap.put("title", title);
//                        postMap.put("description", desc);
//                        postMap.put("user_id", current_user_id);
//                        postMap.put("timestamp", FieldValue.serverTimestamp());
//
//                        firebaseFirestore.collection("posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentReference> task) {
//
//
//
//                                if (task.isSuccessful()) {
//
//                                    Toast.makeText(BlogViewMain.this, "Post published", Toast.LENGTH_LONG).show();
//                                    startActivity(new Intent(getApplicationContext(), BlogViewMain.class));
//                                    progressDialog.dismiss();
////                                                        finish();
//
//                                } else {
//
//                                    String error = task.getException().getMessage();
//                                    Toast.makeText(BlogViewMain.this, "Error : " + error, Toast.LENGTH_SHORT).show();
//
//                                }
//                            }
//                        });
//
//                    } else if (title.isEmpty() && desc.isEmpty()) {//had && postImageUri == null
//
//                        Toast.makeText(BlogViewMain.this, "There is nothing to post", Toast.LENGTH_LONG).show();
//
//                    } else {
////                        if (postImageUri == null) {
////
////                            Toast.makeText(PostActivity.this, "Please choose an image", Toast.LENGTH_LONG).show();
////                            cancel = true;
////
////                        }
//                        if (title.isEmpty()) {
//
//                            blogTitle.setError(getString(R.string.error_field_required));
//                            focusView = blogTitle;
//                            cancel = true;
//
//                        }
//                        if (desc.isEmpty()) {
//
//                            blogDescription.setError(getString(R.string.error_field_required));
//                            focusView = blogTitle;
//                            cancel = true;
//                        }
//                        if(title.isEmpty() || desc.isEmpty()){//had  || postImageUri == null
//                            Toast.makeText(BlogViewMain.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                        }
//                        if (cancel) {
//                            // There was an error; don't attempt posting and focus the
//                            // post field with an error.
//                            focusView.requestFocus();
//                        }
//
//                    }
//                }
//            }
//        });

//        if (firebaseAuth.getCurrentUser() != null) {


//            mainBottomNav = findViewById(R.id.mainBottomNav);

        //fragments
//            homeFragment = new HomeFragment();
//            accountFragment = new AccountFragment();
//            notificationsFragment = new NotificationsFragment();

//            replaceFragment(homeFragment); due to replacement

//            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//
//                    switch (menuItem.getItemId()) {
//
//                        case R.id.bottom_action_home:
//                            replaceFragment(homeFragment);
//                            return true;

//                        case R.id.bottom_action_account:
//                            replaceFragment(accountFragment);
//                            return true;

//                        default:
//                            return false;
//
//                    }
//
//                }
//            });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addBlogConcept);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent post = new Intent(BlogViewMain.this, PostActivity.class);
                startActivity(post);
            }
        });
//        }

        //    from home fragment start

        //ordering the post according to the time of posting
//        Thread loading = new Thread(){
//            @Override
//            public void run() {
//                try {
//                    sleep(2000);
//                    startActivity(new Intent(BlogViewMain.this,BlogViewMain.class));
//                    finish();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        };
//        loading.start();

        if (firebaseAuth.getCurrentUser() != null) {
            Query firstQuery = firebaseFirestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING);

            //enables us to retrieve the data in real time with the recent posts appearing first
            firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

//
                    if (!documentSnapshots.isEmpty()) {
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                BlogPostGettersSetters blogPost = doc.getDocument().toObject(BlogPostGettersSetters.class).withId(blogPostId);

                                blog_list.add(blogPost);

                                blogRecyclerAdapter.notifyDataSetChanged();

                            }
                        }
                    }
                }
            });


//

        }
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = getInstance().getCurrentUser();
        if (currentUser == null){
            sendToLogin();
        }else{

            current_user_id = firebaseAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){

                        if (!task.getResult().exists()){

                            startActivity(new Intent(BlogViewMain.this,AccountSetupActivity.class));
                            finish();

                        }

                    }else{

                        String error = task.getException().getMessage();
                        Toast.makeText(BlogViewMain.this, "Error : " + error, Toast.LENGTH_LONG).show();

                    }


                }
            });
        }
    }

    public void sendToLogin() {
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){

            case R.id.action_logout_btn:

                logout();
                break;

            case R.id.action_settings_btn:

                startActivity(new Intent(BlogViewMain.this,AccountSetupActivity.class));
                break;


            default:
                return false;
        }
        return true ;

    }

    private void logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Logging out will close the app");
        builder.setNegativeButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();

//        startActivity(new Intent(BlogViewMain.this,BlogViewMain.class));

    }

//    private void replaceFragment(Fragment fragment){
//
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.main_container, fragment);
//        fragmentTransaction.commit();
//    }

    @Override
    public void onBackPressed(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to close?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }
}
