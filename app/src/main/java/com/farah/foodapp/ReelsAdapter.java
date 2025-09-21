package com.farah.foodapp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.comments.CommentsDialog;
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
        player.pause();

        holder.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        holder.tvTitle.setText(reel.getTitle());
        holder.tvRestaurant.setText(reel.getRestaurant());

        holder.tvLikeCount.setText(String.valueOf(reel.getLikesCount()));
        holder.tvCommentCount.setText(String.valueOf(reel.getCommentsCount()));

        // لايك
        holder.btnLike.setOnClickListener(v -> {
            if (reel.isLiked()) {
                holder.btnLike.setColorFilter(Color.WHITE);
                reel.setLikesCount(reel.getLikesCount() - 1);
                reel.setLiked(false);
            } else {
                holder.btnLike.setColorFilter(Color.RED);
                reel.setLikesCount(reel.getLikesCount() + 1);
                reel.setLiked(true);
            }
            holder.tvLikeCount.setText(String.valueOf(reel.getLikesCount()));
        });

        // فتح الكومنتات
        holder.btnComment.setOnClickListener(v -> {
            CommentsDialog dialog = new CommentsDialog(context, reel.getComments(), reel, (ReelsActivity) context);
            dialog.show();
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
        TextView tvTitle, tvRestaurant, tvLikeCount, tvCommentCount;
        ImageButton btnLike, btnComment, btnShare;

        public ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
        }
    }
}
