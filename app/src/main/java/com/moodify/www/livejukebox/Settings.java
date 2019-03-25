package com.moodify.www.livejukebox;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
/*
Allows admin user to create artists and add songs
 */

public class Settings extends AppCompatActivity implements ClearSongsListener, ClearArtistsListener{
    private static final String TAG = "Settings";
    private MaterialButton add_artist;
    private MaterialButton add_song;
    private MaterialButton clear_songs;
    private MaterialButton clear_artists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Settings");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d(TAG, "onCreate: Activity started");
        add_artist = findViewById(R.id.bt_add_artist);
        add_song = findViewById(R.id.bt_add_song);
        clear_songs = findViewById(R.id.bt_clear_songs);
        clear_artists = findViewById(R.id.bt_clear_artists);

        add_artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Settings.this, AddArtist.class);
                startActivity(i);
            }
        });


        add_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Settings.this, AddSong.class);
                startActivity(i);
            }
        });

        clear_songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog clear_alert = new AlertDialog.Builder(Settings.this).create();
                clear_alert.setTitle("Clear All Songs?");
                clear_alert.setMessage("This action is permanent");
                clear_alert.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                clear_alert.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Context context = getApplicationContext();
                                new ClearSongsAsyncTask(Settings.this).execute(context);
                            }
                        });
                clear_alert.show();
            }
        });

        clear_artists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog clear_alert = new AlertDialog.Builder(Settings.this).create();
                clear_alert.setTitle("Clear All Artists?");
                clear_alert.setMessage("This action is permanent");
                clear_alert.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                clear_alert.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Context context = getApplicationContext();
                                new ClearArtistsAsyncTask(Settings.this).execute(context);
                            }
                        });
                clear_alert.show();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(Settings.this, MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSongsCleared(Boolean success) {
        if(success){
            Toast.makeText(this, "Songs Cleared", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "No songs exist!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onArtistsCleared(Boolean success) {
        if(success){
            Toast.makeText(this, "Artists Cleared", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "No artists exist!", Toast.LENGTH_SHORT).show();
        }
    }

    static class ClearSongsAsyncTask extends AsyncTask<Context, Void, Boolean>{
        private final ClearSongsListener listener;
        ClearSongsAsyncTask(ClearSongsListener listener){this.listener = listener;}
        @Override
        protected Boolean doInBackground(Context... contexts) {
            Boolean success;
            SQLiteDatabase db;
            ArtistDBHelper dbHelper;
            dbHelper = new ArtistDBHelper(contexts[0]);
            db = dbHelper.getWritableDatabase();
            success = db.delete(ArtistDBContract.Songs.TABLE_NAME,
                    null,
                    null) > 0;
            return success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            listener.onSongsCleared(aBoolean);
            super.onPostExecute(aBoolean);
        }
    }

    static class ClearArtistsAsyncTask extends AsyncTask<Context, Void, Boolean>{
        private final ClearArtistsListener listener;
        ClearArtistsAsyncTask(ClearArtistsListener listener){this.listener = listener;}
        @Override
        protected Boolean doInBackground(Context... contexts) {
            Boolean success;
            SQLiteDatabase db;
            ArtistDBHelper dbHelper;
            dbHelper = new ArtistDBHelper(contexts[0]);
            db = dbHelper.getWritableDatabase();
            success = db.delete(ArtistDBContract.Artists.TABLE_NAME,
                    null,
                    null) > 0;
            return success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            listener.onArtistsCleared(aBoolean);
            super.onPostExecute(aBoolean);
        }
    }
}

interface ClearSongsListener{
    void onSongsCleared(Boolean success);
}

interface ClearArtistsListener{
    void onArtistsCleared(Boolean success);
}
