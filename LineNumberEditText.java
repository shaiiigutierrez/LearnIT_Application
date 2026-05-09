package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.animation.ObjectAnimator;
import android.widget.FrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import androidx.annotation.NonNull;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import java.util.HashMap;
import java.util.Map;

public class HomeScreen extends BaseActivity {

    private static final int LANGUAGE_SELECTION_REQUEST = 2001;
    private CustomBottomNavigation bottomNavigation;
    private boolean isDropdownOpen = false;
    private String currentLanguage = null; // No default language for new users
    private String currentDifficulty = "DATA ACOLYTE"; // Default difficulty
    private LivesManager livesManager;
    private TextView coinsTextView;
    private ImageView life1, life2, life3, life4, life5;
    private Handler handler;
    private Runnable livesUpdateRunnable;
    private UserDataManager userDataManager;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check network connectivity and log status
        NetworkUtils.logNetworkStatus(this);

        // Initialize lives manager
        livesManager = new LivesManager(this);
        userDataManager = UserDataManager.getInstance(this);
        
        // Set up cache update callback for lives changes
        livesManager.setCacheUpdateCallback(new LivesManager.CacheUpdateCallback() {
            @Override
            public void onLivesChanged(int newLivesCount) {
                userDataManager.updateLives(newLivesCount);
            }
        });
        
        // Preload all data to populate cache
        userDataManager.preloadAllData();
        
        handler = new Handler(Looper.getMainLooper());
        
        // Setup custom bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setCurrentActivity("HomeScreen");
        }
        
        // Initialize UI elements
        initializeLivesUI();

        // Initialize dropdown elements
        final View dropdownOverlay = findViewById(R.id.dropdownOverlay);
        final LinearLayout dropdownLevelsContainer = findViewById(R.id.dropdownLevelsContainer);
        final LinearLayout dataAcolyteLevel = findViewById(R.id.dataAcolyteLevel);
        final LinearLayout systemKnightLevel = findViewById(R.id.systemKnightLevel);
        final LinearLayout codeWardenLevel = findViewById(R.id.codeWardenLevel);
        final LinearLayout techEmperorLevel = findViewById(R.id.techEmperorLevel);
        final LinearLayout codeAbyssLevel = findViewById(R.id.codeAbyssLevel);
        final LinearLayout[] levels = {dataAcolyteLevel, systemKnightLevel, codeWardenLevel, techEmperorLevel, codeAbyssLevel};
        // Ensure all levels start invisible
        for (LinearLayout level : levels) {
            if (level != null) level.setVisibility(View.INVISIBLE);
        }
        dropdownLevelsContainer.setVisibility(View.INVISIBLE);
        dropdownOverlay.setVisibility(View.GONE);
        dropdownOverlay.setAlpha(0f);
        final boolean[] isDropdownVisible = {false};
        
        // Initialize quiz progress
        QuizProgress quizProgress = new QuizProgress(this);
        

        
        // Check authentication status
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("HomeScreen", "User authentication status in onCreate: " + (user != null ? "Authenticated" : "Not authenticated"));
        
        if (user == null) {
            Log.w("HomeScreen", "No user authenticated in onCreate - this might cause issues");
        }
        
        // Load data with instant display and background refresh
        loadDataWithCache();
        
        // Load current language and setup UI accordingly
        loadCurrentLanguageFromFirestore();
        
        // Get reference to subtitle TextView
        final TextView subTitle = findViewById(R.id.subTitle);
        
        // Setup difficulty level click listeners will be handled by loadCurrentLanguageFromFirestore
        // Setup levels icon click listener for difficulty dropdown
        ImageView levelsIcon = findViewById(R.id.levelsIcon);
        if (levelsIcon != null) {
            levelsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDropdownVisible[0]) {
                    // Show overlay and dropdown
                    dropdownOverlay.setVisibility(View.VISIBLE);
                    dropdownOverlay.setClickable(true);
                    dropdownOverlay.setFocusable(true);
                    ObjectAnimator.ofFloat(dropdownOverlay, "alpha", 0f, 1f).setDuration(250).start();
                    dropdownLevelsContainer.setVisibility(View.VISIBLE);
                    Animation dropdownAnim = AnimationUtils.loadAnimation(HomeScreen.this, R.anim.dropdown_animation);
                    if (dataAcolyteLevel != null) {
                        dataAcolyteLevel.setVisibility(View.VISIBLE);
                        dataAcolyteLevel.startAnimation(dropdownAnim);
                    }
                    if (systemKnightLevel != null) {
                        systemKnightLevel.postDelayed(() -> {
                            systemKnightLevel.setVisibility(View.VISIBLE);
                            systemKnightLevel.startAnimation(dropdownAnim);
                        }, 100);
                    }
                    if (codeWardenLevel != null) {
                        codeWardenLevel.postDelayed(() -> {
                            codeWardenLevel.setVisibility(View.VISIBLE);
                            codeWardenLevel.startAnimation(dropdownAnim);
                        }, 100);
                    }
                    if (techEmperorLevel != null) {
                        techEmperorLevel.postDelayed(() -> {
                            techEmperorLevel.setVisibility(View.VISIBLE);
                            techEmperorLevel.startAnimation(dropdownAnim);
                        }, 100);
                    }
                    if (codeAbyssLevel != null) {
                        codeAbyssLevel.postDelayed(() -> {
                            codeAbyssLevel.setVisibility(View.VISIBLE);
                            codeAbyssLevel.startAnimation(dropdownAnim);
                        }, 100);
                    }

                    // Disable navigation when dropdown opens
                    disableNavigationDuringDropdown();

                    // Consume touches on the level area while dropdown is open
                    final View levelScroll = findViewById(R.id.levelScroll);
                    if (levelScroll != null) {
                        levelScroll.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return true; // consume all touches
                            }
                        });
                        levelScroll.setAlpha(0.9f);
                    }

                    // Allow tapping the dim overlay to close the dropdown
                    dropdownOverlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ObjectAnimator.ofFloat(dropdownOverlay, "alpha", 1f, 0f).setDuration(250).start();
                            dropdownOverlay.postDelayed(() -> dropdownOverlay.setVisibility(View.GONE), 250);
                            dropdownLevelsContainer.setVisibility(View.INVISIBLE);
                            for (LinearLayout level : levels) {
                                if (level != null) level.setVisibility(View.INVISIBLE);
                            }
                            enableNavigationAfterDropdown();
                            View levelScroll = findViewById(R.id.levelScroll);
                            if (levelScroll != null) {
                                levelScroll.setOnTouchListener(null);
                                levelScroll.setAlpha(1f);
                            }
                            isDropdownVisible[0] = false;
                            isDropdownOpen = false;
                            dropdownOverlay.setOnClickListener(null);
                            dropdownOverlay.setClickable(false);
                            dropdownOverlay.setFocusable(false);
                        }
                    });
                } else {
                    // Hide overlay and dropdown
                    ObjectAnimator.ofFloat(dropdownOverlay, "alpha", 1f, 0f).setDuration(250).start();
                    dropdownOverlay.postDelayed(() -> dropdownOverlay.setVisibility(View.GONE), 250);
                    dropdownLevelsContainer.setVisibility(View.INVISIBLE);
                    for (LinearLayout level : levels) {
                        if (level != null) level.setVisibility(View.INVISIBLE);
                    }

                    // Enable navigation when dropdown closes
                    enableNavigationAfterDropdown();

                                                // Re-enable level interactions
                            View levelScroll = findViewById(R.id.levelScroll);
                            if (levelScroll != null) {
                                levelScroll.setOnTouchListener(null);
                                levelScroll.setAlpha(1f);
                            }
                    dropdownOverlay.setOnClickListener(null);
                    dropdownOverlay.setClickable(false);
                    dropdownOverlay.setFocusable(false);
                }
                isDropdownVisible[0] = !isDropdownVisible[0];
                isDropdownOpen = isDropdownVisible[0];
            }
        });
        }

        final TextView languageTitle = findViewById(R.id.languageTitle);
        if (languageTitle != null) {
            // Set the language title based on the current language
            if (currentLanguage == null) {
                languageTitle.setText("Select a language here ->");
            } else {
                languageTitle.setText(currentLanguage);
            }
        }

        ImageView replaceButton = findViewById(R.id.replaceButton);
        replaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, LanguageSelection.class);
                startActivity(intent);
            }
        });
        configureScrollView();
    }

    /**
     * Disables the bottom navigation bar and language selection button when dropdown is open.
     */
    private void disableNavigationDuringDropdown() {
        if (isDropdownOpen) return; // Already disabled
        
        try {
            if (bottomNavigation != null) {
                // Find the menu buttons and disable their click listeners
                LinearLayout btnShop = bottomNavigation.findViewById(R.id.btnShop);
                LinearLayout btnHome = bottomNavigation.findViewById(R.id.btnHome);
                LinearLayout btnProfile = bottomNavigation.findViewById(R.id.btnProfile);
                
                if (btnShop != null) {
                    btnShop.setOnClickListener(null);
                    btnShop.setEnabled(false);
                    btnShop.setClickable(false);
                }
                if (btnHome != null) {
                    btnHome.setOnClickListener(null);
                    btnHome.setEnabled(false);
                    btnHome.setClickable(false);
                }
                if (btnProfile != null) {
                    btnProfile.setOnClickListener(null);
                    btnProfile.setEnabled(false);
                    btnProfile.setClickable(false);
                }
                
                // Make the entire bottom navigation appear disabled
                bottomNavigation.setAlpha(0.5f);
                bottomNavigation.setEnabled(false);
            }
            
            // Disable language selection button
            ImageView replaceButton = findViewById(R.id.replaceButton);
            if (replaceButton != null) {
                replaceButton.setOnClickListener(null);
                replaceButton.setEnabled(false);
                replaceButton.setClickable(false);
                replaceButton.setAlpha(0.5f);
            }
            
            isDropdownOpen = true;
        } catch (Exception e) {
            // Handle any errors silently
        }
    }
    
    /**
     * Re-enables the bottom navigation bar and language selection button when dropdown is closed.
     */
    private void enableNavigationAfterDropdown() {
        if (!isDropdownOpen) return; // Already enabled
        
        try {
            if (bottomNavigation != null) {
                // Find the menu buttons and re-enable their click listeners
                LinearLayout btnShop = bottomNavigation.findViewById(R.id.btnShop);
                LinearLayout btnHome = bottomNavigation.findViewById(R.id.btnHome);
                LinearLayout btnProfile = bottomNavigation.findViewById(R.id.btnProfile);
                
                if (btnShop != null) {
                    btnShop.setEnabled(true);
                    btnShop.setClickable(true);
                }
                if (btnHome != null) {
                    btnHome.setEnabled(true);
                    btnHome.setClickable(true);
                }
                if (btnProfile != null) {
                    btnProfile.setEnabled(true);
                    btnProfile.setClickable(true);
                }
                
                // Restore the bottom navigation appearance
                bottomNavigation.setAlpha(1.0f);
                bottomNavigation.setEnabled(true);
                
                // Re-initialize the bottom navigation to restore click listeners
                if (bottomNavigation != null) {
                    bottomNavigation.setCurrentActivity("HomeScreen");
                }
                
                // Re-initialize click listeners using the public method
                bottomNavigation.reinitializeClickListeners();
            }
            
            // Re-enable language selection button
            ImageView replaceButton = findViewById(R.id.replaceButton);
            if (replaceButton != null) {
                replaceButton.setEnabled(true);
                replaceButton.setClickable(true);
                replaceButton.setAlpha(1.0f);
                
                // Re-set the original click listener
                replaceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeScreen.this, LanguageSelection.class);
                        startActivityForResult(intent, LANGUAGE_SELECTION_REQUEST);
                    }
                });
            }
            
            isDropdownOpen = false;
        } catch (Exception e) {
            // Handle any errors silently
        }
    }
    
    @Override
    public void onBackPressed() {
        // Check if dropdown is visible and close it if so
        View dropdownOverlay = findViewById(R.id.dropdownOverlay);
        if (dropdownOverlay != null && dropdownOverlay.getVisibility() == View.VISIBLE) {
            // Close dropdown and re-enable navigation
            LinearLayout dropdownLevelsContainer = findViewById(R.id.dropdownLevelsContainer);
            LinearLayout dataAcolyteLevel = findViewById(R.id.dataAcolyteLevel);
            LinearLayout systemKnightLevel = findViewById(R.id.systemKnightLevel);
            LinearLayout codeWardenLevel = findViewById(R.id.codeWardenLevel);
            LinearLayout techEmperorLevel = findViewById(R.id.techEmperorLevel);
            LinearLayout codeAbyssLevel = findViewById(R.id.codeAbyssLevel);
            LinearLayout[] levels = {dataAcolyteLevel, systemKnightLevel, codeWardenLevel, techEmperorLevel, codeAbyssLevel};
            
            ObjectAnimator.ofFloat(dropdownOverlay, "alpha", 1f, 0f).setDuration(250).start();
            dropdownOverlay.postDelayed(() -> dropdownOverlay.setVisibility(View.GONE), 250);
            dropdownLevelsContainer.setVisibility(View.INVISIBLE);
            for (LinearLayout level : levels) {
                if (level != null) level.setVisibility(View.INVISIBLE);
            }
            
            enableNavigationAfterDropdown();
            return;
        }
        
        super.onBackPressed();
    }


    
    private void updateDifficultyLevels(String language, QuizProgress quizProgress) {
        // Get references to all difficulty level containers
        LinearLayout dataAcolyteLevel = findViewById(R.id.dataAcolyteLevel);
        LinearLayout systemKnightLevel = findViewById(R.id.systemKnightLevel);
        LinearLayout codeWardenLevel = findViewById(R.id.codeWardenLevel);
        LinearLayout techEmperorLevel = findViewById(R.id.techEmperorLevel);
        
        // Data Acolyte - check if language is selected
        if (dataAcolyteLevel != null) {
            if (currentLanguage != null) {
                // Language is selected, Data Acolyte is unlocked
                dataAcolyteLevel.setAlpha(1.0f);
                
                // Get progress percentage from Firestore
                quizProgress.getProgressPercentage(language, "DataAcolyte", new QuizProgress.ProgressPercentageCallback() {
                    @Override
                    public void onSuccess(int progressPercentage) {
                        // Update progress circle
                        android.widget.ProgressBar progressCircle = dataAcolyteLevel.findViewById(R.id.progress_circle);
                        if (progressCircle != null) {
                            progressCircle.setVisibility(View.VISIBLE);
                            progressCircle.setProgress(progressPercentage);
                        }
                        
                        // Update percentage text
                        TextView percentageText = dataAcolyteLevel.findViewById(R.id.percentage_text);
                        if (percentageText != null) {
                            percentageText.setVisibility(View.VISIBLE);
                            percentageText.setText(progressPercentage + "%");
                        }
                        
                        // Hide lock icon
                        ImageView lockIcon = dataAcolyteLevel.findViewById(R.id.lock_icon);
                        if (lockIcon != null) {
                            lockIcon.setVisibility(View.GONE);
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                                            // Error occurred, set default progress
                    android.widget.ProgressBar progressCircle = dataAcolyteLevel.findViewById(R.id.progress_circle);
                    if (progressCircle != null) {
                        progressCircle.setVisibility(View.VISIBLE);
                        progressCircle.setProgress(0);
                    }
                    
                    // Update percentage text
                    TextView percentageText = dataAcolyteLevel.findViewById(R.id.percentage_text);
                    if (percentageText != null) {
                        percentageText.setVisibility(View.VISIBLE);
                        percentageText.setText("0%");
                    }
                        
                        // Hide lock icon
                        ImageView lockIcon = dataAcolyteLevel.findViewById(R.id.lock_icon);
                        if (lockIcon != null) {
                            lockIcon.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                // No language selected, Data Acolyte is locked
                dataAcolyteLevel.setAlpha(0.4f);
                
                // Hide progress circle and show lock icon
                android.widget.ProgressBar progressCircle = dataAcolyteLevel.findViewById(R.id.progress_circle);
                if (progressCircle != null) {
                    progressCircle.setVisibility(View.GONE);
                }
                
                // Show lock icon
                ImageView lockIcon = dataAcolyteLevel.findViewById(R.id.lock_icon);
                if (lockIcon != null) {
                    lockIcon.setVisibility(View.VISIBLE);
                }
            }
        }
        
        // System Knight is unlocked if all Data Acolyte questions are completed
        if (systemKnightLevel != null) {
            quizProgress.isDifficultyUnlocked(language, "SystemKnight", new QuizProgress.ProgressCallback() {
                @Override
                public void onSuccess() {
                    // System Knight is unlocked
                    systemKnightLevel.setAlpha(1.0f);
                    
                    // Show progress circle and percentage text
                    android.widget.ProgressBar progressCircle = systemKnightLevel.findViewById(R.id.progress_circle_system_knight);
                    TextView percentageText = systemKnightLevel.findViewById(R.id.percentage_text_system_knight);
                    if (progressCircle != null) {
                        progressCircle.setVisibility(View.VISIBLE);
                    }
                    if (percentageText != null) {
                        percentageText.setVisibility(View.VISIBLE);
                    }
                    
                    // Hide lock icon
                    ImageView lockIcon = systemKnightLevel.findViewById(R.id.lock_icon_system_knight);
                    if (lockIcon != null) {
                        lockIcon.setVisibility(View.GONE);
                    }
                    
                    // Get progress percentage for System Knight
                    quizProgress.getProgressPercentage(language, "SystemKnight", new QuizProgress.ProgressPercentageCallback() {
                        @Override
                        public void onSuccess(int progressPercentage) {
                            // Use existing XML structure
                            android.widget.ProgressBar progressCircle = systemKnightLevel.findViewById(R.id.progress_circle_system_knight);
                            if (progressCircle != null) {
                                progressCircle.setProgress(progressPercentage);
                            }
                            
                            // Update the percentage text
                            TextView percentageText = systemKnightLevel.findViewById(R.id.percentage_text_system_knight);
                            if (percentageText != null) {
                                percentageText.setText(progressPercentage + "%");
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            // Error getting progress, set to 0%
                            android.widget.ProgressBar progressCircle = systemKnightLevel.findViewById(R.id.progress_circle_system_knight);
                            if (progressCircle != null) {
                                progressCircle.setProgress(0);
                            }
                            
                            // Update the percentage text
                            TextView percentageText = systemKnightLevel.findViewById(R.id.percentage_text_system_knight);
                            if (percentageText != null) {
                                percentageText.setText("0%");
                            }
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    // System Knight is locked - make it dim and show lock icon
                    systemKnightLevel.setAlpha(0.4f);
                    
                    // Hide progress circle and percentage text
                    android.widget.ProgressBar progressCircle = systemKnightLevel.findViewById(R.id.progress_circle_system_knight);
                    TextView percentageText = systemKnightLevel.findViewById(R.id.percentage_text_system_knight);
                    if (progressCircle != null) {
                        progressCircle.setVisibility(View.GONE);
                    }
                    if (percentageText != null) {
                        percentageText.setVisibility(View.GONE);
                    }
                    
                    // Show lock icon
                    ImageView lockIcon = systemKnightLevel.findViewById(R.id.lock_icon_system_knight);
                    if (lockIcon != null) {
                        lockIcon.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
                

        
        // Code Warden is unlocked if all System Knight questions are completed
        if (codeWardenLevel != null) {
            quizProgress.isDifficultyUnlocked(language, "CodeWarden", new QuizProgress.ProgressCallback() {
                @Override
                public void onSuccess() {
                    // Code Warden is unlocked
                    codeWardenLevel.setAlpha(1.0f);
                    
                    // Show progress circle and percentage text
                    android.widget.ProgressBar progressCircle = codeWardenLevel.findViewById(R.id.progress_circle_code_warden);
                    TextView percentageText = codeWardenLevel.findViewById(R.id.percentage_text_code_warden);
                    if (progressCircle != null) {
                        progressCircle.setVisibility(View.VISIBLE);
                    }
                    if (percentageText != null) {
                        percentageText.setVisibility(View.VISIBLE);
                    }
                    
                    // Hide lock icon
                    ImageView lockIcon = codeWardenLevel.findViewById(R.id.lock_icon_code_warden);
                    if (lockIcon != null) {
                        lockIcon.setVisibility(View.GONE);
                    }
                    
                    // Get progress percentage for Code Warden
                    quizProgress.getProgressPercentage(language, "CodeWarden", new QuizProgress.ProgressPercentageCallback() {
                        @Override
                        public void onSuccess(int progressPercentage) {
                            // Use existing XML structure
                            android.widget.ProgressBar progressCircle = codeWardenLevel.findViewById(R.id.progress_circle_code_warden);
                            if (progressCircle != null) {
                                progressCircle.setProgress(progressPercentage);
                            }
                            
                            // Update the percentage text
                            TextView percentageText = codeWardenLevel.findViewById(R.id.percentage_text_code_warden);
                            if (percentageText != null) {
                                percentageText.setText(progressPercentage + "%");
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            // Error getting progress, set to 0%
                            android.widget.ProgressBar progressCircle = codeWardenLevel.findViewById(R.id.progress_circle_code_warden);
                            if (progressCircle != null) {
                                progressCircle.setProgress(0);
                            }
                            
                            // Update the percentage text
                            TextView percentageText = codeWardenLevel.findViewById(R.id.percentage_text_code_warden);
                            if (percentageText != null) {
                                percentageText.setText("0%");
                            }
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    // Code Warden is locked - make it dim and show lock icon
                    codeWardenLevel.setAlpha(0.4f);
                    
                    // Hide progress circle and percentage text
                    android.widget.ProgressBar progressCircle = codeWardenLevel.findViewById(R.id.progress_circle_code_warden);
                    TextView percentageText = codeWardenLevel.findViewById(R.id.percentage_text_code_warden);
                    if (progressCircle != null) {
                        progressCircle.setVisibility(View.GONE);
                    }
                    if (percentageText != null) {
                        percentageText.setVisibility(View.GONE);
                    }
                    
                    // Show lock icon
                    ImageView lockIcon = codeWardenLevel.findViewById(R.id.lock_icon_code_warden);
                    if (lockIcon != null) {
                        lockIcon.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        
        // Tech Emperor is unlocked if all Code Warden questions are completed
        if (techEmperorLevel != null) {
            quizProgress.isDifficultyUnlocked(language, "TechEmperor", new QuizProgress.ProgressCallback() {
                @Override
                public void onSuccess() {
                    // Tech Emperor is unlocked
                    techEmperorLevel.setAlpha(1.0f);
                    
                    // Show progress circle and percentage text
                    android.widget.ProgressBar progressCircle = techEmperorLevel.findViewById(R.id.progress_circle_tech_emperor);
                    TextView percentageText = techEmperorLevel.findViewById(R.id.percentage_text_tech_emperor);
                    if (progressCircle != null) {
                        progressCircle.setVisibility(View.VISIBLE);
                    }
                    if (percentageText != null) {
                        percentageText.setVisibility(View.VISIBLE);
                    }
                    
                    // Hide lock icon
                    ImageView lockIcon = techEmperorLevel.findViewById(R.id.lock_icon_tech_emperor);
                    if (lockIcon != null) {
                        lockIcon.setVisibility(View.GONE);
                    }
                    
                    // Get progress percentage for Tech Emperor
                    quizProgress.getProgressPercentage(language, "TechEmperor", new QuizProgress.ProgressPercentageCallback() {
                        @Override
                        public void onSuccess(int progressPercentage) {
                            // Use existing XML structure
                            android.widget.ProgressBar progressCircle = techEmperorLevel.findViewById(R.id.progress_circle_tech_emperor);
                            if (progressCircle != null) {
                                progressCircle.setProgress(progressPercentage);
                            }
                            
                            // Update the percentage text
                            TextView percentageText = techEmperorLevel.findViewById(R.id.percentage_text_tech_emperor);
                            if (percentageText != null) {
                                percentageText.setText(progressPercentage + "%");
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            // Error getting progress, set to 0%
                            android.widget.ProgressBar progressCircle = techEmperorLevel.findViewById(R.id.progress_circle_tech_emperor);
                            if (progressCircle != null) {
                                progressCircle.setProgress(0);
                            }
                            
                            // Update the percentage text
                            TextView percentageText = techEmperorLevel.findViewById(R.id.percentage_text_tech_emperor);
                            if (percentageText != null) {
                                percentageText.setText("0%");
                            }
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    // Tech Emperor is locked - make it dim and show lock icon
                    techEmperorLevel.setAlpha(0.4f);
                    
                    // Hide progress circle and percentage text
                    android.widget.ProgressBar progressCircle = techEmperorLevel.findViewById(R.id.progress_circle_tech_emperor);
                    TextView percentageText = techEmperorLevel.findViewById(R.id.percentage_text_tech_emperor);
                    if (progressCircle != null) {
                        progressCircle.setVisibility(View.GONE);
                    }
                    if (percentageText != null) {
                        percentageText.setVisibility(View.GONE);
                    }
                    
                    // Show lock icon
                    ImageView lockIcon = techEmperorLevel.findViewById(R.id.lock_icon_tech_emperor);
                    if (lockIcon != null) {
                        lockIcon.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        
        // Code Abyss is unlocked if all Tech Emperor questions are completed
        LinearLayout codeAbyssLevel = findViewById(R.id.codeAbyssLevel);
        if (codeAbyssLevel != null) {
            // For new users, immediately lock Code Abyss
            if (currentLanguage == null || currentLanguage.isEmpty()) {
                codeAbyssLevel.setAlpha(0.4f);
                android.widget.ProgressBar progressCircle = codeAbyssLevel.findViewById(R.id.progress_circle_code_abyss);
                if (progressCircle != null) {
                    progressCircle.setVisibility(View.GONE);
                }
                TextView percentageText = codeAbyssLevel.findViewById(R.id.percentage_text_code_abyss);
                if (percentageText != null) {
                    percentageText.setVisibility(View.GONE);
                }
                ImageView lockIcon = codeAbyssLevel.findViewById(R.id.lock_icon_code_abyss);
                if (lockIcon != null) {
                    lockIcon.setVisibility(View.VISIBLE);
                }
            } else {
                // Check if Tech Emperor is completed to determine Code Abyss unlock status
                quizProgress.areAllQuestionsCompleted(language, "TechEmperor", 10, new QuizProgress.ProgressCallback() {
                @Override
                public void onSuccess() {
                    // Tech Emperor is completed, Code Abyss is unlocked
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            codeAbyssLevel.setAlpha(1.0f);
                            
                            // Show progress circle and percentage text
                            android.widget.ProgressBar progressCircle = codeAbyssLevel.findViewById(R.id.progress_circle_code_abyss);
                            TextView percentageText = codeAbyssLevel.findViewById(R.id.percentage_text_code_abyss);
                            if (progressCircle != null) {
                                progressCircle.setVisibility(View.VISIBLE);
                            }
                            if (percentageText != null) {
                                percentageText.setVisibility(View.VISIBLE);
                            }
                            
                            // Hide lock icon
                            ImageView lockIcon = codeAbyssLevel.findViewById(R.id.lock_icon_code_abyss);
                            if (lockIcon != null) {
                                lockIcon.setVisibility(View.GONE);
                            }
                            
                            // Get progress percentage for Code Abyss
                            quizProgress.getProgressPercentage(language, "CodeAbyss", new QuizProgress.ProgressPercentageCallback() {
                                @Override
                                public void onSuccess(int progressPercentage) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            android.widget.ProgressBar progressCircle = codeAbyssLevel.findViewById(R.id.progress_circle_code_abyss);
                                            if (progressCircle != null) {
                                                progressCircle.setProgress(progressPercentage);
                                            }
                                            
                                            TextView percentageText = codeAbyssLevel.findViewById(R.id.percentage_text_code_abyss);
                                            if (percentageText != null) {
                                                percentageText.setText(progressPercentage + "%");
                                            }
                                        }
                                    });
                                }
                                
                                @Override
                                public void onError(String error) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            android.widget.ProgressBar progressCircle = codeAbyssLevel.findViewById(R.id.progress_circle_code_abyss);
                                            if (progressCircle != null) {
                                                progressCircle.setProgress(0);
                                            }
                                            
                                            TextView percentageText = codeAbyssLevel.findViewById(R.id.percentage_text_code_abyss);
                                            if (percentageText != null) {
                                                percentageText.setText("0%");
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    // Tech Emperor is not completed, Code Abyss is locked
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            codeAbyssLevel.setAlpha(0.4f);
                            
                            // Hide progress circle and percentage text
                            android.widget.ProgressBar progressCircle = codeAbyssLevel.findViewById(R.id.progress_circle_code_abyss);
                            TextView percentageText = codeAbyssLevel.findViewById(R.id.percentage_text_code_abyss);
                            if (progressCircle != null) {
                                progressCircle.setVisibility(View.GONE);
                            }
                            if (percentageText != null) {
                                percentageText.setVisibility(View.GONE);
                            }
                            
                            // Show lock icon
                            ImageView lockIcon = codeAbyssLevel.findViewById(R.id.lock_icon_code_abyss);
                            if (lockIcon != null) {
                                lockIcon.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });
        }
        }
        
        // Refresh difficulty level click listeners and level buttons after updating difficulty levels
        setupDifficultyLevelClickListeners(language);
        refreshLevelButtons();
    }
    
    private void setupLevelButtons(String language) {
        QuizProgress quizProgress = new QuizProgress(this);
        
        Log.d("HomeScreen", "Setting up Data Acolyte level buttons for " + language);
        
        // Check if language is selected
        if (language != null && !language.isEmpty()) {
            Log.d("HomeScreen", "Language is selected: " + language + ", setting up levels with level 1 unlocked");
            
            // Setup all 10 level buttons for Data Acolyte
            for (int i = 1; i <= 10; i++) {
                int levelId = getResources().getIdentifier("level" + i, "id", getPackageName());
                ImageView levelView = findViewById(levelId);
                
                if (levelView != null) {
                    final int levelNumber = i;
                    
                    if (levelNumber == 1) {
                        // Level 1 is unlocked for users who have selected a language
                        Log.d("HomeScreen", "Setting level 1 as unlocked for language: " + language);
                        levelView.setImageResource(R.drawable.level1_unlocked);
                        levelView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkLivesBeforeQuizWithDetails(language, "DataAcolyte", levelNumber);
                            }
                        });
                    } else {
                        // For levels 2-10, check if the previous level is completed (progressive unlocking)
                        Log.d("HomeScreen", "Checking if level " + (levelNumber - 1) + " is completed to unlock level " + levelNumber);
                        quizProgress.isQuestionCompleted(language, "DataAcolyte", levelNumber - 1, new QuizProgress.ProgressCallback() {
                            @Override
                            public void onSuccess() {
                                // Previous level is completed, so this level is unlocked
                                Log.d("HomeScreen", "Level " + (levelNumber - 1) + " is completed, unlocking level " + levelNumber);
                                
                                // Use specific unlocked images for levels 2-10
                                int unlockedImageResource = getUnlockedLevelImageResource(levelNumber);
                                levelView.setImageResource(unlockedImageResource);
                                levelView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        checkLivesBeforeQuizWithDetails(language, "DataAcolyte", levelNumber);
                                    }
                                });
                            }
                            
                            @Override
                            public void onError(String error) {
                                // Previous level is not completed, so this level is locked
                                Log.d("HomeScreen", "Level " + (levelNumber - 1) + " is not completed, keeping level " + levelNumber + " locked");
                                levelView.setImageResource(R.drawable.image_level_locked);
                                levelView.setOnClickListener(null);
                            }
                        });
                    }
                }
            }
        } else {
            Log.d("HomeScreen", "No language selected, setting all levels as locked");
            
            // For new users: All levels are locked initially
            for (int i = 1; i <= 10; i++) {
                int levelId = getResources().getIdentifier("level" + i, "id", getPackageName());
                ImageView levelView = findViewById(levelId);
                
                if (levelView != null) {
                    final int levelNumber = i;
                    Log.d("HomeScreen", "Setting level " + levelNumber + " as locked for new user");
                    levelView.setImageResource(R.drawable.image_level_locked);
                    levelView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(HomeScreen.this, "Please select a language first to unlock levels", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
    
    private void unlockLevel1ForLanguage(String language) {
        Log.d("HomeScreen", "Automatically unlocking level 1 for language: " + language);
        
        // Get level 1 view
        ImageView level1View = findViewById(R.id.level1);
        
        if (level1View != null) {
            // Unlock level 1 immediately
            level1View.setImageResource(R.drawable.level1_unlocked);
            level1View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLivesBeforeQuizWithDetails(language, "DataAcolyte", 1);
                }
            });
            
            Log.d("HomeScreen", "Level 1 unlocked for " + language);
        }
        
        // Check completion status for levels 2-10 and unlock them progressively
        setupProgressiveLevelUnlocking(language);
    }
    
    private void setupProgressiveLevelUnlocking(String language) {
        QuizProgress quizProgress = new QuizProgress(this);
        
        Log.d("HomeScreen", "Setting up progressive level unlocking for " + language + " - Data Acolyte");
        
        // Check each level from 2 to 10 to see if it should be unlocked
        for (int i = 2; i <= 10; i++) {
            final int levelNumber = i;
            int levelId = getResources().getIdentifier("level" + i, "id", getPackageName());
            ImageView levelView = findViewById(levelId);
            
            if (levelView != null) {
                Log.d("HomeScreen", "Checking completion status for level " + levelNumber + " by checking if level " + (levelNumber - 1) + " is completed");
                
                // Check if the previous level is completed
                quizProgress.isQuestionCompleted(language, "DataAcolyte", levelNumber - 1, new QuizProgress.ProgressCallback() {
                    @Override
                    public void onSuccess() {
                        // Previous level is completed, so this level is unlocked
                        Log.d("HomeScreen", "✓ Level " + (levelNumber - 1) + " is completed, unlocking level " + levelNumber);
                        
                        // Use specific unlocked images for levels 2-10
                        int unlockedImageResource = getUnlockedLevelImageResource(levelNumber);
                        levelView.setImageResource(unlockedImageResource);
                        levelView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkLivesBeforeQuizWithDetails(language, "DataAcolyte", levelNumber);
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        // Previous level is not completed, so this level is locked
                        Log.d("HomeScreen", "✗ Level " + (levelNumber - 1) + " is not completed, keeping level " + levelNumber + " locked");
                        levelView.setImageResource(R.drawable.image_level_locked);
                        levelView.setOnClickListener(null);
                    }
                });
            } else {
                Log.w("HomeScreen", "Level view not found for level " + levelNumber);
            }
        }
    }
    
    private void checkAndUnlockLevel1IfLanguageSelected() {
        Log.d("HomeScreen", "Checking if language was selected and updating UI accordingly");
        
        // Load current language from cache
        userDataManager.getLanguage(new UserDataManager.LanguageCallback() {
            @Override
            public void onLanguageLoaded(String language) {
                if (language != null && !language.isEmpty()) {
                    Log.d("HomeScreen", "Language found in cache: " + language);
                    
                    // Update current language
                    currentLanguage = language;
                    
                    // Update language title
                    TextView languageTitle = findViewById(R.id.languageTitle);
                    if (languageTitle != null) {
                        languageTitle.setText(language);
                        Log.d("HomeScreen", "Language title updated to: " + language);
                    }
                    
                    // Update subtitle to show current difficulty (default to Data Acolyte)
                    currentDifficulty = "DATA ACOLYTE";
                    updateSubtitle("DATA ACOLYTE");
                    
                    // Update difficulty levels to show Data Acolyte as unlocked
                    updateDifficultyLevelsForLanguageSelection(language);
                    
                    // Unlock level 1 and setup level buttons
                    unlockLevel1ForLanguage(language);
                    
                    // Refresh the display to ensure level 1 is visible
                    refreshLevelDisplay();
                    
                } else {
                    Log.d("HomeScreen", "No language found in cache");
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e("HomeScreen", "Error loading language: " + error);
            }
        });
    }
    
    private void updateSubtitle(String difficulty) {
        TextView subTitle = findViewById(R.id.subTitle);
        if (subTitle != null) {
            subTitle.setText(difficulty);
        }
    }
    
    private void updateDifficultyLevelsForLanguageSelection(String language) {
        Log.d("HomeScreen", "Updating difficulty levels for language selection: " + language);
        
        // Get references to all difficulty level containers
        LinearLayout dataAcolyteLevel = findViewById(R.id.dataAcolyteLevel);
        LinearLayout systemKnightLevel = findViewById(R.id.systemKnightLevel);
        LinearLayout codeWardenLevel = findViewById(R.id.codeWardenLevel);
        LinearLayout techEmperorLevel = findViewById(R.id.techEmperorLevel);
        
        // Data Acolyte - unlock it since language is selected
        if (dataAcolyteLevel != null) {
            dataAcolyteLevel.setAlpha(1.0f);
            
            // Show progress circle with 0% progress (new user)
            android.widget.ProgressBar progressCircle = dataAcolyteLevel.findViewById(R.id.progress_circle);
            if (progressCircle != null) {
                progressCircle.setVisibility(View.VISIBLE);
                progressCircle.setProgress(0);
            }
            
            // Show percentage text with 0%
            TextView percentageText = dataAcolyteLevel.findViewById(R.id.percentage_text);
            if (percentageText != null) {
                percentageText.setVisibility(View.VISIBLE);
                percentageText.setText("0%");
            }
            
            // Hide lock icon
            ImageView lockIcon = dataAcolyteLevel.findViewById(R.id.lock_icon);
            if (lockIcon != null) {
                lockIcon.setVisibility(View.GONE);
            }
            
            // Set click listener for Data Acolyte
            dataAcolyteLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentDifficulty = "DATA ACOLYTE";
                    updateSubtitle("DATA ACOLYTE");
                    saveCurrentDifficultyToFirestore("DATA ACOLYTE");
                    setupLevelButtons(language);
                }
            });
        }
        
        // Keep other difficulties locked for new users
        if (systemKnightLevel != null) {
            systemKnightLevel.setAlpha(0.5f);
            android.widget.ProgressBar progressCircle = systemKnightLevel.findViewById(R.id.progress_circle);
            if (progressCircle != null) {
                progressCircle.setVisibility(View.GONE);
            }
            TextView percentageText = systemKnightLevel.findViewById(R.id.percentage_text);
            if (percentageText != null) {
                percentageText.setVisibility(View.GONE);
            }
            ImageView lockIcon = systemKnightLevel.findViewById(R.id.lock_icon);
            if (lockIcon != null) {
                lockIcon.setVisibility(View.VISIBLE);
            }
        }
        
        if (codeWardenLevel != null) {
            codeWardenLevel.setAlpha(0.5f);
            android.widget.ProgressBar progressCircle = codeWardenLevel.findViewById(R.id.progress_circle);
            if (progressCircle != null) {
                progressCircle.setVisibility(View.GONE);
            }
            TextView percentageText = codeWardenLevel.findViewById(R.id.percentage_text);
            if (percentageText != null) {
                percentageText.setVisibility(View.GONE);
            }
            ImageView lockIcon = codeWardenLevel.findViewById(R.id.lock_icon);
            if (lockIcon != null) {
                lockIcon.setVisibility(View.VISIBLE);
            }
        }
        
        if (techEmperorLevel != null) {
            techEmperorLevel.setAlpha(0.5f);
            android.widget.ProgressBar progressCircle = techEmperorLevel.findViewById(R.id.progress_circle);
            if (progressCircle != null) {
                progressCircle.setVisibility(View.GONE);
            }
            TextView percentageText = techEmperorLevel.findViewById(R.id.percentage_text);
            if (percentageText != null) {
                percentageText.setVisibility(View.GONE);
            }
            ImageView lockIcon = techEmperorLevel.findViewById(R.id.lock_icon);
            if (lockIcon != null) {
                lockIcon.setVisibility(View.VISIBLE);
            }
        }
        
        LinearLayout codeAbyssLevel = findViewById(R.id.codeAbyssLevel);
        if (codeAbyssLevel != null) {
            codeAbyssLevel.setAlpha(0.4f);
            android.widget.ProgressBar progressCircle = codeAbyssLevel.findViewById(R.id.progress_circle_code_abyss);
            if (progressCircle != null) {
                progressCircle.setVisibility(View.GONE);
            }
            TextView percentageText = codeAbyssLevel.findViewById(R.id.percentage_text_code_abyss);
            if (percentageText != null) {
                percentageText.setVisibility(View.GONE);
            }
            ImageView lockIcon = codeAbyssLevel.findViewById(R.id.lock_icon_code_abyss);
            if (lockIcon != null) {
                lockIcon.setVisibility(View.VISIBLE);
            }
        }
    }
    
    private void refreshLevelDisplay() {
        Log.d("HomeScreen", "Refreshing level display to ensure visibility");
        
        // Force a layout refresh to ensure level 1 is visible
        View levelScroll = findViewById(R.id.levelScroll);
        if (levelScroll != null) {
            levelScroll.requestLayout();
            levelScroll.invalidate();
        }
        
        // Ensure level 1 is visible by scrolling to it
        levelScroll.post(new Runnable() {
            @Override
            public void run() {
                ScrollView scrollView = findViewById(R.id.levelScroll);
                if (scrollView != null) {
                    // Scroll to show level 1
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });
    }
    
    private void setupLevelButtonsAfterLanguageSelection(String language) {
        Log.d("HomeScreen", "Setting up Data Acolyte level buttons after language selection for " + language);
        
        // Get level 1 view and unlock it
        ImageView level1View = findViewById(R.id.level1);
        if (level1View != null) {
            level1View.setImageResource(R.drawable.level1_unlocked);
            level1View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLivesBeforeQuizWithDetails(language, "DataAcolyte", 1);
                }
            });
            Log.d("HomeScreen", "Data Acolyte Level 1 is unlocked after language selection");
        }
        
        // Setup progressive unlocking for levels 2-10
        setupProgressiveLevelUnlocking(language);
    }
    
    private void setupSystemKnightLevels(String language) {
        QuizProgress quizProgress = new QuizProgress(this);
        
        Log.d("HomeScreen", "Checking if System Knight is unlocked for " + language);
        
        // First check if System Knight difficulty is unlocked
        quizProgress.isDifficultyUnlocked(language, "SystemKnight", new QuizProgress.ProgressCallback() {
            @Override
            public void onSuccess() {
                // System Knight is unlocked, now setup individual levels
                Log.d("HomeScreen", "System Knight is UNLOCKED for " + language + ", setting up level buttons");
                setupLevelButtonsForDifficulty(language, "SystemKnight");
            }
            
            @Override
            public void onError(String error) {
                // System Knight is locked, all levels should be locked
                Log.d("HomeScreen", "System Knight is LOCKED for " + language + ": " + error);
                lockAllLevels();
            }
        });
    }
    
    private void setupCodeWardenLevels(String language) {
        QuizProgress quizProgress = new QuizProgress(this);
        
        Log.d("HomeScreen", "Checking if Code Warden is unlocked for " + language);
        
        // First check if Code Warden difficulty is unlocked
        quizProgress.isDifficultyUnlocked(language, "CodeWarden", new QuizProgress.ProgressCallback() {
            @Override
            public void onSuccess() {
                // Code Warden is unlocked, now setup individual levels
                Log.d("HomeScreen", "Code Warden is UNLOCKED for " + language + ", setting up level buttons");
                setupLevelButtonsForDifficulty(language, "CodeWarden");
            }
            
            @Override
            public void onError(String error) {
                // Code Warden is locked, all levels should be locked
                Log.d("HomeScreen", "Code Warden is LOCKED for " + language + ": " + error);
                lockAllLevels();
            }
        });
    }
    
    private void setupTechEmperorLevels(String language) {
        QuizProgress quizProgress = new QuizProgress(this);
        
        Log.d("HomeScreen", "Checking if Tech Emperor is unlocked for " + language);
        
        // First check if Tech Emperor difficulty is unlocked
        quizProgress.isDifficultyUnlocked(language, "TechEmperor", new QuizProgress.ProgressCallback() {
            @Override
            public void onSuccess() {
                // Tech Emperor is unlocked, now setup individual levels
                Log.d("HomeScreen", "Tech Emperor is UNLOCKED for " + language + ", setting up level buttons");
                setupLevelButtonsForDifficulty(language, "TechEmperor");
            }
            
            @Override
            public void onError(String error) {
                // Tech Emperor is locked, all levels should be locked
                Log.d("HomeScreen", "Tech Emperor is LOCKED for " + language + ": " + error);
                lockAllLevels();
            }
        });
    }
    
    private void setupCodeAbyssLevels(String language) {
        QuizProgress quizProgress = new QuizProgress(this);
        
        Log.d("HomeScreen", "Checking if Code Abyss is unlocked for " + language);
        
        // First check if Code Abyss difficulty is unlocked
        quizProgress.isDifficultyUnlocked(language, "CodeAbyss", new QuizProgress.ProgressCallback() {
            @Override
            public void onSuccess() {
                // Code Abyss is unlocked, now setup individual levels
                Log.d("HomeScreen", "Code Abyss is UNLOCKED for " + language + ", setting up level buttons");
                setupLevelButtonsForDifficulty(language, "CodeAbyss");
            }
            
            @Override
            public void onError(String error) {
                // Code Abyss is locked, all levels should be locked
                Log.d("HomeScreen", "Code Abyss is LOCKED for " + language + ": " + error);
                lockAllLevels();
            }
        });
    }
    
    /**
     * Generic method to setup level buttons for any difficulty with progressive unlocking
     */
    private void setupLevelButtonsForDifficulty(String language, String difficulty) {
        QuizProgress quizProgress = new QuizProgress(this);
        
        Log.d("HomeScreen", "Setting up level buttons for " + difficulty + " in " + language);
        
        // Setup all 10 level buttons for the specified difficulty
        for (int i = 1; i <= 10; i++) {
            int levelId = getResources().getIdentifier("level" + i, "id", getPackageName());
            ImageView levelView = findViewById(levelId);
            
            if (levelView != null) {
                final int levelNumber = i;
                
                if (levelNumber == 1) {
                    // First level is unlocked if difficulty is unlocked
                    Log.d("HomeScreen", "Level 1 is unlocked for " + difficulty);
                    levelView.setImageResource(R.drawable.level1_unlocked);
                    levelView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkLivesBeforeQuizWithDetails(language, difficulty, levelNumber);
                        }
                    });
                } else {
                    // Check if the previous level is completed (progressive unlocking)
                    Log.d("HomeScreen", "Checking if level " + (levelNumber - 1) + " is completed to unlock level " + levelNumber);
                    quizProgress.isQuestionCompleted(language, difficulty, levelNumber - 1, new QuizProgress.ProgressCallback() {
                        @Override
                        public void onSuccess() {
                            // Previous level is completed, this level is unlocked
                            Log.d("HomeScreen", "Level " + (levelNumber - 1) + " is completed, unlocking level " + levelNumber);
                            
                            // Use specific unlocked images for levels 2-10
                            int unlockedImageResource = getUnlockedLevelImageResource(levelNumber);
                            levelView.setImageResource(unlockedImageResource);
                            
                            levelView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    checkLivesBeforeQuizWithDetails(language, difficulty, levelNumber);
                                }
                            });
                        }
                        
                        @Override
                        public void onError(String error) {
                            // Previous level not completed, keep locked
                            Log.d("HomeScreen", "Level " + (levelNumber - 1) + " is not completed, keeping level " + levelNumber + " locked");
                            levelView.setImageResource(R.drawable.image_level_locked);
                            levelView.setOnClickListener(null);
                        }
                    });
                }
            }
        }
    }
    
    /**
     * Get the unlocked image resource for a specific level
     */
    private int getUnlockedLevelImageResource(int levelNumber) {
        switch (levelNumber) {
            case 1:
                return R.drawable.level1_unlocked;
            case 2:
                return R.drawable.level2_unlocked;
            case 3:
                return R.drawable.level3_unlocked;
            case 4:
                return R.drawable.level4_unlocked;
            case 5:
                return R.drawable.level5_unlocked;
            case 6:
                return R.drawable.level6_unlocked;
            case 7:
                return R.drawable.level7_unlocked;
            case 8:
                return R.drawable.level8_unlocked;
            case 9:
                return R.drawable.level9_unlocked;
            case 10:
                return R.drawable.level10_unlocked;
            default:
                return R.drawable.image_level; // Default fallback
        }
    }
    
    /**
     * Lock all level buttons (used when difficulty is locked)
     */
    private void lockAllLevels() {
        for (int i = 1; i <= 10; i++) {
            int levelId = getResources().getIdentifier("level" + i, "id", getPackageName());
            ImageView levelView = findViewById(levelId);
            
            if (levelView != null) {
                levelView.setImageResource(R.drawable.image_level_locked);
                levelView.setOnClickListener(null);
            }
        }
    }
    
    /**
     * Refresh level buttons based on current difficulty and progress
     */
    private void refreshLevelButtons() {
        if (currentLanguage == null) {
            Log.d("HomeScreen", "refreshLevelButtons: currentLanguage is null, skipping refresh");
            return;
        }
        
        Log.d("HomeScreen", "Refreshing level buttons for language: " + currentLanguage + ", difficulty: " + currentDifficulty);
        
        // Use the stored current difficulty instead of reading from subtitle
        switch (currentDifficulty) {
            case "DATA ACOLYTE":
                Log.d("HomeScreen", "Setting up Data Acolyte level buttons");
                setupLevelButtons(currentLanguage);
                break;
            case "SYSTEM KNIGHT":
                Log.d("HomeScreen", "Setting up System Knight level buttons");
                setupSystemKnightLevels(currentLanguage);
                break;
            case "CODE WARDEN":
                Log.d("HomeScreen", "Setting up Code Warden level buttons");
                setupCodeWardenLevels(currentLanguage);
                break;
            case "TECH EMPEROR":
                Log.d("HomeScreen", "Setting up Tech Emperor level buttons");
                setupTechEmperorLevels(currentLanguage);
                break;
            default:
                // Default to Data Acolyte
                Log.d("HomeScreen", "Unknown difficulty, defaulting to Data Acolyte");
                setupLevelButtons(currentLanguage);
                break;
        }
    }
    
    /**
     * Handle quiz completion and update level progression
     */
    private void handleQuizCompletion(String language, String difficulty, int level) {
        Log.d("HomeScreen", "Handling quiz completion for " + language + " " + difficulty + " level " + level);
        
        // Update the question completion status
        QuizProgress quizProgress = new QuizProgress(this);
        quizProgress.setQuestionCompleted(language, difficulty, level, true, new QuizProgress.ProgressCallback() {
            @Override
            public void onSuccess() {
                Log.d("HomeScreen", "Successfully marked question " + level + " as completed");
                
                // Check if this completion unlocks the next difficulty
                checkAndUnlockNextDifficulty(language, difficulty);
                
                // Refresh the level buttons to show updated progress
                refreshLevelButtons();
            }
            
            @Override
            public void onError(String error) {
                Log.e("HomeScreen", "Error marking question as completed: " + error);
            }
        });
    }
    
    /**
     * Debug method to check user's current progress
     */
    private void debugUserProgress(String language) {
        Log.d("HomeScreen", "=== DEBUG: Checking user progress for " + language + " ===");
        
        QuizProgress quizProgress = new QuizProgress(this);
        
        // Check Data Acolyte progress
        quizProgress.areAllQuestionsCompleted(language, "DataAcolyte", 10, new QuizProgress.ProgressCallback() {
            @Override
            public void onSuccess() {
                Log.d("HomeScreen", "✓ Data Acolyte is 100% completed");
                
                // Check if System Knight should be unlocked
                quizProgress.isDifficultyUnlocked(language, "SystemKnight", new QuizProgress.ProgressCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("HomeScreen", "✓ System Knight should be UNLOCKED");
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.d("HomeScreen", "✗ System Knight should be LOCKED: " + error);
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                Log.d("HomeScreen", "✗ Data Acolyte is NOT 100% completed: " + error);
            }
        });
        
        // Print all user progress data

    }
    

    
    /**
     * Reset difficulty levels to initial state (for language switching)
     */
    private void resetDifficultyLevelsToInitialState() {
        
        // Get references to all difficulty level containers
        LinearLayout dataAcolyteLevel = findViewById(R.id.dataAcolyteLevel);
        LinearLayout systemKnightLevel = findViewById(R.id.systemKnightLevel);
        LinearLayout codeWardenLevel = findViewById(R.id.codeWardenLevel);
        LinearLayout techEmperorLevel = findViewById(R.id.techEmperorLevel);
        
        // Data Acolyte - locked for new users, show lock icon
        if (dataAcolyteLevel != null) {
            dataAcolyteLevel.setAlpha(0.4f);
            android.widget.ProgressBar progressCircle = dataAcolyteLevel.findViewById(R.id.progress_circle);
            if (progressCircle != null) {
                progressCircle.setVisibility(View.GONE);
            }
            // Hide percentage text
            TextView percentageText = dataAcolyteLevel.findViewById(R.id.percentage_text);
            if (percentageText != null) {
                percentageText.setVisibility(View.GONE);
            }
            // Show lock icon
            ImageView lockIcon = dataAcolyteLevel.findViewById(R.id.lock_icon);
            if (lockIcon != null) {
                lockIcon.setVisibility(View.VISIBLE);
            }
        }
        
        // System Knight - initially locked, show lock icon
        if (systemKnightLevel != null) {
            systemKnightLevel.setAlpha(0.4f);
            android.widget.ProgressBar progressCircle = systemKnightLevel.findViewById(R.id.progress_circle_system_knight);
            TextView percentageText = systemKnightLevel.findViewById(R.id.percentage_text_system_knight);
            if (progressCircle != null) {
                progressCircle.setVisibility(View.GONE);
            }
            if (percentageText != null) {
                percentageText.setVisibility(View.GONE);
            }
            // Show lock icon
            ImageView lockIcon = systemKnightLevel.findViewById(R.id.lock_icon_system_knight);
            if (lockIcon != null) {
                lockIcon.setVisibility(View.VISIBLE);
            }
        }
        
        // Code Warden - initially locked, show lock icon
        if (codeWardenLevel != null) {
            codeWardenLevel.setAlpha(0.4f);
            android.widget.ProgressBar progressCircle = codeWardenLevel.findViewById(R.id.progress_circle_code_warden);
            TextView percentageText = codeWardenLevel.findViewById(R.id.percentage_text_code_warden);
            if (progressCircle != null) {
                progressCircle.setVisibility(View.GONE);
            }
            if (percentageText != null) {
                percentageText.setVisibility(View.GONE);
            }
            // Show lock icon
            ImageView lockIcon = codeWardenLevel.findViewById(R.id.lock_icon_code_warden);
            if (lockIcon != null) {
                lockIcon.setVisibility(View.VISIBLE);
            }
        }
        
        // Tech Emperor - initially locked, show lock icon
        if (techEmperorLevel != null) {
            techEmperorLevel.setAlpha(0.4f);
            android.widget.ProgressBar progressCircle = techEmperorLevel.findViewById(R.id.progress_circle_tech_emperor);
            TextView percentageText = techEmperorLevel.findViewById(R.id.percentage_text_tech_emperor);
            if (progressCircle != null) {
                progressCircle.setVisibility(View.GONE);
            }
            if (percentageText != null) {
                percentageText.setVisibility(View.GONE);
            }
            // Show lock icon
            ImageView lockIcon = techEmperorLevel.findViewById(R.id.lock_icon_tech_emperor);
            if (lockIcon != null) {
                lockIcon.setVisibility(View.VISIBLE);
            }
        }
    }
    
    /**
     * Check if completing the current difficulty unlocks the next difficulty
     */
    private void checkAndUnlockNextDifficulty(String language, String difficulty) {
        QuizProgress quizProgress = new QuizProgress(this);
        
        // Check if all questions in the current difficulty are completed
        quizProgress.areAllQuestionsCompleted(language, difficulty, 10, new QuizProgress.ProgressCallback() {
            @Override
            public void onSuccess() {
                Log.d("HomeScreen", "All questions completed for " + difficulty + ". Next difficulty should be unlocked.");
                
                // Update difficulty levels to reflect the new unlock status
                updateDifficultyLevels(language, quizProgress);
                
                // Show unlock notification
                String nextDifficulty = getNextDifficulty(difficulty);
                if (nextDifficulty != null) {
                    Toast.makeText(HomeScreen.this, "Congratulations! " + nextDifficulty + " is now unlocked!", Toast.LENGTH_LONG).show();
                }
                
                // Unlock other languages when any difficulty level is completed
                // This ensures users must progress in their chosen language before exploring others
                Toast.makeText(HomeScreen.this, "Other programming languages are now unlocked!", Toast.LENGTH_LONG).show();
                unlockOtherLanguages(language);
            }
            
            @Override
            public void onError(String error) {
                Log.d("HomeScreen", "Not all questions completed for " + difficulty + " yet.");
            }
        });
    }
    
    /**
     * Unlock other programming languages when any difficulty level is completed
     */
    private void unlockOtherLanguages(String completedLanguage) {
        Log.d("HomeScreen", "Unlocking other languages after completing a difficulty level in " + completedLanguage);
        
        // Update the UserDataManager to reflect that other languages are now unlocked
        // This will be used by the LanguageSelection screen to show unlocked languages
        if ("JAVA".equals(completedLanguage)) {
            // Java difficulty is completed, unlock C#
            userDataManager.updateLanguageUnlockStatus("C#", true);
            Log.d("HomeScreen", "C# is now unlocked");
        } else if ("C#".equals(completedLanguage)) {
            // C# difficulty is completed, unlock Java
            userDataManager.updateLanguageUnlockStatus("JAVA", true);
            Log.d("HomeScreen", "Java is now unlocked");
        }
    }
    
    /**
     * Get the next difficulty level
     */
    private String getNextDifficulty(String currentDifficulty) {
        switch (currentDifficulty) {
            case "DataAcolyte":
                return "System Knight";
            case "SystemKnight":
                return "Code Warden";
            case "CodeWarden":
                return "Tech Emperor";
            case "TechEmperor":
                return "Code Abyss";
            default:
                return null;
        }
    }
    
    /**
     * Setup click listeners for all difficulty levels
     */
    private void setupDifficultyLevelClickListenersForNewUser() {
        Log.d("HomeScreen", "Setting up difficulty level click listeners for new user");
        
        // Get references to difficulty level containers
        final LinearLayout dataAcolyteLevel = findViewById(R.id.dataAcolyteLevel);
        final LinearLayout systemKnightLevel = findViewById(R.id.systemKnightLevel);
        final LinearLayout codeWardenLevel = findViewById(R.id.codeWardenLevel);
        final LinearLayout techEmperorLevel = findViewById(R.id.techEmperorLevel);
        
        // All difficulty levels are locked for new users
        if (dataAcolyteLevel != null) {
            dataAcolyteLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(HomeScreen.this, "Please select a language first to unlock difficulty levels", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        if (systemKnightLevel != null) {
            systemKnightLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(HomeScreen.this, "Please select a language first to unlock difficulty levels", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        if (codeWardenLevel != null) {
            codeWardenLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(HomeScreen.this, "Please select a language first to unlock difficulty levels", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        if (techEmperorLevel != null) {
            techEmperorLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(HomeScreen.this, "Please select a language first to unlock difficulty levels", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        LinearLayout codeAbyssLevel = findViewById(R.id.codeAbyssLevel);
        if (codeAbyssLevel != null) {
            codeAbyssLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(HomeScreen.this, "Please select a language first to unlock difficulty levels", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void setupDifficultyLevelClickListeners(String language) {
        Log.d("HomeScreen", "Setting up difficulty level click listeners for " + language);
        
        // Get references to difficulty level containers
        final LinearLayout dataAcolyteLevel = findViewById(R.id.dataAcolyteLevel);
        final LinearLayout systemKnightLevel = findViewById(R.id.systemKnightLevel);
        final LinearLayout codeWardenLevel = findViewById(R.id.codeWardenLevel);
        final LinearLayout techEmperorLevel = findViewById(R.id.techEmperorLevel);
        final LinearLayout codeAbyssLevel = findViewById(R.id.codeAbyssLevel);
        final TextView subTitle = findViewById(R.id.subTitle);
        
        // Data Acolyte - check if language is selected
        if (dataAcolyteLevel != null) {
            if (currentLanguage != null) {
                // Language is selected, Data Acolyte is unlocked
                dataAcolyteLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("HomeScreen", "Data Acolyte clicked");
                        // Update current difficulty
                        currentDifficulty = "DATA ACOLYTE";
                        
                        // Update subtitle to show selected difficulty
                        if (subTitle != null) {
                            subTitle.setText("DATA ACOLYTE");
                        }
                        
                        // Save current difficulty to Firestore
                        saveCurrentDifficultyToFirestore("DATA ACOLYTE");
                        
                        // Update level buttons for Data Acolyte
                        setupLevelButtonsAfterLanguageSelection(language);
                        
                        // Hide dropdown
                        hideDifficultyDropdown();
                    }
                });
            } else {
                // No language selected, show message
                dataAcolyteLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(HomeScreen.this, "Please select a language first to unlock difficulty levels", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        
        // System Knight - check if unlocked
        if (systemKnightLevel != null) {
            QuizProgress progress = new QuizProgress(HomeScreen.this);
            progress.isDifficultyUnlocked(language, "SystemKnight", new QuizProgress.ProgressCallback() {
                @Override
                public void onSuccess() {
                    // System Knight is unlocked
                    Log.d("HomeScreen", "System Knight is UNLOCKED for " + language);
                    systemKnightLevel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("HomeScreen", "System Knight clicked");
                            // Update current difficulty
                            currentDifficulty = "SYSTEM KNIGHT";
                            
                            // Update subtitle to show selected difficulty
                            if (subTitle != null) {
                                subTitle.setText("SYSTEM KNIGHT");
                            }
                            
                            // Save current difficulty to Firestore
                            saveCurrentDifficultyToFirestore("SYSTEM KNIGHT");
                            
                            // Update level buttons for System Knight
                            setupSystemKnightLevels(language);
                            
                            // Hide dropdown
                            hideDifficultyDropdown();
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    // System Knight is locked
                    Log.d("HomeScreen", "System Knight is LOCKED for " + language + ". Error: " + error);
                    systemKnightLevel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(HomeScreen.this, "Complete all Data Acolyte levels to unlock System Knight", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
        
        // Code Warden - check if unlocked
        if (codeWardenLevel != null) {
            QuizProgress progress = new QuizProgress(HomeScreen.this);
            progress.isDifficultyUnlocked(language, "CodeWarden", new QuizProgress.ProgressCallback() {
                @Override
                public void onSuccess() {
                    // Code Warden is unlocked
                    Log.d("HomeScreen", "Code Warden is UNLOCKED for " + language);
                    codeWardenLevel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("HomeScreen", "Code Warden clicked");
                            // Update current difficulty
                            currentDifficulty = "CODE WARDEN";
                            
                            // Update subtitle to show selected difficulty
                            if (subTitle != null) {
                                subTitle.setText("CODE WARDEN");
                            }
                            
                            // Save current difficulty to Firestore
                            saveCurrentDifficultyToFirestore("CODE WARDEN");
                            
                            // Update level buttons for Code Warden
                            setupCodeWardenLevels(language);
                            
                            // Hide dropdown
                            hideDifficultyDropdown();
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    // Code Warden is locked
                    Log.d("HomeScreen", "Code Warden is LOCKED for " + language + ". Error: " + error);
                    codeWardenLevel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(HomeScreen.this, "Complete all System Knight levels to unlock Code Warden", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
        
        // Tech Emperor - check if unlocked
        if (techEmperorLevel != null) {
            QuizProgress progress = new QuizProgress(HomeScreen.this);
            progress.isDifficultyUnlocked(language, "TechEmperor", new QuizProgress.ProgressCallback() {
                @Override
                public void onSuccess() {
                    // Tech Emperor is unlocked
                    Log.d("HomeScreen", "Tech Emperor is UNLOCKED for " + language);
                    techEmperorLevel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("HomeScreen", "Tech Emperor clicked");
                            // Update current difficulty
                            currentDifficulty = "TECH EMPEROR";
                            
                            // Update subtitle to show selected difficulty
                            if (subTitle != null) {
                                subTitle.setText("TECH EMPEROR");
                            }
                            
                            // Save current difficulty to Firestore
                            saveCurrentDifficultyToFirestore("TECH EMPEROR");
                            
                            // Update level buttons for Tech Emperor
                            setupTechEmperorLevels(language);
                            
                            // Hide dropdown
                            hideDifficultyDropdown();
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    // Tech Emperor is locked
                    Log.d("HomeScreen", "Tech Emperor is LOCKED for " + language + ". Error: " + error);
                    techEmperorLevel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(HomeScreen.this, "Complete all Code Warden levels to unlock Tech Emperor", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
        
        // Code Abyss - check if unlocked
        if (codeAbyssLevel != null) {
            // For new users or when language is not set, immediately lock Code Abyss
            if (currentLanguage == null || currentLanguage.isEmpty()) {
                Log.d("HomeScreen", "New user detected, locking Code Abyss");
                codeAbyssLevel.setAlpha(0.4f);
                android.widget.ProgressBar progressCircle = codeAbyssLevel.findViewById(R.id.progress_circle_code_abyss);
                if (progressCircle != null) {
                    progressCircle.setVisibility(View.GONE);
                }
                TextView percentageText = codeAbyssLevel.findViewById(R.id.percentage_text_code_abyss);
                if (percentageText != null) {
                    percentageText.setVisibility(View.GONE);
                }
                ImageView lockIcon = codeAbyssLevel.findViewById(R.id.lock_icon_code_abyss);
                if (lockIcon != null) {
                    lockIcon.setVisibility(View.VISIBLE);
                }
                codeAbyssLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(HomeScreen.this, "Please select a language first to unlock levels", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                QuizProgress progress = new QuizProgress(HomeScreen.this);
                progress.isDifficultyUnlocked(language, "CodeAbyss", new QuizProgress.ProgressCallback() {
                @Override
                public void onSuccess() {
                    // Code Abyss is unlocked
                    Log.d("HomeScreen", "Code Abyss is UNLOCKED for " + language);
                    codeAbyssLevel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("HomeScreen", "Code Abyss clicked");
                            // Update current difficulty
                            currentDifficulty = "CODE ABYSS";
                            
                            // Update subtitle to show selected difficulty
                            if (subTitle != null) {
                                subTitle.setText("CODE ABYSS");
                            }
                            
                            // Save current difficulty to Firestore
                            saveCurrentDifficultyToFirestore("CODE ABYSS");
                            
                            // Update level buttons for Code Abyss
                            setupCodeAbyssLevels(language);
                            
                            // Hide dropdown
                            hideDifficultyDropdown();
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    // Code Abyss is locked
                    Log.d("HomeScreen", "Code Abyss is LOCKED for " + language + ". Error: " + error);
                    codeAbyssLevel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(HomeScreen.this, "Complete all Tech Emperor levels to unlock Code Abyss", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
        }
    }
    
    /**
     * Hide the difficulty dropdown
     */
    private void hideDifficultyDropdown() {
        View dropdownOverlay = findViewById(R.id.dropdownOverlay);
        LinearLayout dropdownLevelsContainer = findViewById(R.id.dropdownLevelsContainer);
        LinearLayout dataAcolyteLevel = findViewById(R.id.dataAcolyteLevel);
        LinearLayout systemKnightLevel = findViewById(R.id.systemKnightLevel);
        LinearLayout codeWardenLevel = findViewById(R.id.codeWardenLevel);
        LinearLayout techEmperorLevel = findViewById(R.id.techEmperorLevel);
        LinearLayout codeAbyssLevel = findViewById(R.id.codeAbyssLevel);
        LinearLayout[] levels = {dataAcolyteLevel, systemKnightLevel, codeWardenLevel, techEmperorLevel, codeAbyssLevel};
        
        ObjectAnimator.ofFloat(dropdownOverlay, "alpha", 1f, 0f).setDuration(250).start();
        dropdownOverlay.postDelayed(() -> dropdownOverlay.setVisibility(View.GONE), 250);
        dropdownLevelsContainer.setVisibility(View.INVISIBLE);
        for (LinearLayout level : levels) {
            if (level != null) level.setVisibility(View.INVISIBLE);
        }
        
        // Re-enable navigation when dropdown closes
        enableNavigationAfterDropdown();
        
        // Re-enable level interactions
        View levelScroll = findViewById(R.id.levelScroll);
        if (levelScroll != null) {
            levelScroll.setOnTouchListener(null);
            levelScroll.setAlpha(1f);
        }
        
        dropdownOverlay.setOnClickListener(null);
        dropdownOverlay.setClickable(false);
        dropdownOverlay.setFocusable(false);
    }
    
    private void resetLevelButtonsToLocked() {
        // Reset all level buttons to show as locked
        for (int i = 1; i <= 10; i++) {
            int levelId = getResources().getIdentifier("level" + i, "id", getPackageName());
            ImageView levelView = findViewById(levelId);
            
            if (levelView != null) {
                final int levelNumber = i;
                
                // Set all levels as locked
                levelView.setImageResource(R.drawable.image_level_locked);
                
                // Set click listener to show locked message
                levelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Don't show a toast here, just disable the button
                    }
                });
            }
        }
    }
    
    private void initializeLivesUI() {
        // Initialize life image views
        life1 = findViewById(R.id.life1);
        life2 = findViewById(R.id.life2);
        life3 = findViewById(R.id.life3);
        life4 = findViewById(R.id.life4);
        life5 = findViewById(R.id.life5);
        
        // Initialize coins text view
        coinsTextView = findViewById(R.id.coinsCount);
        
        // Load lives with instant display and background refresh
        userDataManager.getLives(new UserDataManager.LivesCallback() {
            @Override
            public void onLivesLoaded(Integer lives) {
                updateLivesDisplayFromCache(lives);
            }
            
            @Override
            public void onError(String error) {
                // Handle error silently, will be updated when fresh data loads
            }
        });
        
        // Initialize coins display
        updateCoinsDisplay();
    }
    
    private void updateLivesDisplay(int currentLives, long nextRegenerationTime) {
        // Update visibility of life images based on current lives count
        if (life1 != null) life1.setVisibility(currentLives >= 1 ? View.VISIBLE : View.INVISIBLE);
        if (life2 != null) life2.setVisibility(currentLives >= 2 ? View.VISIBLE : View.INVISIBLE);
        if (life3 != null) life3.setVisibility(currentLives >= 3 ? View.VISIBLE : View.INVISIBLE);
        if (life4 != null) life4.setVisibility(currentLives >= 4 ? View.VISIBLE : View.INVISIBLE);
        if (life5 != null) life5.setVisibility(currentLives >= 5 ? View.VISIBLE : View.INVISIBLE);
        
        if (nextRegenerationTime > 0) {
            // Start countdown timer
            startLivesCountdown(nextRegenerationTime);
        } else {
            // Stop countdown if lives are full
            stopLivesCountdown();
        }
    }
    
    private void updateCoinsDisplay() {
        // Load coins with instant display and background refresh
        userDataManager.getCoins(new UserDataManager.CoinsCallback() {
            @Override
            public void onCoinsLoaded(Long coins) {
                if (coinsTextView != null) {
                    coinsTextView.setText(String.valueOf(coins));
                }
            }
            
            @Override
            public void onError(String error) {
                if (coinsTextView != null) {
                    coinsTextView.setText("0");
                }
            }
        });
    }
    
    private void startLivesCountdown(long nextRegenerationTime) {
        stopLivesCountdown(); // Stop any existing countdown
        
        livesUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long timeRemaining = nextRegenerationTime - currentTime;
                
                if (timeRemaining <= 0) {
                    // Time's up, refresh lives
                    livesManager.getCurrentLives(new LivesManager.LivesCallback() {
                        @Override
                        public void onLivesUpdated(int currentLives, long nextRegenerationTime) {
                            updateLivesDisplay(currentLives, nextRegenerationTime);
                        }
                        
                        @Override
                        public void onError(String error) {
                            Toast.makeText(HomeScreen.this, "Error updating lives: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Continue countdown
                    handler.postDelayed(this, 1000); // Update every second
                }
            }
        };
        
        handler.post(livesUpdateRunnable);
    }
    
    private void stopLivesCountdown() {
        if (livesUpdateRunnable != null) {
            handler.removeCallbacks(livesUpdateRunnable);
            livesUpdateRunnable = null;
        }
    }
    
    private void checkLivesBeforeQuiz() {
        livesManager.getCurrentLives(new LivesManager.LivesCallback() {
            @Override
            public void onLivesUpdated(int currentLives, long nextRegenerationTime) {
                if (currentLives > 0) {
                    // Start quiz directly without deducting a life
                    // Lives will only be deducted when answering incorrectly
                    startQuiz();
                } else {
                    // Show no lives dialog
                    showNoLivesDialog();
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(HomeScreen.this, "Error checking lives: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showNoLivesDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("No Lives Remaining");
        builder.setMessage("You have no lives remaining. Would you like to purchase more lives or wait for them to regenerate?");
        builder.setPositiveButton("Purchase Lives", (dialog, which) -> {
            // Navigate to shop
            Intent intent = new Intent(HomeScreen.this, ShopInterface.class);
            startActivity(intent);
        });
        builder.setNegativeButton("Wait", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }
    
    private void startQuiz() {
        // This method will be called when the quiz should start
        // You can implement the quiz start logic here
        Intent intent = new Intent(HomeScreen.this, LearnItQuiz.class);
        intent.putExtra("language", currentLanguage);
        intent.putExtra("difficulty", "DataAcolyte"); // You can make this dynamic
        startActivity(intent);
    }
    
    private void checkLivesBeforeQuizWithDetails(String language, String difficulty, int level) {
        livesManager.getCurrentLives(new LivesManager.LivesCallback() {
            @Override
            public void onLivesUpdated(int currentLives, long nextRegenerationTime) {
                if (currentLives > 0) {
                    // Start the quiz directly without deducting a life
                    // Lives will only be deducted when answering incorrectly
                    Intent intent = new Intent(HomeScreen.this, LearnItQuiz.class);
                    intent.putExtra("level", level);
                    intent.putExtra("language", language);
                    intent.putExtra("difficulty", difficulty);
                    startActivity(intent);
                } else {
                    // Show no lives dialog
                    showNoLivesDialog();
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(HomeScreen.this, "Error checking lives: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Initialize and start particle effect
        initializeParticleEffect();
    }

    /**
     * Initialize and start the particle effect animation
     */
    private void initializeParticleEffect() {
        ParticleView particleView = findViewById(R.id.particleBackground);
        if (particleView != null) {
            particleView.startAnimation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop particle animation to save battery
        ParticleView particleView = findViewById(R.id.particleBackground);
        if (particleView != null) {
            particleView.stopAnimation();
        }
    }



    /**
     * Loads the current language from Firestore or returns default for new users
     */
    private void loadCurrentLanguageFromFirestore() {
        Log.d("HomeScreen", "loadCurrentLanguageFromFirestore called");
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.d("HomeScreen", "Current user: " + (user != null ? user.getUid() : "null"));
            if (user != null) {
                Log.d("HomeScreen", "User is authenticated, loading language from Firestore");
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("User Quiz Records").document(user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot document) {
                            Log.d("HomeScreen", "Firestore document loaded successfully");
                            if (document.exists() && document.contains("selectedLanguage")) {
                                // User has selected a language
                                String language = document.getString("selectedLanguage");
                                currentLanguage = language;
                                Log.d("HomeScreen", "Language found in Firestore: " + language);
                                
                                // Update UI
                                TextView languageTitle = findViewById(R.id.languageTitle);
                                if (languageTitle != null) {
                                    languageTitle.setText(language);
                                }
                                
                                // Update difficulty levels and setup
                                // First reset difficulty levels to initial state
                                resetDifficultyLevelsToInitialState();
                                
                                // Then update with actual progress for the loaded language
                                QuizProgress quizProgress = new QuizProgress(HomeScreen.this);
                                updateDifficultyLevels(language, quizProgress);
                                
                                // Setup difficulty level click listeners
                                setupDifficultyLevelClickListeners(language);
                                
                                // Setup level buttons for current difficulty (or Data Acolyte if none)
                                setupLevelButtonsForCurrentDifficulty();
                            } else {
                                // New user - show language selection prompt
                                Log.d("HomeScreen", "No language found in Firestore, showing selection prompt");
                                currentLanguage = null;
                                TextView languageTitle = findViewById(R.id.languageTitle);
                                if (languageTitle != null) {
                                    languageTitle.setText("Select a language here ->");
                                }
                                
                                // Reset difficulty levels for new user
                                resetLevelButtonsToLocked();
                                
                                // Lock all difficulty levels for new user
                                resetDifficultyLevelsToInitialState();
                                
                                // Setup difficulty level click listeners for locked state
                                setupDifficultyLevelClickListenersForNewUser();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("HomeScreen", "Error loading from Firestore: " + e.getMessage());
                            // Error loading from Firestore, show language selection prompt
                            currentLanguage = null;
                            TextView languageTitle = findViewById(R.id.languageTitle);
                            if (languageTitle != null) {
                                languageTitle.setText("Select a language here ->");
                            }
                            resetLevelButtonsToLocked();
                            resetDifficultyLevelsToInitialState();
                            setupDifficultyLevelClickListenersForNewUser();
                        }
                    });
            } else {
                // No user logged in, show language selection prompt
                Log.w("HomeScreen", "No user logged in, showing language selection prompt");
                currentLanguage = null;
                TextView languageTitle = findViewById(R.id.languageTitle);
                if (languageTitle != null) {
                    languageTitle.setText("Select a language here ->");
                }
                resetLevelButtonsToLocked();
                resetDifficultyLevelsToInitialState();
                setupDifficultyLevelClickListenersForNewUser();
            }
        } catch (Exception e) {
            // Error occurred, show language selection prompt
            currentLanguage = null;
            TextView languageTitle = findViewById(R.id.languageTitle);
            if (languageTitle != null) {
                languageTitle.setText("Select a language here ->");
            }
            resetLevelButtonsToLocked();
            resetDifficultyLevelsToInitialState();
            setupDifficultyLevelClickListenersForNewUser();
        }
    }
    
    /**
     * Loads the current language from SharedPreferences (legacy method)
     */
    private String loadCurrentLanguage() {
        try {
            android.content.SharedPreferences prefs = getSharedPreferences("LearnItPrefs", MODE_PRIVATE);
            String savedLanguage = prefs.getString("selectedLanguage", "C#");
            return savedLanguage;
        } catch (Exception e) {
            return "C#"; // Default fallback
        }
    }
    
    /**
     * Saves the current language to Firestore
     */
    private void saveCurrentLanguage(String language) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> languageData = new HashMap<>();
                languageData.put("selectedLanguage", language);
                db.collection("User Quiz Records").document(user.getUid())
                    .set(languageData, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Language saved successfully to Firestore
                            currentLanguage = language;
                            
                            // Update UI
                            TextView languageTitle = findViewById(R.id.languageTitle);
                            if (languageTitle != null) {
                                languageTitle.setText(language);
                            }
                            
                            // Update difficulty levels and setup
                            // First reset difficulty levels to initial state
                            resetDifficultyLevelsToInitialState();
                            
                            // Then update with actual progress for the language
                            QuizProgress quizProgress = new QuizProgress(HomeScreen.this);
                            updateDifficultyLevels(language, quizProgress);
                            
                            // Setup difficulty level click listeners
                            setupDifficultyLevelClickListeners(language);
                            
                            // Setup level buttons for current difficulty (or Data Acolyte if none)
                            setupLevelButtonsForCurrentDifficulty();
                            
                            // Automatically unlock level 1 for the selected language
                            unlockLevel1ForLanguage(language);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeScreen.this, "Error saving language: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            } else {
                Toast.makeText(HomeScreen.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(HomeScreen.this, "Error saving language: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        Log.d("HomeScreen", "onResume called");
        
        // Resume particle animation
        ParticleView particleView = findViewById(R.id.particleBackground);
        if (particleView != null) {
            particleView.startAnimation();
        }
        
        // Check if user is still authenticated
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("HomeScreen", "User authentication status in onResume: " + (user != null ? "Authenticated" : "Not authenticated"));
        
        if (user == null) {
            Log.w("HomeScreen", "User not authenticated in onResume, this might explain the redirect to LogInScreen");
        }
        
        // Load data with instant display and background refresh
        loadDataWithCache();
        
        // Check if language was recently selected and ensure level 1 is visible
        checkAndUnlockLevel1IfLanguageSelected();
    }
    
    private void loadDataWithCache() {
        // Load lives with instant display
        userDataManager.getLives(new UserDataManager.LivesCallback() {
            @Override
            public void onLivesLoaded(Integer lives) {
                // Update lives display immediately with cached data
                updateLivesDisplayFromCache(lives);
            }
            
            @Override
            public void onError(String error) {
                // Handle error silently, will be updated when fresh data loads
            }
        });
        
        // Load coins with instant display
        userDataManager.getCoins(new UserDataManager.CoinsCallback() {
            @Override
            public void onCoinsLoaded(Long coins) {
                // Update coins display immediately with cached data
                if (coinsTextView != null) {
                    coinsTextView.setText(String.valueOf(coins));
                }
            }
            
            @Override
            public void onError(String error) {
                // Handle error silently, will be updated when fresh data loads
            }
        });
        
        // Load language with instant display
        userDataManager.getLanguage(new UserDataManager.LanguageCallback() {
            @Override
            public void onLanguageLoaded(String language) {
                // Update language display immediately with cached data
                updateLanguageDisplayFromCache(language);
            }
            
            @Override
            public void onError(String error) {
                // Handle error silently, will be updated when fresh data loads
            }
        });
        
        // Load difficulty with instant display
        userDataManager.getDifficulty(new UserDataManager.DifficultyCallback() {
            @Override
            public void onDifficultyLoaded(String difficulty) {
                // Update difficulty display immediately with cached data
                updateDifficultyDisplayFromCache(difficulty);
            }
            
            @Override
            public void onError(String error) {
                // Handle error silently, will be updated when fresh data loads
            }
        });
    }
    
    private void updateLivesDisplayFromCache(int lives) {
        // Update life indicators based on cached lives count
        if (life1 != null) {
            life1.setVisibility(lives >= 1 ? View.VISIBLE : View.INVISIBLE);
        }
        if (life2 != null) {
            life2.setVisibility(lives >= 2 ? View.VISIBLE : View.INVISIBLE);
        }
        if (life3 != null) {
            life3.setVisibility(lives >= 3 ? View.VISIBLE : View.INVISIBLE);
        }
        if (life4 != null) {
            life4.setVisibility(lives >= 4 ? View.VISIBLE : View.INVISIBLE);
        }
        if (life5 != null) {
            life5.setVisibility(lives >= 5 ? View.VISIBLE : View.INVISIBLE);
        }
    }
    
    private void updateLanguageDisplayFromCache(String language) {
        currentLanguage = language;
        TextView languageTitle = findViewById(R.id.languageTitle);
        if (languageTitle != null) {
            if (language == null || language.isEmpty()) {
                languageTitle.setText("Select a language here ->");
                // For new users, lock everything
                resetLevelButtonsToLocked();
                resetDifficultyLevelsToInitialState();
                setupDifficultyLevelClickListenersForNewUser();
            } else {
                languageTitle.setText(language);
                // Update difficulty levels and setup with cached language
                resetDifficultyLevelsToInitialState();
                QuizProgress quizProgress = new QuizProgress(this);
                updateDifficultyLevels(language, quizProgress);
                setupDifficultyLevelClickListeners(language);
                setupLevelButtonsForCurrentDifficulty();
                
                // Automatically unlock level 1 for the loaded language
                unlockLevel1ForLanguage(language);
            }
        }
    }
    
    private void updateDifficultyDisplayFromCache(String difficulty) {
        currentDifficulty = difficulty;
        TextView subTitle = findViewById(R.id.subTitle);
        if (subTitle != null) {
            subTitle.setText(difficulty);
        }
        setupLevelButtonsForCurrentDifficulty();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        Log.d("HomeScreen", "onDestroy called");
        
        if (livesManager != null) {
            livesManager.cleanup();
        }
        stopLivesCountdown();
    }

    /**
     * Configure ScrollView for proper scrolling functionality
     */
    private void configureScrollView() {
        ScrollView levelScroll = findViewById(R.id.levelScroll);
        if (levelScroll != null) {
            // Enable scrolling
            levelScroll.setScrollbarFadingEnabled(false);
            levelScroll.setVerticalScrollBarEnabled(true);
            levelScroll.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            
            // Ensure ScrollView is properly configured
            levelScroll.setFillViewport(true);
            levelScroll.setSmoothScrollingEnabled(true);
            
            // Remove any existing touch listeners that might interfere
            levelScroll.setOnTouchListener(null);
            
            // Scroll to bottom to show level 1 first
            levelScroll.post(new Runnable() {
                @Override
                public void run() {
                    levelScroll.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }
    
    /**
     * Opens language selection screen for user to choose their programming language
     */
    private void openLanguageSelection() {
        Log.d("HomeScreen", "openLanguageSelection called");
        Intent intent = new Intent(HomeScreen.this, LanguageSelection.class);
        startActivityForResult(intent, LANGUAGE_SELECTION_REQUEST);
        Log.d("HomeScreen", "LanguageSelection activity started with request code: " + LANGUAGE_SELECTION_REQUEST);
    }
    
    /**
     * Saves language to Firestore in background without blocking UI
     */
    private void saveLanguageToFirestoreBackground(String language) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> languageData = new HashMap<>();
                languageData.put("selectedLanguage", language);
                db.collection("User Quiz Records").document(user.getUid())
                    .set(languageData, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Language saved successfully (no UI update needed)
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Log error but don't show to user to avoid interruption
                            Log.e("HomeScreen", "Error saving language to Firestore: " + e.getMessage());
                        }
                    });
            }
        } catch (Exception e) {
            // Log error but don't show to user to avoid interruption
            Log.e("HomeScreen", "Error in saveLanguageToFirestoreBackground: " + e.getMessage());
        }
    }
    
    /**
     * Saves current difficulty level to Firestore
     */
    private void saveCurrentDifficultyToFirestore(String difficulty) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> difficultyData = new HashMap<>();
                difficultyData.put("currentDifficulty", difficulty);
                db.collection("User Quiz Records").document(user.getUid())
                    .set(difficultyData, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("HomeScreen", "Current difficulty saved to Firestore: " + difficulty);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("HomeScreen", "Error saving difficulty to Firestore: " + e.getMessage());
                        }
                    });
            }
        } catch (Exception e) {
            Log.e("HomeScreen", "Error in saveCurrentDifficultyToFirestore: " + e.getMessage());
        }
    }
    
    /**
     * Loads current difficulty level from Firestore
     */
    private void loadCurrentDifficultyFromFirestore() {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("User Quiz Records").document(user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot document) {
                            if (document.exists() && document.contains("currentDifficulty")) {
                                String difficulty = document.getString("currentDifficulty");
                                if (difficulty != null) {
                                    currentDifficulty = difficulty;
                                    Log.d("HomeScreen", "Loaded current difficulty from Firestore: " + difficulty);
                                    
                                    // Update the subtitle to show the loaded difficulty
                                    TextView subTitle = findViewById(R.id.subTitle);
                                    if (subTitle != null) {
                                        subTitle.setText(difficulty);
                                    }
                                    
                                    // Setup level buttons for the loaded difficulty
                                    setupLevelButtonsForCurrentDifficulty();
                                }
                            } else {
                                Log.d("HomeScreen", "No difficulty found in Firestore, using default: DATA ACOLYTE");
                                currentDifficulty = "DATA ACOLYTE";
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("HomeScreen", "Error loading difficulty from Firestore: " + e.getMessage());
                            currentDifficulty = "DATA ACOLYTE";
                        }
                    });
            } else {
                Log.w("HomeScreen", "No user logged in, using default difficulty");
                currentDifficulty = "DATA ACOLYTE";
            }
        } catch (Exception e) {
            Log.e("HomeScreen", "Error in loadCurrentDifficultyFromFirestore: " + e.getMessage());
            currentDifficulty = "DATA ACOLYTE";
        }
    }
    
    /**
     * Setup level buttons based on the current difficulty
     */
    private void setupLevelButtonsForCurrentDifficulty() {
        if (currentLanguage == null) return;
        
        Log.d("HomeScreen", "Setting up level buttons for current difficulty: " + currentDifficulty);
        
        switch (currentDifficulty) {
            case "DATA ACOLYTE":
                setupLevelButtons(currentLanguage);
                break;
            case "SYSTEM KNIGHT":
                setupSystemKnightLevels(currentLanguage);
                break;
            case "CODE WARDEN":
                setupCodeWardenLevels(currentLanguage);
                break;
            case "TECH EMPEROR":
                setupTechEmperorLevels(currentLanguage);
                break;
            default:
                setupLevelButtons(currentLanguage);
                break;
        }
    }
    
    /**
     * Setup click listener for language title to allow language selection
     */
    private void setupLanguageTitleClickListener() {
        TextView languageTitle = findViewById(R.id.languageTitle);
        if (languageTitle != null) {
            Log.d("HomeScreen", "Setting up language title click listener");
            languageTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HomeScreen", "Language title clicked, opening language selection");
                    // Open language selection screen
                    openLanguageSelection();
                }
            });
        } else {
            Log.w("HomeScreen", "languageTitle not found in layout");
        }
    }
}
