package com.example.spoti5.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spoti5.Adapters.CollectionAdapter;
import com.example.spoti5.Models.AlbumModel;
import com.example.spoti5.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private GridView gvCollectionSongs;
    private List<AlbumModel> listAlbum;
    private CollectionAdapter adapter;
    private RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        gvCollectionSongs = view.findViewById(R.id.gvCollectionSongs);

        // Khởi tạo danh sách album
        listAlbum = new ArrayList<>();

        // Khởi tạo adapter và gán cho GridView
        adapter = new CollectionAdapter(getContext(), listAlbum);
        gvCollectionSongs.setAdapter(adapter);
        //call API
        fecthAlbum();

        gvCollectionSongs.setOnItemClickListener((parent, view1, position, id) -> {
            AlbumModel selectedAlbum = listAlbum.get(position);
            String albumId = selectedAlbum.getId(); // Lấy ID của album được chọn
            System.out.println("Album ID: " + albumId);

            // Tạo fragment mới và truyền albumId
            SongListFragment songListFragment = SongListFragment.newInstance(albumId);

            // Ẩn layout chính, hiện fragment danh sách bài hát
            view.findViewById(R.id.home_main_layout).setVisibility(View.GONE);
            view.findViewById(R.id.home_inner_container).setVisibility(View.VISIBLE);

            // Hiển thị fragment mới trong frame chứa
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_inner_container, songListFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void fecthAlbum() {
        requestQueue = Volley.newRequestQueue(getContext());
        String url = "https://api.jamendo.com/v3.0/albums/?client_id=6f74d6ca&format=json&limit=6";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        listAlbum.clear(); // Xóa dữ liệu cũ nếu có
                        JSONArray data = response.getJSONArray("results");

                        // In ra log để debug
                        System.out.println("API Response: " + response.toString());
                        System.out.println("Number of albums: " + data.length());

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject albumJson = data.getJSONObject(i);

                            // In ra thông tin từng album để debug
                            System.out.println("Album " + i + ": " + albumJson.toString());

                            AlbumModel album = new AlbumModel(
                                    albumJson.getString("id"),
                                    albumJson.getString("name"),
                                    albumJson.getString("releasedate"),
                                    albumJson.getString("artist_id"),
                                    albumJson.getString("artist_name"),
                                    albumJson.getString("image"),
                                    albumJson.getString("zip"),
                                    albumJson.getString("shorturl"),
                                    albumJson.getString("shareurl"),
                                    albumJson.getBoolean("zip_allowed"),
                                    null // Danh sách bài hát có thể để null hoặc lấy sau
                            );

                            listAlbum.add(album);
                        }

                        // Thông báo cho adapter cập nhật UI
                        adapter.notifyDataSetChanged();

                        // In ra số lượng album đã load
                        System.out.println("Total albums loaded: " + listAlbum.size());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("JSON Parsing Error: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    System.out.println("Volley Error: " + error.getMessage());
                }
        );
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // timeout in ms (10s)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }
}