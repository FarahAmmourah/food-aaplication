package com.farah.foodapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ReelsActivity extends AppCompatActivity {

    private VideoView videoView;
    private int currentIndex = 0;
    private int[] videos = {
            R.raw.reel1,
            R.raw.reel2,
            R.raw.reel3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels);

        videoView = findViewById(R.id.videoView);
        Button btnBack = findViewById(R.id.btnBack);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        playVideo(currentIndex);

        // سوايب بين الفيديوهات
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY < 0) {
                        // Swipe up
                        nextVideo();
                    } else {
                        // Swipe down
                        previousVideo();
                    }
                    return true;
                }
                return false;
            }
        });

        videoView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        btnBack.setOnClickListener(v -> finish());
    }

    private void playVideo(int index) {
        if (index >= 0 && index < videos.length) {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + videos[index]);
            videoView.setVideoURI(uri);
            videoView.setOnPreparedListener(mp -> mp.setLooping(true));
            videoView.start();
        }
    }

    private void nextVideo() {
        currentIndex++;
        if (currentIndex >= videos.length) {
            currentIndex = 0;
        }
        playVideo(currentIndex);
    }

    private void previousVideo() {
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = videos.length - 1;
        }
        playVideo(currentIndex);
    }
}
