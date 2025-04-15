package com.example.spoti5.Models;

import java.util.List;

public class AlbumModel {
    private String id;
    private String name;
    private String releaseDate;
    private String artistId;
    private String artistName;
    private String imageUrl;
    private String zipUrl;
    private String shortUrl;
    private String shareUrl;
    private boolean zipAllowed;
    private List<SongModel> tracks;

    public AlbumModel() {
    }
    public AlbumModel(String id, String name, String artistName, String imageUrl) {
        this.id = id;
        this.name = name;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
    }
    public AlbumModel(String id, String name, String releaseDate, String artistId, String artistName, String imageUrl, String zipUrl, String shortUrl, String shareUrl, boolean zipAllowed, List<SongModel> tracks) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.artistId = artistId;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.zipUrl = zipUrl;
        this.shortUrl = shortUrl;
        this.shareUrl = shareUrl;
        this.zipAllowed = zipAllowed;
        this.tracks = tracks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getZipUrl() {
        return zipUrl;
    }

    public void setZipUrl(String zipUrl) {
        this.zipUrl = zipUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public boolean isZipAllowed() {
        return zipAllowed;
    }

    public void setZipAllowed(boolean zipAllowed) {
        this.zipAllowed = zipAllowed;
    }

    public List<SongModel> getTracks() {
        return tracks;
    }

    public void setTracks(List<SongModel> tracks) {
        this.tracks = tracks;
    }
}
