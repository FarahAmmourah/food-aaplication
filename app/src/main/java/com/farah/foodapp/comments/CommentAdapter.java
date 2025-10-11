package com.farah.foodapp.comments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<String> comments;

    public CommentAdapter(List<String> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        String commentText = comments.get(position);

        if (commentText.contains(":")) {
            String[] parts = commentText.split(":", 2);
            holder.tvUsername.setText(parts[0].trim());
            holder.tvComment.setText(parts[1].trim());
        } else {
            holder.tvUsername.setText("User");
            holder.tvComment.setText(commentText);
        }

        holder.imgLike.setOnClickListener(v -> {
            if (holder.imgLike.getTag() == null || !(boolean) holder.imgLike.getTag()) {
                holder.imgLike.setColorFilter(holder.itemView.getContext().getColor(R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.imgLike.setTag(true);
            } else {
                holder.imgLike.setColorFilter(holder.itemView.getContext().getColor(android.R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.imgLike.setTag(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile, imgLike;
        TextView tvUsername, tvComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            imgLike = itemView.findViewById(R.id.imgLike);
        }
    }
}
