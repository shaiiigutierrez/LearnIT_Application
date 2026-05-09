package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResultInterface extends BaseActivity {

    private UserDataManager userDataManager;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_result_interface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDataManager = UserDataManager.getInstance(this);

        try {
            CustomBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
            if (bottomNavigation != null) {
                bottomNavigation.setCurrentActivity("ResultInterface");
            }

            Intent intent = getIntent();
            int score = intent.getIntExtra("score", 0);
            int totalQuestions = intent.getIntExtra("totalQuestions", 10);
            String levelTitle = intent.getStringExtra("levelTitle");
            int coinsEarned = intent.getIntExtra("coinsEarned", 0);
            boolean isLevelCompleted = intent.getBooleanExtra("isLevelCompleted", false);

            TextView scoreNumber = findViewById(R.id.scoreNumber);
            TextView text = findViewById(R.id.text);
            TextView pointsEarned = findViewById(R.id.pointsEarned);
            TextView difficultyLevel = findViewById(R.id.difficultyLevel);

            // Extract difficulty from level title
            String difficulty = extractDifficultyFromLevelTitle(levelTitle);
            
            // Display difficulty level
            if (difficulty != null && !difficulty.isEmpty()) {
                difficultyLevel.setText(difficulty);
            } else {
                difficultyLevel.setText("Data Acolyte");
            }

            // Display score and coins earned with real-time calculation
            // Always calculate coins earned dynamically based on difficulty and score
            int calculatedCoinsEarned = calculateCoinsEarned(score, difficulty);
            
            // Update UI immediately with calculated values
            scoreNumber.setText(score + "/" + totalQuestions);
            pointsEarned.setText("you have earned " + calculatedCoinsEarned + " coins");
            
            // Log the calculation for debugging
            Log.d("ResultInterface", "Score: " + score + "/" + totalQuestions + 
                  ", Difficulty: " + difficulty + 
                  ", Coins per answer: " + getCoinValuePerDifficulty(difficulty) + 
                  ", Total coins earned: " + calculatedCoinsEarned);
            
            if (isLevelCompleted) {
                // Show congratulatory message for level completion
                text.setText("Congratulations! You've completed this level!");
            } else {
                // Show appropriate message based on score
                if (score >= totalQuestions * 0.8) {
                    text.setText("Excellent! Keep up the great work!");
                } else if (score >= totalQuestions * 0.6) {
                    text.setText("Good! You're making progress!");
                } else if (score >= totalQuestions * 0.4) {
                    text.setText("Fair! Keep practicing to improve!");
                } else {
                    text.setText("Keep practicing! You'll get better!");
                }
            }
            
            // Award coins for both level completion and regular quiz results
            awardCoins(calculatedCoinsEarned);

            Button btnMistakes = findViewById(R.id.btnMistakes);
            btnMistakes.setOnClickListener(v -> {
                Intent intent2 = new Intent(ResultInterface.this, ReviewMistake.class);
                startActivity(intent2);
            });

            Button btnExit = findViewById(R.id.btnExit);
            btnExit.setOnClickListener(v -> {
                Intent intent2 = new Intent(ResultInterface.this, HomeScreen.class);
                startActivity(intent2);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Extract difficulty level from level title
     * @param levelTitle e.g., "Level 10 - DataAcolyte" -> "DataAcolyte"
     * @return difficulty level string
     */
    private String extractDifficultyFromLevelTitle(String levelTitle) {
        if (levelTitle == null || levelTitle.isEmpty()) {
            return "DataAcolyte"; // Default difficulty
        }
        
        if (levelTitle.contains(" - ")) {
            return levelTitle.split(" - ")[1];
        }
        
        return levelTitle;
    }
    
    /**
     * Calculate coins earned based on score and difficulty level
     * @param score number of correct answers
     * @param difficulty difficulty level (DataAcolyte, SystemKnight, CodeWarden, TechEmperor)
     * @return total coins earned
     */
    private int calculateCoinsEarned(int score, String difficulty) {
        int coinValuePerCorrectAnswer = getCoinValuePerDifficulty(difficulty);
        return score * coinValuePerCorrectAnswer;
    }
    
    /**
     * Get coin value per correct answer based on difficulty level
     * @param difficulty difficulty level
     * @return coins per correct answer
     */
         private int getCoinValuePerDifficulty(String difficulty) {
         if (difficulty == null) {
             return 10; // Default value
         }
         
         switch (difficulty) {
             case "DataAcolyte":
                 return 10; // 10 coins per correct answer
             case "SystemKnight":
                 return 20; // 20 coins per correct answer
             case "CodeWarden":
                 return 40; // 40 coins per correct answer
             case "TechEmperor":
                 return 80; // 80 coins per correct answer
             case "CodeAbyss":
                 return 160; // 160 coins per correct answer
             default:
                 return 10; // Default value for unknown difficulties
         }
     }
    
    private void awardCoins(int coinsEarned) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show immediate feedback that coins are being awarded
        Toast.makeText(this, "Awarding " + coinsEarned + " coins...", Toast.LENGTH_SHORT).show();
        
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("User Quiz Records")
            .document(user.getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Long currentCoins = documentSnapshot.getLong("coins");
                    int newCoins = (currentCoins != null ? currentCoins.intValue() : 0) + coinsEarned;
                    
                    firestore.collection("User Quiz Records")
                        .document(user.getUid())
                        .update("coins", newCoins)
                        .addOnSuccessListener(aVoid -> {
                            // Update cache with new coins value immediately
                            userDataManager.updateCoins((long) newCoins);
                            
                            Long totalCoinsEarned = documentSnapshot.getLong("statistics.totalCoinsEarned");
                            int newTotalCoinsEarned = (totalCoinsEarned != null ? totalCoinsEarned.intValue() : 0) + coinsEarned;
                            
                            firestore.collection("User Quiz Records")
                                .document(user.getUid())
                                .update("statistics.totalCoinsEarned", newTotalCoinsEarned)
                                .addOnSuccessListener(aVoid2 -> {
                                    // Show success message with animation
                                    Toast.makeText(ResultInterface.this, 
                                        "🎉 Earned " + coinsEarned + " coins! Total: " + newCoins, 
                                        Toast.LENGTH_LONG).show();
                                    
                                    Log.d("ResultInterface", "Successfully awarded " + coinsEarned + 
                                          " coins. New total: " + newCoins);
                                })
                                .addOnFailureListener(e -> {
                                    // Still show success for coins, even if statistics update fails
                                    Toast.makeText(ResultInterface.this, 
                                        "🎉 Earned " + coinsEarned + " coins! Total: " + newCoins, 
                                        Toast.LENGTH_LONG).show();
                                    
                                    Log.w("ResultInterface", "Coins awarded but statistics update failed: " + e.getMessage());
                                });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ResultInterface.this, 
                                "❌ Failed to update coins: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                            
                            Log.e("ResultInterface", "Failed to update coins: " + e.getMessage());
                        });
                } else {
                    Toast.makeText(ResultInterface.this, 
                        "❌ User document not found", 
                        Toast.LENGTH_SHORT).show();
                    
                    Log.e("ResultInterface", "User document not found for coin award");
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(ResultInterface.this, 
                    "❌ Failed to get user data: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                
                Log.e("ResultInterface", "Failed to get user data for coin award: " + e.getMessage());
            });
    }
} 