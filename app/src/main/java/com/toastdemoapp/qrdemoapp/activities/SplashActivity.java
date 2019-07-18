package com.toastdemoapp.qrdemoapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import  com.toastdemoapp.qrdemoapp.R;
import com.toastdemoapp.qrdemoapp.managers.UserManager;

public class SplashActivity extends AppCompatActivity {

    ImageView splash_imageview;
    private final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash_imageview = findViewById(R.id.splash_image_view);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        splash_imageview.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(UserManager.getInstance().isUserLoggedIn()){
                    intent = new Intent(SplashActivity.this , ScanActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this , LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
