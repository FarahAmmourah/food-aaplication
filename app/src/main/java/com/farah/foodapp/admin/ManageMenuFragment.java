package com.farah.foodapp.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.farah.foodapp.menu.FoodItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManageMenuFragment extends Fragment {

    private MenuAdapterAdmin menuAdapterAdmin;
    private final List<FoodItem> menuItemList = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_menu, container, false);

        Button btnAddNewItem = view.findViewById(R.id.btnAddNewItem);
        RecyclerView rvMenuItems = view.findViewById(R.id.rvMenuItems);

        firestore = FirebaseFirestore.getInstance();

        rvMenuItems.setLayoutManager(new LinearLayoutManager(getContext()));
        menuAdapterAdmin = new MenuAdapterAdmin(menuItemList);
        rvMenuItems.setAdapter(menuAdapterAdmin);

        // Load menu items from Firestore
        loadMenuItems();

        // Add New Item dialog
        btnAddNewItem.setOnClickListener(v -> showAddItemDialog());

        return view;
    }

    private void loadMenuItems() {
        firestore.collection("menu")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    menuItemList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        FoodItem item = new FoodItem(
                                doc.getString("name") != null ? doc.getString("name") : "",
                                doc.getString("description") != null ? doc.getString("description") : "",
                                doc.getString("restaurant") != null ? doc.getString("restaurant") : "",
                                doc.getDouble("smallPrice") != null ? doc.getDouble("smallPrice") : 0.0,
                                doc.getDouble("largePrice") != null ? doc.getDouble("largePrice") : 0.0,
                                doc.getLong("rating") != null ? Objects.requireNonNull(doc.getLong("rating")).floatValue() : 0f,
                                R.drawable.ic_launcher_background
                        );
                        menuItemList.add(item);
                    }
                    menuAdapterAdmin.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load menu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showAddItemDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_menu_item, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText etName = dialogView.findViewById(R.id.etItemName);
        EditText etIngredients = dialogView.findViewById(R.id.etIngredients);
        EditText etPrice = dialogView.findViewById(R.id.etPrice);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);

        Button btnGenerateAI = dialogView.findViewById(R.id.btnGenerateAI);

        btnGenerateAI.setOnClickListener(v -> {
            String ingredients = etIngredients.getText().toString().trim();

            if (ingredients.isEmpty()) {
                Toast.makeText(getContext(), "Enter ingredients first", Toast.LENGTH_SHORT).show();
                return;
            }
            String generatedDescription = "Delicious " + ingredients + " prepared to perfection!";
            etDescription.setText(generatedDescription);
        });



        btnAdd.setOnClickListener(view -> {
            String name = etName.getText().toString().trim();
            String ingredients = etIngredients.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Name and price are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);

            // Create new FoodItem
            FoodItem newItem = new FoodItem(
                    name,
                    description,
                    "", // You can set restaurant here if needed
                    price,
                    0.0, // Large price default
                    0f,  // Rating default
                    R.drawable.ic_launcher_background
            );

            String restaurantId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            firestore.collection("restaurants")
                    .document(restaurantId)
                    .collection("menu")
                    .add(newItem)
                    .addOnSuccessListener(docRef -> {
                        menuItemList.add(newItem);
                        menuAdapterAdmin.notifyItemInserted(menuItemList.size() - 1);
                        Toast.makeText(getContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            dialog.dismiss();
        });
    }
}
