package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SurpriseQuizIntroduction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_surprise_quiz_introduction);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Setup button click listeners
        setupButtons();
    }
    
    private void setupButtons() {
        View skipButton = findViewById(R.id.skip);
        View continueButton = findViewById(R.id.continue_btn);
        
        if (skipButton != null) {
            skipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Go back to HomeScreen
                    Intent intent = new Intent(SurpriseQuizIntroduction.this, HomeScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
        
        if (continueButton != null) {
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to BonusQuizInterface
                    Intent intent = new Intent(SurpriseQuizIntroduction.this, BonusQuizInterface.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}