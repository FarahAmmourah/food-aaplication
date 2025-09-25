package com.farah.foodapp.cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.farah.foodapp.menu.MenuActivity;
import com.farah.foodapp.profile.ProfileActivity;
import com.farah.foodapp.R;
import com.farah.foodapp.reel.ReelsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CartActivity extends AppCompatActivity {

    private LinearLayout layoutEmptyCart;
    private TextView tvTotalPrice;
    private Button btnCancelCart, btnOrderNow;
    private BottomNavigationView bottomNavigationView;

    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCancelCart = findViewById(R.id.btnCancelCart);
        btnOrderNow = findViewById(R.id.btnOrderNow);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.nav_cart);

        updateCartUI();

        btnCancelCart.setOnClickListener(v -> {
            totalPrice = 0.0;
            updateCartUI();
            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show();
        });

        btnOrderNow.setOnClickListener(v -> {
            if (totalPrice > 0) {
                Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });

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

    private void updateCartUI() {
        tvTotalPrice.setText("Total: " + totalPrice + " JD");

        if (totalPrice == 0) {
            layoutEmptyCart.setVisibility(LinearLayout.VISIBLE);
        } else {
            layoutEmptyCart.setVisibility(LinearLayout.GONE);
        }
    }
}
