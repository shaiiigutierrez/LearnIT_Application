package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class ForgotPasswordInterface extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordInterface";
    private static final int MAX_ATTEMPTS_PER_HOUR = 3;
    private static final long RATE_LIMIT_WINDOW_MS = 60 * 60 * 1000; // 1 hour
    private static final long MIN_TIME_BETWEEN_REQUESTS_MS = 30 * 1000; // 30 seconds
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private EditText txtUserEmail;
    private Button btnSignIn, btnDone;
    private FirebaseAuth mAuth;
    
    // Rate limiting
    private AtomicInteger attemptsThisHour = new AtomicInteger(0);
    private AtomicLong lastAttemptTime = new AtomicLong(0);
    private AtomicLong rateLimitWindowStart = new AtomicLong(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_interface);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        txtUserEmail = findViewById(R.id.txtUserEmail);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnDone = findViewById(R.id.btnDone);

        // Set click listeners
        btnSignIn.setOnClickListener(v -> sendPasswordResetEmail());
        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordInterface.this, SignInScreen.class);
            startActivity(intent);
            finish();
        });
    }

    private void sendPasswordResetEmail() {
        String email = txtUserEmail.getText().toString().trim();

        // Validate email
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check rate limiting
        if (!checkRateLimit()) {
            long remainingTime = getRemainingCooldownTime();
            String message = "Too many attempts. Please wait " + (remainingTime / 1000) + " seconds before trying again.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return;
        }

        // Check network connectivity
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please check your network and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        // Show loading state
        btnSignIn.setEnabled(false);
        btnSignIn.setText("Sending...");

        // Send password reset email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    // Reset button state
                    btnSignIn.setEnabled(true);
                    btnSignIn.setText("Send");

                    if (task.isSuccessful()) {
                        // Increment attempt counter
                        attemptsThisHour.incrementAndGet();
                        lastAttemptTime.set(System.currentTimeMillis());
                        
                        Toast.makeText(ForgotPasswordInterface.this, 
                            "Password reset email sent to " + email, Toast.LENGTH_LONG).show();
                        
                        // Clear the email field
                        txtUserEmail.setText("");
                        
                        // Show instructions
                        Toast.makeText(ForgotPasswordInterface.this, 
                            "Please check your email and follow the link to reset your password. " +
                            "The link will expire in 1 hour.", 
                            Toast.LENGTH_LONG).show();
                        
                    } else {
                        handlePasswordResetError(task, email);
                    }
                });
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Check email format
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return false;
        }
        
        // Check length
        if (email.length() > 254) {
            return false;
        }
        
        return true;
    }

    private boolean checkRateLimit() {
        long currentTime = System.currentTimeMillis();
        
        // Reset counter if window has passed
        if (currentTime - rateLimitWindowStart.get() > RATE_LIMIT_WINDOW_MS) {
            attemptsThisHour.set(0);
            rateLimitWindowStart.set(currentTime);
        }
        
        // Check if too many attempts
        if (attemptsThisHour.get() >= MAX_ATTEMPTS_PER_HOUR) {
            return false;
        }
        
        // Check minimum time between requests
        long timeSinceLastAttempt = currentTime - lastAttemptTime.get();
        if (timeSinceLastAttempt < MIN_TIME_BETWEEN_REQUESTS_MS && lastAttemptTime.get() > 0) {
            return false;
        }
        
        return true;
    }

    private long getRemainingCooldownTime() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastAttempt = currentTime - lastAttemptTime.get();
        return Math.max(0, MIN_TIME_BETWEEN_REQUESTS_MS - timeSinceLastAttempt);
    }

    private void handlePasswordResetError(Task<Void> task, String email) {
        String errorMessage = "Failed to send password reset email.";
        
        if (task.getException() != null) {
            Exception exception = task.getException();
            
            if (exception instanceof FirebaseAuthInvalidUserException) {
                // Don't reveal if email exists or not
                errorMessage = "If an account exists with this email, a password reset link has been sent.";
            } else {
                String exceptionMessage = exception.getMessage();
                if (exceptionMessage != null) {
                    if (exceptionMessage.contains("invalid email")) {
                        errorMessage = "Please enter a valid email address.";
                    } else if (exceptionMessage.contains("network")) {
                        errorMessage = "Network error. Please check your internet connection.";
                    } else {
                        errorMessage = "An error occurred. Please try again later.";
                    }
                }
            }
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear sensitive data
        if (txtUserEmail != null) {
            txtUserEmail.setText("");
        }
    }
}