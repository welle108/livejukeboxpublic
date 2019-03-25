package com.moodify.www.livejukebox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class ArtistDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "LiveJukeBox.db";

    private static final String SQL_CREATE_ARTIST_TABLE =
            "CREATE TABLE " + ArtistDBContract.Artists.TABLE_NAME + " (" +
                    ArtistDBContract.Artists._ID + " INTEGER PRIMARY KEY," +
                    ArtistDBContract.Artists.COLUMN_FIRST_NAME + " VARCHAR(30)," +
                    ArtistDBContract.Artists.COLUMN_LAST_NAME + " VARCHAR(30)," +
                    ArtistDBContract.Artists.COLUMN_INSTRUMENT + " VARCHAR(30)," +
                    ArtistDBContract.Artists.COLUMN_EMAIL + " VARCHAR(200))";

    private static final String SQL_CREATE_SONG_TABLE =
            "CREATE TABLE " + ArtistDBContract.Songs.TABLE_NAME + " (" +
                    ArtistDBContract.Songs._ID + " INTEGER PRIMARY KEY," +
                    ArtistDBContract.Songs.COLUMN_ARTIST_ID + " INTEGER," +
                    ArtistDBContract.Songs.COLUMN_TITLE + " VARCHAR(50)," +
                    ArtistDBContract.Songs.COLUMN_ORIGINAL_ARTIST + " VARCHAR(50)," +
                    ArtistDBContract.Songs.COLUMN_URL + " VARCHAR(2083)," +
                    "FOREIGN KEY (" + ArtistDBContract.Songs.COLUMN_ARTIST_ID + ") " +
                    "REFERENCES "+ ArtistDBContract.Artists.TABLE_NAME +"("+ ArtistDBContract.Artists._ID +"))";

    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + ArtistDBContract.Users.TABLE_NAME + " (" +
                    ArtistDBContract.Users._ID + "INTEGER PRIMARY KEY," +
                    ArtistDBContract.Users.COLUMN_EMAIL+ " VARCHAR(200)," +
                    ArtistDBContract.Users.COLUMN_PHONE_NUMBER + " VARCHAR(15)," +
                    ArtistDBContract.Users.COLUMN_INSTAGRAM_HANDLE + " VARCHAR(200))";

    private static final String SQL_DROP_ARTIST_TABLE =
            "DROP TABLE IF EXISTS " + ArtistDBContract.Artists.TABLE_NAME;

    private static final String SQL_DROP_SONG_TABLE =
            "DROP TABLE IF EXISTS " + ArtistDBContract.Songs.TABLE_NAME;

    private static final String SQL_DROP_USER_TABLE =
            "DROP TABLE IF EXISTS " + ArtistDBContract.Users.TABLE_NAME;

    ArtistDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ARTIST_TABLE);
        db.execSQL(SQL_CREATE_SONG_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        db.execSQL(SQL_DROP_ARTIST_TABLE);
        db.execSQL(SQL_DROP_SONG_TABLE);
        db.execSQL(SQL_DROP_USER_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }
}
