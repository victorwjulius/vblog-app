package com.gprovs.earnestly;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class PostActivity extends AppCompatActivity {

    private ImageButton blogImageButton;
    private ProgressBar postingProgress;


    private EditText blogTitle,blogDescription;
    private Button postingBtn;

    private static final int GALLERY_REQUEST = 1;
    private Uri postImageUri = null;
    private Bitmap compressedImageFile;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;

    private Toolbar postToolBar;

    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private DatabaseReference mDatabaseUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postToolBar = findViewById(R.id.postToolbar);
        setSupportActionBar(postToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postingProgress = findViewById(R.id.postUploadProgressBar);
//        blogImageButton = findViewById(R.id.postImageBtn);
        blogTitle = findViewById(R.id.edtBlogTitle);
        blogDescription = findViewById(R.id.edtBlogDesc);
        postingBtn = findViewById(R.id.postingBtn);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();

//        blogImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//
//                    if ((ContextCompat.checkSelfPermission(PostActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//                        && (ContextCompat.checkSelfPermission(PostActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
//
//                        ActivityCompat.requestPermissions(PostActivity.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
//
//                    }
//
//                        imagePicker();
//
//                }else {
//
//                    imagePicker();
//                }
//
//            }
//        });

        postingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firebaseAuth.getCurrentUser() != null) {


                    blogTitle.setError(null);
                    blogDescription.setError(null);
                    View focusView = null;
                    Boolean cancel = false;
                    final String title = blogTitle.getText().toString();
                    final String desc = blogDescription.getText().toString();

                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc)) {//had  && postImageUri != null

                        postingProgress.setVisibility(View.VISIBLE);

//                        final String randomName = UUID.randomUUID().toString();

//                        StorageReference filepath = storageReference.child("post_images").child(randomName + ".jpg");

//                        filepath.putFile(postImageUri).addOnCompleteListener(PostActivity.this,new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
//
////                                final String downloadUri = task.getResult().getDownloadUrl().toString();

//                                if (task.isSuccessful()) {

//                                    File newImageFile = new File(postImageUri.getPath());
//
//                                    try {
//                                        compressedImageFile = new Compressor(PostActivity.this)
//                                                .setMaxHeight(100)
//                                                .setMaxWidth(100)
//                                                .setQuality(2)
//                                                .compressToBitmap(newImageFile);
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                                    byte[] thumbData = baos.toByteArray();


//                                    UploadTask uploadTask = storageReference.child("post_images/thumbs").child(randomName + ".jpg")
//                                            .putBytes(thumbData);
//
//                                    uploadTask.addOnSuccessListener(PostActivity.this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                        @Override
//                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                                            String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();

                                            Map<String, Object> postMap = new HashMap<>();
//                                            postMap.put("image_url", downloadUri);
//                                            postMap.put("thumb_url", downloadThumbUri);
                                            postMap.put("title", title);
                                            postMap.put("description", desc);
                                            postMap.put("user_id", current_user_id);
                                            postMap.put("timestamp", FieldValue.serverTimestamp());

                                            firebaseFirestore.collection("posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                                    postingProgress.setVisibility(View.INVISIBLE);

                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(PostActivity.this, "Post published", Toast.LENGTH_LONG).show();

                                                        startActivity(new Intent(PostActivity.this, BlogViewMain.class));
//                                                        finish();

                                                    } else {

                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(PostActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });


//                                        }
//                                    })
//                                    .addOnFailureListener(PostActivity.this,new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//
//                                            postingProgress.setVisibility(View.INVISIBLE);
//
//                                            String error = task.getException().getMessage().toString();
//                                            Toast.makeText(PostActivity.this, "Error : " + error, Toast.LENGTH_LONG).show();
//
//                                        }
//                                    });


//                                } else {
//
//                                    postingProgress.setVisibility(View.INVISIBLE);
//                                    String error = task.getException().getMessage();
//                                    Toast.makeText(PostActivity.this, "Error : " + error, Toast.LENGTH_LONG).show();
//
//                                }
//                            }
//                        });

                    } else if (title.isEmpty() && desc.isEmpty()) {//had && postImageUri == null

                        Toast.makeText(PostActivity.this, "There is nothing to post", Toast.LENGTH_LONG).show();

                    } else {
//                        if (postImageUri == null) {
//
//                            Toast.makeText(PostActivity.this, "Please choose an image", Toast.LENGTH_LONG).show();
//                            cancel = true;
//
//                        }
                        if (title.isEmpty()) {

                            blogTitle.setError(getString(R.string.error_field_required));
                            focusView = blogTitle;
                            cancel = true;

                        }
                        if (desc.isEmpty()) {

                            blogDescription.setError(getString(R.string.error_field_required));
                            focusView = blogTitle;
                            cancel = true;
                        }
                        if(title.isEmpty() || desc.isEmpty()){//had  || postImageUri == null
                            Toast.makeText(PostActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        }
                        if (cancel) {
                            // There was an error; don't attempt posting and focus the
                            // post field with an error.
                            focusView.requestFocus();
                        }

                    }
                }
            }
        });

    }

//    private void imagePicker() {
//
//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setMaxCropResultSize(1500,1500)
//                .setAspectRatio(1,1)
//                .start(PostActivity.this);
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK){
//
//                postImageUri = result.getUri();
//                blogImageButton.setImageURI(postImageUri);
//
//            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
//
//                Exception cropError = result.getError();
//                Toast.makeText(this, "Error : " + cropError, Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

}
