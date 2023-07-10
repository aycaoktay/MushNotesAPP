package com.sao.mushnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginAvtivity extends AppCompatActivity {

    EditText loginEmail , loginPassword ;
    Button loginButton ;
    ProgressBar progressBar;
    TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_avtivity);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton= findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_bar);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        loginButton.setOnClickListener((v) -> loginUser());
        signupRedirectText.setOnClickListener((v)-> startActivity(new Intent(LoginAvtivity.this , CreateAccountActivity.class))  );

    }
    void loginUser(){
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        boolean isValidated = validateData(email,password);
        if (!isValidated){
            return;
        }
        loginAccountInFirebase(email , password);

    }
    void loginAccountInFirebase(String email , String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                changeInProgress(false);
                if (task.isSuccessful()){
                    // Giriş başarılı oldu
                    startActivity(new Intent(LoginAvtivity.this , MainActivity.class));
                    finish();
                }else{
                    // Giriş başarısız oldu
                    Utility.showToast(LoginAvtivity.this , task.getException().getLocalizedMessage());
                }
            }
        });

    }
    void changeInProgress (boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData (String email , String password ){
        //Validate the data that are input user.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6 ){
            loginPassword.setError("Password length is invalid");
            return false;
        }
        return true;
    }
}