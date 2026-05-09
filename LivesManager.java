package com.example.learnit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class IntroductionScreen extends BaseActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button btnNext;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_introduction_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewPager = findViewById(R.id.introViewPager);
        tabLayout = findViewById(R.id.pageIndicator);

        IntroPagerAdapter adapter = new IntroPagerAdapter();
        viewPager.setAdapter(adapter);

        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(TabLayout.Tab tab, int position) {
                tab.view.setBackgroundResource(R.drawable.tab_selector_clean_circles);
            }
        });
        mediator.attach();

        tabLayout.setSelectedTabIndicator(null);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        
        int tabCount = tabLayout.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View tabView = ((View) tabLayout.getChildAt(0)).findViewById(android.R.id.tabs);
                if (tabView != null) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    params.setMargins(8, 0, 8, 0);
                    tabView.setLayoutParams(params);
                }
            }
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
        
        // Initialize and start particle effect
        initializeParticleEffect();
    }

    /**
     * Initialize and start the particle effect animation
     */
    private void initializeParticleEffect() {
        ParticleView particleView = findViewById(R.id.particleBackground);
        if (particleView != null) {
            particleView.startAnimation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop particle animation to save battery
        ParticleView particleView = findViewById(R.id.particleBackground);
        if (particleView != null) {
            particleView.stopAnimation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume particle animation
        ParticleView particleView = findViewById(R.id.particleBackground);
        if (particleView != null) {
            particleView.startAnimation();
        }
    }

    private class IntroPagerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<IntroPagerAdapter.IntroViewHolder> {

        @Override
        public IntroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new IntroViewHolder(view);
        }

        @Override
        public void onBindViewHolder(IntroViewHolder holder, int position) {
            if (position == 2) {
                btnNext = holder.itemView.findViewById(R.id.btnNext);
                if (btnNext != null) {
                    btnNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(IntroductionScreen.this, HomeScreen.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return R.layout.slide_intro_1;
            } else if (position == 1) {
                return R.layout.slide_intro_2;
            } else if (position == 2) {
                return R.layout.slide_intro_3;
            } else {
                return R.layout.slide_intro_1;
            }
        }

        class IntroViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            IntroViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}