package com.farah.foodapp.orders;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.farah.foodapp.orders.OrderModel;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<OrderModel> orderList;

    public OrdersAdapter(List<OrderModel> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        holder.tvRestaurant.setText(order.getRestaurantName());
        holder.tvTotal.setText("JOD " + String.format("%.2f", order.getTotal()));
        holder.tvStatus.setText(order.getStatus());
        holder.tvAddress.setText(order.getAddress());
        holder.tvEta.setText(order.getEta());

        if ("Preparing".equals(order.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#FFA500")); // Orange
        } else if ("On the way".equals(order.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#2196F3")); // Blue
        } else if ("Completed".equals(order.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvRestaurant, tvTotal, tvStatus, tvAddress, tvEta;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvEta = itemView.findViewById(R.id.tvEta);
        }
    }
}
