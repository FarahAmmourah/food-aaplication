package com.farah.foodapp.admin.admin_profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.farah.foodapp.R;
import com.farah.foodapp.admin.AdminDashboardActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AdminProfileActivity extends AppCompatActivity {

    private TextView tvAvatar, tvUsername, tvEmail, tvPhone;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        tvAvatar = findViewById(R.id.tv_admin_avatar);
        tvUsername = findViewById(R.id.tv_admin_username);
        tvEmail = findViewById(R.id.tv_admin_email);
        tvPhone = findViewById(R.id.tv_admin_phone);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return id == R.id.nav_profile;
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        AdminProfilePagerAdapter adapter = new AdminProfilePagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Profile");
            else tab.setText("Stats");
        }).attach();

        loadAdminProfile();
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
}
