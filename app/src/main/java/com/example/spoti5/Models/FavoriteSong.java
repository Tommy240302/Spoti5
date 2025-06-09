package com.example.spoti5.Models;

public class FavoriteSong {
    private int id;
    private String songId;
    private int listId;

    public FavoriteSong() {}

    public FavoriteSong(String songId, int listId) {
        this.songId = songId;
        this.listId = listId;
    }

    public int getId() {
        return id;
    }

    public String getSongId() {
        return songId;
    }

    public int getListId() {
        return listId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }
}
