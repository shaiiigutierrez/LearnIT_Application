package com.example.learnit;

import android.graphics.Color;

/**
 * Configuration class for particle effects
 * This allows easy customization of particle appearance and behavior
 */
public class ParticleConfig {
    
    // Particle count settings
    public static final int DEFAULT_PARTICLE_COUNT = 50;
    public static final int MIN_PARTICLE_COUNT = 20;
    public static final int MAX_PARTICLE_COUNT = 100;
    
    // Particle size settings
    public static final int MIN_PARTICLE_SIZE = 8;
    public static final int MAX_PARTICLE_SIZE = 15;
    
    // Animation speed settings
    public static final float MIN_SPEED = 0.2f;
    public static final float MAX_SPEED = 0.8f;
    
    // Pulse effect settings
    public static final float MIN_PULSE_SPEED = 0.02f;
    public static final float MAX_PULSE_SPEED = 0.05f;
    
    // Color palette for particles
    public static final int[] PARTICLE_COLORS = {
        Color.parseColor("#00FFFF"), // Bright Cyan (main color from image)
        Color.parseColor("#00E5E5"), // Slightly darker cyan
        Color.parseColor("#00CCCC"), // Medium cyan
        Color.parseColor("#00B3B3"), // Darker cyan
        Color.parseColor("#00FFFF"), // Bright Cyan (repeated for more variety)
        Color.parseColor("#00E5E5"), // Slightly darker cyan (repeated)
        Color.parseColor("#00CCCC"), // Medium cyan (repeated)
        Color.parseColor("#00B3B3")  // Darker cyan (repeated)
    };
    
    // Alpha settings
    public static final int MIN_ALPHA = 200;
    public static final int MAX_ALPHA = 255;
    
    /**
     * Get a random color from the particle color palette
     */
    public static int getRandomColor() {
        return PARTICLE_COLORS[(int) (Math.random() * PARTICLE_COLORS.length)];
    }
    
    /**
     * Get a random particle size between min and max
     */
    public static float getRandomSize() {
        return MIN_PARTICLE_SIZE + (float) (Math.random() * (MAX_PARTICLE_SIZE - MIN_PARTICLE_SIZE));
    }
    
    /**
     * Get a random speed between min and max
     */
    public static float getRandomSpeed() {
        return MIN_SPEED + (float) (Math.random() * (MAX_SPEED - MIN_SPEED));
    }
    
    /**
     * Get a random pulse speed between min and max
     */
    public static float getRandomPulseSpeed() {
        return MIN_PULSE_SPEED + (float) (Math.random() * (MAX_PULSE_SPEED - MIN_PULSE_SPEED));
    }
    
    /**
     * Get a random alpha value between min and max
     */
    public static int getRandomAlpha() {
        return MIN_ALPHA + (int) (Math.random() * (MAX_ALPHA - MIN_ALPHA));
    }
}
