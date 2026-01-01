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
/* here we extend recycle view because we want to fill the recycle with info
* using this adapter*/
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Object> comments;

/* constructor has one att whic is list of objs becase of the firebase*/
    public CommentAdapter(List<Object> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    /*
    create the shape of a single element within a RecyclerView.*/
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {// prameters: ViewGroup parent == recycle view , viewType:nor /rep .... always nor

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);// turn xml to real view
        return new CommentViewHolder(view);// returns an Object  Type: CommentViewHolder
    }


    @Override // fill with data
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {//par1: name ,comm, likebt
        // par2: the num of the comm
        Object obj = comments.get(position);// why ob ? because of the type
        String user = "User"; //default vals
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

        holder.imgLike.setOnClickListener(v -> {//tag saves tem values
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

        public CommentViewHolder(@NonNull View itemView) {// view that comes from oncreate
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            imgLike = itemView.findViewById(R.id.imgLike);
        }
    }
}
