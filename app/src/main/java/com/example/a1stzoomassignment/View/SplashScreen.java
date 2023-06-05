package com.example.a1stzoomassignment.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.a1stzoomassignment.R;

import android.content.Intent;
import android.os.Handler;


public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }
}
