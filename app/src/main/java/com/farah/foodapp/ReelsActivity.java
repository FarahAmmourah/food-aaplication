package com.farah.foodapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class ReelsActivity extends AppCompatActivity {

    private ViewPager2 viewPagerReels;
    private ReelsAdapter reelsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels);

        viewPagerReels = findViewById(R.id.viewPagerReels);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // عرّف قائمة الريلز
        List<ReelItem> reelList = new ArrayList<>();
        reelList.add(new ReelItem(R.raw.r1, "Pizza Margherita", "@Mario's Pizzeria"));
        reelList.add(new ReelItem(R.raw.r2, "Chicken Burger", "@Burger House"));
        reelList.add(new ReelItem(R.raw.r3, "Pasta Carbonara", "@Italian Corner"));

        reelsAdapter = new ReelsAdapter(this, reelList);
        viewPagerReels.setAdapter(reelsAdapter);

        // خلي Reels selected
        bottomNavigationView.setSelectedItemId(R.id.nav_reels);

        // التنقل
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
    }
}
