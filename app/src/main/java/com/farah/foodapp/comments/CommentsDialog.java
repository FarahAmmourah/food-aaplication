package com.farah.foodapp.comments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.farah.foodapp.reel.ReelItem;
import com.farah.foodapp.reel.ReelsActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class CommentsDialog extends BottomSheetDialog {

    private List<String> comments;
    private CommentAdapter adapter;

    public CommentsDialog(@NonNull Context context, List<String> comments, ReelItem reel, ReelsActivity reelsActivity) {
        super(context);
        this.comments = comments;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_comments, null);
        setContentView(view);

        RecyclerView recyclerComments = view.findViewById(R.id.recyclerComments);
        EditText etComment = view.findViewById(R.id.etComment);
        Button btnSend = view.findViewById(R.id.btnSend);

        adapter = new CommentAdapter(comments);
        recyclerComments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerComments.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String newComment = etComment.getText().toString().trim();
            if (!newComment.isEmpty()) {
                comments.add(newComment);
                adapter.notifyItemInserted(comments.size() - 1);
                recyclerComments.scrollToPosition(comments.size() - 1);
                etComment.setText("");
            }
        });
    }
}
