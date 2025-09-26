package com.farah.foodapp.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartChangedListener {
        void onCartUpdated();
    }

    private Context context;
    private List<CartItem> cartItems;
    private OnCartChangedListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartChangedListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvCartName.setText(item.getName() + " (" + item.getSize() + ")");
        holder.tvCartRestaurant.setText(item.getRestaurant());
        holder.tvCartQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvCartPrice.setText("JOD " + String.format("%.2f", item.getPrice() * item.getQuantity()));
        holder.imgCartFood.setImageResource(item.getImageResId());

        holder.btnDecrease.setOnClickListener(v -> {
            CartManager.decreaseItem(item);
            notifyDataSetChanged();
            if (listener != null) listener.onCartUpdated();
        });

        holder.btnIncrease.setOnClickListener(v -> {
            CartManager.increaseItem(item);
            notifyDataSetChanged();
            if (listener != null) listener.onCartUpdated();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCartFood;
        TextView tvCartName, tvCartRestaurant, tvCartQuantity, tvCartPrice;
        ImageButton btnDecrease, btnIncrease;
        CardView cardView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCartFood = itemView.findViewById(R.id.imgCartFood);
            tvCartName = itemView.findViewById(R.id.tvCartName);
            tvCartRestaurant = itemView.findViewById(R.id.tvCartRestaurant);
            tvCartQuantity = itemView.findViewById(R.id.tvCartQuantity);
            tvCartPrice = itemView.findViewById(R.id.tvCartPrice);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            cardView = (CardView) itemView;
        }
    }
}
