package com.farah.foodapp.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.farah.foodapp.cart.CartManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> implements Filterable {

    private Context context;
    private List<FoodItem> foodList;
    private List<FoodItem> foodListFull;

    public FoodAdapter(Context context, List<FoodItem> foodList) {
        this.context = context;
        this.foodList = foodList;
        this.foodListFull = new ArrayList<>(foodList);
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodList.get(position);

        holder.tvFoodName.setText(item.getName());
        holder.tvFoodDesc.setText(item.getDescription());
        holder.tvRestaurant.setText(item.getRestaurant());
        holder.tvRating.setText("★ " + item.getRating());
        holder.imgFood.setImageResource(item.getImageResId());

        holder.tvSmall.setText("Small  $" + item.getSmallPrice());
        holder.tvLarge.setText("Large  $" + item.getLargePrice());

        holder.btnSmall.setOnClickListener(v -> {
            CartManager.addItem(item.getName(), item.getRestaurant(), "Small", item.getSmallPrice(), item.getImageResId());
            notifyCartBadge();
        });

        holder.btnLarge.setOnClickListener(v -> {
            CartManager.addItem(item.getName(), item.getRestaurant(), "Large", item.getLargePrice(), item.getImageResId());
            notifyCartBadge();
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    @Override
    public Filter getFilter() {
        return foodFilter;
    }

    private Filter foodFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FoodItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(foodListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();
                for (FoodItem item : foodListFull) {
                    if (item.getName().toLowerCase(Locale.ROOT).contains(filterPattern)
                            || item.getRestaurant().toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            foodList.clear();
            foodList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    private void notifyCartBadge() {
        if (context instanceof MenuActivity) {
            ((MenuActivity) context).updateCartBadge();
        }
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood, iconAddSmall, iconAddLarge;
        TextView tvFoodName, tvFoodDesc, tvRestaurant, tvRating, tvSmall, tvLarge;
        LinearLayout btnSmall, btnLarge;
        CardView cardView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodDesc = itemView.findViewById(R.id.tvFoodDesc);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvRating = itemView.findViewById(R.id.tvRating);

            btnSmall = itemView.findViewById(R.id.btnSmall);
            btnLarge = itemView.findViewById(R.id.btnLarge);

            tvSmall = btnSmall.findViewById(R.id.tvSmall);
            tvLarge = btnLarge.findViewById(R.id.tvLarge);

            iconAddSmall = btnSmall.findViewById(R.id.iconAddSmall);
            iconAddLarge = btnLarge.findViewById(R.id.iconAddLarge);

            cardView = (CardView) itemView;
        }
    }
}
