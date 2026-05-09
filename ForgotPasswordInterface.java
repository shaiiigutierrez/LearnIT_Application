package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BonusQuizInterface extends AppCompatActivity {

    private Button checkButton, continueButton;
    private TextView feedbackText;
    private boolean isCorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bonus_quiz_interface);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialize views
        initializeViews();
        setupButtons();
    }
    
    private void initializeViews() {
        checkButton = findViewById(R.id.checkButton);
        continueButton = findViewById(R.id.continueButton);
        feedbackText = findViewById(R.id.feedbackText);
    }
    
    private void setupButtons() {
        if (checkButton != null) {
            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer();
                }
            });
        }
        
        if (continueButton != null) {
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Go back to HomeScreen
                    Intent intent = new Intent(BonusQuizInterface.this, HomeScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
    
    private void checkAnswer() {
        // For now, let's assume the correct answer is "println"
        // In a real implementation, you would check the actual user input
        isCorrect = true; // Placeholder logic
        
        if (isCorrect) {
            feedbackText.setText("Correct! Great job on the surprise quiz!");
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            Toast.makeText(this, "Bonus points earned!", Toast.LENGTH_SHORT).show();
        } else {
            feedbackText.setText("Incorrect. The correct answer is 'println'.");
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        
        feedbackText.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.GONE);
        continueButton.setVisibility(View.VISIBLE);
    }
}