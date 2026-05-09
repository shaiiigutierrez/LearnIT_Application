package com.example.learnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class SoundManager {
    private static SoundManager instance;
    private MediaPlayer mediaPlayer;
    private boolean isSoundOn = false;
    private boolean isInitialized = false;
    private boolean wasPlayingBeforePause = false;
    private SharedPreferences preferences;
    private Context context;
    
    private static final String PREF_NAME = "LearnITPrefs";
    private static final String KEY_SOUND_ON = "sound_on";

    private SoundManager(Context context) {
        this.context = context.getApplicationContext();
        preferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        isSoundOn = preferences.getBoolean(KEY_SOUND_ON, false);
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    public void initialize(Context context) {
        if (isInitialized == false) {
            // Release any existing MediaPlayer to prevent memory leaks
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            
            mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.sample_sound_effects);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                isInitialized = true;

                if (isSoundOn == true) {
                    mediaPlayer.start();
                }
            }
        }
    }

    public void playSound() {
        isSoundOn = true;
        savePreference();

        if (mediaPlayer != null && isMediaPlayerValid()) {
            boolean isPlaying = mediaPlayer.isPlaying();
            if (isPlaying == false) {
                mediaPlayer.start();
            }
        }
    }

    public void resumeSound() {
        // Ensure MediaPlayer is initialized if needed
        if (isSoundOn && !isInitialized) {
            // This shouldn't happen with singleton, but just in case
            return;
        }
        
        // Only resume if sound is enabled and we have a valid MediaPlayer
        if (isSoundOn && mediaPlayer != null && wasPlayingBeforePause && isMediaPlayerValid()) {
            try {
                boolean isPlaying = mediaPlayer.isPlaying();
                if (isPlaying == false) {
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                // If there's an error, reset the state
                wasPlayingBeforePause = false;
            }
        }
    }

    public void stopSound() {
        if (mediaPlayer != null && isMediaPlayerValid()) {
            boolean isPlaying = mediaPlayer.isPlaying();
            if (isPlaying == true) {
                mediaPlayer.pause();
            }
        }
        isSoundOn = false;
        wasPlayingBeforePause = false;
        savePreference();
    }

    public void pause() {
        if (mediaPlayer != null && isMediaPlayerValid()) {
            try {
                boolean isPlaying = mediaPlayer.isPlaying();
                wasPlayingBeforePause = isPlaying;
                if (isPlaying == true) {
                    mediaPlayer.pause();
                }
            } catch (Exception e) {
                // If there's an error, reset the state
                wasPlayingBeforePause = false;
            }
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isInitialized = false;
            wasPlayingBeforePause = false;
        }
    }

    public boolean isSoundOn() {
        return isSoundOn;
    }

    private void savePreference() {
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_SOUND_ON, isSoundOn);
            editor.apply();
        }
    }

    private boolean isMediaPlayerValid() {
        try {
            return mediaPlayer != null && mediaPlayer.isPlaying() || !mediaPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }
}
