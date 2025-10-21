package com.farah.foodapp.orders;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

public class OrdersActivity extends AppCompatActivity {

    private OrdersAdapter adapter;
    private final List<OrderModel> orderList = new ArrayList<>();
    private final Map<String, String> lastStatuses = new HashMap<>();

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

        listenToOrders();
    }

    private void listenToOrders() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    orderList.clear();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            OrderModel order = doc.toObject(OrderModel.class);
                            order.setId(doc.getId());
                            orderList.add(order);
                            maybeNotify(order);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void maybeNotify(OrderModel order) {
        String last = lastStatuses.get(order.getId());
        String current = order.getStatus();
        if (last == null || !last.equals(current)) {
            lastStatuses.put(order.getId(), current);
            showOrderNotification(order, current);
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
        saveNotificationToFirestore("Your order is now " + status);
    }

    private void saveNotificationToFirestore(String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "anonymous";

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Order Update");
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("userId", userId);

        db.collection("notifications")
                .add(notification);
    }
}
