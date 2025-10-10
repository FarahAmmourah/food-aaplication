package com.farah.foodapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.farah.foodapp.cards.AddCardActivity;
import com.farah.foodapp.cards.CardStorage;
import com.farah.foodapp.cart.CartManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvSubtotal, tvDiscount, tvDeliveryFee, tvServiceFee, tvTotal, tvAddress;
    private RadioGroup rgPaymentMethod;
    private TextView btnAddCard;

    private static final double DELIVERY_FEE = 3.00;
    private static final double SERVICE_FEE = 0.20;
    private static final double DISCOUNT = 1.00;

    private ActivityResultLauncher<Intent> addCardLauncher;
    private ActivityResultLauncher<Intent> pickLocationLauncher;

    private String tempCardNumber, tempCardExpiry, tempCardHolder;
    private boolean newCardSaved = false;

    private double selectedLat = 0;
    private double selectedLon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvServiceFee = findViewById(R.id.tvServiceFee);
        tvTotal = findViewById(R.id.tvTotal);
        tvAddress = findViewById(R.id.tvAddress);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        btnAddCard = findViewById(R.id.btnAddCard);

        findViewById(R.id.btnPlaceOrder).setOnClickListener(v -> placeOrder());
        findViewById(R.id.btnCancelOrder).setOnClickListener(v -> finish());

        addCardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean paymentSuccess = result.getData().getBooleanExtra("payment_success", false);

                        if (paymentSuccess) {
                            placeOrderWithStatus("stripe", "completed");
                            return;
                        }
                        tempCardNumber = result.getData().getStringExtra("tempCardNumber");
                        tempCardExpiry = result.getData().getStringExtra("tempCardExpiry");
                        tempCardHolder = result.getData().getStringExtra("tempCardHolder");
                        newCardSaved = false;
                    } else if (result.getResultCode() == RESULT_OK) {
                        newCardSaved = true;
                    }
                    loadCards();
                }
        );

        btnAddCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCardActivity.class);
            addCardLauncher.launch(intent);
        });

        pickLocationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String addr = result.getData().getStringExtra("pickedAddress");
                        selectedLat = result.getData().getDoubleExtra("pickedLat", 0);
                        selectedLon = result.getData().getDoubleExtra("pickedLon", 0);

                        if (addr != null && !addr.isEmpty()) {
                            tvAddress.setText(addr);
                        } else {
                            tvAddress.setText(String.format("Lat: %.5f, Lon: %.5f", selectedLat, selectedLon));
                        }
                    }
                }
        );

        findViewById(R.id.ivMapPlaceholder).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapPickActivity.class);
            pickLocationLauncher.launch(intent);
        });
        findViewById(R.id.tvChangeLocation).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapPickActivity.class);
            pickLocationLauncher.launch(intent);
        });

        updateSummary();
        loadCards();
    }

    private void updateSummary() {
        double subtotal = CartManager.getSubtotal();
        double total = subtotal - DISCOUNT + DELIVERY_FEE + SERVICE_FEE;

        tvSubtotal.setText("Subtotal: JOD " + String.format("%.2f", subtotal));
        tvDiscount.setText("Discount: -JOD " + String.format("%.2f", DISCOUNT));
        tvDeliveryFee.setText("Delivery: JOD " + String.format("%.2f", DELIVERY_FEE));
        tvServiceFee.setText("Service fee: JOD " + String.format("%.2f", SERVICE_FEE));
        tvTotal.setText("Total: JOD " + String.format("%.2f", total));
    }

    @SuppressLint("SetTextI18n")
    private void loadCards() {
        rgPaymentMethod.removeAllViews();

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "guest";

        RadioButton rbCash = new RadioButton(this);
        rbCash.setText("Cash");
        rbCash.setTextColor(getResources().getColor(R.color.foreground));
        rgPaymentMethod.addView(rbCash);

        if (tempCardNumber != null && !tempCardNumber.isEmpty()) {
            RadioButton rbTemp = new RadioButton(this);
            rbTemp.setText("**** " + tempCardNumber.substring(tempCardNumber.length() - 4)
                    + " (" + tempCardHolder + ")");
            rbTemp.setTextColor(getResources().getColor(R.color.foreground));
            rgPaymentMethod.addView(rbTemp, 0);
            rbTemp.setChecked(true);
        } else {
            rbCash.setChecked(true);
        }

        CardStorage.getCards(this, cards -> {
            for (CardStorage.CardModel card : cards) {
                LinearLayout wrapper = new LinearLayout(this);
                wrapper.setOrientation(LinearLayout.HORIZONTAL);

                RadioButton rb = new RadioButton(this);
                rb.setText("**** " + card.getCardNumber() + " (" + card.getHolderName() + ")");
                rb.setTextColor(getResources().getColor(R.color.foreground));
                rb.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                ImageButton btnRemove = new ImageButton(this);
                btnRemove.setImageResource(R.drawable.ic_deleted);
                btnRemove.setBackgroundColor(Color.TRANSPARENT);

                btnRemove.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Remove Card")
                            .setMessage("Are you sure you want to delete this card?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                CardStorage.deleteCard(userId, card.getId(), () -> {
                                    Toast.makeText(this, "Card deleted", Toast.LENGTH_SHORT).show();
                                    loadCards();
                                });
                            })
                            .setNegativeButton("No", null)
                            .show();
                });

                wrapper.addView(rb);
                wrapper.addView(btnRemove);
                rgPaymentMethod.addView(wrapper, 0);

                if (newCardSaved && cards.get(cards.size() - 1).getId().equals(card.getId())) {
                    rb.setChecked(true);
                }
            }
        });
    }

    private void placeOrder() {
        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        RadioButton selectedRadio = findViewById(selectedId);

        if (selectedRadio != null && selectedRadio.getText().toString().equals("Cash")) {
            placeOrderWithStatus("cash", "pending");
        } else {
            placeOrderWithStatus("stripe", "completed");
        }
    }

    private void placeOrderWithStatus(String paymentMethodId, String paymentStatus) {
        if (CartManager.getCartItems().isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "guest";

        double subtotal = CartManager.getSubtotal();
        double total = subtotal - DISCOUNT + DELIVERY_FEE + SERVICE_FEE;

        String restaurantName = !CartManager.getCartItems().isEmpty()
                ? CartManager.getCartItems().get(0).getRestaurant() : "";

        ArrayList<String> itemsList = new ArrayList<>();
        for (com.farah.foodapp.cart.CartItem item : CartManager.getCartItems()) {
            String desc = item.getQuantity() + "x " + item.getName()
                    + (item.getSize() != null ? " (" + item.getSize() + ")" : "")
                    + " - " + String.format("%.2f JOD", item.getPrice() * item.getQuantity());
            itemsList.add(desc);
        }

        HashMap<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("restaurantName", restaurantName);
        orderData.put("paymentMethod", paymentMethodId);
        orderData.put("paymentStatus", paymentStatus);
        orderData.put("total", total);
        orderData.put("status", "Preparing");
        orderData.put("address", tvAddress.getText().toString());
        orderData.put("lat", selectedLat);
        orderData.put("lon", selectedLon);
        orderData.put("createdAt", System.currentTimeMillis());
        orderData.put("items", itemsList);
        orderData.put("eta", "30-40 min");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders")
                .add(orderData)
                .addOnSuccessListener(doc -> {
                    db.collection("orders").document(doc.getId())
                            .update("id", doc.getId());
                    Toast.makeText(this, "Order placed using: " + paymentMethodId, Toast.LENGTH_SHORT).show();
                    CartManager.clearCart();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showOrderNotification(String title, String message) {
        String channelId = "order_channel";
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Order Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo_app)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
