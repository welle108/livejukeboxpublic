package com.moodify.www.livejukebox;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static java.net.HttpURLConnection.HTTP_OK;


public class ViewShow extends Activity implements PopulateRecyclerListener,
        SongRecyclerViewClickListener {
    private static final String TAG = "ViewShow";
    int pos = -1;
    private RecyclerView song_recycler_view;
    private SongListAdapter adapter;
    private LinkedList<Song> song_list;
    private HashMap<Long, Artist> artist_list;
    private FloatingActionButton bt_request_song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_show);
        Context context = getApplicationContext();
        bt_request_song = findViewById(R.id.bt_request_song);
        song_recycler_view = findViewById(R.id.rv_song_list);
        new PopulateRecyclerLists(this).execute(context);
    }

    /**
     * Interface method which populates RecyclerView and Activity Song
     * and Artist lists.
     * @param returns Class containing lists of current songs and artists
     *                from PopulateRecyclerLists
     */
    @Override
    public void onListsPopulated(PopulateRecyclerReturns returns) {
        Log.d(TAG, "onListsPopulated: Method entered");
        final Context context = getBaseContext();
        song_list = new LinkedList<>(returns.getSongs());
        artist_list = new HashMap<>(returns.getArtists());
        adapter = new SongListAdapter(this, song_list, artist_list, this);
        song_recycler_view.setAdapter(adapter);
        song_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "onListsPopulated: RecyclerView populated. " +
                "Song and Artists lists populated.");
        bt_request_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos > -1){
                    final EditText email = new EditText(context);
                    int dp_value = 100;
                    final float d = context.getResources().getDisplayMetrics().density;
                    int margin = (int) (dp_value * d);
                    email.setHint("Enter email");
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(margin, 0, margin, 0);
                    email.setLayoutParams(lp);
                    final Song song = song_list.get(pos);
                    final Artist artist = artist_list.get(song_list.get(pos).getArtistId());
                    String dialog_message = song.getTitle() + " - " + song.getOriginalArtist()
                            + "\n \n" +
                            "Performer: " + artist.getFirstName() + " " + artist.getLastName();
                    AlertDialog clear_alert =
                            new AlertDialog.Builder(ViewShow.this).create();
                    clear_alert.setTitle("Request song?");
                    clear_alert.setMessage(dialog_message);
                    clear_alert.setView(email);
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
                                    String from = "DO NOT REPLY <ADMIN@LiveJukeBox.com>";
                                    String to = artist.getEmail();
                                    String subject = "LIVEJUKEBOX ALERT";
                                    String message = "Hey, " + artist.getFirstName() + "!\n" +
                                            "Your song "+ song.getTitle() + " by " +
                                            song.getOriginalArtist()
                                            + " was just requested.\n \n" +
                                            "Here is a link to a leadsheet for your song:\n" +
                                            "* "+song.getURL() +
                                            "\n \n" +
                                            "Thanks, " +
                                            "LiveJukeBox";
                                    RetrofitClient.getInstance()
                                            .getApi()
                                            .sendAlert(from, to, subject, message)
                                            .enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call,
                                                                       Response<ResponseBody>
                                                                               response)
                                                {
                                                    if (response.code() == HTTP_OK) {
                                                        try {
                                                            JSONObject obj =
                                                                    new JSONObject(response
                                                                            .body().string());
                                                            Toast.makeText(ViewShow.this,
                                                                    "Song Requested!",
                                                                    Toast.LENGTH_SHORT).show();
                                                        } catch (JSONException | IOException e) {
                                                            Toast.makeText(ViewShow.this,
                                                                    "Failed to send",
                                                                    Toast.LENGTH_SHORT).show();
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call,
                                                                      Throwable t) {
                                                    Toast.makeText(ViewShow.this,
                                                            t.getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                    clear_alert.show();
                }
                else {
                    Toast.makeText(ViewShow.this, getResources().getString(R.string.
                            no_song_selected), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Listens for user to select view in recycler
     * @param v Current selected view
     * @param position RecyclerView position of view
     */
    @Override
    public void songListClicked(View v, int position) {
        this.pos = position;
    }

    static class PopulateRecyclerLists extends AsyncTask<Context, Void, PopulateRecyclerReturns>{
        private final PopulateRecyclerListener listener;

        PopulateRecyclerLists(PopulateRecyclerListener listener){this.listener = listener;}

        @Override
        protected PopulateRecyclerReturns doInBackground(Context... contexts) {
            PopulateRecyclerReturns returns;
            LinkedList<Song> song_list = new LinkedList<>();
            HashMap<Long, Artist> artist_list = new HashMap<>();
            SQLiteDatabase db;
            ArtistDBHelper dbHelper;
            dbHelper = new ArtistDBHelper(contexts[0]);
            db = dbHelper.getReadableDatabase();
            Cursor song_cursor = db.rawQuery("SELECT * FROM "+
                            ArtistDBContract.Songs.TABLE_NAME,
                    null);
            Cursor artist_cursor = db.rawQuery("SELECT * FROM " +
                            ArtistDBContract.Artists.TABLE_NAME,
                    null);
            while (song_cursor.moveToNext()){
                Song curr_song = new Song(song_cursor.getLong(0),
                        song_cursor.getLong(1),
                        song_cursor.getString(2),
                        song_cursor.getString(3),
                        song_cursor.getString(4));
                song_list.add(curr_song);
            }
            while (artist_cursor.moveToNext()){
                Artist curr_artist = new Artist(artist_cursor.getLong(0),
                        artist_cursor.getString(1),
                        artist_cursor.getString(2),
                        artist_cursor.getString(3),
                        artist_cursor.getString(4));
                artist_list.put(curr_artist.getId(),curr_artist);
            }
            returns = new PopulateRecyclerReturns(song_list, artist_list);
            db.close();
            dbHelper.close();
            artist_cursor.close();
            song_cursor.close();
            return returns;
        }

        @Override
        protected void onPostExecute(PopulateRecyclerReturns populateRecyclerReturns) {
            super.onPostExecute(populateRecyclerReturns);
            listener.onListsPopulated(populateRecyclerReturns);
        }
    }
}


class PopulateRecyclerReturns{
    private LinkedList<Song> song_list;
    private HashMap<Long, Artist> artist_list;

    PopulateRecyclerReturns(LinkedList<Song> song_list,
                            HashMap<Long, Artist> artist_list){
        this.song_list = song_list;
        this.artist_list = artist_list;
    }

    LinkedList<Song> getSongs(){return song_list;}
    HashMap<Long,Artist> getArtists(){return artist_list;}
}

interface PopulateRecyclerListener{
    void onListsPopulated(PopulateRecyclerReturns returns);
}

interface SongRecyclerViewClickListener{
    void songListClicked(View v, int position);
}