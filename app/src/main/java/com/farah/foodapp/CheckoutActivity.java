package com.farah.foodapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.farah.foodapp.cart.CartManager;
import com.google.firebase.auth.FirebaseAuth;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvSubtotal, tvDiscount, tvDeliveryFee, tvServiceFee, tvTotal;
    private RadioGroup rgPaymentMethod;
    private TextView btnAddCard;

    private static final double DELIVERY_FEE = 3.00;
    private static final double SERVICE_FEE = 0.20;
    private static final double DISCOUNT = 1.00;

    private ActivityResultLauncher<Intent> addCardLauncher;

    private String tempCardNumber, tempCardExpiry, tempCardHolder;

    private boolean newCardSaved = false;

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

        findViewById(R.id.btnPlaceOrder).setOnClickListener(v -> placeOrder());
        findViewById(R.id.btnCancelOrder).setOnClickListener(v -> finish());

        addCardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
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
        rgPaymentMethod.removeAllViews();

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "guest";

        RadioButton rbCash = new RadioButton(this);
        rbCash.setId(R.id.rbCash);
        rbCash.setText("Cash");
        rbCash.setTextColor(getResources().getColor(R.color.foreground));
        rgPaymentMethod.addView(rbCash);
        rbCash.setChecked(true);

        if (tempCardNumber != null && !tempCardNumber.isEmpty()) {
            RadioButton rbTemp = new RadioButton(this);
            rbTemp.setText("**** " + tempCardNumber.substring(tempCardNumber.length() - 4)
                    + " (" + tempCardHolder + ")");
            rbTemp.setTextColor(getResources().getColor(R.color.foreground));
            rgPaymentMethod.addView(rbTemp, 0);
            rbTemp.setChecked(true);
        }

        CardStorage.getCards(this, cards -> {
            for (CardStorage.CardModel card : cards) {
                LinearLayout cardLayout = new LinearLayout(this);
                cardLayout.setOrientation(LinearLayout.HORIZONTAL);

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

                cardLayout.addView(rb);
                cardLayout.addView(btnRemove);

                rgPaymentMethod.addView(cardLayout, 0);

                if (newCardSaved && cards.get(cards.size() - 1).getId().equals(card.getId())) {
                    rb.setChecked(true);
                }
            }
        });
    }

    private void placeOrder() {
        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        RadioButton selected = findViewById(selectedId);

        if (selected != null) {
            Toast.makeText(this, "Order placed using: " + selected.getText(), Toast.LENGTH_SHORT).show();

            CartManager.clearCart();

            tempCardNumber = null;
            tempCardExpiry = null;
            tempCardHolder = null;
            newCardSaved = false;

            Intent resultIntent = new Intent();
            resultIntent.putExtra("orderPlaced", true);
            setResult(RESULT_OK, resultIntent);

            finish();
        } else {
            Toast.makeText(this, "Please select payment method", Toast.LENGTH_SHORT).show();
        }
    }
}
