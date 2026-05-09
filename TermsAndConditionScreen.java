package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInScreen extends AppCompatActivity {

    private EditText txtUserEmail, txtUserPassword;
    private Button btnSignIn;
    private TextView txtSignUp, txtForgotPassword;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(SignInScreen.this, HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_screen);

        mAuth = FirebaseAuth.getInstance();
        txtUserEmail = findViewById(R.id.txtUserEmail);
        txtUserPassword = findViewById(R.id.txtUserPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtSignUp = findViewById(R.id.txtSignUp);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInScreen.this, ForgotPasswordInterface.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(txtUserEmail.getText());
                String password = String.valueOf(txtUserPassword.getText());

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignInScreen.this, "Please complete all the fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean networkAvailable = NetworkUtils.isNetworkAvailable(SignInScreen.this);
                if (networkAvailable == false) {
                    Toast.makeText(SignInScreen.this, "No internet connection. Please check your network and try again.", Toast.LENGTH_LONG).show();
                    NetworkUtils.logNetworkStatus(SignInScreen.this);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignInScreen.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignInScreen.this, HomeScreen.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    String errorMessage = "Authentication failed.";
                                    if (task.getException() != null) {
                                        String exceptionMessage = task.getException().getMessage();
                                        if (exceptionMessage != null) {
                                            if (exceptionMessage.contains("no user record")) {
                                                errorMessage = "No account found with this email. Please sign up first.";
                                            } else if (exceptionMessage.contains("password is invalid")) {
                                                errorMessage = "Incorrect password. Please try again.";
                                            } else if (exceptionMessage.contains("network")) {
                                                errorMessage = "Network error. Please check your internet connection and try again.";
                                            } else {
                                                errorMessage = "Authentication failed: " + exceptionMessage;
                                            }
                                        }
                                    }
                                    Toast.makeText(SignInScreen.this, errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInScreen.this, SignUpScreen.class);
                startActivity(intent);
            }
        });
    }
}