package com.farah.foodapp.cards;

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

    public interface CardSaveCallback {
        void onComplete(boolean success);
    }

    public static void saveCard(Context context, String cardNumber, String expiry, String holderName, CardSaveCallback callback) {
        if (context == null) {
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

        CardModel card = new CardModel(null, last4, expiry, holderName);

        userCards.add(card)
                .addOnSuccessListener(documentReference -> {
                    if (callback != null) callback.onComplete(true);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onComplete(false);
                });
    }

    public static void saveCard(Context context, String cardNumber, String expiry, String holderName) {
        saveCard(context, cardNumber, expiry, holderName, null);
    }

    public interface CardListCallback {
        void onCardsLoaded(List<CardModel> cards);
    }

    public static void getCards(Context context, CardListCallback callback) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
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
                            String number = data.get("cardNumber") == null ? "" : String.valueOf(data.get("cardNumber"));
                            String expiry = data.get("expiry") == null ? "" : String.valueOf(data.get("expiry"));
                            String holder = data.get("holderName") == null ? "" : String.valueOf(data.get("holderName"));
                            String id = doc.getId();
                            cards.add(new CardModel(id, number, expiry, holder));
                        }
                    }
                    if (callback != null) callback.onCardsLoaded(cards);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onCardsLoaded(new ArrayList<>());
                });
    }

    public static void deleteCard(String userId, String cardId, Runnable onComplete) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_NAME)
                .document(userId)
                .collection("userCards")
                .document(cardId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (onComplete != null) onComplete.run();
                })
                .addOnFailureListener(e -> {
                    if (onComplete != null) onComplete.run();
                });
    }

    public static class CardModel {
        private String id;
        private String cardNumber; // last4
        private String expiry;
        private String holderName;

        public CardModel() {}

        public CardModel(String id, String cardNumber, String expiry, String holderName) {
            this.id = id;
            this.cardNumber = cardNumber;
            this.expiry = expiry;
            this.holderName = holderName;
        }

        public String getId() { return id; }
        public String getCardNumber() { return cardNumber; }
        public String getExpiry() { return expiry; }
        public String getHolderName() { return holderName; }

        public void setId(String id) { this.id = id; }
        public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
        public void setExpiry(String expiry) { this.expiry = expiry; }
        public void setHolderName(String holderName) { this.holderName = holderName; }
    }
}
