package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FeedbackScreen extends BaseActivity {

    private ImageView[] stars;
    private int currentRating = 0;
    private EditText feedbackInput;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_feedback_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setCurrentActivity("FeedbackScreen");
        }

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackScreen.this, ProfileInterface.class);
                startActivity(intent);
                finish();
            }
        });

        initializeViews();
        setupStarRating();
        setupSubmitButton();
    }

    private void initializeViews() {
        feedbackInput = findViewById(R.id.feedbackInput);
        stars = new ImageView[5];
        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);
    }

    private void setupStarRating() {
        for (int i = 0; i < stars.length; i++) {
            final int starIndex = i;
            stars[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRating(starIndex + 1);
                }
            });
        }
    }

    private void setRating(int rating) {
        currentRating = rating;
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_outline);
            }
        }
    }

    private void setupSubmitButton() {
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });
    }

    private void submitFeedback() {
        String feedback = feedbackInput.getText().toString().trim();
        if (currentRating == 0 || feedback.isEmpty()) {
            Toast.makeText(this, "Please rate and enter feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String docName;
        if (currentRating == 1) {
            docName = currentRating + " star";
        } else {
            docName = currentRating + " stars";
        }

        db.collection("Feedbacks")
                .document(docName)
                .update("feedbacks", FieldValue.arrayUnion(feedback))
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FeedbackScreen.this, "Thank you for your feedback!", Toast.LENGTH_LONG).show();
                        feedbackInput.setText("");
                        setRating(0);
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Map<String, Object> initialData = new HashMap<>();
                        initialData.put("feedbacks", new ArrayList<>(Arrays.asList(feedback)));

                        db.collection("Feedbacks")
                                .document(docName)
                                .set(initialData)
                                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(FeedbackScreen.this, "Thank you for your feedback!", Toast.LENGTH_LONG).show();
                                        feedbackInput.setText("");
                                        setRating(0);
                                    }
                                });
                    }
                });
    }
}
