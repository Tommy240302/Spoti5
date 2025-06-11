package com.example.spoti5.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.spoti5.Adapters.PlaylistAdapter;
import com.example.spoti5.Models.FavoriteList; // Nếu đổi tên model thì đổi thành Playlist
import com.example.spoti5.R;
import com.example.spoti5.SQLite.PlaylistDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class    PlaylistDialog {

    public interface OnPlaylistsSelectedListener {
        void onPlaylistsSelected(Set<Integer> selectedPlaylistIds, Set<Integer> initialSelectedPlaylistIds);
    }

    public interface OnNewPlaylistCreatedListener {
        void onNewPlaylistCreated(int newPlaylistId);
    }


    public static void show(Context context, int songId, OnPlaylistsSelectedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.playlist_dialog, null);
        builder.setView(dialogView);

        ListView lvPlaylists = dialogView.findViewById(R.id.lvFavoriteLists);
        Button btnCreate = dialogView.findViewById(R.id.btnCreateNewList);

        PlaylistDAO playlistDAO = new PlaylistDAO(context);
        List<FavoriteList> playlists = playlistDAO.getAll();

        // Dùng mảng 1 phần tử để lưu adapter có thể gán lại trong lambda
        final PlaylistAdapter[] adapterRef = new PlaylistAdapter[1];
        adapterRef[0] = new PlaylistAdapter(context, playlists, songId);
        lvPlaylists.setAdapter(adapterRef[0]);

        AlertDialog dialog = builder.create();

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Xác nhận", (d, which) -> {
            PlaylistAdapter adapter = adapterRef[0];
            listener.onPlaylistsSelected(
                    adapter.getSelectedPlaylistIds(),
                    adapter.getInitialSelectedPlaylistIds());
            dialog.dismiss();
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Hủy", (d, which) -> dialog.dismiss());

        dialog.show();

        btnCreate.setOnClickListener(v -> {
            showCreatePlaylistDialog(context, newPlaylistId -> {
                // Sau khi tạo mới, tải lại danh sách
                List<FavoriteList> updatedPlaylists = playlistDAO.getAll();
                adapterRef[0] = new PlaylistAdapter(context, updatedPlaylists, songId);
                lvPlaylists.setAdapter(adapterRef[0]);
            });
        });


    }

    private static void showCreatePlaylistDialog(Context context, OnNewPlaylistCreatedListener listener) {
        EditText input = new EditText(context);
        input.setHint("Tên danh sách phát");

        new AlertDialog.Builder(context)
                .setTitle("Tạo danh sách phát mới")
                .setView(input)
                .setPositiveButton("Tạo", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        PlaylistDAO dao = new PlaylistDAO(context);
                        long id = dao.insert(name, false);
                        if (id > 0) {
                            listener.onNewPlaylistCreated((int) id);
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }


}

