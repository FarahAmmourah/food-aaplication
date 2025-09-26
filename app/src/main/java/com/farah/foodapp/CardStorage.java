package com.farah.foodapp;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardStorage {

    private static final String COLLECTION_NAME = "cards";

    /**
     * Callback for save result.
     */
    public interface CardSaveCallback {
        void onComplete(boolean success);
    }

    /**
     * Save card into Firestore under: cards/{userId}/userCards/{autoId}
     * We validate cardNumber (digits only, length 12..19), expiry (MM/YY), holderName non-empty.
     * We store only the last 4 digits (for display) + expiry + holderName.
     */
    public static void saveCard(Context context, String cardNumber, String expiry, String holderName, CardSaveCallback callback) {
        if (context == null) {
            if (callback != null) callback.onComplete(false);
            return;
        }

        // Basic validation (defensive, though CheckoutActivity also validates)
        if (cardNumber == null || !cardNumber.matches("\\d+")) {
            Toast.makeText(context, "Card number must contain digits only", Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onComplete(false);
            return;
        }
        if (cardNumber.length() < 12 || cardNumber.length() > 19) {
            Toast.makeText(context, "Card number must be between 12 and 19 digits", Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onComplete(false);
            return;
        }
        if (expiry == null || !expiry.matches("(0[1-9]|1[0-2])/(\\d{2})")) {
            Toast.makeText(context, "Expiry must be in format MM/YY", Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onComplete(false);
            return;
        }
        if (holderName == null || holderName.trim().isEmpty()) {
            Toast.makeText(context, "Card holder name is required", Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onComplete(false);
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onComplete(false);
            return;
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String last4 = cardNumber.substring(cardNumber.length() - 4);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCards = db.collection(COLLECTION_NAME)
                .document(userId)
                .collection("userCards");

        CardModel card = new CardModel(last4, expiry, holderName);

        userCards.add(card)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Card saved in Firebase", Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onComplete(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error saving card: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onComplete(false);
                });
    }

    /**
     * Overload for backward compatibility: save without callback.
     */
    public static void saveCard(Context context, String cardNumber, String expiry, String holderName) {
        saveCard(context, cardNumber, expiry, holderName, null);
    }

    /**
     * Callback used when cards are loaded from Firestore.
     */
    public interface CardListCallback {
        void onCardsLoaded(List<CardModel> cards);
    }

    /**
     * Fetch all cards for current user (cards/{userId}/userCards).
     * Each CardModel.cardNumber contains the stored last4.
     */
    public static void getCards(Context context, CardListCallback callback) {
        if (context == null) {
            if (callback != null) callback.onCardsLoaded(new ArrayList<>());
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onCardsLoaded(new ArrayList<>());
            return;
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_NAME)
                .document(userId)
                .collection("userCards")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<CardModel> cards = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Map<String, Object> data = doc.getData();
                        if (data != null) {
                            Object numObj = data.get("cardNumber");
                            Object expiryObj = data.get("expiry");
                            Object holderObj = data.get("holderName");
                            String number = numObj == null ? "" : String.valueOf(numObj);
                            String expiry = expiryObj == null ? "" : String.valueOf(expiryObj);
                            String holder = holderObj == null ? "" : String.valueOf(holderObj);
                            cards.add(new CardModel(number, expiry, holder));
                        }
                    }
                    if (callback != null) callback.onCardsLoaded(cards);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error fetching cards: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onCardsLoaded(new ArrayList<>());
                });
    }

    /**
     * Simple POJO for card data (we store only last4 in cardNumber).
     */
    public static class CardModel {
        private String cardNumber; // last4
        private String expiry;
        private String holderName;

        public CardModel() { }

        public CardModel(String cardNumber, String expiry, String holderName) {
            this.cardNumber = cardNumber;
            this.expiry = expiry;
            this.holderName = holderName;
        }

        public String getCardNumber() { return cardNumber; }
        public String getExpiry() { return expiry; }
        public String getHolderName() { return holderName; }

        public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
        public void setExpiry(String expiry) { this.expiry = expiry; }
        public void setHolderName(String holderName) { this.holderName = holderName; }
    }
}
