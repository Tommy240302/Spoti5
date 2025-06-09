package com.example.spoti5.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spoti5.Adapters.SearchAdapter;
import com.example.spoti5.Adapters.SongAdapter;
import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private Button btnSearch;
    private ListView lvSearchResults;

    private SearchAdapter searchAdapter;

    private SongAdapter songAdapter;
    private List<SongModel> searchResults = new ArrayList<>();

    private final Handler handler = new Handler();
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        lvSearchResults = view.findViewById(R.id.lvSearchResults);

        searchAdapter = new SearchAdapter(getContext(), new ArrayList<>());
        lvSearchResults.setAdapter(searchAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // debounce 500ms
                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (!query.isEmpty()) {
                        searchSongs(query);
                    } else {
                        searchResults.clear();
                        searchAdapter.updateData(searchResults);
                    }
                };
                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lvSearchResults.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(requireContext(), PlayActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("songList", new ArrayList<>(searchAdapter.getSongList())); // songList đã được lọc
            requireContext().startActivity(intent);
        });


        return view;
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(query)) {
            Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }
        // Gọi API tìm kiếm bài hát
        searchSongs(query);
    }

    private void searchSongs(String keyword) {
        String url = "https://api.jamendo.com/v3.0/tracks/?client_id=6f74d6ca&format=json&limit=20&search=" + keyword;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        searchResults.clear();
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject track = results.getJSONObject(i);
                            SongModel song = new SongModel();
                            song.setId(track.getString("id"));
                            song.setName(track.getString("name"));
                            song.setArtistName(track.getString("artist_name"));
                            song.setAlbumName(track.getString("album_name"));
                            song.setAudioUrl(track.getString("audio"));
                            song.setImage(track.getString("image"));
                            searchResults.add(song);
                        }
                        searchAdapter.updateData(searchResults);
                        Log.d("searchSongs", "Found " + searchResults.size() + " songs for keyword: " + keyword);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e("searchSongs", "Error searching songs: " + error.getMessage());
                });

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }
}
