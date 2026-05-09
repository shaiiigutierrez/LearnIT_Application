package com.example.learnit;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class CustomBottomNavigation extends LinearLayout {
    
    private LinearLayout btnShop, btnHome, btnProfile;
    private TextView tvShop, tvHome, tvProfile;
    private ImageView imgShop, imgLanguages, imgProfile;
    private Context context;
    private String currentActivity = "";
    
    public CustomBottomNavigation(Context context) {
        super(context);
        this.context = context;
        init();
    }
    
    public CustomBottomNavigation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }
    
    public CustomBottomNavigation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }
    
    private void init() {
        LayoutInflater.from(context).inflate(R.layout.custom_bottom_navigation, this, true);
        
        btnShop = findViewById(R.id.btnShop);
        btnHome = findViewById(R.id.btnHome);
        btnProfile = findViewById(R.id.btnProfile);
        
        tvShop = findViewById(R.id.tvShop);
        tvHome = findViewById(R.id.tvHome);
        tvProfile = findViewById(R.id.tvProfile);
        
        if (btnShop != null) {
            imgShop = (ImageView) btnShop.getChildAt(0);
        }
        if (btnHome != null) {
            imgLanguages = (ImageView) btnHome.getChildAt(0);
        }
        if (btnProfile != null) {
            imgProfile = (ImageView) btnProfile.getChildAt(0);
        }
        
        setupClickListeners();
    }
    
    private void setupClickListeners() {
        if (btnShop != null) {
            btnShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShopInterface.class);
                    context.startActivity(intent);
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).overridePendingTransition(0, 0);
                        ((android.app.Activity) context).finish();
                    }
                }
            });
        }

        if (btnHome != null) {
            btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HomeScreen.class);
                    context.startActivity(intent);
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).overridePendingTransition(0, 0);
                        ((android.app.Activity) context).finish();
                    }
                }
            });
        }
        
        if (btnProfile != null) {
            btnProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileInterface.class);
                    context.startActivity(intent);
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).overridePendingTransition(0, 0);
                        ((android.app.Activity) context).finish();
                    }
                }
            });
        }
    }
    
    public void setCurrentActivity(String activityName) {
        this.currentActivity = activityName;
        updateSelectedState();
    }
    
    public void reinitializeClickListeners() {
        setupClickListeners();
    }
    
    private void updateSelectedState() {
        if (btnShop != null) btnShop.setSelected(false);
        if (btnHome != null) btnHome.setSelected(false);
        if (btnProfile != null) btnProfile.setSelected(false);
        
        if (imgShop != null) {
            imgShop.setScaleX(1.0f);
            imgShop.setScaleY(1.0f);
        }
        if (imgLanguages != null) {
            imgLanguages.setScaleX(1.0f);
            imgLanguages.setScaleY(1.0f);
        }
        if (imgProfile != null) {
            imgProfile.setScaleX(1.0f);
            imgProfile.setScaleY(1.0f);
        }
        
        if (tvShop != null) {
            tvShop.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        if (tvHome != null) {
            tvHome.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        if (tvProfile != null) {
            tvProfile.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        
        if (currentActivity.equals("ShopInterface")) {
            if (btnShop != null) btnShop.setSelected(true);
            if (tvShop != null) {
                tvShop.setTextColor(getResources().getColor(android.R.color.white));
            }
            if (imgShop != null) {
                imgShop.setScaleX(1.2f);
                imgShop.setScaleY(1.2f);
            }
        } else if (currentActivity.equals("HomeScreen")) {
            if (btnHome != null) btnHome.setSelected(true);
            if (tvHome != null) {
                tvHome.setTextColor(getResources().getColor(android.R.color.white));
            }
            if (imgLanguages != null) {
                imgLanguages.setScaleX(1.2f);
                imgLanguages.setScaleY(1.2f);
            }
        } else if (currentActivity.equals("LanguageSelection")) {
            if (btnHome != null) btnHome.setSelected(true);
            if (tvHome != null) {
                tvHome.setTextColor(getResources().getColor(android.R.color.white));
            }
            if (imgLanguages != null) {
                imgLanguages.setScaleX(1.2f);
                imgLanguages.setScaleY(1.2f);
            }
        } else if (currentActivity.equals("ProfileInterface")) {
            if (btnProfile != null) btnProfile.setSelected(true);
            if (tvProfile != null) {
                tvProfile.setTextColor(getResources().getColor(android.R.color.white));
            }
            if (imgProfile != null) {
                imgProfile.setScaleX(1.2f);
                imgProfile.setScaleY(1.2f);
            }
        }
    }
}