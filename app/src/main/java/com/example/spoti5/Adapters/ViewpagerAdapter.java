package com.example.spoti5.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.spoti5.Fragments.HomeFragment;
import com.example.spoti5.Fragments.HomeFragment2;
import com.example.spoti5.Fragments.SettingsFragment;

public class ViewpagerAdapter extends FragmentStateAdapter {
    public ViewpagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public int getItemCount() {
        return 3;  // Có 3 fragment
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new HomeFragment2();
            case 2:
                return new SettingsFragment();
            default:
                return new HomeFragment();  // Trả về Fragment mặc định nếu lỗi
        }
    }
}
