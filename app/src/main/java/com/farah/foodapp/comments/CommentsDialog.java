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

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new CommentAdapter(comments);
        recyclerComments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerComments.setAdapter(adapter);

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
}
