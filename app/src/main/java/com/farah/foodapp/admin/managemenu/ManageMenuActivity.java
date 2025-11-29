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

public class ManageMenuActivity extends Fragment {

    private MenuAdapterAdmin menuAdapter;
    private List<FoodItemAdmin> menuItemList = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_manage_menu, container, false);
        firestore = FirebaseFirestore.getInstance();

        RecyclerView rvMenu = view.findViewById(R.id.recyclerViewMenu);
        rvMenu.setLayoutManager(new LinearLayoutManager(getContext()));
        menuAdapter = new MenuAdapterAdmin(menuItemList);
        rvMenu.setAdapter(menuAdapter);

        Button btnAdd = view.findViewById(R.id.btnAddItem);
        btnAdd.setOnClickListener(v -> showAddItemDialog());

        loadMenuItems();

        return view;
    }

    private void loadMenuItems() {
        String restaurantId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        firestore.collection("restaurants")
                .document(restaurantId)
                .collection("menu")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    menuItemList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        FoodItemAdmin item = doc.toObject(FoodItemAdmin.class);
                        menuItemList.add(item);
                    }
                    menuAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load menu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showAddItemDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_menu_item, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();
        dialog.show();

        EditText etName = dialogView.findViewById(R.id.inputName);
        EditText etIngredients = dialogView.findViewById(R.id.inputIngredients);
        EditText etPrice = dialogView.findViewById(R.id.inputSmallPrice);
        EditText etDescription = dialogView.findViewById(R.id.inputDescription);
        EditText etImageUrl = dialogView.findViewById(R.id.inputImageUrl);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        Button btnGenerateAI = dialogView.findViewById(R.id.btnGenerateAI);

        btnGenerateAI.setOnClickListener(v -> {
            String ingredients = etIngredients.getText().toString().trim();
            if (!ingredients.isEmpty()) {
                etDescription.setText("Delicious " + ingredients + " prepared to perfection!");
            } else {
                Toast.makeText(getContext(), "Enter ingredients first", Toast.LENGTH_SHORT).show();
            }
        });

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String ingredients = etIngredients.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Name and price are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double smallPrice;
            try {
                smallPrice = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }

            String restaurantId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


            FoodItemAdmin newItem = new FoodItemAdmin(
                    name,
                    description.isEmpty() ? "Delicious " + name + " prepared with care!" : description,
                    restaurantId,
                    smallPrice,
                    ingredients,
                    0f,
                    imageUrl
            );

            firestore.collection("restaurants")
                    .document(restaurantId)
                    .collection("menu")
                    .add(newItem)
                    .addOnSuccessListener(docRef -> {
                        menuItemList.add(newItem);
                        menuAdapter.notifyItemInserted(menuItemList.size() - 1);
                        Toast.makeText(getContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}
