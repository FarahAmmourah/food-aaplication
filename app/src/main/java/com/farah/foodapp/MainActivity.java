package com.farah.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = findViewById(R.id.appLogo);
        TextView appName = findViewById(R.id.appName);

        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        logo.startAnimation(zoomIn);

        new Handler().postDelayed(() -> {
            appName.setVisibility(TextView.VISIBLE);
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            appName.startAnimation(slideUp);
            appName.startAnimation(fadeIn);
        }, 1000);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME);
    }
}
