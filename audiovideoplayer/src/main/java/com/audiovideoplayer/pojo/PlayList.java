package com.audiovideoplayer.pojo;

public class PlayList {
    private String title,artist,album,mediaUrl,imagUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PlayList(String title, String artist, String album, String mediaUrl, String imagUrl) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.mediaUrl = mediaUrl;
        this.imagUrl = imagUrl;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getImagUrl() {
        return imagUrl;
    }

    public void setImagUrl(String imagUrl) {
        this.imagUrl = imagUrl;
    }
}
