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
    private LayoutInflater inflater;

    private List<SongModel> originalList;

    public SongAdapter(Context context, List<SongModel> songList) {
        this.context = context;
        this.songList = songList;
        this.inflater = LayoutInflater.from(context);
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

    static class ViewHolder {
        ImageView imgSong;
        TextView tvTitle, tvArtist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_song, parent, false);
            holder = new ViewHolder();
            holder.imgSong = convertView.findViewById(R.id.imgSong);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvArtist = convertView.findViewById(R.id.tvArtist);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SongModel song = songList.get(position);
        holder.tvTitle.setText(song.getName());
        holder.tvArtist.setText(song.getArtistName());

        Glide.with(context)
                .load(song.getImage())
                .placeholder(R.drawable.baseline_error_24)
                .centerCrop()
                .into(holder.imgSong);

        return convertView;
    }
    public void filter(String query) {
        songList.clear();
        if (query.isEmpty()) {
            songList.addAll(originalList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (SongModel song : originalList) {
                if (song.getName().toLowerCase().contains(lowerQuery) ||
                        song.getArtistName().toLowerCase().contains(lowerQuery)) {
                    songList.add(song);
                }
            }
        }
        notifyDataSetChanged();
    }

}
