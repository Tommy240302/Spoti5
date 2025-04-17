package com.example.spoti5.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spoti5.Adapters.SongAdapter;
import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SongListFragment extends Fragment {
    private static final String ARG_ALBUM_ID = "album_id";

    private String albumId;
    private ListView lvSongs;
    private List<SongModel> songList;
    private SongAdapter songAdapter;

    public static SongListFragment newInstance(String albumId) {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALBUM_ID, albumId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumId = getArguments().getString(ARG_ALBUM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        lvSongs = view.findViewById(R.id.lvSongs);
        songList = new ArrayList<>();
        songAdapter = new SongAdapter(getContext(), songList);
        lvSongs.setAdapter(songAdapter);

        fetchSongs(albumId);

        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Quay lại fragment trước đó
            requireActivity().getSupportFragmentManager().popBackStack();

            // Hiện lại layout chính của HomeFragment
            requireActivity().findViewById(R.id.home_main_layout).setVisibility(View.VISIBLE);
            requireActivity().findViewById(R.id.home_inner_container).setVisibility(View.GONE);
        });


        return view;
    }

    private void fetchSongs(String albumId) {
        String url = "https://api.jamendo.com/v3.0/tracks/?client_id=6f74d6ca&album_id=" + albumId + "&format=json&limit=20";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        songList.clear();
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject track = results.getJSONObject(i);
                            SongModel song = new SongModel();
                            song.setId(track.getString("id"));
                            song.setName(track.getString("name"));
                            song.setArtistName(track.getString("artist_name"));
                            song.setAlbumName(track.getString("album_name"));
                            song.setAudioUrl(track.getString("audio"));
                            song.setImage(track.getString("image"));
                            songList.add(song);
                        }
                        songAdapter.notifyDataSetChanged();
                        System.out.println("Total songs from album loaded: " + songList.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }
}
