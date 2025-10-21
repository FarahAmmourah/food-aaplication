package com.farah.foodapp.admin.admin_profile.orderhistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.farah.foodapp.admin.activeorders.OrderAdmin;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final List<OrderAdmin> orders;

    public OrderHistoryAdapter(List<OrderAdmin> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderAdmin order = orders.get(position);

        StringBuilder itemsText = new StringBuilder();
        if (order.getItems() != null) {
            for (String item : order.getItems()) {
                itemsText.append(item).append("\n");
            }
        }
        holder.tvOrderDetails.setText(itemsText.toString().trim());

        holder.tvCustomerName.setText("Customer: " + (order.getCustomerName() != null ? order.getCustomerName() : "N/A"));

        holder.tvAddress.setText("Address: " + (order.getCustomerAddress() != null ? order.getCustomerAddress() : "N/A"));

        holder.tvTotal.setText("Total: " + order.getTotal() + " JOD");
        holder.tvStatus.setText("Status: " + order.getStatus());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderDetails, tvCustomerName, tvAddress, tvTotal, tvStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDetails = itemView.findViewById(R.id.tvOrderDetails);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
