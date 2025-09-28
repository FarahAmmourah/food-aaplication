package com.farah.foodapp.cards;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.farah.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddCardActivity extends AppCompatActivity {

    private EditText etCardNumber, etExpiry, etHolder;
    private Switch switchSaveCard;
    private Button btnSaveCard, btnCancelCard;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etHolder = findViewById(R.id.etHolder);
        switchSaveCard = findViewById(R.id.switchSaveCard);
        btnSaveCard = findViewById(R.id.btnSaveCard);
        btnCancelCard = findViewById(R.id.btnCancelCard);

        db = FirebaseFirestore.getInstance();

        switchSaveCard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchSaveCard.getThumbDrawable().setTint(getColor(R.color.primary));
                switchSaveCard.getTrackDrawable().setTint(getColor(R.color.primary_light));
            } else {
                switchSaveCard.getThumbDrawable().setTint(getColor(R.color.gray));
                switchSaveCard.getTrackDrawable().setTint(getColor(R.color.gray_light));
            }
        });

        btnSaveCard.setOnClickListener(v -> saveOrUseCard());
        btnCancelCard.setOnClickListener(v -> finish());
    }


    private void saveOrUseCard() {
        String number = etCardNumber.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();
        String holder = etHolder.getText().toString().trim();

        if (number.isEmpty() || expiry.isEmpty() || holder.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String last4 = number.length() >= 4 ? number.substring(number.length() - 4) : number;

        if (switchSaveCard.isChecked()) {
            String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : "guest";

            Map<String, Object> card = new HashMap<>();
            card.put("cardNumber", last4);
            card.put("expiry", expiry);
            card.put("holderName", holder);

            db.collection("cards")
                    .document(userId)
                    .collection("userCards")
                    .add(card)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Card saved", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error saving card", Toast.LENGTH_SHORT).show());
        } else {
            Intent intent = new Intent();
            intent.putExtra("tempCardNumber", number);
            intent.putExtra("tempCardExpiry", expiry);
            intent.putExtra("tempCardHolder", holder);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
