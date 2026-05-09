package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ReviewMistake extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_review_mistake;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewMistake.this, HomeScreen.class);
                startActivity(intent);
            }
        });
    }
}