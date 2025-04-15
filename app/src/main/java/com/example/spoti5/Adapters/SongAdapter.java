package com.example.spoti5.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;

import java.util.List;

public class SongAdapter extends BaseAdapter {
    private Context context;
    private List<SongModel> songList;

    public SongAdapter(Context context, List<SongModel> songList) {
        this.context = context;
        this.songList = songList;
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_song, null);

        }

        ImageView imgSong = view.findViewById(R.id.imgSong);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvArtist = view.findViewById(R.id.tvArtist);

        SongModel song = songList.get(i);
        tvTitle.setText(song.getName());
        tvArtist.setText(song.getArtistName());

        Glide.with(context)
                .load(song.getImage())
                .placeholder(R.drawable.baseline_error_24)
                .into(imgSong);

        return view;
    }
}
