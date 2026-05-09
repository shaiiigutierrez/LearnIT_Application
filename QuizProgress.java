package com.example.learnit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleView extends View {
    private List<Particle> particles;
    private Paint paint;
    private Random random;
    private int screenWidth, screenHeight;
    private boolean isAnimating = false;
    
    // Animation properties
    private int particleCount = ParticleConfig.DEFAULT_PARTICLE_COUNT;
    private static final int ANIMATION_DURATION = 8000; // 8 seconds

    public ParticleView(Context context) {
        super(context);
        init();
    }

    public ParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParticleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        particles = new ArrayList<>();
        paint = new Paint();
        paint.setAntiAlias(true);
        random = new Random();
        
        // Set background to transparent
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        createParticles();
    }

    private void createParticles() {
        particles.clear();
        
        for (int i = 0; i < particleCount; i++) {
            Particle particle = new Particle();
            particle.x = random.nextFloat() * screenWidth;
            particle.y = random.nextFloat() * screenHeight;
            particle.size = ParticleConfig.getRandomSize();
            particle.color = ParticleConfig.getRandomColor();
            particle.alpha = ParticleConfig.getRandomAlpha();
            particle.speedX = (random.nextFloat() - 0.5f) * ParticleConfig.getRandomSpeed();
            particle.speedY = (random.nextFloat() - 0.5f) * ParticleConfig.getRandomSpeed();
            particle.pulseSpeed = ParticleConfig.getRandomPulseSpeed();
            particle.pulsePhase = random.nextFloat() * (float) (2 * Math.PI);
            
            particles.add(particle);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (particles.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        
        for (Particle particle : particles) {
            // Update particle position
            particle.x += particle.speedX;
            particle.y += particle.speedY;
            
            // Wrap around screen edges
            if (particle.x < 0) particle.x = screenWidth;
            if (particle.x > screenWidth) particle.x = 0;
            if (particle.y < 0) particle.y = screenHeight;
            if (particle.y > screenHeight) particle.y = 0;
            
            // Calculate pulsing effect
            float pulse = (float) (0.5f + 0.5f * Math.sin(currentTime * particle.pulseSpeed + particle.pulsePhase));
            int currentAlpha = (int) (particle.alpha * pulse);
            
            // Set paint properties
            paint.setColor(particle.color);
            paint.setAlpha(currentAlpha);
            
            // Draw particle as a glowing circle
            canvas.drawCircle(particle.x, particle.y, particle.size, paint);
            
            // Add a subtle glow effect
            paint.setAlpha(currentAlpha / 3);
            canvas.drawCircle(particle.x, particle.y, particle.size * 2, paint);
        }
        
        if (isAnimating) {
            invalidate();
        }
    }

    public void startAnimation() {
        isAnimating = true;
        invalidate();
    }

    public void stopAnimation() {
        isAnimating = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    public void setParticleCount(int count) {
        if (count != particleCount) {
            particleCount = count;
            createParticles();
        }
    }

    private static class Particle {
        float x, y;
        float size;
        int color;
        int alpha;
        float speedX, speedY;
        float pulseSpeed;
        float pulsePhase;
    }
}
