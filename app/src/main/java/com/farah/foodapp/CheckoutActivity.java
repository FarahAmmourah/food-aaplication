package com.farah.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.farah.foodapp.cart.CartManager;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvSubtotal, tvDiscount, tvDeliveryFee, tvServiceFee, tvTotal;
    private RadioGroup rgPaymentMethod;
    private TextView btnAddCard;
    private Button btnPlaceOrder, btnCancelOrder;

    private static final double DELIVERY_FEE = 3.00;
    private static final double SERVICE_FEE = 0.20;
    private static final double DISCOUNT = 1.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvServiceFee = findViewById(R.id.tvServiceFee);
        tvTotal = findViewById(R.id.tvTotal);

        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        btnAddCard = findViewById(R.id.btnAddCard);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
        btnCancelOrder.setOnClickListener(v -> finish());

        btnAddCard.setOnClickListener(v -> showCardDialog());

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

    private void loadCards() {
        CardStorage.getCards(this, cards -> {
            rgPaymentMethod.removeAllViews();

            if (cards != null && !cards.isEmpty()) {
                for (CardStorage.CardModel card : cards) {
                    RadioButton rb = new RadioButton(this);
                    rb.setId(View.generateViewId());
                    String last4 = card.getCardNumber();
                    if (last4 == null) last4 = "";
                    rb.setText("**** " + last4);
                    rb.setTextColor(getResources().getColor(R.color.foreground));
                    rgPaymentMethod.addView(rb);
                }
            }

            RadioButton rbCash = new RadioButton(this);
            rbCash.setId(R.id.rbCash);
            rbCash.setText("Cash");
            rbCash.setTextColor(getResources().getColor(R.color.foreground));
            rgPaymentMethod.addView(rbCash);
            rbCash.setChecked(true);
        });
    }

    private void placeOrder() {
        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        RadioButton selected = selectedId != -1 ? findViewById(selectedId) : null;

        if (selected != null) {
            String method = selected.getText().toString();
            Toast.makeText(this, "Order placed using: " + method, Toast.LENGTH_SHORT).show();

            CartManager.clearCart();

            Intent intent = new Intent(this, com.farah.foodapp.cart.CartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Please select payment method", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCardDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        final EditText etCard = new EditText(this);
        etCard.setHint("Card Number (digits only)");
        etCard.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etCard);

        final EditText etExpiry = new EditText(this);
        etExpiry.setHint("Expiry (MM/YY)");
        layout.addView(etExpiry);

        final EditText etHolder = new EditText(this);
        etHolder.setHint("Card Holder Name");
        layout.addView(etHolder);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Card")
                .setView(layout)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dlg -> {
            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setOnClickListener(v -> {
                String cardNumber = etCard.getText().toString().trim();
                String expiry = etExpiry.getText().toString().trim();
                String holder = etHolder.getText().toString().trim();

                if (cardNumber.isEmpty() || !cardNumber.matches("\\d+")) {
                    Toast.makeText(CheckoutActivity.this, "Card number must contain digits only", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cardNumber.length() < 12 || cardNumber.length() > 19) {
                    Toast.makeText(CheckoutActivity.this, "Card number must be between 12 and 19 digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (expiry.isEmpty() || !expiry.matches("(0[1-9]|1[0-2])/(\\d{2})")) {
                    Toast.makeText(CheckoutActivity.this, "Expiry must be in format MM/YY", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (holder.isEmpty()) {
                    Toast.makeText(CheckoutActivity.this, "Card holder name is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                CardStorage.saveCard(CheckoutActivity.this, cardNumber, expiry, holder, success -> {
                    if (success) {
                        dialog.dismiss();
                        loadCards();
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Failed to save card, try again", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        dialog.show();
    }
}
