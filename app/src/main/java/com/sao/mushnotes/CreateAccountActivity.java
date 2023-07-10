package com.sao.mushnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText signupEmail , signupPassword , confirmpassword;
    Button signupButton ;
    ProgressBar progressBar;
    TextView loginRedirectText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        confirmpassword = findViewById(R.id.confirm_password);
        signupButton= findViewById(R.id.signup_button);
        progressBar = findViewById(R.id.progress_bar);
        loginRedirectText = findViewById(R.id.loginRedirectText);


        signupButton.setOnClickListener(v -> createAccount());
        loginRedirectText.setOnClickListener(v -> finish());

    }
    void createAccount(){
        String email = signupEmail.getText().toString();
        String password = signupPassword.getText().toString();
        String Confirmpassword = confirmpassword.getText().toString();

        boolean isValidated = validateData(email , password , Confirmpassword) ;
        if (!isValidated){
            return;
        }
        createAccountInFirebase(email , password);


    }

    void createAccountInFirebase(String email , String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //creating account is done
                            Utility.showToast(CreateAccountActivity.this, "Signup is Succesful, Check your email to verify");
                            Toast.makeText(CreateAccountActivity.this, "Signup is Succesful, Check your email to verify", Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else{
                            //fail
                            Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                });

    }
    void changeInProgress (boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            signupButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            signupButton.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData (String email , String password , String Confirmpassword){
        //Validate the data that are input user.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signupEmail.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6 ){
            signupPassword.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(Confirmpassword)){
            confirmpassword.setError("Passwords are not matches");
            return false;
        }
        return true;
    }

}