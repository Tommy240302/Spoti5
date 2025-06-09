package com.example.spoti5.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.spoti5.Fragments.PlaylistDialog;
import com.example.spoti5.Models.FavoriteSong;
import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;
import com.example.spoti5.SQLite.PlaylistDAO;
import com.example.spoti5.SQLite.SongDAO;

import java.util.List;
import java.util.Set;

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
        ImageView btnFavorite = view.findViewById(R.id.btnFavorite);

        SongModel song = songList.get(i);
        tvTitle.setText(song.getName());
        tvArtist.setText(song.getArtistName());

        Glide.with(context)
                .load(song.getImage())
                .placeholder(R.drawable.baseline_error_24)
                .into(imgSong);

        SongDAO songDAO = new SongDAO(context);

        boolean isInAnyPlaylist = songDAO.isSongInAnyPlaylist(Integer.parseInt(song.getId()));

        btnFavorite.setImageResource(isInAnyPlaylist ? R.drawable.icon_success : R.drawable.icon_add);

        btnFavorite.setOnClickListener(v -> {
            PlaylistDialog.show(context, Integer.parseInt(song.getId()), new PlaylistDialog.OnPlaylistsSelectedListener() {
                @Override
                public void onPlaylistsSelected(Set<Integer> selectedPlaylistIds, Set<Integer> initialSelectedPlaylistIds) {
                    SongDAO songDAO = new SongDAO(context);

                    // 1. Lưu bài hát vào bảng Song nếu chưa có
                    songDAO.insertOrIgnoreSong(song);

                    // 2. Thêm vào playlist mới được chọn
                    for (int id : selectedPlaylistIds) {
                        if (!initialSelectedPlaylistIds.contains(id)) {
                            songDAO.addSongToPlaylist(song.getId(), id);
                        }
                    }

                    // Xóa khỏi playlist bị bỏ chọn
                    for (int id : initialSelectedPlaylistIds) {
                        if (!selectedPlaylistIds.contains(id)) {
                            songDAO.removeSongFromPlaylist(song.getId(), id);
                        }
                    }

                    // Cập nhật icon
                    boolean isInAny = false;
                    for (int id : selectedPlaylistIds) {
                        if (songDAO.isSongInPlaylist(song.getId(), id)) {
                            isInAny = true;
                            break;
                        }
                    }
                    btnFavorite.setImageResource(isInAny ? R.drawable.icon_success : R.drawable.icon_add);

                    Toast.makeText(context, "Cập nhật danh sách phát thành công", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}
