package com.farah.foodapp.reel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.farah.foodapp.cart.CartManager;
import com.farah.foodapp.comments.CommentsDialog;
import com.farah.foodapp.menu.RestaurantDetailsActivity;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

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

        // 🎥 تشغيل الفيديو
        ExoPlayer player = new ExoPlayer.Builder(context).build();
        holder.playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(reel.getVideoUrl()));
        player.setMediaItem(mediaItem);
        player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
        player.prepare();
        player.pause();

        // 📝 بيانات الريل
        holder.tvTitle.setText(reel.getTitle());
        holder.tvRestaurant.setText(reel.getRestaurant());
        holder.btnOrder.setText("ORDER NOW - $" + reel.getPrice());
        holder.tvLikeCount.setText(String.valueOf(reel.getLikesCount()));
        holder.tvCommentCount.setText(String.valueOf(reel.getCommentsCount()));

        // 🟠 فتح شاشة المنيو عند الضغط على اسم المطعم
        holder.tvRestaurant.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestaurantDetailsActivity.class);
            intent.putExtra("restaurantId", reel.getRestaurantId()); // تمرير ID
            context.startActivity(intent);
        });

        // ❤️ زر اللايك
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

        // 💬 زر الكومنت
        holder.btnComment.setOnClickListener(v -> {
            CommentsDialog dialog = new CommentsDialog(context, reel.getComments(), reel, (ReelsActivity) context);
            dialog.show();
        });

        // 📤 زر الشير
        holder.btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check this out: " + reel.getVideoUrl());
            context.startActivity(Intent.createChooser(shareIntent, "Share Reel via"));
        });

        // 🛒 زر الأوردر ناو (الإضافة إلى الكارت)
        holder.btnOrder.setOnClickListener(v -> {
            CartManager.addItem(
                    reel.getTitle(),          // اسم الأكلة
                    reel.getRestaurant(),     // اسم المطعم
                    "Regular",                // الحجم الافتراضي
                    reel.getPrice(),          // السعر
                    R.drawable.ic_launcher_background // صورة مؤقتة
            );

            Toast.makeText(context, reel.getTitle() + " added to cart!", Toast.LENGTH_SHORT).show();
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
        PlayerView playerView;
        TextView tvTitle, tvRestaurant, tvLikeCount, tvCommentCount;
        ImageButton btnLike, btnComment, btnShare;
        Button btnOrder;

        public ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnOrder = itemView.findViewById(R.id.btnOrder);
        }
    }
}
