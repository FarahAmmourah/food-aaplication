package com.farah.foodapp.admin.admin_profile.specials;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.farah.foodapp.R;
import com.farah.foodapp.admin.managemenu.FoodItemAdmin;

import java.util.List;

public class SpecialsAdapter extends RecyclerView.Adapter<SpecialsAdapter.TopViewHolder> {

    private final List<FoodItemAdmin> topItems;

    public SpecialsAdapter(List<FoodItemAdmin> topItems) {
        this.topItems = topItems;
    }

    @NonNull
    @Override
    public TopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_admin, parent, false);
        return new TopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopViewHolder holder, int position) {
        FoodItemAdmin item = topItems.get(position);

        holder.tvName.setText(item.getName());
        holder.tvDesc.setText(item.getDescription());
        holder.tvRating.setText("★ " + item.getRating());
        holder.tvPrice.setText("Price: " + item.getSmallPrice() + " JD");

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_food_placeholder)
                .centerCrop()
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return topItems.size();
    }

    static class TopViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvDesc, tvRating, tvPrice;

        public TopViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivFood);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvDesc = itemView.findViewById(R.id.tvItemDescription);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            tvRating = itemView.findViewById(R.id.tvItemRating);
        }
    }
}
