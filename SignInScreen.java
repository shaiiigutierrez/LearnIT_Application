package com.example.learnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class QuizProgress {
    private static final String TAG = "QuizProgress";
    private static final String PREF_NAME = "QuizProgress";
    private static final String KEY_C_SHARP_DATA_ACOLYTE_COMPLETED = "csharp_data_acolyte_completed";
    private static final String KEY_C_SHARP_DATA_ACOLYTE_SCORE = "csharp_data_acolyte_score";
    
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    
    public interface ProgressCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public interface ProgressPercentageCallback {
        void onSuccess(int percentage);
        void onError(String error);
    }
    
    public interface HighestQuestionCallback {
        void onSuccess(int questionNumber);
        void onError(String error);
    }
    
    public QuizProgress(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }
    
    /**
     * Get current user's document reference
     */
    private DocumentReference getUserDocument() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return null;
        return firestore.collection("User Quiz Records").document(user.getUid());
    }
    
    /**
     * Set level completion status in Firestore
     */
    public void setLevelCompleted(String language, String difficulty, boolean completed, ProgressCallback callback) {
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            if (callback != null) callback.onError("User not authenticated");
            return;
        }
        
        // Use the exact case as stored in SignUpScreen (mixed case)
        String fieldPath = "progress." + language.toLowerCase() + "." + difficulty + ".completed";
        Map<String, Object> update = new HashMap<>();
        update.put(fieldPath, completed);
        
        userRef.update(update)
            .addOnSuccessListener(aVoid -> {
                if (callback != null) callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating level completion", e);
                if (callback != null) callback.onError("Failed to update level completion");
            });
    }
    
    /**
     * Check if level is completed from Firestore
     */
    public void isLevelCompleted(String language, String difficulty, ProgressCallback callback) {
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            if (callback != null) callback.onError("User not authenticated");
            return;
        }
        
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> progress = (Map<String, Object>) documentSnapshot.get("progress");
                if (progress != null) {
                    Map<String, Object> languageProgress = (Map<String, Object>) progress.get(language.toLowerCase());
                    if (languageProgress != null) {
                        // Use the exact case as stored in SignUpScreen (mixed case)
                        Map<String, Object> difficultyProgress = (Map<String, Object>) languageProgress.get(difficulty);
                        if (difficultyProgress != null) {
                            Boolean completed = (Boolean) difficultyProgress.get("completed");
                            if (completed != null && completed) {
                                if (callback != null) callback.onSuccess();
                            } else {
                                if (callback != null) callback.onError("Level not completed");
                            }
                            return;
                        }
                    }
                }
                // Default to not completed (level doesn't exist or is false)
                if (callback != null) callback.onError("Level not completed");
            } else {
                // No document exists, default to not completed
                if (callback != null) callback.onError("Level not completed");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting level completion", e);
            if (callback != null) callback.onError("Failed to get level completion");
        });
    }
    
    /**
     * Set level score in Firestore
     */
    public void setLevelScore(String language, String difficulty, int score, ProgressCallback callback) {
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            if (callback != null) callback.onError("User not authenticated");
            return;
        }
        
        // Use the exact case as stored in SignUpScreen (mixed case)
        String fieldPath = "progress." + language.toLowerCase() + "." + difficulty + ".score";
        Map<String, Object> update = new HashMap<>();
        update.put(fieldPath, score);
        
        userRef.update(update)
            .addOnSuccessListener(aVoid -> {
                if (callback != null) callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating level score", e);
                if (callback != null) callback.onError("Failed to update level score");
            });
    }
    
    /**
     * Get level score from Firestore
     */
    public void getLevelScore(String language, String difficulty, ProgressCallback callback) {
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            if (callback != null) callback.onError("User not authenticated");
            return;
        }
        
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> progress = (Map<String, Object>) documentSnapshot.get("progress");
                if (progress != null) {
                    Map<String, Object> languageProgress = (Map<String, Object>) progress.get(language.toLowerCase());
                    if (languageProgress != null) {
                        // Use the exact case as stored in SignUpScreen (mixed case)
                        Map<String, Object> difficultyProgress = (Map<String, Object>) languageProgress.get(difficulty);
                        if (difficultyProgress != null) {
                            Long score = (Long) difficultyProgress.get("score");
                            if (callback != null) callback.onSuccess();
                            return;
                        }
                    }
                }
                // Default to 0 if no score data found
                if (callback != null) callback.onSuccess();
            } else {
                // No document exists, default to 0
                if (callback != null) callback.onSuccess();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting level score", e);
            if (callback != null) callback.onError("Failed to get level score");
        });
    }
    
    /**
     * Set individual question completion status in Firestore
     */
    public void setQuestionCompleted(String language, String difficulty, int questionNumber, boolean completed, ProgressCallback callback) {
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            if (callback != null) callback.onError("User not authenticated");
            return;
        }
        
        // Use the exact case as stored in SignUpScreen (mixed case)
        String fieldPath = "progress." + language.toLowerCase() + "." + difficulty + ".questions." + questionNumber;
        Map<String, Object> update = new HashMap<>();
        update.put(fieldPath, completed);
        
        userRef.update(update)
            .addOnSuccessListener(aVoid -> {
                if (callback != null) callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating question completion", e);
                if (callback != null) callback.onError("Failed to update question completion");
            });
    }
    
    /**
     * Check if individual question is completed from Firestore
     */
    public void isQuestionCompleted(String language, String difficulty, int questionNumber, ProgressCallback callback) {
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            if (callback != null) callback.onError("User not authenticated");
            return;
        }
        
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> progress = (Map<String, Object>) documentSnapshot.get("progress");
                if (progress != null) {
                    Map<String, Object> languageProgress = (Map<String, Object>) progress.get(language.toLowerCase());
                    if (languageProgress != null) {
                        // Use the exact case as stored in SignUpScreen (mixed case)
                        Map<String, Object> difficultyProgress = (Map<String, Object>) languageProgress.get(difficulty);
                        if (difficultyProgress != null) {
                            Map<String, Object> questions = (Map<String, Object>) difficultyProgress.get("questions");
                            if (questions != null) {
                                Boolean completed = (Boolean) questions.get(String.valueOf(questionNumber));
                                if (completed != null && completed) {
                                    if (callback != null) callback.onSuccess();
                                } else {
                                    if (callback != null) callback.onError("Question not completed");
                                }
                                return;
                            }
                        }
                    }
                }
                // Default to not completed (question doesn't exist or is false)
                if (callback != null) callback.onError("Question not completed");
            } else {
                // No document exists, default to not completed
                if (callback != null) callback.onError("Question not completed");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting question completion", e);
            if (callback != null) callback.onError("Failed to get question completion");
        });
    }
    
    /**
     * Check if all questions in a level are completed
     */
    public void areAllQuestionsCompleted(String language, String difficulty, int totalQuestions, ProgressCallback callback) {
        Log.d(TAG, "Checking if all questions completed for " + difficulty + " in " + language + " (total: " + totalQuestions + ")");
        
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            Log.e(TAG, "User not authenticated");
            if (callback != null) callback.onError("User not authenticated");
            return;
        }
        
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> progress = (Map<String, Object>) documentSnapshot.get("progress");
                if (progress != null) {
                    Map<String, Object> languageProgress = (Map<String, Object>) progress.get(language.toLowerCase());
                    if (languageProgress != null) {
                        // Use the exact case as stored in SignUpScreen (mixed case)
                        Map<String, Object> difficultyProgress = (Map<String, Object>) languageProgress.get(difficulty);
                        if (difficultyProgress != null) {
                            Map<String, Object> questions = (Map<String, Object>) difficultyProgress.get("questions");
                            if (questions != null) {
                                Log.d(TAG, "Found questions data for " + difficulty + " (key: " + difficulty + "): " + questions.toString());
                                boolean allCompleted = true;
                                for (int i = 1; i <= totalQuestions; i++) {
                                    Boolean completed = (Boolean) questions.get(String.valueOf(i));
                                    Log.d(TAG, "Question " + i + " completed: " + completed);
                                    if (completed == null || !completed) {
                                        allCompleted = false;
                                        Log.d(TAG, "Question " + i + " is not completed, breaking");
                                        break;
                                    }
                                }
                                if (allCompleted) {
                                    Log.d(TAG, "All questions completed for " + difficulty);
                                    if (callback != null) callback.onSuccess();
                                } else {
                                    Log.d(TAG, "Not all questions completed for " + difficulty);
                                    if (callback != null) callback.onError("Not all questions completed");
                                }
                                return;
                            } else {
                                Log.d(TAG, "No questions data found for " + difficulty);
                            }
                        } else {
                            Log.d(TAG, "No difficulty progress found for " + difficulty + " (key: " + difficulty + ")");
                            Log.d(TAG, "Available keys in languageProgress: " + languageProgress.keySet().toString());
                        }
                    } else {
                        Log.d(TAG, "No language progress found for " + language);
                    }
                } else {
                    Log.d(TAG, "No progress data found");
                }
                // Default to not completed (all questions are false for new users)
                Log.d(TAG, "Defaulting to not completed for " + difficulty);
                if (callback != null) callback.onError("Not all questions completed");
            } else {
                // No document exists, default to not completed
                Log.d(TAG, "No document exists, defaulting to not completed for " + difficulty);
                if (callback != null) callback.onError("Not all questions completed");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error checking all questions completion", e);
            if (callback != null) callback.onError("Failed to check questions completion");
        });
    }
    
    /**
     * Get the highest unlocked question number
     */
    public void getHighestUnlockedQuestion(String language, String difficulty, HighestQuestionCallback callback) {
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            if (callback != null) callback.onError("User not authenticated");
            return;
        }
        
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> progress = (Map<String, Object>) documentSnapshot.get("progress");
                if (progress != null) {
                    Map<String, Object> languageProgress = (Map<String, Object>) progress.get(language.toLowerCase());
                    if (languageProgress != null) {
                        // Use the exact case as stored in SignUpScreen (mixed case)
                        Map<String, Object> difficultyProgress = (Map<String, Object>) languageProgress.get(difficulty);
                        if (difficultyProgress != null) {
                            Map<String, Object> questions = (Map<String, Object>) difficultyProgress.get("questions");
                            if (questions != null) {
                                int questionNumber = 1;
                                while (questions.containsKey(String.valueOf(questionNumber)) && 
                                       (Boolean) questions.get(String.valueOf(questionNumber))) {
                                    questionNumber++;
                                }
                                if (callback != null) callback.onSuccess(questionNumber);
                                return;
                            }
                        }
                    }
                }
                // Default to 1 if no questions data found
                if (callback != null) callback.onSuccess(1);
            } else {
                // No document exists, default to 1
                if (callback != null) callback.onSuccess(1);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting highest unlocked question", e);
            if (callback != null) callback.onError("Failed to get highest unlocked question");
        });
    }
    
    /**
     * Check if a difficulty level is unlocked
     */
    public void isDifficultyUnlocked(String language, String difficulty, ProgressCallback callback) {
        Log.d(TAG, "=== isDifficultyUnlocked called ===");
        Log.d(TAG, "Language: " + language + ", Difficulty: " + difficulty);
        
        if ("DataAcolyte".equals(difficulty)) {
            // Data Acolyte is always unlocked
            Log.d(TAG, "Data Acolyte is always unlocked");
            if (callback != null) callback.onSuccess();
            return;
        }
        
        // For other difficulties, check if previous difficulty is completed
        String previousDifficulty = getPreviousDifficulty(difficulty);
        Log.d(TAG, "Previous difficulty for " + difficulty + " is: " + previousDifficulty);
        
        if (previousDifficulty != null) {
            Log.d(TAG, "Checking if " + previousDifficulty + " is completed to unlock " + difficulty);
            Log.d(TAG, "Calling areAllQuestionsCompleted for " + previousDifficulty);
            areAllQuestionsCompleted(language, previousDifficulty, 10, new ProgressCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "SUCCESS: Previous difficulty " + previousDifficulty + " is completed - " + difficulty + " is UNLOCKED");
                    if (callback != null) callback.onSuccess();
                }
                
                @Override
                public void onError(String error) {
                    Log.d(TAG, "ERROR: Previous difficulty " + previousDifficulty + " is NOT completed - " + difficulty + " is LOCKED. Error: " + error);
                    if (callback != null) callback.onError(error);
                }
            });
        } else {
            Log.d(TAG, "ERROR: No previous difficulty found for " + difficulty);
            if (callback != null) callback.onError("No previous difficulty found");
        }
        Log.d(TAG, "=== isDifficultyUnlocked finished ===");
    }
    
    /**
     * Get the difficulty key for Firestore (lowercase)
     */
    private String getDifficultyKey(String difficulty) {
        return difficulty.toLowerCase();
    }
    
    /**
     * Get the previous difficulty level
     */
    private String getPreviousDifficulty(String difficulty) {
        switch (difficulty) {
            case "SystemKnight":
                return "DataAcolyte";
            case "CodeWarden":
                return "SystemKnight";
            case "TechEmperor":
                return "CodeWarden";
            case "CodeAbyss":
                return "TechEmperor";
            default:
                return null;
        }
    }
    
    /**
     * Test method to manually mark all questions in Data Acolyte, System Knight, and Code Warden as completed for testing
     */
    public void testMarkAllDifficultiesCompleted(String language) {
        Log.d(TAG, "TEST: Marking all Data Acolyte, System Knight, and Code Warden questions as completed for " + language);
        
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            Log.e(TAG, "User not authenticated for test");
            return;
        }
        
        Map<String, Object> updates = new HashMap<>();
        
        // Mark all Data Acolyte questions as completed
        for (int i = 1; i <= 10; i++) {
            String fieldPath = "progress." + language.toLowerCase() + ".DataAcolyte.questions." + i;
            updates.put(fieldPath, true);
        }
        
        // Mark all System Knight questions as completed
        for (int i = 1; i <= 10; i++) {
            String fieldPath = "progress." + language.toLowerCase() + ".SystemKnight.questions." + i;
            updates.put(fieldPath, true);
        }
        
        // Mark all Code Warden questions as completed
        for (int i = 1; i <= 10; i++) {
            String fieldPath = "progress." + language.toLowerCase() + ".CodeWarden.questions." + i;
            updates.put(fieldPath, true);
        }
        
        userRef.update(updates)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "TEST: Successfully marked all Data Acolyte, System Knight, and Code Warden questions as completed");
                debugPrintUserProgress(); // Print the updated data
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "TEST: Error marking questions as completed", e);
            });
    }
    
    /**
     * Test method to manually mark all questions in Data Acolyte and System Knight as completed for testing
     */
    public void testMarkDataAcolyteAndSystemKnightCompleted(String language) {
        Log.d(TAG, "TEST: Marking all Data Acolyte and System Knight questions as completed for " + language);
        
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            Log.e(TAG, "User not authenticated for test");
            return;
        }
        
        Map<String, Object> updates = new HashMap<>();
        
        // Mark all Data Acolyte questions as completed
        for (int i = 1; i <= 10; i++) {
            String fieldPath = "progress." + language.toLowerCase() + ".DataAcolyte.questions." + i;
            updates.put(fieldPath, true);
        }
        
        // Mark all System Knight questions as completed
        for (int i = 1; i <= 10; i++) {
            String fieldPath = "progress." + language.toLowerCase() + ".SystemKnight.questions." + i;
            updates.put(fieldPath, true);
        }
        
        userRef.update(updates)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "TEST: Successfully marked all Data Acolyte and System Knight questions as completed");
                debugPrintUserProgress(); // Print the updated data
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "TEST: Error marking questions as completed", e);
            });
    }
    
    /**
     * Test method to manually mark all questions in Data Acolyte as completed for testing
     */
    public void testMarkDataAcolyteCompleted(String language) {
        Log.d(TAG, "TEST: Marking all Data Acolyte questions as completed for " + language);
        
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            Log.e(TAG, "User not authenticated for test");
            return;
        }
        
        Map<String, Object> updates = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            String fieldPath = "progress." + language.toLowerCase() + ".DataAcolyte.questions." + i;
            updates.put(fieldPath, true);
        }
        
        userRef.update(updates)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "TEST: Successfully marked all Data Acolyte questions as completed");
                debugPrintUserProgress(); // Print the updated data
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "TEST: Error marking Data Acolyte questions as completed", e);
            });
    }
    
    /**
     * Debug method to print current user's progress data
     */
    public void debugPrintUserProgress() {
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            Log.e(TAG, "User not authenticated for debug");
            return;
        }
        
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d(TAG, "=== DEBUG: User Progress Data ===");
                Log.d(TAG, "Document ID: " + documentSnapshot.getId());
                
                Map<String, Object> progress = (Map<String, Object>) documentSnapshot.get("progress");
                if (progress != null) {
                    Log.d(TAG, "Progress data found: " + progress.toString());
                    
                    for (String languageKey : progress.keySet()) {
                        Log.d(TAG, "Language: " + languageKey);
                        Map<String, Object> languageProgress = (Map<String, Object>) progress.get(languageKey);
                        if (languageProgress != null) {
                            for (String difficultyKey : languageProgress.keySet()) {
                                Log.d(TAG, "  Difficulty: " + difficultyKey);
                                Map<String, Object> difficultyProgress = (Map<String, Object>) languageProgress.get(difficultyKey);
                                if (difficultyProgress != null) {
                                    Log.d(TAG, "    Difficulty data: " + difficultyProgress.toString());
                                    
                                    Map<String, Object> questions = (Map<String, Object>) difficultyProgress.get("questions");
                                    if (questions != null) {
                                        Log.d(TAG, "    Questions data: " + questions.toString());
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "No progress data found");
                }
                Log.d(TAG, "=== END DEBUG ===");
            } else {
                Log.d(TAG, "No document exists for user");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting user document for debug", e);
        });
    }
    
    /**
     * Get progress percentage for a difficulty level
     */
    public void getProgressPercentage(String language, String difficulty, ProgressPercentageCallback callback) {
        DocumentReference userRef = getUserDocument();
        if (userRef == null) {
            if (callback != null) callback.onError("User not authenticated");
            return;
        }
        
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> progress = (Map<String, Object>) documentSnapshot.get("progress");
                if (progress != null) {
                    Map<String, Object> languageProgress = (Map<String, Object>) progress.get(language.toLowerCase());
                    if (languageProgress != null) {
                        // Use the exact case as stored in SignUpScreen (mixed case)
                        Map<String, Object> difficultyProgress = (Map<String, Object>) languageProgress.get(difficulty);
                        if (difficultyProgress != null) {
                            Map<String, Object> questions = (Map<String, Object>) difficultyProgress.get("questions");
                            if (questions != null) {
                                int completedQuestions = 0;
                                for (int i = 1; i <= 10; i++) {
                                    Boolean completed = (Boolean) questions.get(String.valueOf(i));
                                    if (completed != null && completed) {
                                        completedQuestions++;
                                    }
                                }
                                int percentage = (completedQuestions * 100) / 10;
                                if (callback != null) callback.onSuccess(percentage);
                                return;
                            }
                        }
                    }
                }
                // Default to 0% if no progress data found
                if (callback != null) callback.onSuccess(0);
            } else {
                // No document exists, default to 0%
                if (callback != null) callback.onSuccess(0);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting progress percentage", e);
            if (callback != null) callback.onError("Failed to get progress percentage");
        });
    }
    
    // Legacy methods for backward compatibility (using SharedPreferences)
    public void setLevelCompleted(String language, String difficulty, boolean completed) {
        String key = getKey(language, difficulty, "completed");
        sharedPreferences.edit().putBoolean(key, completed).apply();
    }
    
    public boolean isLevelCompleted(String language, String difficulty) {
        String key = getKey(language, difficulty, "completed");
        return sharedPreferences.getBoolean(key, false);
    }
    
    public void setLevelScore(String language, String difficulty, int score) {
        String key = getKey(language, difficulty, "score");
        sharedPreferences.edit().putInt(key, score).apply();
    }
    
    public int getLevelScore(String language, String difficulty) {
        String key = getKey(language, difficulty, "score");
        return sharedPreferences.getInt(key, 0);
    }
    
    public void setQuestionCompleted(String language, String difficulty, int questionNumber, boolean completed) {
        String key = getKey(language, difficulty, "question_" + questionNumber);
        sharedPreferences.edit().putBoolean(key, completed).apply();
    }
    
    public boolean isQuestionCompleted(String language, String difficulty, int questionNumber) {
        String key = getKey(language, difficulty, "question_" + questionNumber);
        return sharedPreferences.getBoolean(key, false);
    }
    
    public boolean areAllQuestionsCompleted(String language, String difficulty, int totalQuestions) {
        for (int i = 1; i <= totalQuestions; i++) {
            if (!isQuestionCompleted(language, difficulty, i)) {
                return false;
            }
        }
        return true;
    }
    
    public int getHighestUnlockedQuestion(String language, String difficulty) {
        int questionNumber = 1;
        while (isQuestionCompleted(language, difficulty, questionNumber)) {
            questionNumber++;
        }
        return questionNumber;
    }
    
    /**
     * Legacy method - DO NOT USE. Use the Firestore-based version instead.
     * This method uses SharedPreferences which is not synchronized with Firestore.
     */
    @Deprecated
    public boolean isDifficultyUnlocked(String language, String difficulty) {
        Log.w(TAG, "Using deprecated SharedPreferences-based isDifficultyUnlocked method. Use Firestore version instead.");
        if ("DataAcolyte".equals(difficulty)) {
            return true;
        } else if ("SystemKnight".equals(difficulty)) {
            return areAllQuestionsCompleted(language, "DataAcolyte", 10);
        } else if ("CodeWarden".equals(difficulty)) {
            return areAllQuestionsCompleted(language, "SystemKnight", 10);
        } else if ("TechEmperor".equals(difficulty)) {
            return areAllQuestionsCompleted(language, "CodeWarden", 10);
        }
        return false;
    }
    
    private String getKey(String language, String difficulty, String type) {
        return language.toLowerCase() + "_" + difficulty.toLowerCase() + "_" + type;
    }
    
    // Specific methods for C# Data Acolyte
    public void setCSharpDataAcolyteCompleted(boolean completed) {
        setLevelCompleted("C#", "DataAcolyte", completed);
    }
    
    public boolean isCSharpDataAcolyteCompleted() {
        return isLevelCompleted("C#", "DataAcolyte");
    }
    
    public void setCSharpDataAcolyteScore(int score) {
        setLevelScore("C#", "DataAcolyte", score);
    }
    
    public int getCSharpDataAcolyteScore() {
        return getLevelScore("C#", "DataAcolyte");
    }
}