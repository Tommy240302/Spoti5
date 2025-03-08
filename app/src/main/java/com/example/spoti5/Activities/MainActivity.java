package com.example.spoti5.Activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.spoti5.Fragments.HomeFragment2;
import com.example.spoti5.R;

public class MainActivity extends AppCompatActivity {
    private FrameLayout fragmentHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentHolder = findViewById(R.id.main_frame_layout);

        setFragment(new HomeFragment2());
    }
    private void setFragment(HomeFragment2 fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolder.getId(), fragment);
        fragmentTransaction.commit();
    }
}