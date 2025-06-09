package com.example.spoti5.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spoti5.Fragments.PlayActivity;
import com.example.spoti5.Models.FavoriteList;
import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;
import com.example.spoti5.SQLite.PlaylistDAO;
import com.example.spoti5.SQLite.SongDAO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistAdapter extends BaseAdapter {

    private Context context;
    private List<FavoriteList> playlistList;
    private int songId;
    private SongDAO songDAO;
    private Set<Integer> selectedPlaylistIds = new HashSet<>();
    private Set<Integer> initialSelectedPlaylistIds = new HashSet<>();

    public PlaylistAdapter(Context context, List<FavoriteList> playlistList, int songId) {
        this.context = context;
        this.playlistList = playlistList;
        this.songId = songId;
        this.songDAO = new SongDAO(context);

        for (FavoriteList playlist : playlistList) {
            if (songDAO.isSongInPlaylist(String.valueOf(songId), playlist.getListId())) {
                selectedPlaylistIds.add(playlist.getListId());
                initialSelectedPlaylistIds.add(playlist.getListId());
            }
        }
    }

    @Override
    public int getCount() {
        return playlistList.size();
    }

    @Override
    public Object getItem(int position) {
        return playlistList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Set<Integer> getSelectedPlaylistIds() {
        return selectedPlaylistIds;
    }

    public Set<Integer> getInitialSelectedPlaylistIds() {
        return initialSelectedPlaylistIds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
            holder = new ViewHolder();
            holder.tvPlaylistName = convertView.findViewById(R.id.tvPlaylistName);
            holder.cbPlaylist = convertView.findViewById(R.id.cbSelect);
            holder.btnPlayPlaylist = convertView.findViewById(R.id.btnPlayPlaylist);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FavoriteList playlist = playlistList.get(position);
        holder.tvPlaylistName.setText(playlist.getListName());

        // Reset listener trước để tránh lỗi
        holder.cbPlaylist.setOnCheckedChangeListener(null);

        //ẩn nút play, xóa trong dialog
        holder.btnPlayPlaylist.setVisibility(View.GONE);
        holder.btnDelete.setVisibility(View.GONE);

        // Đặt trạng thái checkbox
        holder.cbPlaylist.setChecked(selectedPlaylistIds.contains(playlist.getListId()));

        holder.cbPlaylist.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPlaylistIds.add(playlist.getListId());
            } else {
                selectedPlaylistIds.remove(playlist.getListId());
            }
        });

        return convertView;
    }

    // Thêm ViewHolder để tối ưu hiệu năng
    private static class ViewHolder {
        TextView tvPlaylistName;
        CheckBox cbPlaylist;
        ImageButton btnPlayPlaylist, btnDelete;
    }

}
