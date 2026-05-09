package com.example.learnit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.EditText;

public class LineNumberEditText extends EditText {
    private Paint lineNumberPaint;
    private Paint lineSeparatorPaint;
    private int lineNumberWidth = 60;
    private int lineNumberColor = Color.rgb(180, 180, 180); // Light grey for dark theme
    private int lineNumberBackgroundColor = Color.rgb(30, 30, 30); // Dark background
    private int lineSeparatorColor = Color.rgb(60, 60, 60); // Darker grey for separators
    private int backgroundColor = Color.rgb(25, 25, 35); // Dark blue-grey background
    private int textColor = Color.WHITE; // White text for dark theme

    // Syntax highlighting colors
    private int keywordColor = Color.rgb(255, 182, 193); // Pink/magenta for keywords
    private int stringColor = Color.rgb(144, 238, 144); // Bright green for strings
    private int commentColor = Color.rgb(173, 216, 230); // Light cyan for comments

    // Java keywords for syntax highlighting
    private final String[] javaKeywords = {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private boolean isHighlighting = false;

    public LineNumberEditText(Context context) {
        super(context);
        init();
    }

    public LineNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        try {
            // Set up the paint for line numbers
            lineNumberPaint = new Paint();
            lineNumberPaint.setColor(lineNumberColor);
            lineNumberPaint.setTextSize(getTextSize());
            lineNumberPaint.setTypeface(android.graphics.Typeface.MONOSPACE);
            lineNumberPaint.setAntiAlias(true);

            // Set up the paint for line separators
            lineSeparatorPaint = new Paint();
            lineSeparatorPaint.setColor(lineSeparatorColor);
            lineSeparatorPaint.setStrokeWidth(1f);
            lineSeparatorPaint.setStyle(Paint.Style.STROKE);

            // Set dark theme colors
            setBackgroundColor(backgroundColor);
            setTextColor(textColor);

            // Set padding to make room for line numbers
            setPadding(lineNumberWidth + 10, getPaddingTop(), getPaddingRight(), getPaddingBottom());

            // Add text change listener to redraw line numbers and apply syntax highlighting
            addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        invalidate();
                        // Apply syntax highlighting immediately
                        if (!isHighlighting && s != null && s.length() > 0) {
                            post(() -> safeApplySyntaxHighlighting(s));
                        }
                    } catch (Exception e) {
                        // Ignore drawing errors
                    }
                }
            });
        } catch (Exception e) {
            // Fallback to simple EditText if initialization fails
            fallbackToSimpleEditText();
        }
    }

    private void fallbackToSimpleEditText() {
        setBackgroundColor(backgroundColor);
        setTextColor(textColor);
        setTypeface(android.graphics.Typeface.MONOSPACE);
    }

    private void safeApplySyntaxHighlighting(Editable text) {
        if (isHighlighting || text == null) return;

        try {
            isHighlighting = true;

            // Store current cursor position
            int cursorPosition = getSelectionStart();

            // Get current text content
            String originalText = text.toString();
            if (originalText.isEmpty()) return;

            // Create a copy for highlighting
            SpannableStringBuilder builder = new SpannableStringBuilder(originalText);

            // Apply highlighting
            applyKeywordHighlighting(builder);
            applyStringHighlighting(builder);
            applyCommentHighlighting(builder);

            // Apply highlighting immediately
            text.replace(0, text.length(), builder);

            // Restore cursor position
            if (cursorPosition <= text.length()) {
                setSelection(cursorPosition);
            }
        } catch (Exception e) {
            // Ignore highlighting errors
        } finally {
            isHighlighting = false;
        }
    }

    private void applyKeywordHighlighting(SpannableStringBuilder builder) {
        try {
            String text = builder.toString();
            for (String keyword : javaKeywords) {
                int index = 0;
                while ((index = text.indexOf(keyword, index)) != -1) {
                    // Check word boundaries
                    boolean isWordStart = index == 0 || !Character.isLetterOrDigit(text.charAt(index - 1));
                    boolean isWordEnd = index + keyword.length() >= text.length() ||
                            !Character.isLetterOrDigit(text.charAt(index + keyword.length()));

                    if (isWordStart && isWordEnd) {
                        builder.setSpan(new ForegroundColorSpan(keywordColor),
                                index, index + keyword.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    index += keyword.length();
                }
            }
        } catch (Exception e) {
            // Ignore keyword highlighting errors
        }
    }

    private void applyStringHighlighting(SpannableStringBuilder builder) {
        try {
            String text = builder.toString();
            int start = 0;
            while ((start = text.indexOf("\"", start)) != -1) {
                int end = text.indexOf("\"", start + 1);
                if (end != -1) {
                    builder.setSpan(new ForegroundColorSpan(stringColor),
                            start, end + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = end + 1;
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            // Ignore string highlighting errors
        }
    }

    private void applyCommentHighlighting(SpannableStringBuilder builder) {
        try {
            String text = builder.toString();
            int start = 0;
            while ((start = text.indexOf("//", start)) != -1) {
                int end = text.indexOf("\n", start);
                if (end == -1) end = text.length();
                builder.setSpan(new ForegroundColorSpan(commentColor),
                        start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = end;
            }
        } catch (Exception e) {
            // Ignore comment highlighting errors
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            // Draw line number background
            canvas.drawColor(lineNumberBackgroundColor);

            // Save canvas state
            canvas.save();

            // Clip the line number area
            canvas.clipRect(0, 0, lineNumberWidth, getHeight());

            // Get the layout for proper line positioning
            Layout layout = getLayout();
            if (layout != null) {
                int lineCount = layout.getLineCount();

                // Draw line numbers using layout information
                for (int i = 0; i < lineCount; i++) {
                    String lineNumber = String.valueOf(i + 1);

                    // Get the baseline position for this line
                    int baseline = layout.getLineBaseline(i);

                    // Calculate x position (left-aligned in the line number area)
                    float x = 10;

                    // Use the actual baseline from the layout
                    float y = baseline + getPaddingTop();

                    canvas.drawText(lineNumber, x, y, lineNumberPaint);
                }
            } else {
                // Fallback if layout is not available
                String text = getText().toString();
                String[] lines = text.split("\n", -1);

                if (lines.length == 0) {
                    lines = new String[]{""};
                }

                // Get line height
                Rect bounds = new Rect();
                lineNumberPaint.getTextBounds("0", 0, 1, bounds);
                int lineHeight = bounds.height() + 8;

                // Calculate text baseline offset
                int baselineOffset = getPaddingTop() + bounds.height() / 2;

                // Draw line numbers
                for (int i = 0; i < lines.length; i++) {
                    String lineNumber = String.valueOf(i + 1);
                    float x = 10;
                    float y = baselineOffset + (i * lineHeight);

                    canvas.drawText(lineNumber, x, y, lineNumberPaint);
                }
            }

            // Restore canvas state
            canvas.restore();

            // Draw the main text content
            super.onDraw(canvas);

            // Draw horizontal line separators
            drawLineSeparators(canvas, layout);
        } catch (Exception e) {
            // If drawing fails, just draw the normal text
            super.onDraw(canvas);
        }
    }

    private void drawLineSeparators(Canvas canvas, Layout layout) {
        try {
            if (layout != null) {
                int lineCount = layout.getLineCount();

                for (int i = 0; i < lineCount - 1; i++) {
                    // Get the bottom position of the current line
                    int lineBottom = layout.getLineBottom(i);

                    // Draw horizontal line separator
                    float y = lineBottom + getPaddingTop();
                    canvas.drawLine(lineNumberWidth, y, getWidth(), y, lineSeparatorPaint);
                }
            } else {
                // Fallback if layout is not available
                String text = getText().toString();
                String[] lines = text.split("\n", -1);

                if (lines.length > 1) {
                    Rect bounds = new Rect();
                    lineNumberPaint.getTextBounds("0", 0, 1, bounds);
                    int lineHeight = bounds.height() + 8;
                    int baselineOffset = getPaddingTop() + bounds.height() / 2;

                    for (int i = 0; i < lines.length - 1; i++) {
                        float y = baselineOffset + ((i + 1) * lineHeight) + (lineHeight / 2);
                        canvas.drawLine(lineNumberWidth, y, getWidth(), y, lineSeparatorPaint);
                    }
                }
            }
        } catch (Exception e) {
            // Ignore separator drawing errors
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        try {
            invalidate();
        } catch (Exception e) {
            // Ignore invalidation errors
        }
    }

    public void setLineNumberColor(int color) {
        lineNumberColor = color;
        if (lineNumberPaint != null) {
            lineNumberPaint.setColor(color);
        }
        try {
            invalidate();
        } catch (Exception e) {
            // Ignore invalidation errors
        }
    }

    public void setLineNumberBackgroundColor(int color) {
        lineNumberBackgroundColor = color;
        try {
            invalidate();
        } catch (Exception e) {
            // Ignore invalidation errors
        }
    }

    public void setLineSeparatorColor(int color) {
        lineSeparatorColor = color;
        if (lineSeparatorPaint != null) {
            lineSeparatorPaint.setColor(color);
        }
        try {
            invalidate();
        } catch (Exception e) {
            // Ignore invalidation errors
        }
    }

    public void setLineNumberWidth(int width) {
        lineNumberWidth = width;
        setPadding(lineNumberWidth + 10, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        try {
            invalidate();
        } catch (Exception e) {
            // Ignore invalidation errors
        }
    }
}
