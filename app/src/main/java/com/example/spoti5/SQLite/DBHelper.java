package com.example.spoti5.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Spoti5";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bảng bài hát
        db.execSQL("CREATE TABLE Song (" +
                "song_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "artist TEXT," +
                "duration INTEGER," +
                "audio_url TEXT," +
                "image_url TEXT)");

        // Bảng danh sách phát
        db.execSQL("CREATE TABLE Playlist (" +
                "playlist_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id TEXT NOT NULL," + // Lấy từ FirebaseAuth.getInstance().getCurrentUser().getUid()
                "name TEXT NOT NULL," +
                "is_favorite INTEGER DEFAULT 0)");

        // Bảng quan hệ nhiều-nhiều giữa playlist và bài hát
        db.execSQL("CREATE TABLE Playlist_Song (" +
                "playlist_id INTEGER," +
                "song_id INTEGER," +
                "PRIMARY KEY (playlist_id, song_id)," +
                "FOREIGN KEY(playlist_id) REFERENCES Playlist(playlist_id) ON DELETE CASCADE," +
                "FOREIGN KEY(song_id) REFERENCES Song(song_id) ON DELETE CASCADE)");
        // Thêm playlist mặc định "Yêu thích"
        // user_id tạm để trống '' (bạn có thể sửa sau khi có user id thật)
        db.execSQL("INSERT INTO Playlist (user_id, name, is_favorite) VALUES ('', 'Yêu thích', 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Playlist_Song");
        db.execSQL("DROP TABLE IF EXISTS Playlist");
        db.execSQL("DROP TABLE IF EXISTS Song");
        onCreate(db);
    }
}
