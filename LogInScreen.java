package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import androidx.annotation.NonNull;

public class LanguageSelection extends BaseActivity {
    private static final String TAG = "LanguageSelection";
    private UserDataManager userDataManager;
    private QuizProgress quizProgress;
    private String currentSelectedLanguage = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_language_selection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDataManager = UserDataManager.getInstance(this);
        quizProgress = new QuizProgress(this);

        try {
            // Setup custom bottom navigation
                    CustomBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setCurrentActivity("LanguageSelection");
        }

            // Setup back button click listener
            ImageButton backButton = findViewById(R.id.backButton);
            if (backButton != null) {
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish(); // Close this activity and return to HomeScreen
                    }
                });
            }

            // Load current selected language first
            loadCurrentLanguageAndSetupUI();
            
            // Show a toast to confirm the activity loaded successfully
            Toast.makeText(this, "Language Selection Screen Loaded", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading language selection: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Return to previous screen if there's an error
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reapply language locking when returning to this screen
        if (currentSelectedLanguage != null) {
            applyLanguageLocking();
        }
    }

    private void loadCurrentLanguageAndSetupUI() {
        userDataManager.getLanguage(new UserDataManager.LanguageCallback() {
            @Override
            public void onLanguageLoaded(String language) {
                currentSelectedLanguage = language;
                Log.d(TAG, "Current selected language: " + language);
                
                // Setup language card listeners with locking logic
                setupLanguageCardListeners();
                
                // Load and display user progress
                loadUserProgress();
                
                // Apply language locking based on completion status
                applyLanguageLocking();
                
                // If a language is selected, check if Data Acolyte is completed to unlock other language
                if (language != null && !language.isEmpty()) {
                    checkDataAcolyteCompletionAndUnlockOtherLanguage(language);
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading current language: " + error);
                currentSelectedLanguage = null;
                setupLanguageCardListeners();
                loadUserProgress();
                applyLanguageLocking();
            }
        });
    }
    
    private void checkDataAcolyteCompletionAndUnlockOtherLanguage(String language) {
        quizProgress.areAllQuestionsCompleted(language, "DataAcolyte", 10, new QuizProgress.ProgressCallback() {
            @Override
            public void onSuccess() {
                // Data Acolyte is completed, unlock the other language
                Log.d(TAG, "Data Acolyte completed for " + language + ", unlocking other language");
                if ("JAVA".equals(language)) {
                    userDataManager.updateLanguageUnlockStatus("C#", true);
                } else if ("C#".equals(language)) {
                    userDataManager.updateLanguageUnlockStatus("JAVA", true);
                }
                
                // Refresh the language locking display
                applyLanguageLocking();
            }
            
            @Override
            public void onError(String error) {
                // Data Acolyte is not completed, keep other language locked
                Log.d(TAG, "Data Acolyte not completed for " + language + ", keeping other language locked");
            }
        });
    }

    private void setupLanguageCardListeners() {
        try {
            // Java card click listener
            LinearLayout javaCard = findViewById(R.id.javaCard);
            if (javaCard != null) {
                javaCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleLanguageSelection("JAVA");
                    }
                });
            } else {
                Log.w(TAG, "Java card not found in layout");
                Toast.makeText(this, "Java card not found", Toast.LENGTH_SHORT).show();
            }

            // C# card click listener
            LinearLayout csharpCard = findViewById(R.id.csharpCard);
            if (csharpCard != null) {
                csharpCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleLanguageSelection("C#");
                    }
                });
            } else {
                Log.w(TAG, "C# card not found in layout");
                Toast.makeText(this, "C# card not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up language card listeners: " + e.getMessage(), e);
            Toast.makeText(this, "Error setting up language cards: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void handleLanguageSelection(String language) {
        // If this language is already selected, do nothing
        if (language.equals(currentSelectedLanguage)) {
            Toast.makeText(this, language + " is already selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the language is locked
        if (isLanguageLocked(language)) {
            String lockMessage = "To unlock " + language + ", you must first complete a difficulty level in your chosen language.";
            Toast.makeText(this, lockMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        // Language is available, select it - provide immediate visual feedback
        Toast.makeText(LanguageSelection.this, language + " selected!", Toast.LENGTH_SHORT).show();
        
        // Apply immediate UI changes for instant feedback
        if (currentSelectedLanguage != null && !currentSelectedLanguage.isEmpty()) {
            // Lock the previously selected language immediately
            lockLanguage(currentSelectedLanguage);
        }
        
        // Unlock the newly selected language immediately
        unlockLanguage(language);
        
        // Update current selected language
        currentSelectedLanguage = language;
        
        returnSelectedLanguage(language);
    }

    private boolean isLanguageLocked(String language) {
        // If no language is currently selected, both languages are available for initial selection
        if (currentSelectedLanguage == null || currentSelectedLanguage.isEmpty()) {
            return false;
        }

        // If the language is already selected, it's not locked
        if (language.equals(currentSelectedLanguage)) {
            return false;
        }

        // For the other language, check if it has been unlocked through difficulty level completion
        // This will be checked asynchronously in applyLanguageLocking()
        // Default to locked until we confirm it's unlocked
        return true;
    }



    private void applyLanguageLocking() {
        if (currentSelectedLanguage == null || currentSelectedLanguage.isEmpty()) {
            // No language selected yet - both languages should be available for initial selection
            // Don't apply any locking, let users choose their first language
            unlockLanguage("JAVA");
            unlockLanguage("C#");
            return;
        }

        // A language is selected - ensure the selected language is unlocked and check the other
        if (currentSelectedLanguage.equals("JAVA")) {
            unlockLanguage("JAVA"); // Always unlock the selected language
            checkAndApplyLanguageUnlockStatus("C#");
        } else if (currentSelectedLanguage.equals("C#")) {
            unlockLanguage("C#"); // Always unlock the selected language
            checkAndApplyLanguageUnlockStatus("JAVA");
        }
    }
    
    private void checkAndApplyLanguageUnlockStatus(String language) {
        final String languageKey = language; // Make language final for lambda
        userDataManager.getLanguageUnlockStatus(language, new UserDataManager.LanguageUnlockCallback() {
            @Override
            public void onLanguageUnlockStatusLoaded(boolean isUnlocked) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isUnlocked) {
                            unlockLanguage(languageKey);
                            Log.d(TAG, languageKey + " is unlocked based on unlock status");
                        } else {
                            lockLanguage(languageKey);
                            Log.d(TAG, languageKey + " is locked based on unlock status");
                        }
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                // On error, default to locked
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lockLanguage(languageKey);
                        Log.d(TAG, languageKey + " is locked due to error: " + error);
                    }
                });
            }
        });
    }

    private void lockLanguage(String language) {
        LinearLayout languageCard = null;
        if (language.equals("JAVA")) {
            languageCard = findViewById(R.id.javaCard);
        } else if (language.equals("C#")) {
            languageCard = findViewById(R.id.csharpCard);
        }

        if (languageCard != null) {
            // Apply immediate visual feedback
            languageCard.setEnabled(false);
            languageCard.setAlpha(0.5f); // dimmed
            languageCard.setClickable(false);
            
            // Add visual feedback with animation
            languageCard.animate()
                .alpha(0.5f)
                .setDuration(200)
                .start();
                
            Log.d(TAG, language + " language card locked and dimmed with animation");
        }
    }

    private void unlockLanguage(String language) {
        LinearLayout languageCard = null;
        if (language.equals("JAVA")) {
            languageCard = findViewById(R.id.javaCard);
        } else if (language.equals("C#")) {
            languageCard = findViewById(R.id.csharpCard);
        }

        if (languageCard != null) {
            // Apply immediate visual feedback
            languageCard.setEnabled(true);
            languageCard.setClickable(true);
            
            // Add visual feedback with animation
            languageCard.animate()
                .alpha(1.0f)
                .setDuration(200)
                .start();
                
            Log.d(TAG, language + " language card unlocked with animation");
        }
    }

    private void returnSelectedLanguage(String language) {
        try {
            Log.d(TAG, "returnSelectedLanguage called with: " + language);
            Toast.makeText(this, "Language selected: " + language, Toast.LENGTH_LONG).show();
            
            // Update current selected language
            currentSelectedLanguage = language;
            
            // Update cache with selected language
            userDataManager.updateLanguage(language);
            
            // Save language to Firestore
            saveLanguageToFirestore(language);
            
            // Apply language locking after selection
            applyLanguageLocking();
            
            // Don't automatically return to HomeScreen - let user navigate back manually
            Log.d(TAG, "Language selected and cached, user can navigate back manually");
            
        } catch (Exception e) {
            Log.e(TAG, "Error returning selected language: " + e.getMessage(), e);
            Toast.makeText(this, "Error selecting language: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void saveLanguageToFirestore(String language) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("User Quiz Records").document(user.getUid())
                    .update("selectedLanguage", language)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Language saved to Firestore successfully: " + language);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error saving language to Firestore: " + e.getMessage());
                        }
                    });
            } else {
                Log.e(TAG, "User not authenticated, cannot save language to Firestore");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in saveLanguageToFirestore: " + e.getMessage());
        }
    }
    
    /**
     * Loads user progress from Firestore and updates the UI
     */
    private void loadUserProgress() {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("User Quiz Records").document(user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot document) {
                            if (document.exists()) {
                                Log.d(TAG, "Document exists, calculating progress...");
                                Log.d(TAG, "Document data: " + document.getData());
                                
                                // Calculate and display Java progress
                                int javaProgress = calculateLanguageProgress(document, "java");
                                Log.d(TAG, "Java progress calculated: " + javaProgress);
                                updateProgressUI("Java", javaProgress);
                                
                                // Calculate and display C# progress
                                int csharpProgress = calculateLanguageProgress(document, "c#");
                                Log.d(TAG, "C# progress calculated: " + csharpProgress);
                                updateProgressUI("C#", csharpProgress);
                                
                            } else {
                                Log.d(TAG, "Document does not exist, setting default progress");
                                // New user, set default progress
                                updateProgressUI("Java", 0);
                                updateProgressUI("C#", 0);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error loading user progress: " + e.getMessage());
                            // Set default progress on error
                            updateProgressUI("Java", 0);
                            updateProgressUI("C#", 0);
                        }
                    });
            } else {
                Log.w(TAG, "No user logged in, setting default progress");
                // No user logged in, set default progress
                updateProgressUI("Java", 0);
                updateProgressUI("C#", 0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in loadUserProgress: " + e.getMessage());
            updateProgressUI("Java", 0);
            updateProgressUI("C#", 0);
        }
    }
    
    /**
     * Calculates the total progress for a specific language
     * @param document Firestore document containing user progress
     * @param language Language key ("java" or "c#")
     * @return Number of completed questions (0-40)
     */
    private int calculateLanguageProgress(DocumentSnapshot document, String language) {
        try {
            int totalCompleted = 0;
            Log.d(TAG, "Calculating progress for language: " + language);
            
            // Get the progress map for the language
            if (document.contains("progress")) {
                java.util.Map<String, Object> progressMap = (java.util.Map<String, Object>) document.get("progress");
                Log.d(TAG, "Progress map keys: " + (progressMap != null ? progressMap.keySet() : "null"));
                
                if (progressMap != null && progressMap.containsKey(language)) {
                    java.util.Map<String, Object> languageMap = (java.util.Map<String, Object>) progressMap.get(language);
                    Log.d(TAG, "Language map keys: " + (languageMap != null ? languageMap.keySet() : "null"));
                    
                    if (languageMap != null) {
                                                 // Check each difficulty level - use the exact case as stored in SignUpScreen
                         String[] difficulties = {"DataAcolyte", "SystemKnight", "CodeWarden", "TechEmperor", "CodeAbyss"};
                        
                        for (String difficulty : difficulties) {
                            if (languageMap.containsKey(difficulty)) {
                                java.util.Map<String, Object> difficultyMap = (java.util.Map<String, Object>) languageMap.get(difficulty);
                                Log.d(TAG, "Difficulty " + difficulty + " map: " + (difficultyMap != null ? difficultyMap.keySet() : "null"));
                                
                                if (difficultyMap != null && difficultyMap.containsKey("questions")) {
                                    java.util.Map<String, Object> questionsMap = (java.util.Map<String, Object>) difficultyMap.get("questions");
                                    Log.d(TAG, "Questions map keys: " + (questionsMap != null ? questionsMap.keySet() : "null"));
                                    
                                    if (questionsMap != null) {
                                        // Count completed questions in this difficulty
                                        for (int i = 1; i <= 10; i++) {
                                            String questionKey = String.valueOf(i);
                                            if (questionsMap.containsKey(questionKey)) {
                                                Boolean isCompleted = (Boolean) questionsMap.get(questionKey);
                                                if (isCompleted != null && isCompleted) {
                                                    totalCompleted++;
                                                    Log.d(TAG, "Question " + i + " in " + difficulty + " is completed");
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.d(TAG, "Difficulty " + difficulty + " not found in language map");
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Language " + language + " not found in progress map");
                }
            } else {
                Log.d(TAG, "Progress field not found in document");
            }
            
            Log.d(TAG, "Total completed questions for " + language + ": " + totalCompleted);
            return totalCompleted;
        } catch (Exception e) {
            Log.e(TAG, "Error calculating progress for " + language + ": " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Updates the UI with the calculated progress
     * @param language Language name ("Java" or "C#")
     * @param completedQuestions Number of completed questions
     */
    private void updateProgressUI(String language, int completedQuestions) {
        try {
            Log.d(TAG, "Updating progress UI for " + language + " with " + completedQuestions + " completed questions");
            
            // Run UI updates on main thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (language.equals("Java")) {
                            ProgressBar progressBar = findViewById(R.id.progressBarJava);
                            TextView itemsAnswered = findViewById(R.id.itemsAnsweredJava);
                            
                            Log.d(TAG, "Java progressBar found: " + (progressBar != null));
                            Log.d(TAG, "Java itemsAnswered found: " + (itemsAnswered != null));
                            
                            if (progressBar != null) {
                                progressBar.setMax(40);
                                progressBar.setProgress(completedQuestions);
                                Log.d(TAG, "Java progressBar updated: max=40, progress=" + completedQuestions);
                            }
                            
                            if (itemsAnswered != null) {
                                itemsAnswered.setText(completedQuestions + "/40");
                                Log.d(TAG, "Java itemsAnswered updated: " + completedQuestions + "/40");
                            }
                        } else if (language.equals("C#")) {
                            ProgressBar progressBar = findViewById(R.id.progressBarCSharp);
                            TextView itemsAnswered = findViewById(R.id.itemsAnsweredCSharp);
                            
                            Log.d(TAG, "C# progressBar found: " + (progressBar != null));
                            Log.d(TAG, "C# itemsAnswered found: " + (itemsAnswered != null));
                            
                            if (progressBar != null) {
                                progressBar.setMax(40);
                                progressBar.setProgress(completedQuestions);
                                Log.d(TAG, "C# progressBar updated: max=40, progress=" + completedQuestions);
                            }
                            
                            if (itemsAnswered != null) {
                                itemsAnswered.setText(completedQuestions + "/40");
                                Log.d(TAG, "C# itemsAnswered updated: " + completedQuestions + "/40");
                            }
                        }
                        
                        // Add a final log to confirm the update completed
                        Log.d(TAG, "Progress UI update completed for " + language + " with " + completedQuestions + " questions");
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error in UI update runnable for " + language + ": " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error updating progress UI for " + language + ": " + e.getMessage());
        }
    }
    

}