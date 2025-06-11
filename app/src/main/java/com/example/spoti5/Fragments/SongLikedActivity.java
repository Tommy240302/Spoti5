package com.example.spoti5.Fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spoti5.Adapters.SongLikedAdapter;

import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;

import java.util.ArrayList;
import java.util.List;

public class SongLikedActivity extends AppCompatActivity {

    private ListView lvSongs;
    private TextView tvEmptyState;
    private ImageView btnBack;
    private List<SongModel> likedSongs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_song_liked); // bạn có thể đổi tên file xml thành activity_song_liked để rõ ràng hơn

        lvSongs = findViewById(R.id.lvSongs);
        btnBack = findViewById(R.id.btnBack);

        likedSongs = loadLikedSongs();

        if (likedSongs == null || likedSongs.isEmpty()) {
            // Hiển thị thông báo rỗng nếu bạn có TextView riêng
            // Nếu không có thì có thể thêm vào XML sau
        } else {
            SongLikedAdapter adapter = new SongLikedAdapter(this, likedSongs);
            lvSongs.setAdapter(adapter);
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private List<SongModel> loadLikedSongs() {
        List<SongModel> list = new ArrayList<>();
        list.add(new SongModel("1", "Shape of You", 240, "10", "Ed Sheeran", "10", "Divide", "100", "", 1, "2017", "", "", "", "", "", "", "", "", true));
        list.add(new SongModel("2", "Blinding Lights", 200, "11", "The Weeknd", "11", "After Hours", "101", "", 2, "2020", "", "", "", "", "", "", "", "", true));
        return list;
    }
}
