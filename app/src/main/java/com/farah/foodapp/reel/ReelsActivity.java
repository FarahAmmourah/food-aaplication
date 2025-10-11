package com.farah.foodapp.reel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.farah.foodapp.R;
import com.farah.foodapp.cart.CartActivity;
import com.farah.foodapp.cart.CartManager;
import com.farah.foodapp.menu.MenuActivity;
import com.farah.foodapp.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ReelsActivity extends AppCompatActivity {
    private ViewPager2 viewPagerReels;
    private ReelsAdapter reelsAdapter;
    private List<ReelItem> reelList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels);

        viewPagerReels = findViewById(R.id.viewPagerReels);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.primary));
        bottomNavigationView.setItemIconTintList(getResources().getColorStateList(R.color.primaryForeground));
        bottomNavigationView.setItemTextColor(getResources().getColorStateList(R.color.primaryForeground));

        reelsAdapter = new ReelsAdapter(this, reelList);
        viewPagerReels.setAdapter(reelsAdapter);

        loadReelsFromFirestore();

        bottomNavigationView.setSelectedItemId(R.id.nav_reels);
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
                                reelHolder.playerView.getPlayer().play();
                            }
                        } else {
                            if (reelHolder.playerView.getPlayer() != null) {
                                reelHolder.playerView.getPlayer().pause();
                            }
                        }
                    }
                }
            }
        });

        updateCartBadge();
    }

    private void loadReelsFromFirestore() {
        FirebaseFirestore.getInstance()
                .collection("reels")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reelList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            String videoUrl = doc.getString("videoUrl");
                            String title = doc.getString("title");
                            String restaurant = doc.getString("restaurant");
                            int likes = doc.getLong("likesCount").intValue();
                            int commentsCount = doc.getLong("commentsCount").intValue();
                            double price = doc.getDouble("price");

                            List<String> comments = (List<String>) doc.get("comments");
                            String restaurantId = doc.getString("restaurantId");

                            String reelId = doc.getId();

                            reelList.add(new ReelItem(
                                    videoUrl,
                                    title,
                                    restaurant,
                                    likes,
                                    commentsCount,
                                    comments,
                                    price,
                                    restaurantId,
                                    reelId // جديد
                            ));
                        } catch (Exception e) {
                            Log.e("Firestore", "Error parsing document", e);
                        }
                    }
                    reelsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to load reels", e));
    }

    public void updateCartBadge() {
        int count = CartManager.getTotalQuantity();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (count > 0) {
            bottomNav.getOrCreateBadge(R.id.nav_cart).setNumber(count);
        } else {
            bottomNav.removeBadge(R.id.nav_cart);
        }
    }
}
