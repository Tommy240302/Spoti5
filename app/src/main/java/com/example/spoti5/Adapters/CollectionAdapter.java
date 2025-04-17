package com.example.spoti5.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.spoti5.Models.AlbumModel;
import com.example.spoti5.R;

import java.util.List;

public class CollectionAdapter extends BaseAdapter {
    private Context context;
    private List<AlbumModel> albums;

    public CollectionAdapter(Context context, List<AlbumModel> albums) {
        this.context = context;
        this.albums = albums;
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int i) {
        return albums.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_collection_songs, null);
        }

        TextView tvItemText = view.findViewById(R.id.item_text);
        ImageView ivItemImage = view.findViewById(R.id.item_image);

        AlbumModel album = albums.get(i);
        tvItemText.setText(album.getName());
        Glide.with(context)
                .load(album.getImageUrl()) // URL ảnh từ API
                .placeholder(R.drawable.baseline_error_24) // Ảnh tạm khi loading
                .into(ivItemImage);
        return view;
    }
}
