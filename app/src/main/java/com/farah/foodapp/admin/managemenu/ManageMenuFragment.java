package com.farah.foodapp.admin.managemenu;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManageMenuFragment extends Fragment {
    private MenuAdapterAdmin menuAdapterAdmin;
    private final List<FoodItemAdmin> menuItemList = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_menu, container, false);
        Button btnAddNewItem = view.findViewById(R.id.btnAddItem);
        RecyclerView rvMenuItems = view.findViewById(R.id.recyclerViewMenu);
        firestore = FirebaseFirestore.getInstance();
        rvMenuItems.setLayoutManager(new LinearLayoutManager(getContext()));
        menuAdapterAdmin = new MenuAdapterAdmin(menuItemList);
        rvMenuItems.setAdapter(menuAdapterAdmin);
        loadMenuItems();
        btnAddNewItem.setOnClickListener(v -> showAddItemDialog());
        return view;
    }

    private void loadMenuItems() {
        String restaurantId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        firestore.collection("restaurants")
                .document(restaurantId)
                .collection("menu")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
            menuItemList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                FoodItemAdmin item = new FoodItemAdmin(doc.getString("name") != null ? doc.getString("name") : "",
                        doc.getString("description") != null ? doc.getString("description") : "",
                        doc.getString("restaurant") != null ? doc.getString("restaurant") : "",
                        doc.getDouble("smallPrice") != null ? doc.getDouble("smallPrice") : 0.0,
                        doc.getString("ingredents") != null ? doc.getString("ingredents") : "",
                        doc.getLong("rating") != null ? Objects.requireNonNull(doc.getLong("rating")).floatValue() : 0f,
                        doc.getString("imageUrl") != null ? doc.getString("imageUrl") : ""  // Add image URL here
                        );
                menuItemList.add(item);
            }
            menuAdapterAdmin.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load menu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showAddItemDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_menu_item, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        EditText etName = dialogView.findViewById(R.id.inputName);
        EditText etIngredients = dialogView.findViewById(R.id.inputIngredients);
        EditText etPrice = dialogView.findViewById(R.id.inputSmallPrice);
        EditText etDescription = dialogView.findViewById(R.id.inputDescription);
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
            FoodItemAdmin newItem = new FoodItemAdmin(
                    name,
                    description,
                    "",
                    price,
                    "",
                    0f,
                    ""
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
                    }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            dialog.dismiss();
        });
    }
}
