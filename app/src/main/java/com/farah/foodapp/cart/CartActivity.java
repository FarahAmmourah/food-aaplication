package com.farah.foodapp.cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.farah.foodapp.MenuActivity;
import com.farah.foodapp.ProfileActivity;
import com.farah.foodapp.R;
import com.farah.foodapp.reel.ReelsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CartActivity extends AppCompatActivity {

    private LinearLayout layoutEmptyCart;
    private LinearLayout layoutCartActions;
    private TextView tvTotalPrice;
    private Button btnCancelCart, btnOrderNow, btnBack;
    private BottomNavigationView bottomNavigationView;

    // بدل CartManager: متغير بسيط للتجربة
    private double fakeTotalPrice = 0.0; // جرّب تخليها 10.5 مثلاً لتشوف الواجهة الثانية

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Bind Views
        btnBack = findViewById(R.id.btnBack);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        layoutCartActions = findViewById(R.id.layoutCartActions);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCancelCart = findViewById(R.id.btnCancelCart);
        btnOrderNow = findViewById(R.id.btnOrderNow);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set navbar selected item
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);

        // Handle back button
        btnBack.setOnClickListener(v -> finish());

        // Show empty or with total
        if (fakeTotalPrice > 0) {
            layoutEmptyCart.setVisibility(LinearLayout.GONE);
            layoutCartActions.setVisibility(LinearLayout.VISIBLE);
            tvTotalPrice.setText("Total: " + fakeTotalPrice + " JD");
        } else {
            layoutEmptyCart.setVisibility(LinearLayout.VISIBLE);
            layoutCartActions.setVisibility(LinearLayout.GONE);
        }

        // Cancel cart
        btnCancelCart.setOnClickListener(v -> {
            fakeTotalPrice = 0; // نفرغ السلة
            layoutEmptyCart.setVisibility(LinearLayout.VISIBLE);
            layoutCartActions.setVisibility(LinearLayout.GONE);
            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show();
        });

        // Order now
        btnOrderNow.setOnClickListener(v -> {
            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
        });

        // Bottom navigation actions
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
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_cart) {
                return true;
            }
            return false;
        });
    }
}
