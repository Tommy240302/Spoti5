package com.example.spoti5.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.spoti5.Fragments.PlayActivity;
import com.example.spoti5.Fragments.PlaylistDialog;
import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;
import com.example.spoti5.SQLite.SongDAO;

import java.util.List;
import java.util.Set;

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private List<SongModel> songList;
    private LayoutInflater inflater;

    public SearchAdapter(Context context, List<SongModel> songs) {
        this.context = context;
        this.songList = songs;
        this.inflater = LayoutInflater.from(context);
    }

    public List<SongModel> getSongList() {
        return songList;
    }

    public void updateData(List<SongModel> newSongs) {
        songList.clear();
        songList.addAll(newSongs);
        notifyDataSetChanged();
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
        return position; // hoặc songList.get(position).getId() nếu có
    }

    static class ViewHolder {
        ImageView imgSong;
        ImageButton btnFavorite;
        TextView tvTitle;
        TextView tvArtist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_song, parent, false);
            holder = new ViewHolder();
            holder.imgSong = convertView.findViewById(R.id.imgSong);
            holder.btnFavorite = convertView.findViewById(R.id.btnFavorite);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvArtist = convertView.findViewById(R.id.tvArtist);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SongModel song = songList.get(position);

        holder.tvTitle.setText(song.getName());
        holder.tvTitle.setTypeface(null, Typeface.BOLD);
        holder.tvTitle.setTextColor(0xFF666666); // Màu xám đậm phù hợp với layout

        holder.tvArtist.setText(song.getArtistName());
        holder.tvArtist.setTextColor(0xFF666666);

        // TODO: Load ảnh bài hát vào imgSong, ví dụ dùng Glide hoặc Picasso
        Glide.with(context).load(song.getImage()).into(holder.imgSong);

        // TODO: Xử lý sự kiện btnFavorite nếu cần
        SongDAO songDAO = new SongDAO(context);
        boolean isInAnyPlaylist = songDAO.isSongInAnyPlaylist(Integer.parseInt(song.getId()));
        holder.btnFavorite.setImageResource(isInAnyPlaylist ? R.drawable.icon_success : R.drawable.icon_add);

        holder.btnFavorite.setOnClickListener(v -> {
            PlaylistDialog.show(context, Integer.parseInt(song.getId()), new PlaylistDialog.OnPlaylistsSelectedListener() {
                @Override
                public void onPlaylistsSelected(Set<Integer> selectedPlaylistIds, Set<Integer> initialSelectedPlaylistIds) {
                    SongDAO dao = new SongDAO(context);

                    // Lưu bài hát nếu chưa có
                    dao.insertOrIgnoreSong(song);

                    // Thêm vào playlist mới
                    for (int id : selectedPlaylistIds) {
                        if (!initialSelectedPlaylistIds.contains(id)) {
                            dao.addSongToPlaylist(song.getId(), id);
                        }
                    }

                    // Xóa khỏi playlist đã bỏ chọn
                    for (int id : initialSelectedPlaylistIds) {
                        if (!selectedPlaylistIds.contains(id)) {
                            dao.removeSongFromPlaylist(song.getId(), id);
                        }
                    }

                    // Cập nhật icon
                    boolean isNowInAny = dao.isSongInAnyPlaylist(Integer.parseInt(song.getId()));
                    holder.btnFavorite.setImageResource(isNowInAny ? R.drawable.icon_success : R.drawable.icon_add);

                    Toast.makeText(context, "Cập nhật danh sách phát thành công", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return convertView;
    }
}
