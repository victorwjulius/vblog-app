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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private Button registerBtn,reglogBtn;
    private EditText confirmPasswordField, emailField, passwordField;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupuiviews();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()){
                    dialog.show();

                    //register user
                    final String user_email = emailField.getText().toString().trim();
                    final String user_password = passwordField.getText().toString().trim();

                    mAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Register.this,AccountSetupActivity.class));
                            }else{
                                dialog.dismiss();
                                String regError = task.getException().getMessage();
                                Toast.makeText(Register.this, "Error : " + regError, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });


        reglogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logfromreg = new Intent(Register.this,Login.class);
                startActivity(logfromreg);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){

            sendToMain();
        }
    }

    private void sendToMain() {
        startActivity(new Intent(Register.this,BlogViewMain.class));
        finish();
    }

    private void setupuiviews(){
        registerBtn = (Button)findViewById(R.id.btnreg);
        emailField = (EditText) findViewById(R.id.edtregmail);
        confirmPasswordField = (EditText)findViewById(R.id.edtregconfirmpass);
        passwordField = (EditText)findViewById(R.id.edtregpass);
        reglogBtn = (Button)findViewById(R.id.btnreglog);

        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("saving...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    private boolean validate(){

        emailField.setError(null);
        passwordField.setError(null);
        View focusView = null;
        Boolean cancel=false;
        Boolean result = false;

        String confirmPass = confirmPasswordField.getText().toString();
        String password = passwordField.getText().toString();
        String email = emailField.getText().toString();


        if (email.isEmpty()){
            emailField.setError(getString(R.string.error_field_required));
                    focusView = emailField;
                    cancel = true;
        }else if (!checkEmail(email)) {
            emailField.setError(getString(R.string.error_invalid_email));
            focusView = emailField;
            cancel = true;
        }
        if (password.isEmpty()){
            passwordField.setError(getString(R.string.error_field_required));
                    focusView = passwordField;
                    cancel = true;
        }else if (password.length()<8){
            passwordField.setError(getString(R.string.password_short));
            focusView = passwordField;
            cancel = true;
        }
        if (confirmPass.isEmpty()) {

            confirmPasswordField.setError(getString(R.string.error_field_required));
            focusView = confirmPasswordField;
            cancel = true;
        }else if (!confirmPass.equals(password)){
            confirmPasswordField.setError(getString(R.string.pass_dont_match));
            focusView = confirmPasswordField;
            cancel = true;
        }

        if (cancel){
            // There was an error; don't attempt registration and focus the
            // form field with an error.
            focusView.requestFocus();
        }else{
            result = true;
        }
        return result;
    }

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
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
