package com.farah.foodapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.ReelViewHolder> {

    private Context context;
    private List<ReelItem> reelList;

    public ReelsAdapter(Context context, List<ReelItem> reelList) {
        this.context = context;
        this.reelList = reelList;
    }

    @NonNull
    @Override
    public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reel, parent, false);
        return new ReelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
        ReelItem reel = reelList.get(position);

        Uri videoUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + reel.getVideoResId());
        holder.videoView.setVideoURI(videoUri);
        holder.videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            holder.videoView.start();
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        });

        holder.tvTitle.setText(reel.getTitle());
        holder.tvRestaurant.setText(reel.getRestaurant());

        // نستخدم متغير داخلي لتتبع حالة اللايك
        final boolean[] isLiked = {false};

        holder.btnLike.setOnClickListener(v -> {
            if (isLiked[0]) {
                // إذا كان متفعّل → رجعه أبيض
                holder.btnLike.setColorFilter(android.graphics.Color.WHITE);
                isLiked[0] = false;
            } else {
                // إذا مش متفعّل → خليه أحمر
                holder.btnLike.setColorFilter(android.graphics.Color.RED);
                isLiked[0] = true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return reelList.size();
    }

    public static class ReelViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView tvTitle, tvRestaurant;
        ImageButton btnLike, btnComment, btnShare;

        public ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }
}
