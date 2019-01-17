package com.gprovs.earnestly;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText email;
    private EditText password;
    private Button login,logregBtn;
    private ProgressDialog dialog;
    private TextView attempts;
    private int counter = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email = (EditText) findViewById(R.id.etlogmail);
        password = (EditText) findViewById(R.id.etlogpass);
        logregBtn = (Button) findViewById(R.id.btnLogreg);
        login = (Button) findViewById(R.id.btnlog);
        attempts = (TextView) findViewById(R.id.tvattempts);
        attempts.setText(getString(R.string.attempts) + String.valueOf(counter));
        dialog = new ProgressDialog(this);
        dialog.setMessage("Confirming...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputcheck()) {
                    dialog.show();
                    validate(email.getText().toString().trim(), password.getText().toString().trim());
                }
            }
        });


        logregBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

            sendToMain();
        }
    }

    private void sendToMain() {
        startActivity(new Intent(Login.this,BlogViewMain.class));
        finish();
    }

    private boolean inputcheck(){
        email.setError(null);
        password.setError(null);
        View focusView = null;

        Boolean cancel=false;
        Boolean result = false;

        String username = email.getText().toString();
        String userpassword = password.getText().toString();

        if (username.isEmpty()){

            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        }
        if (userpassword.isEmpty()){
            password.setError(getString(R.string.error_field_required));
            focusView = password;
            cancel = true;
        }
        if (cancel){
            if (cancel) {
                // There was an error; don't attempt registration and focus the
                // form field with an error.
                focusView.requestFocus();
            }
        }else{
            result = true;
        }
        return result;

    }


    private void validate(String userName, String userPassword){

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()){
                    dialog.dismiss();
                    sendToMain();
                }else{
                    dialog.dismiss();
                    String loginError = task.getException().getMessage();
                    Toast.makeText(Login.this, "Error :" + loginError, Toast.LENGTH_LONG).show();
                    counter--;

                    attempts.setText(getString(R.string.attempts) + String.valueOf(counter));

                    if (counter==0){

                        login.setEnabled(false);

                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Want to leave the app?");
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
