package com.farah.foodapp.orders;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders;
    private OrdersAdapter adapter;
    private List<OrderModel> orderList = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrdersAdapter(orderList);
        recyclerOrders.setAdapter(adapter);

        loadOrders();
        handler.postDelayed(updateRunnable, 60000);
    }

    private void loadOrders() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(query -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        OrderModel order = doc.toObject(OrderModel.class);

                        // inject docId
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
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            for (OrderModel order : orderList) {
                updateOrderStatus(order);
            }
            handler.postDelayed(this, 60000);
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
            FirebaseFirestore.getInstance()
                    .collection("orders")
                    .document(order.getId())
                    .update("status", newStatus)
                    .addOnSuccessListener(aVoid -> loadOrders());
        }
    }
}
