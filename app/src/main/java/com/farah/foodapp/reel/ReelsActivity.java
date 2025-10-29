package com.farah.foodapp.reel;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private static final String PREF_NAME = "reels_prefs";
    private static final String KEY_LAST_POSITION = "last_position";
    private int lastPosition = 0;

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

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        lastPosition = prefs.getInt(KEY_LAST_POSITION, 0);

        loadReelsFromFirestore();

        bottomNavigationView.setSelectedItemId(R.id.nav_reels);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            pauseAllVideos();

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
                lastPosition = position;
                playOnlyCurrent(position);
                saveLastPosition(position);
            }
        });

        updateCartBadge();
    }

    private void saveLastPosition(int position) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_LAST_POSITION, position).apply();
    }

    private void playOnlyCurrent(int position) {
        RecyclerView recyclerView = (RecyclerView) viewPagerReels.getChildAt(0);
        if (recyclerView == null) return;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View view = recyclerView.getChildAt(i);
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (holder instanceof ReelsAdapter.ReelViewHolder) {
                ReelsAdapter.ReelViewHolder reelHolder = (ReelsAdapter.ReelViewHolder) holder;
                if (reelHolder.getBindingAdapterPosition() == position) {
                    if (reelHolder.playerView.getPlayer() != null)
                        reelHolder.playerView.getPlayer().play();
                } else {
                    if (reelHolder.playerView.getPlayer() != null)
                        reelHolder.playerView.getPlayer().pause();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAllVideos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseAllVideos();
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewPagerReels.postDelayed(() -> {
            viewPagerReels.setCurrentItem(lastPosition, false);
            playOnlyCurrent(lastPosition);
        }, 250);
    }

    private void pauseAllVideos() {
        RecyclerView recyclerView = (RecyclerView) viewPagerReels.getChildAt(0);
        if (recyclerView == null) return;
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View view = recyclerView.getChildAt(i);
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (holder instanceof ReelsAdapter.ReelViewHolder) {
                ReelsAdapter.ReelViewHolder reelHolder = (ReelsAdapter.ReelViewHolder) holder;
                if (reelHolder.playerView.getPlayer() != null)
                    reelHolder.playerView.getPlayer().pause();
            }
        }
    }

    private void loadReelsFromFirestore() {
        FirebaseFirestore.getInstance()
                .collectionGroup("reels")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reelList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            String videoUrl = doc.getString("videoUrl");
                            String title = doc.getString("title");
                            String restaurant = doc.getString("restaurant");
                            String restaurantId = doc.getString("restaurantId");
                            String imageUrl = doc.getString("imageUrl");

                            Long likesVal = doc.getLong("likesCount");
                            int likes = likesVal != null ? likesVal.intValue() : 0;

                            Long commentsVal = doc.getLong("commentsCount");
                            int commentsCount = commentsVal != null ? commentsVal.intValue() : 0;

                            Double priceVal = doc.getDouble("price");
                            double price = priceVal != null ? priceVal : 0.0;

                            List<String> comments = (List<String>) doc.get("comments");
                            if (comments == null) comments = new ArrayList<>();

                            reelList.add(new ReelItem(
                                    videoUrl,
                                    title,
                                    restaurant,
                                    likes,
                                    commentsCount,
                                    comments,
                                    price,
                                    restaurantId,
                                    doc.getId(),
                                    imageUrl
                            ));
                        } catch (Exception e) {
                            Log.e("Firestore", "Error parsing document", e);
                        }
                    }

                    reelsAdapter.notifyDataSetChanged();

                    viewPagerReels.post(() -> {
                        viewPagerReels.setCurrentItem(lastPosition, false);
                        playOnlyCurrent(lastPosition);
                    });

                    Log.d("Firestore", "Loaded reels count: " + reelList.size());
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
