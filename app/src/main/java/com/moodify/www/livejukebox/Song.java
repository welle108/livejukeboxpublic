package com.moodify.www.livejukebox;

import java.net.URL;

import androidx.annotation.NonNull;

public class Song {

    private Long id;
    private Long artist_id;
    private String title;
    private String original_artist;
    private String url;

    Song(Long artist_id, String title,
                String original_artist, String url){
        id = null;
        this.artist_id = artist_id;
        this.title = title;
        this.original_artist = original_artist;
        this.url = url;
    }

    Song(Long id, Long artist_id, String title,
         String original_artist, String url){
        this.id = id;
        this.artist_id = artist_id;
        this.title = title;
        this.original_artist = original_artist;
        this.url = url;
    }

    public void setId(Long id){this.id = id;}


    public long getId(){return id;}

    long getArtistId(){return artist_id;}

    String getOriginalArtist(){return original_artist;}

    String getTitle(){return title;}

    String getURL(){return url;}

    @NonNull
    @Override
    public String toString() {
        String r_string =
                "Title: " + getTitle() + "\n" +
                        "Original Artist" + getOriginalArtist();
        return r_string;
    }
}
