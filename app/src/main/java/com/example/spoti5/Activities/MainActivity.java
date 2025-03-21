package com.example.spoti5.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spoti5.Adapters.ViewpagerAdapter;
import com.example.spoti5.Fragments.HomeFragment;
import com.example.spoti5.Fragments.HomeFragment2;
import com.example.spoti5.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private FrameLayout fragmentHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = findViewById(R.id.view_pager);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        ViewpagerAdapter viewpagerAdapter = new ViewpagerAdapter(this);
        mViewPager.setAdapter(viewpagerAdapter);

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mBottomNavigationView.getMenu().getItem(position).setChecked(true);

                // Sửa lỗi setTitle
                if (getSupportActionBar() != null) {
                    switch (position) {
                        case 0:
                            getSupportActionBar().setTitle("Person");
                            break;
                        case 1:
                            getSupportActionBar().setTitle("Home");
                            break;
                        case 2:
                            getSupportActionBar().setTitle("Settings");
                            break;
                    }
                }
            }
        });

        mBottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.person:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.home:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.setting:
                    mViewPager.setCurrentItem(2);
                    break;
            }
            return true;
        });

        // Xử lý sự kiện chọn item trong BottomNavigationView
        mBottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.person:
                        mViewPager.setCurrentItem(0);
                        getSupportActionBar().setTitle("Person");
                        return true;
                    case R.id.home:
                        mViewPager.setCurrentItem(1);
                        getSupportActionBar().setTitle("Home");
                        return true;
                    case R.id.setting:
                        mViewPager.setCurrentItem(2);
                        getSupportActionBar().setTitle("Settings");
                        return true;
                }
                return false;
            }
        });

        // Thiết lập tiêu đề ban đầu
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Person");
        }
    }
}