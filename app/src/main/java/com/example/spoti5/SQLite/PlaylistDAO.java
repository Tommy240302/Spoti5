package com.example.spoti5.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.spoti5.Models.FavoriteList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {
    private SQLiteDatabase db;
    private String currentUserId;

    public PlaylistDAO(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        this.db = dbHelper.getWritableDatabase();

        // Lấy uid người dùng hiện tại từ Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        } else {
            throw new IllegalStateException("Người dùng chưa đăng nhập");
        }
    }

    public boolean isFavoritePlaylistExists(String userId) {
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM Playlist WHERE user_id = ? AND is_favorite = 1",
                new String[]{userId});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
    // Thêm playlist "Yêu thích" mặc định cho user hiện tại
    public long insertFavoritePlaylist() {
        // Có thể kiểm tra tồn tại trước khi thêm (nếu muốn)
        if (!isFavoritePlaylistExists(currentUserId)) {
            return insert("Yêu thích", true);
        }
        return -1; // Đã tồn tại rồi
    }

    // Thêm danh sách phát
    public long insert(String name, boolean isFavorite) {
        ContentValues values = new ContentValues();
        values.put("user_id", currentUserId);
        values.put("name", name);
        values.put("is_favorite", isFavorite ? 1 : 0);
        return db.insert("Playlist", null, values);
    }

    // Xóa danh sách phát theo ID
    public int delete(int playlistId) {
        return db.delete(
                "Playlist",
                "playlist_id = ? AND user_id = ?",
                new String[]{String.valueOf(playlistId), currentUserId}
        );
    }

    // Lấy tất cả playlist của người dùng hiện tại
    public List<FavoriteList> getAll() {
        List<FavoriteList> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT playlist_id, name FROM Playlist WHERE user_id = ?",
                new String[]{currentUserId}
        );

        while (cursor.moveToNext()) {
            FavoriteList item = new FavoriteList();
            item.setListId(cursor.getInt(0));
            item.setListName(cursor.getString(1));
            list.add(item);
        }

        cursor.close();
        return list;
    }

    // Lấy danh sách phát yêu thích (is_favorite = 1)
    public FavoriteList getFavoritePlaylist() {
        Cursor cursor = db.rawQuery(
                "SELECT playlist_id, name FROM Playlist WHERE user_id = ? AND is_favorite = 1 LIMIT 1",
                new String[]{currentUserId}
        );

        FavoriteList favorite = null;
        if (cursor.moveToFirst()) {
            favorite = new FavoriteList();
            favorite.setListId(cursor.getInt(0));
            favorite.setListName(cursor.getString(1));
        }

        cursor.close();
        return favorite;
    }


}
