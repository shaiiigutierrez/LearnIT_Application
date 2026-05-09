package com.example.learnit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CodeEditorView extends LinearLayout {
    private EditText codeEditText;
    private LineNumberView lineNumberView;
    private Paint lineNumberPaint;
    private int lineNumberWidth = 60;
    private int lineHeight = 0;
    private int textSize = 14;
    private int lineNumberColor = Color.rgb(128, 128, 128);
    private int backgroundColor = Color.WHITE;
    private int textColor = Color.BLACK;

    public CodeEditorView(Context context) {
        super(context);
        init();
    }

    public CodeEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CodeEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        try {
            setOrientation(HORIZONTAL);
            setBackgroundColor(backgroundColor);

            // Create line number view
            lineNumberView = new LineNumberView(getContext());
            lineNumberView.setBackgroundColor(Color.rgb(245, 245, 245));
            LayoutParams lineNumberParams = new LayoutParams(lineNumberWidth, LayoutParams.MATCH_PARENT);
            addView(lineNumberView, lineNumberParams);

            // Create code edit text
            codeEditText = new EditText(getContext());
            codeEditText.setBackgroundColor(backgroundColor);
            codeEditText.setTextColor(textColor);
            codeEditText.setTextSize(textSize);
            codeEditText.setGravity(Gravity.TOP | Gravity.START);
            codeEditText.setPadding(10, 10, 10, 10);
            codeEditText.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            codeEditText.setHorizontallyScrolling(true);
            codeEditText.setTypeface(android.graphics.Typeface.MONOSPACE);
            codeEditText.setCursorVisible(true);
            codeEditText.setHint("Input code here...");
            codeEditText.setHintTextColor(Color.rgb(150, 150, 150));

            LayoutParams editTextParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(codeEditText, editTextParams);

            // Add text change listener to update line numbers
            codeEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        lineNumberView.invalidate();
                    } catch (Exception e) {
                        // Ignore drawing errors
                    }
                }
            });

            // Initialize paint for line numbers
            lineNumberPaint = new Paint();
            lineNumberPaint.setColor(lineNumberColor);
            lineNumberPaint.setTextSize(textSize);
            lineNumberPaint.setTypeface(android.graphics.Typeface.MONOSPACE);
            lineNumberPaint.setAntiAlias(true);
        } catch (Exception e) {
            // Fallback to simple EditText if custom view fails
            fallbackToSimpleEditText();
        }
    }

    private void fallbackToSimpleEditText() {
        removeAllViews();
        codeEditText = new EditText(getContext());
        codeEditText.setBackgroundColor(backgroundColor);
        codeEditText.setTextColor(textColor);
        codeEditText.setTextSize(textSize);
        codeEditText.setGravity(Gravity.TOP | Gravity.START);
        codeEditText.setPadding(10, 10, 10, 10);
        codeEditText.setTypeface(android.graphics.Typeface.MONOSPACE);
        codeEditText.setHint("Input code here...");
        codeEditText.setHintTextColor(Color.rgb(150, 150, 150));

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(codeEditText, params);
    }

    public String getCode() {
        return codeEditText != null ? codeEditText.getText().toString() : "";
    }

    public void setCode(String code) {
        if (codeEditText != null) {
            codeEditText.setText(code);
        }
    }

    public void setTextSize(int size) {
        textSize = size;
        if (codeEditText != null) {
            codeEditText.setTextSize(size);
        }
        if (lineNumberPaint != null) {
            lineNumberPaint.setTextSize(size);
        }
        if (lineNumberView != null) {
            lineNumberView.invalidate();
        }
    }

    public void setLineNumberColor(int color) {
        lineNumberColor = color;
        if (lineNumberPaint != null) {
            lineNumberPaint.setColor(color);
        }
        if (lineNumberView != null) {
            lineNumberView.invalidate();
        }
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
        setBackgroundColor(color);
        if (lineNumberView != null) {
            lineNumberView.setBackgroundColor(Color.rgb(245, 245, 245));
        }
        if (codeEditText != null) {
            codeEditText.setBackgroundColor(color);
        }
    }

    public void setTextColor(int color) {
        textColor = color;
        if (codeEditText != null) {
            codeEditText.setTextColor(color);
        }
    }

    private class LineNumberView extends View {
        public LineNumberView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            try {
                super.onDraw(canvas);

                if (codeEditText == null || lineNumberPaint == null) {
                    return;
                }

                String text = codeEditText.getText().toString();
                String[] lines = text.split("\n", -1);

                if (lines.length == 0) {
                    lines = new String[]{""};
                }

                // Calculate line height
                Rect bounds = new Rect();
                lineNumberPaint.getTextBounds("0", 0, 1, bounds);
                lineHeight = bounds.height() + 8;

                // Draw line numbers
                for (int i = 0; i < lines.length; i++) {
                    String lineNumber = String.valueOf(i + 1);
                    float x = lineNumberWidth - lineNumberPaint.measureText(lineNumber) - 10;
                    float y = (i + 1) * lineHeight - 5;

                    canvas.drawText(lineNumber, x, y, lineNumberPaint);
                }
            } catch (Exception e) {
                // Ignore drawing errors
            }
        }
    }
}
