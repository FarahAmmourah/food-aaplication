package com.farah.foodapp.admin.activeorders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActiveOrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActiveOrdersAdapter adapter;
    private List<OrderAdmin> orders = new ArrayList<>();
    private FirebaseFirestore firestore;
    private TextView tvNoOrders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_orders, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewActiveOrders);
        tvNoOrders = view.findViewById(R.id.tvNoOrders);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActiveOrdersAdapter(orders);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        String restaurantId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("restaurants")
                .document(restaurantId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String restaurantName = doc.getString("name");
                        loadActiveOrders(restaurantName);
                    } else {
                        Toast.makeText(getContext(), "Restaurant not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch restaurant: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

        return view;
    }

    private void loadActiveOrders(String restaurantName) {
        firestore.collection("orders")
                .whereEqualTo("restaurantName", restaurantName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    orders.clear();
                    List<OrderAdmin> tempOrders = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String status = doc.getString("status");
                        if (status != null &&
                                (status.equalsIgnoreCase("preparing") || status.equalsIgnoreCase("pending"))) {
                            OrderAdmin order = doc.toObject(OrderAdmin.class);
                            order.setId(doc.getId());
                            tempOrders.add(order);
                        }
                    }

                    if (tempOrders.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        tvNoOrders.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoOrders.setVisibility(View.GONE);
                        fetchUserNames(tempOrders);
                    }
                })
                .addOnFailureListener(e -> {
                    recyclerView.setVisibility(View.GONE);
                    tvNoOrders.setText("Failed to load orders: " + e.getMessage());
                    tvNoOrders.setVisibility(View.VISIBLE);
                });
    }

    private void fetchUserNames(List<OrderAdmin> tempOrders) {
        final int[] remaining = {tempOrders.size()};

        for (OrderAdmin order : tempOrders) {
            firestore.collection("restaurants")
                    .document(order.getUserId())
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            order.setCustomerName(userDoc.getString("name"));
                        }
                    })
                    .addOnCompleteListener(task -> {
                        remaining[0]--;
                        if (remaining[0] == 0) {
                            orders.clear();
                            orders.addAll(tempOrders);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }
}
