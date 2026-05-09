package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.widget.SwitchCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileInterface extends BaseActivity {

    private SwitchCompat darkModeSwitch, soundSwitch;
    private FirebaseAuth auth;
    private Button btnLogOut;
    private TextView displayUserName;
    private FirebaseUser user;
    private UserDataManager userDataManager;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile_interface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        btnLogOut = findViewById(R.id.btnLogOut);
        displayUserName = findViewById(R.id.displayUserName);
        ImageButton editNameButton = findViewById(R.id.editNameButton);
        user = auth.getCurrentUser();
        userDataManager = UserDataManager.getInstance(this);

        if (user == null){
            Intent intent = new Intent(ProfileInterface.this, LogInScreen.class);
            startActivity(intent);
            finish();
        } else {
            // Load username with instant display and background refresh
            userDataManager.getUserName(new UserDataManager.UserDataCallback() {
                @Override
                public void onUserDataLoaded(String username) {
                    displayUserName.setText(username);
                }
                
                @Override
                public void onError(String error) {
                    displayUserName.setText("Error loading username");
                }
            });
        }

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDataManager.clearCache(); // Clear cache on logout
                auth.signOut();
                Intent intent = new Intent(ProfileInterface.this, LogInScreen.class);
                startActivity(intent);
            }
        });

        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileInterface.this);
                builder.setTitle("Edit Username");
                final EditText input = new EditText(ProfileInterface.this);
                input.setHint("Enter new username");
                builder.setView(input);
                builder.setPositiveButton("Save", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        String newUsername = input.getText().toString().trim();
                        if (newUsername.isEmpty() == false) {
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection("User Accounts")
                                .whereEqualTo("uid", user.getUid())
                                .limit(1)
                                .get()
                                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<com.google.firebase.firestore.QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(com.google.firebase.firestore.QuerySnapshot querySnapshot) {
                                        boolean isEmpty = querySnapshot.isEmpty();
                                        if (isEmpty == false) {
                                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                            document.getReference().update("username", newUsername)
                                                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(com.google.android.gms.tasks.Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            userDataManager.updateUsername(newUsername); // Update cache
                                                            displayUserName.setText(newUsername);
                                                        } else {
                                                            displayUserName.setText("Update failed");
                                                        }
                                                    }
                                                });
                                        } else {
                                            displayUserName.setText("User not found");
                                        }
                                    }
                                })
                                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        displayUserName.setText("Update failed");
                                    }
                                });
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        CustomBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setCurrentActivity("ProfileInterface");
        }

        ImageView feedbackButton = findViewById(R.id.feedbackButton);
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileInterface.this, FeedbackScreen.class);
                startActivity(intent);
            }
        });

        setupDarkModeSwitch();
        setupSoundSwitch();
    }

    private void setupDarkModeSwitch() {
        darkModeSwitch = findViewById(R.id.switchDarkMode);
        darkModeSwitch.setChecked(themeManager.isDarkModeEnabled());

        darkModeSwitch.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                themeManager.setDarkModeEnabled(isChecked);
                applyTheme();
                recreate();
            }
        });
    }

    private void setupSoundSwitch() {
        soundSwitch = findViewById(R.id.switchSound);

        soundSwitch.setOnCheckedChangeListener(null);
        soundSwitch.setChecked(soundManager.isSoundOn());

        soundSwitch.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    soundManager.playSound();
                } else {
                    soundManager.stopSound();
                }
            }
        });
    }
}
