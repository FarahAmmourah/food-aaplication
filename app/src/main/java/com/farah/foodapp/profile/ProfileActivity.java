package com.farah.foodapp.profile;

import static com.farah.foodapp.R.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.farah.foodapp.R;
import com.farah.foodapp.cart.CartActivity;
import com.farah.foodapp.menu.MenuActivity;
import com.farah.foodapp.notifications.NotificationActivity;
import com.farah.foodapp.reel.ReelsActivity;
import com.farah.foodapp.orders.OrdersActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    LinearLayout layoutSettings, layoutOrderHistory;
    TextView tvAvatar, tvUsername, tvEmail, tvPhone, tvTotalOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_reels) {
                startActivity(new Intent(this, ReelsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_menu) {
                startActivity(new Intent(this, MenuActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        layoutSettings = findViewById(R.id.layout_settings);
        if (layoutSettings != null) {
            layoutSettings.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            });
        }

        layoutOrderHistory = findViewById(R.id.layout_order_history);
        if (layoutOrderHistory != null) {
            layoutOrderHistory.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, OrdersActivity.class);
                startActivity(intent);
            });
        }

        View layoutFavorites = findViewById(R.id.layout_favorites);
        if (layoutFavorites != null) {
            layoutFavorites.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, FavoritesActivity.class);
                startActivity(intent);
            });
        }

        View layoutNotifications = findViewById(R.id.layout_notifications);
        if (layoutNotifications != null) {
            layoutNotifications.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, NotificationActivity.class);
                startActivity(intent);
            });
        }
        tvTotalOrders = findViewById(R.id.tv_total_orders);
        tvAvatar = findViewById(R.id.tv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);

        loadUserProfile();
    }

    private void loadUserProfile() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String phone = document.getString("phone");

                        tvUsername.setText(name != null ? name : "");
                        tvEmail.setText(email != null ? email : "");
                        tvPhone.setText(phone != null ? phone : "");

                        if (name != null && !name.isEmpty()) {
                            tvAvatar.setText(String.valueOf(name.charAt(0)).toUpperCase());
                        }

                        // 🔹 Now load total orders for this customer
                        loadTotalOrders(uid);
                    }
                });
    }

    private void loadTotalOrders(String userId) {
        FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalOrders = querySnapshot.size();
                    tvTotalOrders.setText(String.valueOf(totalOrders));
                })
                .addOnFailureListener(e -> {
                    tvTotalOrders.setText("0");
                });
    }
}
