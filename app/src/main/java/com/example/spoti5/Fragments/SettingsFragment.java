package com.example.spoti5.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.spoti5.Activities.RegisterActivity;
import com.example.spoti5.Models.FavoriteList;
import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;
import com.example.spoti5.SQLite.PlaylistDAO;
import com.example.spoti5.SQLite.SongDAO;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private Button btnLogout;
    private LinearLayout layoutPlaylistContainer;
    private SongDAO songDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch darkModeSwitch = view.findViewById(R.id.switch_dark_mode);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        int currentMode = prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // Set trạng thái Switch ban đầu
        darkModeSwitch.setChecked(currentMode == AppCompatDelegate.MODE_NIGHT_YES);

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);

            // Lưu chế độ người dùng chọn
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("night_mode", mode);
            editor.apply();

            // Cập nhật lại toàn bộ activity để áp dụng theme mới
            requireActivity().recreate();
        });


        songDAO = new SongDAO(getContext());

        btnLogout = view.findViewById(R.id.btn_logout);
        layoutPlaylistContainer = view.findViewById(R.id.layoutPlaylistContainer);

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        loadPlaylists();

        //thêm danh sách phaát mới
        Button btnAddPlaylist = view.findViewById(R.id.btn_add_playlist);
        btnAddPlaylist.setOnClickListener(v -> {
            // Ví dụ đơn giản: mở dialog nhập tên danh sách phát
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Nhập tên danh sách phát");

            final EditText input = new EditText(getContext());
            input.setHint("VD: Yêu thích, Nhạc chill...");
            builder.setView(input);

            builder.setPositiveButton("Thêm", (dialog, which) -> {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    PlaylistDAO playlistDAO = new PlaylistDAO(getContext());
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    playlistDAO.insert(name, false);
                    loadPlaylists();
                } else {
                    Toast.makeText(getContext(), "Tên không được để trống!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

            builder.show();
        });
        return view;
    }

    private void loadPlaylists() {
        if (layoutPlaylistContainer == null) return;

        layoutPlaylistContainer.removeAllViews();

        PlaylistDAO playlistDAO = new PlaylistDAO(getContext());
        List<FavoriteList> favoriteLists = playlistDAO.getAll();

        Log.d("SettingsFragment", "Số lượng playlist: " + favoriteLists.size());

        for (FavoriteList list : favoriteLists) {
            Log.d("SettingsFragment", "Tên playlist: " + list.getListName());

            View item = LayoutInflater.from(getContext()).inflate(R.layout.item_playlist, layoutPlaylistContainer, false);

            TextView tvName = item.findViewById(R.id.tvPlaylistName);
            tvName.setTextColor(Color.WHITE);
            tvName.setText(list.getListName());

            ImageButton btnPlay = item.findViewById(R.id.btnPlayPlaylist);

            //phát nhạc danh sách phát
            btnPlay.setOnClickListener(v -> {
                // Lấy danh sách bài hát, rồi start PlayActivity như bạn làm trong adapter
                List<SongModel> songs = songDAO.getSongsInPlaylist(list.getListId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                if (songs.isEmpty()) {
                    Toast.makeText(getContext(), "Playlist rỗng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), PlayActivity.class);
                intent.putExtra("songList", new ArrayList<>(songs));
                intent.putExtra("position", 0);
                startActivity(intent);
            });

            //xóa danh sách phát
            ImageButton btnDelete = item.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(v -> {
                // Hiển thị xác nhận xóa (có thể dùng AlertDialog)
                new AlertDialog.Builder(getContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc muốn xóa danh sách phát này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            // Xóa playlist trong database
                            int deletedRows = playlistDAO.delete(list.getListId()); // Convert id sang int nếu cần
                            if (deletedRows > 0) {
                                Toast.makeText(getContext(), "Đã xóa playlist", Toast.LENGTH_SHORT).show();
                                loadPlaylists();
                            } else {
                                Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });

            CheckBox cb = item.findViewById(R.id.cbSelect);
            cb.setVisibility(View.GONE); // Ẩn checkbox

            // Tạo container cho danh sách bài hát con
            LinearLayout songListLayout = new LinearLayout(getContext());
            songListLayout.setOrientation(LinearLayout.VERTICAL);
            songListLayout.setVisibility(View.GONE);
            songListLayout.setPadding(60, 10, 10, 10);

            item.setOnClickListener(v -> {
                if (songListLayout.getChildCount() == 0) {
                    SongDAO songDAO = new SongDAO(getContext());
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    List<SongModel> songs = songDAO.getSongsInPlaylist(list.getListId(), userId);

                    if (songs.isEmpty()) {
                        TextView emptyView = new TextView(getContext());
                        emptyView.setText("Không có bài hát");
                        emptyView.setTextColor(Color.WHITE);
                        songListLayout.addView(emptyView);
                    } else {
                        for (SongModel song : songs) {
                            TextView songView = new TextView(getContext());
                            songView.setText("🎵 " + song.getName() + " - " + song.getArtistName());
                            songView.setTextColor(Color.WHITE);
                            songView.setPadding(0, 5, 0, 5);

                            songView.setOnClickListener(sv -> {
                                Toast.makeText(getContext(), "Đang chọn: " + song.getName(), Toast.LENGTH_SHORT).show();
                                // TODO: mở player phát nhạc
                            });

                            songListLayout.addView(songView);
                        }
                    }
                }

                // Toggle hiện/ẩn danh sách bài hát
                if (songListLayout.getVisibility() == View.VISIBLE) {
                    songListLayout.setVisibility(View.GONE);
                } else {
                    songListLayout.setVisibility(View.VISIBLE);
                }
            });

            layoutPlaylistContainer.addView(item);
            layoutPlaylistContainer.addView(songListLayout);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPlaylists(); // Reload dữ liệu khi fragment hiển thị lại
    }
}
