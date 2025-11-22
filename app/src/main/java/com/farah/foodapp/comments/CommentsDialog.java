package com.farah.foodapp.comments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.farah.foodapp.reel.ReelItem;
import com.farah.foodapp.reel.ReelsActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentsDialog extends BottomSheetDialog {

    private List<String> comments;
    private CommentAdapter adapter;
    private ReelItem reel;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public CommentsDialog(@NonNull Context context, List<String> comments, ReelItem reel, ReelsActivity reelsActivity) {
        super(context);
        this.comments = comments;
        this.reel = reel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_comments, null);
        setContentView(view);

        View bottomSheet = findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }

        RecyclerView recyclerComments = view.findViewById(R.id.recyclerComments);
        EditText etComment = view.findViewById(R.id.etComment);
        Button btnSend = view.findViewById(R.id.btnSend);
        Button btnAnalyze = view.findViewById(R.id.btnAnalyze);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new CommentAdapter(comments);
        recyclerComments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerComments.setAdapter(adapter);

        btnAnalyze.setOnClickListener(v -> {

            int positive = 0;
            int negative = 0;
            int neutral = 0;

            for (String comment : comments) {

                String pureComment = comment.contains(":")
                        ? comment.split(":", 2)[1].trim()
                        : comment;

                String result = detectSentiment(pureComment.toLowerCase());

                switch (result) {
                    case "Positive":
                        positive++;
                        break;
                    case "Negative":
                        negative++;
                        break;
                    default:
                        neutral++;
                        break;
                }
            }

            int total = comments.size();
            if (total == 0) {
                BottomSheetDialog dialog = new BottomSheetDialog(getContext());
                View v2 = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sentiment_result, null);
                dialog.setContentView(v2);

                Button btnClose = v2.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(x -> dialog.dismiss());
                dialog.show();
                return;
            }

            showSentimentBottomSheet(positive, negative, neutral);
        });

        btnSend.setOnClickListener(v -> {
            String newComment = etComment.getText().toString().trim();
            if (!newComment.isEmpty()) {
                FirebaseUser currentUser = auth.getCurrentUser();
                String userName;

                if (currentUser != null) {
                    if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                        userName = currentUser.getDisplayName();
                    } else if (currentUser.getEmail() != null) {
                        userName = currentUser.getEmail();
                    } else {
                        userName = "Anonymous";
                    }
                } else {
                    userName = "Anonymous";
                }

                String formattedComment = userName + ": " + newComment;

                comments.add(formattedComment);
                adapter.notifyItemInserted(comments.size() - 1);
                recyclerComments.scrollToPosition(comments.size() - 1);
                etComment.setText("");

                if (reel != null && reel.getReelId() != null) {
                    db.collection("reels")
                            .document(reel.getReelId())
                            .update("comments", FieldValue.arrayUnion(formattedComment))
                            .addOnSuccessListener(a -> {
                                db.collection("reels")
                                        .document(reel.getReelId())
                                        .update("commentsCount", comments.size());
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed to save comment", Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(getContext(), "Type a comment first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSentimentBottomSheet(int posCount, int negCount, int neutralCount) {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sentiment_result, null);
        dialog.setContentView(v);

        int total = posCount + negCount + neutralCount;

        String posP = (int) ((posCount * 100.0f) / total) + "%";
        String negP = (int) ((negCount * 100.0f) / total) + "%";
        String neuP = (int) ((neutralCount * 100.0f) / total) + "%";

        Button btnClose = v.findViewById(R.id.btnClose);
        ((android.widget.TextView) v.findViewById(R.id.tvPositive)).setText("Positive: " + posCount + " (" + posP + ")");
        ((android.widget.TextView) v.findViewById(R.id.tvNegative)).setText("Negative: " + negCount + " (" + negP + ")");
        ((android.widget.TextView) v.findViewById(R.id.tvNeutral)).setText("Neutral: " + neutralCount + " (" + neuP + ")");

        btnClose.setOnClickListener(x -> dialog.dismiss());

        dialog.show();
    }

    private String detectSentiment(String text) {

        String[] positive = {
                "good", "great", "amazing", "tasty", "delicious",
                "perfect", "nice", "fresh", "wonderful", "love", "fantastic", "fast"
        };

        String[] negative = {
                "bad", "terrible", "cold", "slow", "awful",
                "disappointed", "worse", "late", "dirty", "expensive"
        };

        int pos = 0;
        int neg = 0;

        for (String w : positive) {
            if (text.contains(w)) pos++;
        }

        for (String w : negative) {
            if (text.contains(w)) neg++;
        }

        if (pos > neg) return "Positive";
        if (neg > pos) return "Negative";
        return "Neutral";
    }
}
