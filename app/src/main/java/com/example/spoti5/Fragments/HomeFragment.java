package com.example.spoti5.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.example.spoti5.Adapters.SliderAdapter;
import com.example.spoti5.R;

import java.util.ArrayList;
import java.util.Timer;

import com.example.spoti5.Models.SliderModel;
import com.example.spoti5.Utils.SliderTimer;
import com.google.android.material.tabs.TabLayout;


public class HomeFragment extends Fragment {
    private ViewPager slider;
    private ArrayList<SliderModel> sliderModelList;
    private SliderAdapter sliderAdapter;
    private TabLayout sliderIndicator;
    private Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        slider = view.findViewById(R.id.slider);
        sliderIndicator = view.findViewById(R.id.slider_indicator);

        sliderModelList = new ArrayList<>();
        timer = new Timer();

        sliderModelList.add(new SliderModel(R.drawable.poster1, "Playlist #1"));
        sliderModelList.add(new SliderModel(R.drawable.poster2, "Playlist #2"));
        sliderModelList.add(new SliderModel(R.drawable.poster3, "Playlist #3"));
        sliderModelList.add(new SliderModel(R.drawable.poster4, "Playlist #4"));

        sliderAdapter = new SliderAdapter(getContext(), sliderModelList);
        slider.setAdapter(sliderAdapter);
        sliderIndicator.setupWithViewPager(slider);

        timer.scheduleAtFixedRate(new SliderTimer(getActivity(), slider, sliderModelList.size()), 4000, 6000);

        return view;
    }
}