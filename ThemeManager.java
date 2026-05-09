package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignUpScreen extends AppCompatActivity {

    private static final String TAG = "SignUpScreen";
    private EditText txtUserName, txtUserEmail, txtUserPassword;
    private Button btnSignUp;
    private TextView txtSignIn;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(SignUpScreen.this, HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        mAuth = FirebaseAuth.getInstance();
        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        txtUserPassword = findViewById(R.id.txtUserPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtSignIn = findViewById(R.id.txtSignIn);

        btnSignUp.setOnClickListener(v -> {
            String userName = txtUserName.getText().toString().trim();
            String email = txtUserEmail.getText().toString().trim();
            String password = txtUserPassword.getText().toString().trim();

            if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpScreen.this, "Please complete all the fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check network connectivity before attempting authentication
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(SignUpScreen.this, "No internet connection. Please check your network and try again.", Toast.LENGTH_LONG).show();
                NetworkUtils.logNetworkStatus(this);
                return;
            }

            // Store user data temporarily and navigate to Terms and Conditions
            Intent intent = new Intent(SignUpScreen.this, TermsAndConditionScreen.class);
            intent.putExtra("userName", userName);
            intent.putExtra("userEmail", email);
            intent.putExtra("userPassword", password);
            startActivity(intent);
        });
        
        txtSignIn.setOnClickListener(view -> {
            startActivity(new Intent(SignUpScreen.this, SignInScreen.class));
        });
    }
}