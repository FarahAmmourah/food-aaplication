package com.farah.foodapp.admin;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.farah.foodapp.R;
import com.farah.foodapp.admin.admin_profile.AdminProfileActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class AdminDashboardActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ListenerRegistration notificationListener;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        AdminPagerAdapter adapter = new AdminPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Active Orders");
                    else if (position == 1) tab.setText("Manage Menu");
                    else if (position == 2) tab.setText("My Reels");
                }
        ).attach();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        int openTab = getIntent().getIntExtra("openTab", -1);
        if (openTab >= 0 && viewPager != null) {
            viewPager.setCurrentItem(openTab, false);
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startRestaurantNotificationListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (notificationListener != null) {
            notificationListener.remove();
        }
    }

    private void startRestaurantNotificationListener() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("restaurants")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String restaurantName = doc.getString("name");
                        if (restaurantName != null && !restaurantName.isEmpty()) {
                            listenForNotifications(restaurantName);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        android.util.Log.e("AdminDashboard", "Error fetching restaurant name", e));
    }

    private void listenForNotifications(String restaurantName) {
        notificationListener = FirebaseFirestore.getInstance()
                .collection("notifications")
                .whereEqualTo("restaurantName", restaurantName)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            String title = dc.getDocument().getString("title");
                            String message = dc.getDocument().getString("message");
                            String orderId = dc.getDocument().getString("orderId");

                            if (orderId != null && !orderId.isEmpty()) {
                                FirebaseFirestore.getInstance()
                                        .collection("orders")
                                        .document(orderId)
                                        .get()
                                        .addOnSuccessListener(orderDoc -> {
                                            if (orderDoc.exists()) {
                                                StringBuilder itemsText = new StringBuilder();
                                                java.util.List<String> items = (java.util.List<String>) orderDoc.get("items");
                                                Double total = orderDoc.getDouble("total");

                                                if (items != null && !items.isEmpty()) {
                                                    for (String item : items) {
                                                        itemsText.append("• ").append(item).append("\n");
                                                    }
                                                }

                                                String detailedMessage = message;
                                                if (itemsText.length() > 0) {
                                                    detailedMessage += "\n\nItems:\n" + itemsText;
                                                }
                                                if (total != null) {
                                                    detailedMessage += "\nTotal: " + String.format("%.2f JOD", total);
                                                }

                                                showLocalNotification(title, detailedMessage);
                                            } else {
                                                showLocalNotification(title, message);
                                            }
                                        })
                                        .addOnFailureListener(ex -> {
                                            android.util.Log.e("AdminDashboard", "Error loading order details", ex);
                                            showLocalNotification(title, message);
                                        });
                            } else {
                                showLocalNotification(title, message);
                            }
                        }
                    }
                });
    }


    private void showLocalNotification(String title, String message) {
        String channelId = "restaurant_channel";
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Restaurant Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.putExtra("openTab", 0); // 0 = Active Orders
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this,
                0,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo_app)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
