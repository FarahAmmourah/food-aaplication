package com.farah.foodapp.reel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farah.foodapp.R;
import com.farah.foodapp.cart.CartManager;
import com.farah.foodapp.comments.CommentsDialog;
import com.farah.foodapp.menu.RestaurantDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(uid)
                .collection("favorites")
                .document(reel.getReelId())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        reel.setLiked(true);
                        holder.btnLike.setColorFilter(Color.RED);
                    } else {
                        reel.setLiked(false);
                        holder.btnLike.setColorFilter(Color.WHITE);
                    }
                });

        ExoPlayer player = new ExoPlayer.Builder(context).build();
        holder.playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(reel.getVideoUrl()));
        player.setMediaItem(mediaItem);
        player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
        player.prepare();
        player.pause();

<<<<<<< Updated upstream
        holder.playerView.setOnTouchListener((v, event) -> {
            if (holder.playerView.getPlayer() == null) return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    holder.playerView.getPlayer().pause();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    holder.playerView.getPlayer().play();
                    return true;
            }
            return false;
=======
        holder.playerView.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                holder.ivPlayPause.setImageResource(R.drawable.ic_play);
                holder.ivPlayPause.setVisibility(View.VISIBLE);
            } else {
                player.play();
                holder.ivPlayPause.setImageResource(R.drawable.ic_pause);
                holder.ivPlayPause.setVisibility(View.VISIBLE);
            }
            holder.ivPlayPause.postDelayed(() -> holder.ivPlayPause.setVisibility(View.GONE), 800);
>>>>>>> Stashed changes
        });

        holder.tvTitle.setText(reel.getTitle());
        holder.tvRestaurant.setText(reel.getRestaurant());
        holder.btnOrder.setText("ORDER NOW - $" + reel.getPrice());
        holder.tvLikeCount.setText(String.valueOf(reel.getLikesCount()));
        holder.tvCommentCount.setText(String.valueOf(reel.getCommentsCount()));

        holder.tvRestaurant.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestaurantDetailsActivity.class);
            intent.putExtra("restaurantId", reel.getRestaurantId());
            context.startActivity(intent);
        });

        holder.btnLike.setOnClickListener(v -> {
            if (uid == null) return;

            if (reel.isLiked()) {
                holder.btnLike.setColorFilter(Color.WHITE);
                reel.setLikesCount(reel.getLikesCount() - 1);
                reel.setLiked(false);
                db.collection("users").document(uid)
                        .collection("favorites").document(reel.getReelId()).delete();
            } else {
                holder.btnLike.setColorFilter(Color.RED);
                reel.setLikesCount(reel.getLikesCount() + 1);
                reel.setLiked(true);

                Map<String, Object> fav = new HashMap<>();
                fav.put("videoUrl", reel.getVideoUrl());
                fav.put("title", reel.getTitle());
                fav.put("restaurant", reel.getRestaurant());
                fav.put("price", reel.getPrice());
                fav.put("reelId", reel.getReelId());

                db.collection("users").document(uid)
                        .collection("favorites").document(reel.getReelId()).set(fav);
            }
            holder.tvLikeCount.setText(String.valueOf(reel.getLikesCount()));
        });

        holder.btnComment.setOnClickListener(v -> {
            CommentsDialog dialog = new CommentsDialog(context, reel.getComments(), reel, (ReelsActivity) context);
            dialog.show();
        });

        holder.btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check this out: " + reel.getVideoUrl());
            context.startActivity(Intent.createChooser(shareIntent, "Share Reel via"));
        });

        holder.btnOrder.setOnClickListener(v -> {
<<<<<<< Updated upstream
            CartManager.addItem(
                    reel.getTitle(),
                    reel.getRestaurant(),
                    "Regular",
                    reel.getPrice(),
                    reel.getImageUrl()
            );
            Toast.makeText(context, reel.getTitle() + " added to cart!", Toast.LENGTH_SHORT).show();
            if (context instanceof ReelsActivity) {
                ((ReelsActivity) context).updateCartBadge();
            }
=======
            db.collection("restaurants")
                    .document(reel.getRestaurantId())
                    .collection("menu")
                    .whereEqualTo("name", reel.getTitle())
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                            String name = doc.getString("name");
                            double price = doc.getDouble("price");
                            String imageUrl = doc.getString("imageUrl");
                            CartManager.addItem(
                                    name,
                                    reel.getRestaurant(),
                                    "Regular",
                                    price,
                                    imageUrl
                            );
                            Toast.makeText(context, name + " added to cart!", Toast.LENGTH_SHORT).show();
                            if (context instanceof ReelsActivity) {
                                ((ReelsActivity) context).updateCartBadge();
                            }
                        } else {
                            Toast.makeText(context, "Item not found in menu!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
>>>>>>> Stashed changes
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
        ImageView ivPlayPause;

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
            ivPlayPause = itemView.findViewById(R.id.ivPlayPause);
        }
    }
}
