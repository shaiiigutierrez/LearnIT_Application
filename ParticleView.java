package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogInScreen extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInScreen.this, SignInScreen.class);
                startActivity(intent);
                finish();
            }
        });

        Button btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInScreen.this, SignUpScreen.class);
                startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        // Resume particle animation
        ParticleView particleView = findViewById(R.id.particleBackground);
        if (particleView != null) {
            particleView.startAnimation();
        }
    }
}