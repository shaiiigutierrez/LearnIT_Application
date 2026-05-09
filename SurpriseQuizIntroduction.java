package com.example.learnit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;

public class ShopInterface extends BaseActivity {

    private LivesManager livesManager;
    private TextView coinsTextView;
    private UserDataManager userDataManager;
    private ImageView life1, life2, life3, life4, life5;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_shop_interface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        livesManager = new LivesManager(this);
        userDataManager = UserDataManager.getInstance(this);
        
        // Set up cache update callback for lives changes
        livesManager.setCacheUpdateCallback(new LivesManager.CacheUpdateCallback() {
            @Override
            public void onLivesChanged(int newLivesCount) {
                userDataManager.updateLives(newLivesCount);
            }
        });
        
        CustomBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setCurrentActivity("ShopInterface");
        }
        
        initializeShopUI();
    }
    
    private void initializeShopUI() {
        coinsTextView = findViewById(R.id.coinsCount);
        life1 = findViewById(R.id.life1);
        life2 = findViewById(R.id.life2);
        life3 = findViewById(R.id.life3);
        life4 = findViewById(R.id.life4);
        life5 = findViewById(R.id.life5);
        
        setupPurchaseButtons();
        updateCoinsDisplay();
        updateLivesDisplay();
    }
    
    private void setupPurchaseButtons() {
        Button btnBuyOne = findViewById(R.id.btnBuyOne);
        if (btnBuyOne != null) {
            btnBuyOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    purchaseLife(1, 50);
                }
            });
        }
        
        Button btnBuyFive = findViewById(R.id.btnBuyFive);
        if (btnBuyFive != null) {
            btnBuyFive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    purchaseLife(5, 200);
                }
            });
        }
    }
    
    private void purchaseLife(int livesToPurchase, int cost) {
        livesManager.purchaseLives(new LivesManager.PurchaseCallback() {
            @Override
            public void onPurchaseSuccess(int newLivesCount, int remainingCoins) {
                Toast.makeText(ShopInterface.this, 
                    "Successfully purchased " + livesToPurchase + " life/lives!", 
                    Toast.LENGTH_SHORT).show();
                userDataManager.updateCoins((long) remainingCoins); // Update coins cache
                userDataManager.updateLives(newLivesCount); // Update lives cache
                updateCoinsDisplay();
                updateLivesDisplay(); // Update lives display after purchase
            }
            
            @Override
            public void onPurchaseFailed(String error) {
                Toast.makeText(ShopInterface.this, 
                    "Purchase failed: " + error, 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateCoinsDisplay() {
        // Load coins with instant display and background refresh
        userDataManager.getCoins(new UserDataManager.CoinsCallback() {
            @Override
            public void onCoinsLoaded(Long coins) {
                if (coinsTextView != null) {
                    coinsTextView.setText(String.valueOf(coins));
                }
            }
            
            @Override
            public void onError(String error) {
                if (coinsTextView != null) {
                    coinsTextView.setText("0");
                }
            }
        });
    }
    
    private void updateLivesDisplay() {
        // Load lives with instant display and background refresh
        userDataManager.getLives(new UserDataManager.LivesCallback() {
            @Override
            public void onLivesLoaded(Integer lives) {
                // Update life indicators based on current lives count
                if (life1 != null) {
                    life1.setVisibility(lives >= 1 ? View.VISIBLE : View.INVISIBLE);
                }
                if (life2 != null) {
                    life2.setVisibility(lives >= 2 ? View.VISIBLE : View.INVISIBLE);
                }
                if (life3 != null) {
                    life3.setVisibility(lives >= 3 ? View.VISIBLE : View.INVISIBLE);
                }
                if (life4 != null) {
                    life4.setVisibility(lives >= 4 ? View.VISIBLE : View.INVISIBLE);
                }
                if (life5 != null) {
                    life5.setVisibility(lives >= 5 ? View.VISIBLE : View.INVISIBLE);
                }
            }
            
            @Override
            public void onError(String error) {
                // Hide all lives on error
                if (life1 != null) life1.setVisibility(View.INVISIBLE);
                if (life2 != null) life2.setVisibility(View.INVISIBLE);
                if (life3 != null) life3.setVisibility(View.INVISIBLE);
                if (life4 != null) life4.setVisibility(View.INVISIBLE);
                if (life5 != null) life5.setVisibility(View.INVISIBLE);
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateCoinsDisplay();
        updateLivesDisplay(); // Update lives display when activity resumes
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (livesManager != null) {
            livesManager.cleanup();
        }
    }
} 