package com.example.learnit;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LivesManager {
    private static final String TAG = "LivesManager";
    private static final int MAX_LIVES = 5;
    private static final int REGENERATION_TIME_MINUTES = 5;
    private static final int LIVES_PER_PURCHASE = 1;
    private static final int COST_PER_LIFE = 50;
    
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private Context context;
    private ScheduledExecutorService scheduler;
    
    public interface LivesCallback {
        void onLivesUpdated(int currentLives, long nextRegenerationTime);
        void onError(String error);
    }
    
    public interface PurchaseCallback {
        void onPurchaseSuccess(int newLivesCount, int remainingCoins);
        void onPurchaseFailed(String error);
    }
    
    public interface CacheUpdateCallback {
        void onLivesChanged(int newLivesCount);
    }
    
    private CacheUpdateCallback cacheUpdateCallback;
    
    public void setCacheUpdateCallback(CacheUpdateCallback callback) {
        this.cacheUpdateCallback = callback;
    }
    
    public LivesManager(Context context) {
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    /**
     * Initialize lives for a new user or get existing lives data
     */
    public void initializeLives(LivesCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("User not authenticated");
            return;
        }
        
        DocumentReference userRef = firestore.collection("User Quiz Records")
                .document(user.getUid());
        
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // User exists, check if lives data exists
                if (documentSnapshot.contains("lives")) {
                    // Lives data exists, return current data
                    int currentLives = documentSnapshot.getLong("lives").intValue();
                    long lastRegenerationTime = documentSnapshot.getLong("lastLifeRegeneration");
                    long nextRegenerationTime = calculateNextRegenerationTime(lastRegenerationTime, currentLives);
                    
                    // Update lives if regeneration time has passed
                    updateLivesIfNeeded(userRef, currentLives, lastRegenerationTime, callback);
                } else {
                    // Initialize lives for existing user
                    initializeNewLives(userRef, callback);
                }
            } else {
                callback.onError("User document not found");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting user document", e);
            callback.onError("Failed to get user data");
        });
    }
    
    /**
     * Initialize lives for a new user
     */
    private void initializeNewLives(DocumentReference userRef, LivesCallback callback) {
        Map<String, Object> livesData = new HashMap<>();
        livesData.put("lives", MAX_LIVES);
        livesData.put("lastLifeRegeneration", System.currentTimeMillis());
        
        userRef.update(livesData).addOnSuccessListener(aVoid -> {
            callback.onLivesUpdated(MAX_LIVES, 0);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error initializing lives", e);
            callback.onError("Failed to initialize lives");
        });
    }
    
    /**
     * Update lives if regeneration time has passed
     */
    private void updateLivesIfNeeded(DocumentReference userRef, int currentLives, 
                                   long lastRegenerationTime, LivesCallback callback) {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastRegeneration = currentTime - lastRegenerationTime;
        long regenerationTimeMs = REGENERATION_TIME_MINUTES * 60 * 1000L;
        
        int livesToAdd = (int) (timeSinceLastRegeneration / regenerationTimeMs);
        int newLivesCount = Math.min(MAX_LIVES, currentLives + livesToAdd);
        
        if (newLivesCount > currentLives) {
            // Lives have regenerated, update the database
            Map<String, Object> updates = new HashMap<>();
            updates.put("lives", newLivesCount);
            updates.put("lastLifeRegeneration", currentTime);
            
            userRef.update(updates).addOnSuccessListener(aVoid -> {
                long nextRegenerationTime = (newLivesCount < MAX_LIVES) ? 
                    currentTime + regenerationTimeMs : 0;
                // Update cache if callback is set
                if (cacheUpdateCallback != null) {
                    cacheUpdateCallback.onLivesChanged(newLivesCount);
                }
                callback.onLivesUpdated(newLivesCount, nextRegenerationTime);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error updating lives", e);
                callback.onError("Failed to update lives");
            });
        } else {
            // No lives regenerated yet
            long nextRegenerationTime = (currentLives < MAX_LIVES) ? 
                lastRegenerationTime + regenerationTimeMs : 0;
            callback.onLivesUpdated(currentLives, nextRegenerationTime);
        }
    }
    
    /**
     * Use a life (when user starts a quiz)
     */
    public void useLife(LivesCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("User not authenticated");
            return;
        }
        
        DocumentReference userRef = firestore.collection("User Quiz Records")
                .document(user.getUid());
        
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("lives")) {
                int currentLives = documentSnapshot.getLong("lives").intValue();
                
                if (currentLives > 0) {
                    // Update lives if regeneration time has passed first
                    long lastRegenerationTime = documentSnapshot.getLong("lastLifeRegeneration");
                    updateLivesIfNeeded(userRef, currentLives, lastRegenerationTime, new LivesCallback() {
                        @Override
                        public void onLivesUpdated(int updatedLives, long nextRegenerationTime) {
                            // Now use one life
                            int newLivesCount = updatedLives - 1;
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("lives", newLivesCount);
                            
                            userRef.update(updates).addOnSuccessListener(aVoid -> {
                                // Update cache if callback is set
                                if (cacheUpdateCallback != null) {
                                    cacheUpdateCallback.onLivesChanged(newLivesCount);
                                }
                                callback.onLivesUpdated(newLivesCount, nextRegenerationTime);
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Error using life", e);
                                callback.onError("Failed to use life");
                            });
                        }
                        
                        @Override
                        public void onError(String error) {
                            callback.onError(error);
                        }
                    });
                } else {
                    callback.onError("No lives remaining");
                }
            } else {
                callback.onError("Lives data not found");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting user document", e);
            callback.onError("Failed to get user data");
        });
    }
    
    /**
     * Purchase lives with coins
     */
    public void purchaseLives(PurchaseCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onPurchaseFailed("User not authenticated");
            return;
        }
        
        DocumentReference userRef = firestore.collection("User Quiz Records")
                .document(user.getUid());
        
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                int currentCoins = documentSnapshot.getLong("coins") != null ? 
                    documentSnapshot.getLong("coins").intValue() : 0;
                int currentLives = documentSnapshot.getLong("lives") != null ? 
                    documentSnapshot.getLong("lives").intValue() : 0;
                
                if (currentCoins >= COST_PER_LIFE) {
                    if (currentLives < MAX_LIVES) {
                        // Purchase the life
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("coins", currentCoins - COST_PER_LIFE);
                        updates.put("lives", currentLives + LIVES_PER_PURCHASE);
                        
                        userRef.update(updates).addOnSuccessListener(aVoid -> {
                            int newLivesCount = currentLives + LIVES_PER_PURCHASE;
                            // Update cache if callback is set
                            if (cacheUpdateCallback != null) {
                                cacheUpdateCallback.onLivesChanged(newLivesCount);
                            }
                            callback.onPurchaseSuccess(newLivesCount, 
                                currentCoins - COST_PER_LIFE);
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Error purchasing life", e);
                            callback.onPurchaseFailed("Failed to purchase life");
                        });
                    } else {
                        callback.onPurchaseFailed("Lives are already full");
                    }
                } else {
                    callback.onPurchaseFailed("Not enough coins");
                }
            } else {
                callback.onPurchaseFailed("User document not found");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting user document", e);
            callback.onPurchaseFailed("Failed to get user data");
        });
    }
    
    /**
     * Get current lives status
     */
    public void getCurrentLives(LivesCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("User not authenticated");
            return;
        }
        
        DocumentReference userRef = firestore.collection("User Quiz Records")
                .document(user.getUid());
        
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("lives")) {
                int currentLives = documentSnapshot.getLong("lives").intValue();
                long lastRegenerationTime = documentSnapshot.getLong("lastLifeRegeneration");
                long nextRegenerationTime = calculateNextRegenerationTime(lastRegenerationTime, currentLives);
                
                callback.onLivesUpdated(currentLives, nextRegenerationTime);
            } else {
                callback.onError("Lives data not found");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting lives data", e);
            callback.onError("Failed to get lives data");
        });
    }
    
    /**
     * Calculate next regeneration time
     */
    private long calculateNextRegenerationTime(long lastRegenerationTime, int currentLives) {
        if (currentLives >= MAX_LIVES) {
            return 0; // No regeneration needed
        }
        
        long regenerationTimeMs = REGENERATION_TIME_MINUTES * 60 * 1000L;
        return lastRegenerationTime + regenerationTimeMs;
    }
    

    
    /**
     * Start regeneration timer (for UI updates)
     */
    public void startRegenerationTimer(long nextRegenerationTime, LivesCallback callback) {
        if (nextRegenerationTime <= 0) return;
        
        long delay = nextRegenerationTime - System.currentTimeMillis();
        if (delay > 0) {
            scheduler.schedule(() -> {
                // Timer expired, refresh lives
                getCurrentLives(callback);
            }, delay, TimeUnit.MILLISECONDS);
        }
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
    
    /**
     * Get constants for UI
     */
    public static int getMaxLives() {
        return MAX_LIVES;
    }
    
    public static int getRegenerationTimeMinutes() {
        return REGENERATION_TIME_MINUTES;
    }
    
    public static int getCostPerLife() {
        return COST_PER_LIFE;
    }
}
