package com.farah.foodapp.comments;

import android.graphics.Color;
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

        String user;
        String text;

        if (commentText.contains(":")) {
            String[] parts = commentText.split(":", 2);
            user = parts[0].trim();
            text = parts[1].trim();
        } else {
            user = "User";
            text = commentText;
        }

        holder.tvUsername.setText(user);
        holder.tvComment.setText(text);

        String sentiment = detectSentiment(text.toLowerCase());

        switch (sentiment) {
            case "Positive":
                holder.tvComment.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "Negative":
                holder.tvComment.setTextColor(Color.parseColor("#C62828"));
                break;
            default:
                holder.tvComment.setTextColor(Color.parseColor("#555555"));
                break;
        }

        holder.imgLike.setOnClickListener(v -> {
            if (holder.imgLike.getTag() == null || !(boolean) holder.imgLike.getTag()) {
                holder.imgLike.setColorFilter(holder.itemView.getContext().getColor(R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.imgLike.setTag(true);
            } else {
                holder.imgLike.setColorFilter(holder.itemView.getContext().getColor(android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.imgLike.setTag(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private String detectSentiment(String text) {

        String[] positive = {
                "good", "great", "amazing", "tasty", "delicious",
                "perfect", "nice", "fresh", "wonderful", "love", "fantastic"
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
