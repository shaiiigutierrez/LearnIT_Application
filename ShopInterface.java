package com.example.learnit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuizCompiler extends AppCompatActivity {
    private LineNumberEditText codeEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_compiler);

        // Initialize views
        codeEditor = findViewById(R.id.codeEditor);
        Button submitButton = findViewById(R.id.submitButton);

        // Configure dark theme colors
        codeEditor.setLineNumberColor(android.graphics.Color.rgb(180, 180, 180));
        codeEditor.setLineNumberBackgroundColor(android.graphics.Color.rgb(30, 30, 30));
        codeEditor.setLineSeparatorColor(android.graphics.Color.rgb(60, 60, 60));

        // Set up submit button
        submitButton.setOnClickListener(v -> {
            String code = codeEditor.getText().toString();
            if (code.trim().isEmpty()) {
                Toast.makeText(this, "Please enter some code", Toast.LENGTH_SHORT).show();
            } else {
                // Here you can add your logic to process the submitted code
                Toast.makeText(this, "Code submitted successfully!", Toast.LENGTH_SHORT).show();
                // You can add your code processing logic here
                // For example, send to a compiler service, save to database, etc.
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}