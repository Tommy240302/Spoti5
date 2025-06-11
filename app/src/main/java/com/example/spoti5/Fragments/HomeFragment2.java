package com.example.spoti5.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spoti5.Adapters.SongAdapter;
import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment2 extends Fragment {

    private EditText searchInput;
    private ListView searchResults;
    private SongAdapter songAdapter;
    private TextView tvNoResult;


    private List<SongModel> allSongs = new ArrayList<>();
    private List<SongModel> filteredSongs = new ArrayList<>();

    public HomeFragment2() {
        // Bắt buộc để Fragment khởi tạo
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home2, container, false);

        searchInput = view.findViewById(R.id.search_input);
        searchResults = view.findViewById(R.id.search_results);

        allSongs = getAllSongs(); // giả lập dữ liệu
        filteredSongs.addAll(allSongs);

        songAdapter = new SongAdapter(getContext(), filteredSongs);
        searchResults.setAdapter(songAdapter);

        tvNoResult = view.findViewById(R.id.tv_no_result);



        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSongs(s.toString());
            }
        });

        return view;
    }

    private void filterSongs(String keyword) {
        filteredSongs.clear();
        if (keyword.isEmpty()) {
            filteredSongs.addAll(allSongs);
        } else {
            keyword = keyword.toLowerCase().trim();
            for (SongModel song : allSongs) {
                if (song.getName().toLowerCase().contains(keyword) ||
                        song.getArtistName().toLowerCase().contains(keyword)) {
                    filteredSongs.add(song);
                }
            }
        }

        // Hiển thị hoặc ẩn thông báo "không có kết quả"
        if (filteredSongs.isEmpty()) {
            searchResults.setVisibility(View.GONE);
            tvNoResult.setVisibility(View.VISIBLE);
        } else {
            searchResults.setVisibility(View.VISIBLE);
            tvNoResult.setVisibility(View.GONE);
        }

        songAdapter.notifyDataSetChanged();
    }


    private List<SongModel> getAllSongs() {
        List<SongModel> list = new ArrayList<>();
        list.add(new SongModel("1", "Until I Found You", 180, "a1", "Stephen Sanchez", "", "Easy Listening", "al1", "", 1, "2022", "", "https://example.com/audio1.mp3", "", "", "", "", "", "", true));
        list.add(new SongModel("2", "Perfect", 200, "a2", "Ed Sheeran", "", "Divide", "al2", "", 2, "2017", "", "https://example.com/audio2.mp3", "", "", "", "", "", "", true));
        list.add(new SongModel("3", "Shallow", 190, "a3", "Lady Gaga", "", "A Star Is Born", "al3", "", 3, "2018", "", "https://example.com/audio3.mp3", "", "", "", "", "", "", true));
        return list;
    }
}
