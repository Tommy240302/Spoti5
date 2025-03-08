package com.example.spoti5.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spoti5.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable runnable;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler(Looper.getMainLooper()); // Sửa lỗi cảnh báo
        mAuth = FirebaseAuth.getInstance();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (mAuth.getCurrentUser() != null) {  // Sửa lỗi có thể gây crash
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, RegisterActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

}