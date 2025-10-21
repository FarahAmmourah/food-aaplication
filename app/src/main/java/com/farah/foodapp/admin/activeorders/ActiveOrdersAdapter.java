package com.farah.foodapp.admin.activeorders;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ActiveOrdersAdapter extends RecyclerView.Adapter<ActiveOrdersAdapter.OrderViewHolder> {

    private final List<OrderAdmin> orders;

    public ActiveOrdersAdapter(List<OrderAdmin> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderAdmin order = orders.get(position);
        holder.tvOrderId.setText("Order ID: " + order.getId());
        holder.tvCustomerName.setText("Customer: " + order.getCustomerName());
        holder.tvTotalPrice.setText("Total: " + order.getTotal() + " JD");

        Drawable tick = AppCompatResources.getDrawable(holder.itemView.getContext(), R.drawable.ic_tick_green);
        Drawable cross = AppCompatResources.getDrawable(holder.itemView.getContext(), R.drawable.ic_cross_red);

        String status = order.getStatus();
        if ("completed".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("Completed");
            holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(tick, null, null, null);
        } else if ("cancelled".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("Cancelled");
            holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(cross, null, null, null);
        } else {
            holder.tvStatus.setText(status);
            holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        holder.btnCancel.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("orders")
                    .document(order.getId())
                    .update("status", "cancelled")
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(v.getContext(), "Order cancelled", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(v.getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        holder.btnComplete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("orders")
                    .document(order.getId())
                    .update("status", "completed")
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(v.getContext(), "Order completed", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(v.getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvStatus, tvTotalPrice;
        Button btnCancel, btnComplete;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnComplete = itemView.findViewById(R.id.btnComplete);
        }
    }
}
