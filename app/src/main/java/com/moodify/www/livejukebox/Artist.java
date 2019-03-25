package com.moodify.www.livejukebox;

import android.content.Context;

import java.util.HashSet;

public class Artist {
    private long id;
    private String first_name;
    private String last_name;
    private String instrument;
    private String email;

    Artist(String first_name, String last_name, Long id)
    {
        this.first_name = first_name;
        this.last_name = last_name;
        this.id = id;
    }

    Artist(String first_name, String last_name, String instrument, String email)
    {
        this.first_name = first_name;
        this.last_name = last_name;
        this.instrument = instrument;
        this.email = email;
    }
    Artist(long id, String first_name, String last_name, String instrument, String email)
    {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.instrument = instrument;
        this.email = email;
    }

    void setId(Long id){this.id = id;}

    long getId(){return id;}

    String getFirstName(){return first_name;}

    String getLastName(){return last_name;}

    String getInstrument(){return instrument;}

    String getEmail(){return email;}



    public String toString(){
        return first_name+" "+last_name;
    }
}
