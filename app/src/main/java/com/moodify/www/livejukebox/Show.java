package com.moodify.www.livejukebox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Show {
    private HashSet<Song> songs;
    private HashMap<Long, Artist> artists;
    private LinkedList<Song> req_que;

    Show(HashSet<Song> songs, HashMap<Long, Artist> artists){
        this.songs = songs;
        this.artists = artists;
    }

    public void addToQueue(Song song){
        req_que.offer(song);
    }

    public Song popNext(){
        return req_que.poll();
    }

    public LinkedList<Song> getQueue(){
        return req_que;
    }
}
