package com.farah.foodapp.menu;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

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

        // 📌 لما المستخدم يضغط على الكارد → نفتح Dialog
        holder.cardView.setOnClickListener(v -> showFoodDialog(item));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // 📌 دالة عرض الـ Dialog
    private void showFoodDialog(FoodItem item) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(R.layout.dialog_food_details);

        ImageView imgMeal = dialog.findViewById(R.id.imgMeal);
        TextView tvMealName = dialog.findViewById(R.id.tvMealName);
        TextView tvDescription = dialog.findViewById(R.id.tvDescription);
        TextView btnSmall = dialog.findViewById(R.id.btnSmall);
        TextView btnLarge = dialog.findViewById(R.id.btnLarge);

        imgMeal.setImageResource(item.getImageResId());
        tvMealName.setText(item.getName());
        tvDescription.setText(item.getDescription());
        btnSmall.setText("Small - $" + item.getSmallPrice());
        btnLarge.setText("Large - $" + item.getLargePrice());

        btnSmall.setOnClickListener(v -> {
            CartManager.addItem(item.getName(), item.getRestaurant(), "Small", item.getSmallPrice(), item.getImageResId());
            notifyCartBadge();
            dialog.dismiss();
        });

        btnLarge.setOnClickListener(v -> {
            CartManager.addItem(item.getName(), item.getRestaurant(), "Large", item.getLargePrice(), item.getImageResId());
            notifyCartBadge();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void notifyCartBadge() {
        if (context instanceof MenuActivity) {
            ((MenuActivity) context).updateCartBadge();
        }
    }

    // ✅ ميزة البحث (Filter)
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

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvFoodName, tvFoodDesc, tvRestaurant, tvRating;
        CardView cardView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodDesc = itemView.findViewById(R.id.tvFoodDesc);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvRating = itemView.findViewById(R.id.tvRating);
            cardView = (CardView) itemView;

}}}