package com.farah.foodapp.admin.admin_profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.farah.foodapp.R;
import com.farah.foodapp.admin.AdminDashboardActivity;
import com.farah.foodapp.admin.activeorders.OrderAdmin;
import com.farah.foodapp.profile.ChangePasswordActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    LinearLayout layoutSettings, layoutOrderHistory;
    TextView tvAvatar, tvUsername, tvEmail, tvPhone, tvTotalOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(AdminProfileActivity.this, AdminDashboardActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else return id == R.id.nav_profile;
        });


        tvAvatar = findViewById(R.id.tv_admin_avatar);
        tvUsername = findViewById(R.id.tv_admin_username);
        tvEmail = findViewById(R.id.tv_admin_email);
        tvPhone = findViewById(R.id.tv_admin_phone);
        tvTotalOrders = findViewById(R.id.tv_total_orders);


        layoutSettings = findViewById(R.id.layout_settings);
        layoutOrderHistory = findViewById(R.id.layout_order_history);


        if (layoutSettings != null) {
            layoutSettings.setOnClickListener(v -> startActivity(new Intent(AdminProfileActivity.this, ChangePasswordActivity.class)));
        }

        if (layoutOrderHistory != null) {
            layoutOrderHistory.setOnClickListener(v ->
                    startActivity(new Intent(AdminProfileActivity.this, OrderHistoryActivity.class)));
        }


        loadAdminProfile();
        fetchRestaurantNameAndLoadOrders();

    }

    private void fetchRestaurantNameAndLoadOrders() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseFirestore.getInstance()
                .collection("restaurants")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String restaurantName = doc.getString("name");
                        if (restaurantName != null) {
                            loadTotalOrders(restaurantName);
                        } else {
                            Toast.makeText(this, "Restaurant name not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error fetching restaurant: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void loadAdminProfile() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
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
                    }
                });
    }
    private void loadTotalOrders(String restaurantName) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("orders")
                .get()
                .addOnSuccessListener(query -> {
                    List<OrderAdmin> tempOrders = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : query) {
                        String docRestaurant = doc.getString("restaurantName");
                        if (docRestaurant == null || !docRestaurant.trim().equalsIgnoreCase(restaurantName.trim())) continue;

                        OrderAdmin order = doc.toObject(OrderAdmin.class);
                        order.setId(doc.getId());
                        tempOrders.add(order);
                    }

                    int totalOrders = tempOrders.size();
                    tvTotalOrders.setText(String.valueOf(totalOrders));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading orders: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }


}
