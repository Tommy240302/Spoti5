package com.example.spoti5.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;

import java.util.List;

public class SongLikedAdapter extends BaseAdapter {

    private Context context;
    private List<SongModel> songList;

    public SongLikedAdapter(Context context, List<SongModel> songList) {
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

    static class ViewHolder {
        TextView songTitle;
        TextView artistName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_song_liked, parent, false);
            holder = new ViewHolder();
            holder.songTitle = convertView.findViewById(R.id.tv_title);
            holder.artistName = convertView.findViewById(R.id.tv_artist);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SongModel song = songList.get(position);
        holder.songTitle.setText(song.getName());
        holder.artistName.setText(song.getArtistName());

        return convertView;
    }
}
