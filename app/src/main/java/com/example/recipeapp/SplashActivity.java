package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    ImageView logoImage;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logoImage = findViewById(R.id.logo_image);
        mAuth = FirebaseAuth.getInstance();

        // Animate logo from left to center
        TranslateAnimation animation = new TranslateAnimation(-500, 0, 0, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        logoImage.startAnimation(animation);

        // Delay 3 seconds, then decide where to go
        new Handler().postDelayed(() -> {
            if (mAuth.getCurrentUser() != null) {
                // User is already logged in
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                // User is not logged in
                startActivity(new Intent(SplashActivity.this, login.class));
            }
            finish();
        }, 3000);
    }
}
