package com.farah.foodapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;

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

        ExoPlayer player = new ExoPlayer.Builder(context).build();
        holder.playerView.setPlayer(player);

        Uri videoUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + reel.getVideoResId());
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
        player.prepare();
        player.pause(); // نوقفه، ReelsActivity مسؤول يشغله عند الحاجة

        holder.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        holder.tvTitle.setText(reel.getTitle());
        holder.tvRestaurant.setText(reel.getRestaurant());

        final boolean[] isLiked = {false};
        holder.btnLike.setOnClickListener(v -> {
            if (isLiked[0]) {
                holder.btnLike.setColorFilter(android.graphics.Color.WHITE);
                isLiked[0] = false;
            } else {
                holder.btnLike.setColorFilter(android.graphics.Color.RED);
                isLiked[0] = true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return reelList.size();
    }

    @Override
    public void onViewRecycled(@NonNull ReelViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.playerView.getPlayer() != null) {
            holder.playerView.getPlayer().release();
            holder.playerView.setPlayer(null);
        }
    }

    public static class ReelViewHolder extends RecyclerView.ViewHolder {
        StyledPlayerView playerView;
        TextView tvTitle, tvRestaurant;
        ImageButton btnLike, btnComment, btnShare;

        public ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }
}
