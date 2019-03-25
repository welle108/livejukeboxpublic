package com.moodify.www.livejukebox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddSong extends AppCompatActivity implements GetArtistsListener, AddSongListener, GetCurrentSongsListener {
    private static final String TAG = "AddSong";
    private TextInputEditText if_song_title_input;
    private TextInputEditText if_original_artist_input;
    private TextInputEditText if_song_url_input;
    private ArrayList<Artist> artists = new ArrayList<>();
    private HashSet<Song> curr_songs;
    private Spinner spinner;
    MaterialButton add_song;


    @Override
    public void onArtistsRetrieved(ArrayList<Artist> artist_list) {
        Log.d(TAG, "onArtistsRetrieved: Method Entered");
        ArrayList<CharSequence> name_list = new ArrayList<>();
        this.artists = artist_list;
        for(Artist a : artist_list)
        {
            name_list.add(a.getFirstName() + " " + a.getLastName());
        }
        spinner = findViewById(R.id.artist_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, name_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(

                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        this));

    }

    @Override
    public void onSongInserted(Song song) {
        Toast.makeText(this, "Song inserted \n"+song.toString(), Toast.LENGTH_LONG).show();
        new GetCurrentSongsAsyncTask(this).execute(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Method Entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);
        if_song_title_input = findViewById(R.id.if_song_title);
        if_original_artist_input = findViewById(R.id.if_original_artist);
        if_song_url_input = findViewById(R.id.if_song_url);
        Context context = getApplicationContext();
        new GetCurrentSongsAsyncTask(this).execute(context);
        new GetArtistsTask(this).execute(context);
        add_song = findViewById(R.id.bt_create_song);
        add_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;
                String url_pattern = "^(http:\\/\\/www\\.|https:\\/\\/www\\." +
                        "|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}" +
                        "[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
                if(if_song_title_input.getText() != null &&
                        if_original_artist_input.getText() != null &&
                        if_song_url_input.getText() != null){
                    boolean exists = false;
                    url = if_song_url_input.getText().toString();
                    Pattern url_p = Pattern.compile(url_pattern);
                    Matcher url_m = url_p.matcher(url);
                    if(if_song_title_input.getText().toString().equals("") ||
                            if_original_artist_input.getText().toString().equals("") ||
                            if_song_url_input.getText().toString().equals("") ||
                            spinner.getSelectedItemPosition() == 0){
                        Toast.makeText(AddSong.this,
                                "Error: please enter all fields", Toast.LENGTH_LONG).show();
                    }
                    else if(if_song_title_input.getText().toString().length() > 50) {
                        Toast.makeText(AddSong.this, "Please enter Song Title with" +
                                "less than 50 characters", Toast.LENGTH_SHORT).show();
                    }
                    else if(if_original_artist_input.getText().toString().length() > 50){
                        Toast.makeText(AddSong.this, "Please enter Original Artist with " +
                                "less than 50 characters", Toast.LENGTH_SHORT).show();
                    }
                    else if(url.length() > 2083){
                        Toast.makeText(AddSong.this, "Error: URL too long"
                                , Toast.LENGTH_SHORT).show();
                    }
                    else if(!url_m.matches()){
                        Toast.makeText(AddSong.this, "Error: URL is in invalid format"
                                , Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.d(TAG, "onClick: Fields valid");
                        long artist_id;
                        String song_title = if_song_title_input.getText().toString();
                        String original_artist = if_original_artist_input.getText().toString();
                        for(Song s : curr_songs){
                            String check_original_artist = s.getOriginalArtist().toLowerCase().
                                    replaceAll("//s+", "");
                            String user_original_artist = original_artist.toLowerCase().
                                    replaceAll("//s+", "");
                            String check_title = s.getTitle().toLowerCase().
                                    replaceAll("//s+","");
                            String user_title = song_title.toLowerCase().
                                    replaceAll("//s+", "");
                            if(user_title.equals(check_title) &&
                            user_original_artist.equals(check_original_artist)){
                                exists = true;
                            }

                        }
                        if(exists){
                            Toast.makeText(getApplicationContext(), "Error: Song already exists", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String song_url = if_song_url_input.getText().toString();
                            int pos = spinner.getSelectedItemPosition() - 1;
                            String[] name = spinner.getSelectedItem().toString().split(" ");
                            for (Artist a : artists) {
                                if (name[0].equals(a.getFirstName()) && name[1].equals(a.getLastName())) {
                                    artist_id = artists.get(pos).getId();
                                    Song song = new Song(artist_id, song_title, original_artist, song_url);
                                    Context context = getApplicationContext();
                                    AddSongParams params = new AddSongParams(context, song);
                                    new AddNewSongAsyncTask(AddSong.this).execute(params);
                                }
                            }
                        }

                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: Method Entered");
        super.onStart();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(AddSong.this, Settings.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSongsRetrieved(HashSet<Song> song_list) {
        this.curr_songs = song_list;
    }

    static class GetArtistsTask extends AsyncTask<Context, Void, ArrayList<Artist>>{
        private final GetArtistsListener listener;
        GetArtistsTask(GetArtistsListener listener){
            this.listener = listener;
        }

        @Override
        protected ArrayList<Artist> doInBackground(Context... contexts) {
            Log.d(TAG, "doInBackground: Method Entered");
            ArrayList<Artist> artists = new ArrayList<>();
            SQLiteDatabase db;
            ArtistDBHelper dbHelper;
            dbHelper = new ArtistDBHelper(contexts[0]);
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(ArtistDBContract.Artists.TABLE_NAME,
                    new String[]{ArtistDBContract.Artists.COLUMN_FIRST_NAME,
                    ArtistDBContract.Artists.COLUMN_LAST_NAME,
                    ArtistDBContract.Artists._ID},
                    null,null,null,null,
                    ArtistDBContract.Artists.COLUMN_LAST_NAME + " ASC");
            while(cursor.moveToNext()){
                Artist new_artist = new Artist(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getLong(2));
                artists.add(new_artist);
            }
            cursor.close();
            db.close();
            dbHelper.close();
            return artists;
        }

        @Override
        protected void onPostExecute(ArrayList<Artist> artists) {
            Log.d(TAG, "onPostExecute: Method Entered");
            super.onPostExecute(artists);
            listener.onArtistsRetrieved(artists);
        }
    }

    static class AddNewSongAsyncTask extends AsyncTask<AddSongParams, Void, Song>{
        private final AddSongListener listener;
        AddNewSongAsyncTask(AddSongListener listener){this.listener = listener;}

        @Override
        protected Song doInBackground(AddSongParams... params) {
            Log.d(TAG, "doInBackground: Method Entered");
            Song song;
            SQLiteDatabase db;
            ArtistDBHelper dbHelper;
            dbHelper = new ArtistDBHelper(params[0].context);
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ArtistDBContract.Songs.COLUMN_TITLE, params[0].song.getTitle());
            values.put(ArtistDBContract.Songs.COLUMN_ORIGINAL_ARTIST, params[0].song.getOriginalArtist());
            values.put(ArtistDBContract.Songs.COLUMN_ARTIST_ID, params[0].song.getArtistId());
            values.put(ArtistDBContract.Songs.COLUMN_URL, params[0].song.getURL());
            Long new_row_id = db.insert(ArtistDBContract.Songs.TABLE_NAME, null, values);
            String selection = ArtistDBContract.Songs._ID + " = ?";
            String[] selection_args = {new_row_id.toString()};
            Cursor cursor = db.query(
                    ArtistDBContract.Songs.TABLE_NAME,
                    null,
                    selection,
                    selection_args,
                    null,
                    null,
                    null);
            cursor.moveToNext();
            long artist_id = cursor.getLong(1);
            String title = cursor.getString(2);
            String original_artist = cursor.getString(3);
            String url = cursor.getString(4);
            song = new Song(artist_id, title, original_artist,url);
            dbHelper.close();
            db.close();
            cursor.close();
            return song;
        }

        @Override
        protected void onPostExecute(Song song) {
            Log.d(TAG, "onPostExecute: Method Entered");
            super.onPostExecute(song);
            listener.onSongInserted(song);
        }
    }

    static class GetCurrentSongsAsyncTask extends AsyncTask<Context, Void, HashSet<Song>>{
        private GetCurrentSongsListener listener;

        public  GetCurrentSongsAsyncTask(GetCurrentSongsListener listener){this.listener = listener;}
        @Override
        protected HashSet<Song> doInBackground(Context... contexts) {
            HashSet<Song> songs = new HashSet<>();
            SQLiteDatabase db;
            ArtistDBHelper dbHelper;
            dbHelper = new ArtistDBHelper(contexts[0]);
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM "+ArtistDBContract.Songs.TABLE_NAME,
                    null);
            while (cursor.moveToNext()){
                Song curr_song = new Song(cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4));
                songs.add(curr_song);
            }
            return songs;
        }


        @Override
        protected void onPostExecute(HashSet<Song> songs) {
            super.onPostExecute(songs);
            listener.onSongsRetrieved(songs);
        }
    }

    private static class AddSongParams{
        Context context;
        Song song;

        AddSongParams(Context context, Song song) {
            Log.d(TAG, "AddSongParams: Constructor called");
            this.context = context;
            this.song = song;
        }
    }

}

/**
 * Listens to GetArtistAsyncTask and runs in onPostExecute;
 * Used to fill Artist spinner with all available artists
 */
interface GetArtistsListener{
    void onArtistsRetrieved(ArrayList<Artist> artist_list);
}

/**
 * Listens to AddSongAsyncTask and runs in onPostExecute
 * Displays inserted artist information
 */
interface AddSongListener{
    void onSongInserted(Song song);
}

interface GetCurrentSongsListener{
    void onSongsRetrieved(HashSet<Song> song_list);
}
