package com.example.spoti5.Utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.spoti5.Models.SongModel;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

public class DataSyncHelper {

    public static void syncSongsFromAPI(Context context) {
        String url = "https://your-api.com/songs"; // thay bằng URL thực tế

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);

                            SongModel song = new SongModel(
                                    obj.getString("id"),
                                    obj.getString("name"),
                                    obj.getInt("duration"),
                                    obj.getString("artistId"),
                                    obj.getString("artistName"),
                                    obj.getString("artistIdStr"),
                                    obj.getString("albumName"),
                                    obj.getString("albumId"),
                                    obj.getString("licenseCcurl"),
                                    obj.getInt("position"),
                                    obj.getString("releaseDate"),
                                    obj.getString("albumImage"),
                                    obj.getString("audioUrl"),
                                    obj.getString("audioDownloadUrl"),
                                    obj.getString("proUrl"),
                                    obj.getString("shortUrl"),
                                    obj.getString("shareUrl"),
                                    obj.getString("waveform"),
                                    obj.getString("image"),
                                    obj.getBoolean("audioDownloadAllowed")
                            );

                            db.collection("songs")
                                    .document(song.getId())
                                    .set(song)
                                    .addOnSuccessListener(aVoid -> Log.d("SYNC", "Đã lưu: " + song.getName()))
                                    .addOnFailureListener(e -> Log.e("SYNC", "Lỗi lưu: " + e.getMessage()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> Log.e("SYNC", "Lỗi API: " + error.toString())
        );

        CallAPI.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }
}
