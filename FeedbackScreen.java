package com.example.learnit;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected ThemeManager themeManager;
    protected SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeManager = new ThemeManager(this);
        soundManager = SoundManager.getInstance(this);
        setContentView(getLayoutResourceId());
        applyTheme();
        soundManager.initialize(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        applyTheme();
        soundManager.resumeSound();
    }

    @Override
    protected void onPause() {
        super.onPause();

        soundManager.pause();
    }

    protected void applyTheme() {
        if (themeManager != null) {
            View rootView = findViewById(android.R.id.content);
            if (rootView != null) {
                boolean darkModeEnabled = themeManager.isDarkModeEnabled();
                if (darkModeEnabled == true) {
                    rootView.setSelected(true);
                    rootView.setBackgroundResource(R.drawable.background_dark);
                } else {
                    rootView.setSelected(false);
                    rootView.setBackgroundResource(R.drawable.background);
                }
            }
        }
    }

    protected abstract int getLayoutResourceId();
}
