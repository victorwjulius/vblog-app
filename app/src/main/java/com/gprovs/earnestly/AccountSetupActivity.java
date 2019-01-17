package com.gprovs.earnestly;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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


public class AccountSetupActivity extends AppCompatActivity {

    private Toolbar setupToolbar;
    private CircleImageView setupprofileimg;
    private EditText setupName;
    private Button setupBtn;

    private Uri mainImageUri = null;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    private boolean ischanged=false;

    private ProgressBar setupProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        setupName = findViewById(R.id.edtsetupuid);
        setupBtn = findViewById(R.id.setupBtn);
        setupprofileimg = findViewById(R.id.civprofileimg);

        setupProgress =findViewById(R.id.setupProgress);

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);

//        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(AccountSetupActivity.this,new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        if (task.getResult().exists()) {

                            String name = task.getResult().getString("name");
                            String image = task.getResult().getString("image");

                            mainImageUri = Uri.parse(image);

                            setupName.setText(name);

                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.profileimg);
                            Glide.with(AccountSetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupprofileimg);

                            setupProgress.setVisibility(View.INVISIBLE);
                            setupBtn.setEnabled(true);
                        }
                        setupProgress.setVisibility(View.INVISIBLE);
                        setupBtn.setEnabled(true);
                    } else {
                        setupProgress.setVisibility(View.INVISIBLE);
                        setupBtn.setEnabled(true);
                        String firestoreRetrieveError = task.getException().getMessage();
                        Toast.makeText(AccountSetupActivity.this, "Error : " + firestoreRetrieveError, Toast.LENGTH_LONG).show();
                    }


                }
            });
//        }

        setupprofileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if ((ContextCompat.checkSelfPermission(AccountSetupActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                            (ContextCompat.checkSelfPermission(AccountSetupActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){

                        ActivityCompat.requestPermissions(AccountSetupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                    }

                        launchImagePicker();

                }else {
                    launchImagePicker();
                }
            }
        });

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString();
                Boolean cancel = false;
                setupName.setError(null);
                View focusView = null;

                if (!user_name.isEmpty() && mainImageUri != null && user_name.length()<10) {

                    setupProgress.setVisibility(View.VISIBLE);

                if (ischanged) {


                        user_id = firebaseAuth.getCurrentUser().getUid();
                        final StorageReference image_path = storageReference.child("Profile_images").child(user_id + ".jpg");

                        image_path.putFile(mainImageUri).addOnCompleteListener(AccountSetupActivity.this,new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    storeFirestore(task, user_name);

                                } else {
                                    setupProgress.setVisibility(View.INVISIBLE);
                                    String setupError = task.getException().getMessage();
                                    Toast.makeText(AccountSetupActivity.this, "Image Error : " + setupError, Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }else{
                    storeFirestore(null,user_name);
                }
                }if (user_name.isEmpty()) {
                    cancel = true;
                    focusView = setupName;
                    setupName.setError(getString(R.string.error_field_required));

                }else if (user_name.length()>10){

                    cancel = true;
                    focusView = setupName;
                    setupName.setError(getString(R.string.more_than_10_error));


                }if (cancel){
                    focusView.requestFocus();
                }
                if (mainImageUri == null)
                    {
                        Toast.makeText(AccountSetupActivity.this, "Please choose an image", Toast.LENGTH_LONG).show();
                    }
//                }
            }
        });
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task,String user_name) {

        Uri download_uri;
        if (task != null) {

            download_uri = task.getResult().getDownloadUrl();

        }else{

            download_uri = mainImageUri;

        }
        Map<String,String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", download_uri.toString());

        firebaseFirestore.collection("users").document(user_id).set(userMap).addOnCompleteListener(AccountSetupActivity.this,new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                setupProgress.setVisibility(View.INVISIBLE);

                if (task.isSuccessful()){

                    Toast.makeText(AccountSetupActivity.this, "Profile Updated", Toast.LENGTH_LONG).show();
                    toMain();

                }else{
                    String firestoreError = task.getException().getMessage();
                    Toast.makeText(AccountSetupActivity.this, "Error : " + firestoreError, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void toMain() {

        startActivity(new Intent(AccountSetupActivity.this,BlogViewMain.class));
    }

    private void launchImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AccountSetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){

               mainImageUri = result.getUri();
               setupprofileimg.setImageURI(mainImageUri);

               ischanged=true;

            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                Exception cropError = result.getError();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login.class));
        }
    }
}
