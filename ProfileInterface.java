package com.example.learnit;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NavigationHelper {
    
    public static void setupNavigationButtons(LinearLayout btnShop, LinearLayout btnHome, LinearLayout btnProfile, String currentActivity) {
        resetButtonSize(btnShop);
        resetButtonSize(btnHome);
        resetButtonSize(btnProfile);
        
        if (currentActivity.equals("ShopInterface")) {
            setSelectedButtonSize(btnShop);
        } else if (currentActivity.equals("LanguageSelection")) {
            setSelectedButtonSize(btnHome);
        } else if (currentActivity.equals("ProfileInterface")) {
            setSelectedButtonSize(btnProfile);
        }
    }
    
    private static void setSelectedButtonSize(LinearLayout button) {
        ImageView imageView = (ImageView) button.getChildAt(0);
        if (imageView != null) {
            imageView.setScaleX(1.2f);
            imageView.setScaleY(1.2f);
        }
    }
    
    private static void resetButtonSize(LinearLayout button) {
        ImageView imageView = (ImageView) button.getChildAt(0);
        if (imageView != null) {
            imageView.setScaleX(1.0f);
            imageView.setScaleY(1.0f);
        }
    }
} 