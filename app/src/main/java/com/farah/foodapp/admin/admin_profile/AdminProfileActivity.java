package com.farah.foodapp.admin.admin_profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.farah.foodapp.R;
import com.farah.foodapp.admin.AdminDashboardActivity;
import com.farah.foodapp.profile.ChangePasswordActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    LinearLayout layoutSettings, layoutOrderHistory;
    TextView tvAvatar, tvUsername, tvEmail, tvPhone;

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
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        tvAvatar = findViewById(R.id.tv_admin_avatar);
        tvUsername = findViewById(R.id.tv_admin_username);
        tvEmail = findViewById(R.id.tv_admin_email);
        tvPhone = findViewById(R.id.tv_admin_phone);

        layoutSettings = findViewById(R.id.layout_settings);

        if (layoutSettings != null) {
            layoutSettings.setOnClickListener(v -> {
                Intent intent = new Intent(AdminProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            });
        }

        loadAdminProfile();
    }

    private void loadAdminProfile() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("admins").document(uid).get()
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
}
