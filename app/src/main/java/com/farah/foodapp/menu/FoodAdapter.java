package com.farah.foodapp.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.farah.foodapp.R;
import com.farah.foodapp.cart.CartManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> implements Filterable {

    private Context context;
    private List<FoodItem> foodList;
    private List<FoodItem> foodListFull;// used for search , so we dont mess with original data
    private boolean enableRating;// to remove rating from public menu

    public FoodAdapter(Context context, List<FoodItem> foodList, boolean enableRating) {
        this.context = context;
        this.foodList = foodList;
        this.enableRating = enableRating;
        this.foodListFull = new ArrayList<>();
    }


    @NonNull
    @Override// connects java with xml
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodList.get(position);

        holder.tvFoodName.setText(item.getName());
        holder.tvFoodDesc.setText(item.getDescription());
        holder.tvRestaurant.setText(item.getRestaurantName());
        holder.btnRate.setText("Rate ★ " + String.format(Locale.US, "%.1f", item.getRating()));

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_food_placeholder)
                .error(R.drawable.ic_food_placeholder)
                .into(holder.imgFood);

        if (!enableRating) {
            holder.btnRate.setVisibility(View.GONE);
        }


        holder.btnRate.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rate, null);
            builder.setView(dialogView);

            RatingBar ratingBar = dialogView.findViewById(R.id.dialogRatingBar);

            builder.setPositiveButton("Submit", (dialog, which) -> {
                float newRating = ratingBar.getRating();
                updateMealRating(item, newRating, holder);
                Toast.makeText(context, "Thanks for rating " + item.getName(), Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        holder.cardView.setOnClickListener(v -> showFoodDialog(item));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    private void showFoodDialog(FoodItem item) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(R.layout.dialog_food_details);

        ImageView imgMeal = dialog.findViewById(R.id.imgMeal);
        TextView tvMealName = dialog.findViewById(R.id.tvMealName);
        TextView tvDescription = dialog.findViewById(R.id.tvDescription);
        TextView btnSmall = dialog.findViewById(R.id.btnSmall);
        TextView btnLarge = dialog.findViewById(R.id.btnLarge);

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_food_placeholder)
                .error(R.drawable.ic_food_placeholder)
                .into(imgMeal);

        tvMealName.setText(item.getName());
        tvDescription.setText(item.getDescription());
        btnSmall.setText("Small - $" + item.getSmallPrice());
        btnLarge.setText("Large - $" + item.getLargePrice());

        btnSmall.setOnClickListener(v -> {
            CartManager.addItem(
                    item.getName(),
                    item.getRestaurantName(),
                    "Small",
                    item.getSmallPrice(),
                    item.getImageUrl(),
                    item.getRestaurantId()
            );
            Toast.makeText(context, item.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnLarge.setOnClickListener(v -> {
            CartManager.addItem(
                    item.getName(),
                    item.getRestaurantName(),
                    "Large",
                    item.getLargePrice(),
                    item.getImageUrl(),
                    item.getRestaurantId()
            );
            Toast.makeText(context, item.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateMealRating(FoodItem item, float newRating, FoodViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("restaurants")
                .document("flames_restaurant_01")
                .collection("menu")
                .document(item.getId());

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(docRef);

            Double currentRatingObj = snapshot.getDouble("rating");
            Long ratingCountObj = snapshot.getLong("ratingCount");

            double currentRating = (currentRatingObj != null) ? currentRatingObj : 0.0;
            long ratingCount = (ratingCountObj != null) ? ratingCountObj : 0L;

            double updatedRating = ((currentRating * ratingCount) + newRating) / (ratingCount + 1);

            transaction.update(docRef, "rating", updatedRating);
            transaction.update(docRef, "ratingCount", ratingCount + 1);

            return updatedRating;
        }).addOnSuccessListener(updatedRating -> {
            item.setRating((float) updatedRating.doubleValue());
            holder.btnRate.setText("Rate ★ " + String.format(Locale.US, "%.1f", updatedRating));
        }).addOnFailureListener(e ->
                Toast.makeText(context, "Failed to update rating", Toast.LENGTH_SHORT).show()
        );
    }

    @Override// run when menu calls get filter in menu activity
    public Filter getFilter() {
        return foodFilter;
    }


    private final Filter foodFilter = new Filter() {
        @Override// constraint is the word the user typed in the search
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FoodItem> filteredList = new ArrayList<>();
// if user does not write anything bring all meals
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(foodListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();

                for (FoodItem item : foodListFull) {

                    // brings the name of the meal and checks its not null
                    String name = item.getName() != null
                            ? item.getName().toLowerCase(Locale.ROOT)
                            : "";

                    // brings the name of the resturant and checks its not null
                    String restaurant = item.getRestaurantName() != null
                            ? item.getRestaurantName().toLowerCase(Locale.ROOT)
                            : "";

                    // if the name of the meal or rest is like the user enterd add it to the filtered list
                    if (name.contains(filterPattern) || restaurant.contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
// this function must return  FilterResults object
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;// go to publishResults and show new results
        }

        @Override//removes old vals from foodlist and put new ones
        protected void publishResults(CharSequence constraint, FilterResults results) {
            foodList.clear();

            if (results.values != null) {
                foodList.addAll((List<FoodItem>) results.values);
            }

            notifyDataSetChanged();
        }
    };

    public void setFoodListFull(List<FoodItem> list) {
        foodListFull.clear();
        foodListFull.addAll(list);
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvFoodName, tvFoodDesc, tvRestaurant;
        Button btnRate;
        CardView cardView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodDesc = itemView.findViewById(R.id.tvFoodDesc);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurantName);
            btnRate = itemView.findViewById(R.id.btnRate);
            cardView = (CardView) itemView;
        }
    }
}
