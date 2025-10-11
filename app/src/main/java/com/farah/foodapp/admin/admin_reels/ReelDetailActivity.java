package com.farah.foodapp.admin.admin_reels;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;

import com.farah.foodapp.R;

public class ReelDetailActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private ProgressBar progressBar;

    private boolean isLiked = false;
    private int likeCount = 0;
    private int commentCount = 0;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reel_detail);

        // Views
        playerView = findViewById(R.id.playerView);
        progressBar = findViewById(R.id.progressBar);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvRestaurant = findViewById(R.id.tvRestaurant);
        ImageButton btnBack = findViewById(R.id.btnBack);

        ImageButton btnLike = findViewById(R.id.btnLike);
        TextView tvLikeCount = findViewById(R.id.tvLikeCount);
        ImageButton btnComment = findViewById(R.id.btnComment);
        TextView tvCommentCount = findViewById(R.id.tvCommentCount);
        ImageButton btnShare = findViewById(R.id.btnShare);

        // بيانات من Intent
        String videoUrl = getIntent().getStringExtra("videoUrl");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");

        tvTitle.setText(title);
        tvRestaurant.setText(description);

        CacheDataSource.Factory cacheFactory = VideoCache.getCacheDataSourceFactory(this);

        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        1000,
                        2000,
                        500,
                        500
                )
                .build();

        player = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(cacheFactory))
                .setLoadControl(loadControl)
                .build();

        playerView.setPlayer(player);

        if (videoUrl != null) {
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
            player.setMediaItem(mediaItem);
            player.prepare();
            player.setPlayWhenReady(true);
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
        }

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                } else if (state == Player.STATE_READY) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        btnBack.setOnClickListener(v -> onBackPressed());

        btnLike.setOnClickListener(v -> {
            if (isLiked) {
                btnLike.setColorFilter(Color.WHITE);
                likeCount--;
            } else {
                btnLike.setColorFilter(Color.RED);
                likeCount++;
            }
            isLiked = !isLiked;
            tvLikeCount.setText(String.valueOf(likeCount));
        });

        btnComment.setOnClickListener(v -> {
            commentCount++;
            tvCommentCount.setText(String.valueOf(commentCount));
            Toast.makeText(this, "Open comments dialog here...", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check this reel: " + videoUrl);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) player.setPlayWhenReady(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
