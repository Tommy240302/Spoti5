package com.example.spoti5.Models;

public class SongModel {
    private String id;

    private String name;

    private int duration;

    private String artistId;

    private String artistName;

    private String artistIdStr;

    private String albumName;

    private String albumId;

    private String licenseCcurl;

    private int position;

    private String releaseDate;

    private String albumImage;

    private String audioUrl;

    private String audioDownloadUrl;

    private String proUrl;

    private String shortUrl;

    private String shareUrl;

    private String waveform; // Optional: hoặc có thể parse JSON bên trong thành List nếu cần

    private String image;

    private boolean audioDownloadAllowed;

    // Constructor, getters and setters

    public SongModel() {
    }

    public SongModel(String id, String name, int duration, String artistId, String artistName, String artistIdStr, String albumName, String albumId, String licenseCcurl, int position, String releaseDate, String albumImage, String audioUrl, String audioDownloadUrl, String proUrl, String shortUrl, String shareUrl, String waveform, String image, boolean audioDownloadAllowed) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.artistId = artistId;
        this.artistName = artistName;
        this.artistIdStr = artistIdStr;
        this.albumName = albumName;
        this.albumId = albumId;
        this.licenseCcurl = licenseCcurl;
        this.position = position;
        this.releaseDate = releaseDate;
        this.albumImage = albumImage;
        this.audioUrl = audioUrl;
        this.audioDownloadUrl = audioDownloadUrl;
        this.proUrl = proUrl;
        this.shortUrl = shortUrl;
        this.shareUrl = shareUrl;
        this.waveform = waveform;
        this.image = image;
        this.audioDownloadAllowed = audioDownloadAllowed;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public String getArtistIdStr() {
        return artistIdStr;
    }

    public void setArtistIdStr(String artistIdStr) {
        this.artistIdStr = artistIdStr;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getLicenseCcurl() {
        return licenseCcurl;
    }

    public void setLicenseCcurl(String licenseCcurl) {
        this.licenseCcurl = licenseCcurl;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getAudioDownloadUrl() {
        return audioDownloadUrl;
    }

    public void setAudioDownloadUrl(String audioDownloadUrl) {
        this.audioDownloadUrl = audioDownloadUrl;
    }

    public String getProUrl() {
        return proUrl;
    }

    public void setProUrl(String proUrl) {
        this.proUrl = proUrl;
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

    public String getWaveform() {
        return waveform;
    }

    public void setWaveform(String waveform) {
        this.waveform = waveform;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAudioDownloadAllowed() {
        return audioDownloadAllowed;
    }

    public void setAudioDownloadAllowed(boolean audioDownloadAllowed) {
        this.audioDownloadAllowed = audioDownloadAllowed;
    }
}
