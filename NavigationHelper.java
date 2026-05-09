package com.example.learnit;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LearnItQuiz extends BaseActivity {

    private List<QuestionData> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int selectedAnswerIndex = -1;
    private Button[] choiceButtons;
    private TextView questionText, progressText, codeTemplate, feedbackText;
    private Button checkButton, continueButton;
    private android.widget.ProgressBar progressBar;
    private QuizProgress quizProgress;
    private String language, difficulty;
    private TextView dropTarget;
    private String currentDraggedText = "";
    private LinearLayout codeBlockContainer;
    private CustomBottomNavigation bottomNavigation;
    private TextView livesCountTextView, coinsCountTextView;
    private LivesManager livesManager;
    private UserDataManager userDataManager;
    private boolean currentQuestionResult = false;
    private String currentCorrectAnswer = "";
    private boolean coinsAddedForCurrentQuestion = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_learnit_quiz;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Initialize views
            initializeViews();
            
            // Initialize choice buttons
            choiceButtons = new Button[4];
            choiceButtons[0] = findViewById(R.id.btnChoice1);
            choiceButtons[1] = findViewById(R.id.btnChoice2);
            choiceButtons[2] = findViewById(R.id.btnChoice3);
            choiceButtons[3] = findViewById(R.id.btnChoice4);

            // Get quiz data
            Intent intent = getIntent();
            int level = intent.getIntExtra("level", 1);
            language = intent.getStringExtra("language");
            difficulty = intent.getStringExtra("difficulty");

            // Initialize progress tracking
            quizProgress = new QuizProgress(this);
            
            // Set starting question index
            currentQuestionIndex = level - 1;
            if (currentQuestionIndex < 0) {
                currentQuestionIndex = 0;
            }
            
            // Setup drag and drop functionality
            setupDropTarget();
            
            // Load questions
            loadQuestions();
            
            // Ensure valid starting level
            if (currentQuestionIndex >= questions.size()) {
                currentQuestionIndex = questions.size() - 1;
            }

            // Setup UI elements
            setupBackButton();
            setupChoiceButtons();
            setupButtons();

            // Load first question and initialize display
            loadQuestion();
            initializeLivesAndCoinsDisplay();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        // Setup bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setCurrentActivity("LearnItQuiz");
            disableBottomNavigation(bottomNavigation);
        }

        // Initialize main views
        questionText = findViewById(R.id.questionText);
        progressText = findViewById(R.id.progressText);
        codeTemplate = findViewById(R.id.codeTemplate);
        feedbackText = findViewById(R.id.feedbackText);
        progressBar = findViewById(R.id.quizProgressBar);
        checkButton = findViewById(R.id.checkButton);
        continueButton = findViewById(R.id.continueButton);
        codeBlockContainer = findViewById(R.id.codeBlockContainer);
        
        // Initialize lives and coins display
        livesCountTextView = findViewById(R.id.livesCount);
        coinsCountTextView = findViewById(R.id.coinsCount);
        livesManager = new LivesManager(this);
        userDataManager = UserDataManager.getInstance(this);
    }

    private void loadQuestions() {
        if ("C#".equals(language)) {
            if ("DataAcolyte".equals(difficulty)) {
                questions = QuestionData.getDataAcolyteQuestions();
            } else if ("SystemKnight".equals(difficulty)) {
                questions = QuestionData.getSystemKnightQuestions();
            } else if ("CodeWarden".equals(difficulty)) {
                questions = QuestionData.getCodeWardenQuestions();
            } else if ("TechEmperor".equals(difficulty)) {
                questions = QuestionData.getTechEmperorQuestions();
            } else {
                questions = QuestionData.getDataAcolyteQuestions();
            }
        } else if ("JAVA".equals(language)) {
            if ("DataAcolyte".equals(difficulty)) {
                questions = QuestionData.getJavaDataAcolyteQuestions();
            } else if ("SystemKnight".equals(difficulty)) {
                questions = QuestionData.getJavaSystemKnightQuestions();
            } else if ("CodeWarden".equals(difficulty)) {
                questions = QuestionData.getJavaCodeWardenQuestions();
            } else if ("TechEmperor".equals(difficulty)) {
                questions = QuestionData.getJavaTechEmperorQuestions();
            } else {
                questions = QuestionData.getJavaDataAcolyteQuestions();
            }
        } else {
            questions = QuestionData.getDataAcolyteQuestions();
        }
    }

    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (bottomNavigation != null) {
                    enableBottomNavigation(bottomNavigation);
                }
                Intent intent = new Intent(LearnItQuiz.this, HomeScreen.class);
                startActivity(intent);
            });
            btnBack.setClickable(true);
            btnBack.setEnabled(true);
            btnBack.setFocusable(true);
        }
    }

    private void setupChoiceButtons() {
        for (int i = 0; i < choiceButtons.length; i++) {
            final int index = i;
            choiceButtons[i].setOnLongClickListener(v -> startDrag(v, index));
            choiceButtons[i].setOnClickListener(v -> selectAnswer(index));
        }
    }

    private void setupButtons() {
        checkButton.setOnClickListener(v -> checkAnswer());
        continueButton.setOnClickListener(v -> nextQuestion());
    }

    private void loadQuestion() {
        if (currentQuestionIndex < questions.size()) {
            QuestionData currentQuestion = questions.get(currentQuestionIndex);
            
            // Update question text
            questionText.setText(currentQuestion.getQuestion());
            
            // Update code template and create drop target
            String template = currentQuestion.getCodeTemplate();
            codeTemplate.setText(template);
            
            // Reset the drop target
            if (dropTarget != null) {
                codeBlockContainer.removeView(dropTarget);
            }
            createDropTarget(template);
            
            // Update progress
            progressText.setText("Level " + (currentQuestionIndex + 1) + " of " + questions.size());
            
            // Update progress bar
            int progress = (int) (((float) (currentQuestionIndex + 1) / questions.size()) * 100);
            progressBar.setProgress(progress);
            
            // Update choice buttons
            List<String> choices = currentQuestion.getChoices();
            for (int i = 0; i < choiceButtons.length; i++) {
                if (i < choices.size()) {
                    choiceButtons[i].setText(choices.get(i));
                    choiceButtons[i].setVisibility(View.VISIBLE);
                } else {
                    choiceButtons[i].setVisibility(View.GONE);
                }
            }
            
            // Reset selection and colors
            selectedAnswerIndex = -1;
            currentDraggedText = "";
            resetButtonColors();
            updateChoiceButtonStyles();
            
            // Re-enable drag and drop for the new question
            enableDragAndDrop();
            
            // Hide feedback text
            feedbackText.setVisibility(View.GONE);
            
            // Show check button, hide continue button
            checkButton.setVisibility(View.VISIBLE);
            continueButton.setVisibility(View.GONE);
            
        } else {
            // Quiz completed - save progress
            quizProgress.setLevelScore(language, difficulty, score, new QuizProgress.ProgressCallback() {
                @Override
                public void onSuccess() {
                    // Score saved successfully
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(LearnItQuiz.this, "Error saving score: " + error, Toast.LENGTH_SHORT).show();
                }
            });
            
            // Check if all questions are completed and mark level as completed
            quizProgress.areAllQuestionsCompleted(language, difficulty, questions.size(), new QuizProgress.ProgressCallback() {
                @Override
                public void onSuccess() {
                    quizProgress.setLevelCompleted(language, difficulty, true, new QuizProgress.ProgressCallback() {
                        @Override
                        public void onSuccess() {
                            // Level completion saved successfully
                        }
                        
                        @Override
                        public void onError(String error) {
                            Toast.makeText(LearnItQuiz.this, "Error saving level completion: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(LearnItQuiz.this, "Error checking level completion: " + error, Toast.LENGTH_SHORT).show();
                }
            });
            
            // Re-enable bottom navigation before going to results
            if (bottomNavigation != null) {
                enableBottomNavigation(bottomNavigation);
            }
            
            // Check for random surprise quiz (20% chance)
            if (shouldTriggerSurpriseQuiz()) {
                Intent intent = new Intent(LearnItQuiz.this, SurpriseQuizIntroduction.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LearnItQuiz.this, ResultInterface.class);
                intent.putExtra("score", score);
                intent.putExtra("totalQuestions", questions.size());
                intent.putExtra("levelTitle", "Level " + (currentQuestionIndex + 1) + " - " + difficulty);
                intent.putExtra("coinsEarned", calculateCoinsEarned(score, difficulty)); // Calculate total coins earned dynamically
                intent.putExtra("isLevelCompleted", true);
                startActivity(intent);
            }
        }
    }

    private void selectAnswer(int index) {
        selectedAnswerIndex = index;
        currentDraggedText = ""; // Clear dragged text for click selection
        
        // Get the selected answer text and put it in the drop target
        if (index >= 0 && index < choiceButtons.length) {
            String selectedAnswer = choiceButtons[index].getText().toString();
            if (dropTarget != null) {
                dropTarget.setText(selectedAnswer);
                currentDraggedText = selectedAnswer; // Set this for answer checking
            }
        }
        
        Log.d("LearnItQuiz", "Click selection - Index: " + index + ", Answer: " + (dropTarget != null ? dropTarget.getText().toString() : "null"));
        updateChoiceButtonStyles();
    }

    private void updateChoiceButtonStyles() {
        for (int i = 0; i < choiceButtons.length; i++) {
            if (i == selectedAnswerIndex) {
                choiceButtons[i].setBackgroundResource(R.drawable.selectable_button_background);
                choiceButtons[i].setTextColor(getResources().getColor(android.R.color.white));
            } else {
                choiceButtons[i].setBackgroundResource(R.drawable.selectable_button_background);
                choiceButtons[i].setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }
    
    private void resetButtonColors() {
        for (int i = 0; i < choiceButtons.length; i++) {
            choiceButtons[i].setTextColor(getResources().getColor(android.R.color.black));
        }
    }
    
    private void disableDragAndDrop() {
        for (int i = 0; i < choiceButtons.length; i++) {
            choiceButtons[i].setOnLongClickListener(null);
            choiceButtons[i].setOnClickListener(null);
            choiceButtons[i].setAlpha(0.6f);
        }
        
        codeBlockContainer.setOnDragListener(null);
        
        if (dropTarget != null) {
            dropTarget.setAlpha(0.6f);
        }
    }
    
    private void enableDragAndDrop() {
        for (int i = 0; i < choiceButtons.length; i++) {
            final int index = i;
            choiceButtons[i].setOnLongClickListener(v -> startDrag(v, index));
            choiceButtons[i].setAlpha(1.0f);
        }
        
        setupDropTarget();
        
        if (dropTarget != null) {
            dropTarget.setAlpha(1.0f);
        }
    }

    private void checkAnswer() {
        if (selectedAnswerIndex == -1 && currentDraggedText.isEmpty()) {
            Toast.makeText(this, "Please select an answer or drag a choice to the blank", Toast.LENGTH_SHORT).show();
            return;
        }
        
        refreshLivesAndCoinsDisplay();

        QuestionData currentQuestion = questions.get(currentQuestionIndex);
        
        // Handle both click selection and drag-and-drop
        boolean isCorrect;
        String correctAnswer = currentQuestion.getChoices().get(currentQuestion.getCorrectAnswerIndex());
        
        // Check if user used drag and drop (has dragged text) or click selection
        if (!currentDraggedText.isEmpty()) {
            // User used drag and drop
            isCorrect = currentDraggedText.equals(correctAnswer);
            Log.d("LearnItQuiz", "Drag selection - Dragged: '" + currentDraggedText + "', Correct: '" + correctAnswer + "', IsCorrect: " + isCorrect);
        } else if (selectedAnswerIndex != -1) {
            // User used click selection
            isCorrect = selectedAnswerIndex == currentQuestion.getCorrectAnswerIndex();
            Log.d("LearnItQuiz", "Click selection - Selected: " + selectedAnswerIndex + ", Correct: " + currentQuestion.getCorrectAnswerIndex() + ", IsCorrect: " + isCorrect);
        } else {
            // No answer selected
            isCorrect = false;
            Log.d("LearnItQuiz", "No answer selected");
        }
        
        // Store the result for the dialog
        currentQuestionResult = isCorrect;
        currentCorrectAnswer = correctAnswer;
        coinsAddedForCurrentQuestion = false; // Reset flag for new question
        
        // Show immediate feedback
        if (isCorrect) {
            feedbackText.setText("Correct!");
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            feedbackText.setText("Incorrect, the correct answer is " + correctAnswer);
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        feedbackText.setVisibility(View.VISIBLE);
        
        // Handle lives based on answer correctness
        if (isCorrect) {
            // Correct answer: Increment score (coins will be added when user clicks Continue)
            score++;
        } else {
            // Incorrect answer: Lose 1 life
            loseLife();
        }
        
        // Mark this question as completed regardless of correctness
        quizProgress.setQuestionCompleted(language, difficulty, currentQuestionIndex + 1, true, new QuizProgress.ProgressCallback() {
            @Override
            public void onSuccess() {
                // Question completion saved successfully
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(LearnItQuiz.this, "Error saving progress: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Keep the user's answer in the drop target (don't change it)
        // The correct answer will be highlighted in the choice buttons instead
        
        if (dropTarget != null) {
            Log.d("LearnItQuiz", "Keeping user's answer in drop target: " + currentDraggedText);
        }

        // Show correct answer
        for (int i = 0; i < choiceButtons.length; i++) {
            if (i == currentQuestion.getCorrectAnswerIndex()) {
                choiceButtons[i].setTextColor(getResources().getColor(android.R.color.holo_green_light));
            } else if (i == selectedAnswerIndex && !isCorrect) {
                choiceButtons[i].setTextColor(getResources().getColor(android.R.color.holo_red_light));
            }
        }

        disableDragAndDrop();
        
        checkButton.setVisibility(View.GONE);
        continueButton.setVisibility(View.VISIBLE);
    }

    private void nextQuestion() {
        // Show dialog after each question
        showQuestionResultDialog();
    }
    
    private void showQuestionResultDialog() {
        // Create and show the completion dialog
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_level_complete);
        dialog.setCancelable(false);
        
        // Get dialog views
        TextView tvComplete = dialog.findViewById(R.id.tvComplete);
        TextView tvLevelInfo = dialog.findViewById(R.id.tvLevelInfo);
        TextView tvScore = dialog.findViewById(R.id.tvScore);
        Button btnHome = dialog.findViewById(R.id.btnBack);
        Button btnContinue = dialog.findViewById(R.id.btnNext);
        
        // Set level information
        int currentLevel = currentQuestionIndex + 1;
        tvLevelInfo.setText("Level " + currentLevel);
        
        // Set coins earned based on difficulty level (if correct, 0 if incorrect)
        int coinsEarned = currentQuestionResult ? getCoinValuePerDifficulty(difficulty) : 0;
        tvScore.setText(String.valueOf(coinsEarned));
        
        // Check if this is the last level (level 10)
        if (currentLevel == 10) {
            // Last level - show "COMPLETED" and only "RESULT" button
            tvComplete.setText("COMPLETED");
            
            // Hide the HOME button and change CONTINUE to RESULT
            btnHome.setVisibility(View.GONE);
            btnContinue.setText("RESULT");
            
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    
                    // Add coins only if the answer was correct and coins haven't been added yet
                    if (currentQuestionResult && !coinsAddedForCurrentQuestion) {
                        int coinsToAdd = getCoinValuePerDifficulty(difficulty);
                        addCoinsToUser(coinsToAdd);
                        coinsAddedForCurrentQuestion = true;
                        
                        // Show immediate feedback
                        Toast.makeText(LearnItQuiz.this, "🎉 +" + coinsToAdd + " coins!", Toast.LENGTH_SHORT).show();
                    }
                    
                    // Navigate to ResultInterface with level completion info
                    Intent intent = new Intent(LearnItQuiz.this, ResultInterface.class);
                    intent.putExtra("score", score);
                    intent.putExtra("totalQuestions", questions.size());
                    intent.putExtra("levelTitle", "Level " + currentLevel + " - " + difficulty);
                    intent.putExtra("coinsEarned", coinsEarned);
                    intent.putExtra("isLevelCompleted", true);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            // Not the last level - show normal dialog
            if (currentQuestionResult) {
                tvComplete.setText("CORRECT");
            } else {
                tvComplete.setText("INCORRECT");
            }
            
            // Show both buttons
            btnHome.setVisibility(View.VISIBLE);
            btnContinue.setText("CONTINUE");
            
            // Set button click listeners
            btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    // Return to HomeScreen
                    Intent intent = new Intent(LearnItQuiz.this, HomeScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
            
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    
                    // Add coins only if the answer was correct, user clicks Continue, and coins haven't been added yet
                    if (currentQuestionResult && !coinsAddedForCurrentQuestion) {
                        int coinsToAdd = getCoinValuePerDifficulty(difficulty);
                        addCoinsToUser(coinsToAdd);
                        coinsAddedForCurrentQuestion = true; // Mark as added to prevent duplicate additions
                        
                        // Show immediate feedback
                        Toast.makeText(LearnItQuiz.this, "🎉 +" + coinsToAdd + " coins!", Toast.LENGTH_SHORT).show();
                    }
                    
                    // Proceed to next question or level
                    proceedToNextQuestion();
                }
            });
        }
        
        dialog.show();
    }
    
    private void proceedToNextQuestion() {
        currentQuestionIndex++;
        
        // Check if we've completed all questions for this level
        if (currentQuestionIndex >= questions.size()) {
            // All questions completed for this level
            Toast.makeText(this, "Congratulations! You've completed all questions for this level!", Toast.LENGTH_LONG).show();
            
            // Return to HomeScreen
            Intent intent = new Intent(LearnItQuiz.this, HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            // Load next question
            loadQuestion();
        }
    }
    

    
    private void setupDropTarget() {
        codeBlockContainer.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            return true;
                        }
                        return false;
                        
                    case DragEvent.ACTION_DRAG_ENTERED:
                        if (dropTarget != null) {
                            GradientDrawable background = new GradientDrawable();
                            background.setCornerRadius(8);
                            background.setStroke(4, Color.YELLOW);
                            background.setColor(Color.parseColor("#333333"));
                            dropTarget.setBackground(background);
                        }
                        return true;
                        
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                        
                    case DragEvent.ACTION_DRAG_EXITED:
                        if (dropTarget != null) {
                            GradientDrawable background = new GradientDrawable();
                            background.setCornerRadius(8);
                            background.setStroke(2, Color.WHITE);
                            background.setColor(Color.parseColor("#333333"));
                            dropTarget.setBackground(background);
                        }
                        return true;
                        
                    case DragEvent.ACTION_DROP:
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        String draggedText = item.getText().toString();
                        
                        int draggedIndex = 0;
                        PersistableBundle extras = event.getClipData().getDescription().getExtras();
                        if (extras != null) {
                            draggedIndex = Integer.parseInt(extras.getString("index", "0"));
                        }
                        
                        if (dropTarget != null) {
                            dropTarget.setText(draggedText);
                            currentDraggedText = draggedText;
                            selectedAnswerIndex = -1; // Reset selectedAnswerIndex for drag and drop
                            Log.d("LearnItQuiz", "Drag and drop completed - Text: '" + draggedText + "', Index: " + draggedIndex);
                            updateChoiceButtonStyles();
                        }
                        return true;
                        
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (dropTarget != null && dropTarget.getText().toString().isEmpty()) {
                            GradientDrawable background = new GradientDrawable();
                            background.setCornerRadius(8);
                            background.setStroke(2, Color.WHITE);
                            background.setColor(Color.parseColor("#333333"));
                            dropTarget.setBackground(background);
                        }
                        return true;
                        
                    default:
                        return false;
                }
            }
        });
    }
    
    private void createDropTarget(String template) {
        if (dropTarget != null) {
            codeBlockContainer.removeView(dropTarget);
        }
        
        dropTarget = new TextView(this);
        dropTarget.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        dropTarget.setText(" ");
        dropTarget.setTextSize(16);
        dropTarget.setTextColor(getResources().getColor(android.R.color.white));
        dropTarget.setPadding(20, 10, 20, 10);
        dropTarget.setBackgroundResource(R.drawable.drag_drop_target_background);
        
        codeBlockContainer.addView(dropTarget);
        
        codeTemplate.setVisibility(View.VISIBLE);
        codeTemplate.setText(template);
    }
    
    private boolean startDrag(View view, int index) {
        try {
            String buttonText = ((Button) view).getText().toString();
            
            ClipData.Item item = new ClipData.Item(buttonText);
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData(buttonText, mimeTypes, item);
            
            PersistableBundle extras = dragData.getDescription().getExtras();
            if (extras == null) {
                extras = new PersistableBundle();
                dragData.getDescription().setExtras(extras);
            }
            extras.putString("index", String.valueOf(index));
            
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            
            boolean result;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = view.startDragAndDrop(dragData, shadowBuilder, null, 0);
            } else {
                result = view.startDrag(dragData, shadowBuilder, null, 0);
            }
            
            view.setBackgroundResource(R.drawable.selectable_button_background);
            ((Button) view).setTextColor(getResources().getColor(android.R.color.white));
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void disableBottomNavigation(CustomBottomNavigation bottomNavigation) {
        try {
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
            
            bottomNavigation.setAlpha(0.5f);
            bottomNavigation.setEnabled(false);
            
        } catch (Exception e) {
            // Handle any errors silently
        }
    }
    
    private void enableBottomNavigation(CustomBottomNavigation bottomNavigation) {
        try {
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
            
            bottomNavigation.setAlpha(1.0f);
            bottomNavigation.setEnabled(true);
            
            if (bottomNavigation != null) {
                bottomNavigation.setCurrentActivity("LearnItQuiz");
            }
            
        } catch (Exception e) {
            // Handle any errors silently
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        refreshLivesAndCoinsDisplay();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (livesManager != null) {
            livesManager.cleanup();
        }
    }
    
    @Override
    public void onBackPressed() {
        if (bottomNavigation != null) {
            enableBottomNavigation(bottomNavigation);
        }
        super.onBackPressed();
    }
    
    private void initializeLivesAndCoinsDisplay() {
        livesManager.getCurrentLives(new LivesManager.LivesCallback() {
            @Override
            public void onLivesUpdated(int currentLives, long nextRegenerationTime) {
                updateLivesDisplay(currentLives);
            }
            
            @Override
            public void onError(String error) {
                if (livesCountTextView != null) {
                    livesCountTextView.setText("0");
                }
            }
        });
        
        updateCoinsDisplay();
    }
    
    private void updateLivesDisplay(int currentLives) {
        if (livesCountTextView != null) {
            livesCountTextView.setText(String.valueOf(currentLives));
        }
    }
    
    private void updateCoinsDisplay() {
        // Load coins with instant display and background refresh
        userDataManager.getCoins(new UserDataManager.CoinsCallback() {
            @Override
            public void onCoinsLoaded(Long coins) {
                if (coinsCountTextView != null) {
                    coinsCountTextView.setText(String.valueOf(coins));
                }
            }
            
            @Override
            public void onError(String error) {
                if (coinsCountTextView != null) {
                    coinsCountTextView.setText("0");
                }
            }
        });
    }
    
    private void refreshLivesAndCoinsDisplay() {
        initializeLivesAndCoinsDisplay();
    }

    private boolean shouldTriggerSurpriseQuiz() {
        // Simple random trigger for demonstration.
        // In a real app, this would involve user data, progress, or a more sophisticated algorithm.
        // For now, let's assume a 20% chance of triggering a surprise quiz.
        return Math.random() < 0.2; // 20% chance
    }
    
    private void addCoinsToUser(int coinsToAdd) {
        // Get fresh data directly from Firestore to avoid cache issues
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("User Quiz Records")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long currentCoins = documentSnapshot.getLong("coins");
                        long newCoins = (currentCoins != null ? currentCoins : 0) + coinsToAdd;
                        
                        // Update Firestore with new total
                        firestore.collection("User Quiz Records")
                            .document(user.getUid())
                            .update("coins", newCoins)
                            .addOnSuccessListener(aVoid -> {
                                // Update cache
                                userDataManager.updateCoins(newCoins);
                                
                                                                 // Update display immediately with animation
                                 if (coinsCountTextView != null) {
                                     coinsCountTextView.setText(String.valueOf(newCoins));
                                     // Add a subtle animation to highlight the update
                                     coinsCountTextView.animate()
                                         .scaleX(1.1f)
                                         .scaleY(1.1f)
                                         .setDuration(150)
                                         .withEndAction(() -> {
                                             coinsCountTextView.animate()
                                                 .scaleX(1.0f)
                                                 .scaleY(1.0f)
                                                 .setDuration(150)
                                                 .start();
                                         })
                                         .start();
                                 }
                                 
                                 Log.d("LearnItQuiz", "Added " + coinsToAdd + " coins. Previous: " + currentCoins + ", New total: " + newCoins);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("LearnItQuiz", "Error updating coins in Firestore: " + e.getMessage());
                            });
                    } else {
                        // Document doesn't exist, create it with initial coins
                        long newCoins = coinsToAdd;
                        firestore.collection("User Quiz Records")
                            .document(user.getUid())
                            .set(new java.util.HashMap<String, Object>() {{
                                put("coins", newCoins);
                            }})
                            .addOnSuccessListener(aVoid -> {
                                // Update cache
                                userDataManager.updateCoins(newCoins);
                                
                                                                 // Update display immediately with animation
                                 if (coinsCountTextView != null) {
                                     coinsCountTextView.setText(String.valueOf(newCoins));
                                     // Add a subtle animation to highlight the update
                                     coinsCountTextView.animate()
                                         .scaleX(1.1f)
                                         .scaleY(1.1f)
                                         .setDuration(150)
                                         .withEndAction(() -> {
                                             coinsCountTextView.animate()
                                                 .scaleX(1.0f)
                                                 .scaleY(1.0f)
                                                 .setDuration(150)
                                                 .start();
                                         })
                                         .start();
                                 }
                                 
                                 Log.d("LearnItQuiz", "Created document and added " + coinsToAdd + " coins. New total: " + newCoins);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("LearnItQuiz", "Error creating document with coins: " + e.getMessage());
                            });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LearnItQuiz", "Error getting current coins from Firestore: " + e.getMessage());
                });
        } else {
            Log.e("LearnItQuiz", "User not authenticated");
        }
    }
    
    private void loseLife() {
        livesManager.getCurrentLives(new LivesManager.LivesCallback() {
            @Override
            public void onLivesUpdated(int currentLives, long nextRegenerationTime) {
                if (currentLives > 0) {
                    // Use a life
                    livesManager.useLife(new LivesManager.LivesCallback() {
                        @Override
                        public void onLivesUpdated(int newLives, long nextRegenerationTime) {
                            // Update display
                            updateLivesDisplay(newLives);
                            
                            // Check if user has no lives left
                            if (newLives <= 0) {
                                showNoLivesDialog();
                            }
                            
                            Log.d("LearnItQuiz", "Lost 1 life. Remaining lives: " + newLives);
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e("LearnItQuiz", "Error using life: " + error);
                        }
                    });
                } else {
                    // No lives left
                    showNoLivesDialog();
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e("LearnItQuiz", "Error getting current lives: " + error);
            }
        });
    }
    
    /**
     * Calculate coins earned based on score and difficulty level
     * @param score number of correct answers
     * @param difficulty difficulty level
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
    
    private void showNoLivesDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("No Lives Left")
               .setMessage("You have no lives remaining. You can purchase more lives in the shop or wait for them to regenerate.")
               .setPositiveButton("Go to Shop", new android.content.DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(android.content.DialogInterface dialog, int which) {
                       Intent intent = new Intent(LearnItQuiz.this, ShopInterface.class);
                       startActivity(intent);
                   }
               })
               .setNegativeButton("Go Home", new android.content.DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(android.content.DialogInterface dialog, int which) {
                       Intent intent = new Intent(LearnItQuiz.this, HomeScreen.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                       startActivity(intent);
                       finish();
                   }
               })
               .setCancelable(false)
               .show();
    }
}