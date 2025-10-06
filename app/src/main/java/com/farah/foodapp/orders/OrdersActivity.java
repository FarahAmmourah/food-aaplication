package com.farah.foodapp.orders;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.farah.foodapp.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrdersActivity extends AppCompatActivity {

    private OrdersAdapter adapter;
    private final List<OrderModel> orderList = new ArrayList<>();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        RecyclerView recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrdersAdapter(orderList);
        recyclerOrders.setAdapter(adapter);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(OrdersActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        loadOrders();
        handler.postDelayed(updateRunnable, 2000);
    }

    private void loadOrders() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(query -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        OrderModel order = doc.toObject(OrderModel.class);

                        order = new OrderModel(
                                doc.getId(),
                                order.getUserId(),
                                order.getRestaurantName(),
                                order.getTotal(),
                                order.getStatus(),
                                order.getAddress(),
                                order.getLat(),
                                order.getLon(),
                                order.getCreatedAt(),
                                order.getItems(),
                                order.getEta()
                        );
                        orderList.add(order);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            for (OrderModel order : orderList) {
                updateOrderStatus(order);
            }
            handler.postDelayed(this, 2000);
        }
    };

    private void updateOrderStatus(OrderModel order) {
        long now = System.currentTimeMillis();
        long diff = now - order.getCreatedAt();

        String newStatus = order.getStatus();

        if (diff > 2 * 60 * 1000 && diff <= 5 * 60 * 1000) {
            newStatus = "On the way";
        } else if (diff > 5 * 60 * 1000) {
            newStatus = "Completed";
        }

        if (!newStatus.equals(order.getStatus())) {
            String finalNewStatus = newStatus;

            FirebaseFirestore.getInstance()
                    .collection("orders")
                    .document(order.getId())
                    .update("status", newStatus)
                    .addOnSuccessListener(aVoid -> {
                        loadOrders();


                        showOrderNotification(order, finalNewStatus);
                    });
        }
    }

    private void showOrderNotification(OrderModel order, String status) {
        String channelId = "order_channel";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Order Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        String itemsSummary = (order.getItems() != null && !order.getItems().isEmpty())
                ? android.text.TextUtils.join("\n", order.getItems())
                : "Your order";

        Intent intent = new Intent(this, OrdersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this,
                0,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo_app)
                .setContentTitle("Order Update")
                .setContentText("Your order is now " + status)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(itemsSummary + "\n\nStatus: " + status))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(order.getId().hashCode(), builder.build());
        saveNotificationToFirestore("Order Update", "Your order is now " + status);
    }
    private void saveNotificationToFirestore(String title, String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = null;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("userId", userId != null ? userId : "anonymous");

        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(doc ->
                        android.util.Log.d("OrdersActivity", "Notification saved: " + doc.getId()))
                .addOnFailureListener(e ->
                        android.util.Log.e("OrdersActivity", "Error saving notification", e));
    }
}
