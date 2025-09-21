package com.farah.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ReelsActivity extends AppCompatActivity {

    private ViewPager2 viewPagerReels;
    private ReelsAdapter reelsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels);

        viewPagerReels = findViewById(R.id.viewPagerReels);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // ✅ بيانات الريلز مع أرقام عشوائية للكومنت واللايك
        List<ReelItem> reelList = new ArrayList<>();
        Random random = new Random();

        reelList.add(new ReelItem(
                R.raw.r1,
                "Chicken Burger",
                "@Burger House",
                1000 + random.nextInt(5000), // likes
                50 + random.nextInt(200),    // comments
                Arrays.asList(
                        "The burger is delicious 🔥",
                        "Really tasty 😍",
                        "One of the best I’ve had 👌"
                )
        ));

        reelList.add(new ReelItem(
                R.raw.r2,
                "Pasta Carbonara",
                "@Italian Corner",
                1000 + random.nextInt(5000),
                50 + random.nextInt(200),
                Arrays.asList(
                        "The pasta is amazing 🍝",
                        "Great flavor 😋",
                        "Highly recommended 🤩"
                )
        ));

        reelList.add(new ReelItem(
                R.raw.r3,
                "Pizza Margherita",
                "@Mario's Pizzeria",
                1000 + random.nextInt(5000),
                50 + random.nextInt(200),
                Arrays.asList(
                        "Best pizza ever 🔥",
                        "So delicious 😍",
                        "I always order this 👌"
                )
        ));

        reelsAdapter = new ReelsAdapter(this, reelList);
        viewPagerReels.setAdapter(reelsAdapter);

        // أول ما يفتح يكون على Reels
        bottomNavigationView.setSelectedItemId(R.id.nav_reels);

        // التنقل بين الصفحات
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_menu) {
                startActivity(new Intent(this, MenuActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_reels) {
                return true;
            }
            return false;
        });

        // ✅ التحكم بالفيديوهات عند السوايب
        viewPagerReels.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                RecyclerView recyclerView = (RecyclerView) viewPagerReels.getChildAt(0);
                if (recyclerView == null) return;

                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View view = recyclerView.getChildAt(i);
                    RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);

                    if (holder instanceof ReelsAdapter.ReelViewHolder) {
                        ReelsAdapter.ReelViewHolder reelHolder = (ReelsAdapter.ReelViewHolder) holder;
                        if (reelHolder.getBindingAdapterPosition() == position) {
                            if (reelHolder.playerView.getPlayer() != null) {
                                reelHolder.playerView.getPlayer().play();  // تشغيل الحالي
                            }
                        } else {
                            if (reelHolder.playerView.getPlayer() != null) {
                                reelHolder.playerView.getPlayer().pause(); // إيقاف غيره
                            }
                        }
                    }
                }
            }
        });
    }
}
