package com.farah.foodapp.admin.admin_reels;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.farah.foodapp.R;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class ReelDetailActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reel_detail);

        playerView = findViewById(R.id.playerView);
        progressBar = findViewById(R.id.progressBar);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvRestaurant = findViewById(R.id.tvRestaurant);
        Button btnOrder = findViewById(R.id.btnOrder);

        // استقبل البيانات من Intent
        String videoUrl = getIntent().getStringExtra("videoUrl");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");

        tvTitle.setText(title);
        tvRestaurant.setText(description);

        // إعداد ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        if (videoUrl != null) {
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
            player.setMediaItem(mediaItem);
            player.prepare();
            player.setPlayWhenReady(true); // يشتغل مباشرة
            player.setRepeatMode(Player.REPEAT_MODE_ONE); // يعيد نفسه
        }

        // إظهار/إخفاء اللودينغ حسب حالة الفيديو
        player.addListener(new Player.Listener() {
            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                progressBar.setVisibility(isLoading ? ProgressBar.VISIBLE : ProgressBar.GONE);
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                } else if (state == Player.STATE_READY) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        // زر الأوردر (ممكن توصل مع شاشة الطلبات)
        btnOrder.setOnClickListener(v -> {
            // TODO: اربطها بعملية الطلب
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
        }
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
