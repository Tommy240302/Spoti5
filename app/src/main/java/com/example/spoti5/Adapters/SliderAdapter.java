package com.example.spoti5.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.spoti5.Models.SliderModel;
import com.example.spoti5.R;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<SliderModel> sliderModelList;

    public SliderAdapter(Context context, ArrayList<SliderModel> sliderModelList) {
        this.context = context;
        this.sliderModelList = sliderModelList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_item, null);

        ImageView sliderImage = view.findViewById(R.id.slider_image);
        TextView sliderTitle = view.findViewById(R.id.slider_title);

        sliderImage.setImageResource(sliderModelList.get(position).getImage());
        sliderTitle.setText(sliderModelList.get(position).getSlideName());

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return sliderModelList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
