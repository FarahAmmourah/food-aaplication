package com.farah.foodapp.menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.farah.foodapp.cart.CartActivity;
import com.farah.foodapp.profile.ProfileActivity;
import com.farah.foodapp.reel.ReelsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private TextView tvRestaurantName, tvAddress;
    private RecyclerView recyclerViewMenu;
    private FoodAdapter adapter;
    private ArrayList<FoodItem> menuList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvAddress = findViewById(R.id.tvAddress);
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);

        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));
        menuList = new ArrayList<>();
        adapter = new FoodAdapter(this, menuList);
        recyclerViewMenu.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        String restaurantId = getIntent().getStringExtra("restaurantId");

        if (restaurantId != null) {
            loadRestaurantDetails(restaurantId);
            loadMenuItems(restaurantId);
        } else {
            Toast.makeText(this, "No restaurant found!", Toast.LENGTH_SHORT).show();
        }

        // ✅ BottomNavigationView setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_menu); // نخلي Menu selected

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_reels) {
                startActivity(new Intent(this, ReelsActivity.class));
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
            } else if (id == R.id.nav_menu) {
                return true;
            }
            return false;
        });
    }

    private void loadRestaurantDetails(String restaurantId) {
        db.collection("restaurants").document(restaurantId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String address = documentSnapshot.getString("address");
                        tvRestaurantName.setText(name);
                        tvAddress.setText(address);
                    }
                });
    }

    private void loadMenuItems(String restaurantId) {
        db.collection("restaurants").document(restaurantId)
                .collection("menu")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    menuList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        String desc = doc.getString("description");
                        double price = doc.getDouble("price");

                        menuList.add(new FoodItem(name, desc, price));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading menu", Toast.LENGTH_SHORT).show());
    }
}
