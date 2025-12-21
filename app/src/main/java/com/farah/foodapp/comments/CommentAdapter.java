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
import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Object> comments;

    public CommentAdapter(List<Object> comments) {
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

        Object obj = comments.get(position);
        String user = "User";
        String text = "";

        if (obj instanceof String) {
            String commentStr = (String) obj;
            if (commentStr.contains(":")) {
                String[] parts = commentStr.split(":", 2);
                user = parts[0].trim();
                text = parts[1].trim();
            } else {
                text = commentStr;
            }
        }

        else if (obj instanceof HashMap) {
            Object u = ((HashMap) obj).get("user");
            Object t = ((HashMap) obj).get("text");
            if (u != null) user = u.toString();
            if (t != null) text = t.toString();
        }

        else if (obj instanceof LinkedTreeMap) {
            Object u = ((LinkedTreeMap) obj).get("user");
            Object t = ((LinkedTreeMap) obj).get("text");
            if (u != null) user = u.toString();
            if (t != null) text = t.toString();
        }

        holder.tvUsername.setText(user);
        holder.tvComment.setText(text);
        holder.tvComment.setTextColor(Color.parseColor("#555555"));

        holder.imgLike.setOnClickListener(v -> {
            boolean liked = holder.imgLike.getTag() != null && (boolean) holder.imgLike.getTag();
            holder.imgLike.setColorFilter(
                    holder.itemView.getContext().getColor(liked ? android.R.color.black : R.color.red),
                    android.graphics.PorterDuff.Mode.SRC_IN
            );
            holder.imgLike.setTag(!liked);
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
