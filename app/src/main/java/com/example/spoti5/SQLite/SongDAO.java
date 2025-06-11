package com.example.spoti5.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.spoti5.Models.SongModel;

import java.util.ArrayList;
import java.util.List;

public class SongDAO {
    private SQLiteDatabase db;

    public SongDAO(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    // Lưu thông tin bài hát vào bảng Song (nếu chưa có)
    public long insertOrIgnoreSong(SongModel song) {
        if (isSongExists(song.getId())) return -1;

        ContentValues values = new ContentValues();
        values.put("song_id", song.getId());
        values.put("title", song.getName());
        values.put("artist", song.getArtistName());
        values.put("duration", song.getDuration());
        values.put("image_url", song.getImage());
        values.put("audio_url", song.getAudioUrl());
        return db.insert("Song", null, values);
    }

    // Kiểm tra bài hát có trong bảng Song chưa
    public boolean isSongExists(String songId) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM Song WHERE song_id = ?", new String[]{songId});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Thêm bài hát vào một playlist (playlist_id)
    public long addSongToPlaylist(String songId, int playlistId) {
        if (isSongInPlaylist(songId, playlistId)) return -1;

        ContentValues values = new ContentValues();
        values.put("song_id", songId);
        values.put("playlist_id", playlistId);
        return db.insert("Playlist_Song", null, values);
    }

    // Xóa bài hát khỏi một playlist
    public int removeSongFromPlaylist(String songId, int playlistId) {
        return db.delete("Playlist_Song", "song_id = ? AND playlist_id = ?", new String[]{songId, String.valueOf(playlistId)});
    }

    // Kiểm tra bài hát có trong playlist chưa
    public boolean isSongInPlaylist(String songId, int playlistId) {
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM Playlist_Song WHERE song_id = ? AND playlist_id = ?",
                new String[]{songId, String.valueOf(playlistId)}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean isSongInAnyPlaylist(int songId) {
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM Playlist_Song WHERE song_id = ? LIMIT 1",
                new String[]{String.valueOf(songId)}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }


    // Lấy danh sách bài hát trong một playlist
    public List<SongModel> getSongsInPlaylist(int playlistId, String userId) {
        List<SongModel> songs = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT s.song_id, s.title, s.artist, s.duration, s.image_url, s.audio_url " +
                        "FROM Song s " +
                        "INNER JOIN Playlist_Song ps ON s.song_id = ps.song_id " +
                        "INNER JOIN Playlist p ON ps.playlist_id = p.playlist_id " +
                        "WHERE ps.playlist_id = ? AND p.user_id = ?",
                new String[]{String.valueOf(playlistId), userId}
        );

        while (cursor.moveToNext()) {
            SongModel song = new SongModel();
            song.setId(cursor.getString(0));
            song.setName(cursor.getString(1));
            song.setArtistName(cursor.getString(2));
            song.setDuration(cursor.getInt(3));
            song.setImage(cursor.getString(4));
            song.setAudioUrl(cursor.getString(5));
            songs.add(song);
        }

        cursor.close();
        return songs;
    }

}
