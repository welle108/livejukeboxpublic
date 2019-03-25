package com.moodify.www.livejukebox;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddArtist extends AppCompatActivity implements GetCurrentEmailsListener{

    private static final String TAG = "AddArtist";
    private HashSet<String> current_emails;
    private TextInputEditText if_first_name;
    private TextInputEditText if_last_name;
    private TextInputEditText if_instrument;
    private TextInputEditText if_email;
    private String first_name;
    private String last_name;
    private String instrument;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Method Entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artist);
        MaterialButton bt_create_artist;
        setTitle("Create Artist");
        new GetCurrentEmailsAsyncTask(this).execute(getApplicationContext());
        if_first_name = findViewById(R.id.if_first_name);
        if_last_name = findViewById(R.id.if_last_name);
        if_instrument = findViewById(R.id.if_instrument);
        if_email = findViewById(R.id.if_email);
        bt_create_artist = findViewById(R.id.bt_add_artist_submit);
        final Activity m_activity = this;
        bt_create_artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toast_error_message;
                if(if_first_name.getText() != null &&
                    if_last_name.getText() != null &&
                    if_instrument.getText() != null &&
                    if_email.getText() != null) {
                        if(if_first_name.getText().toString().equals("") ||
                                if_last_name.getText().toString().equals("") ||
                                if_instrument.getText().toString().equals("") ||
                                if_email.getText().toString().equals("")){
                            toast_error_message = getString(R.string.error_empty_field);
                            Toast.makeText(AddArtist.this, toast_error_message, Toast.LENGTH_LONG).show();
                        }
                        else {
                            first_name = if_first_name.getText().toString();
                            last_name = if_last_name.getText().toString();
                            instrument = if_instrument.getText().toString();
                            email = if_email.getText().toString().trim();
                            if(first_name.length()>30)
                            {
                                Toast.makeText(m_activity, "Error: First Name must be 30 characters or less",
                                        Toast.LENGTH_LONG).show();
                            }
                            else if(last_name.length()>30)
                            {
                                Toast.makeText(m_activity, "Error: Last Name must be 30 characters or less",
                                        Toast.LENGTH_LONG).show();
                            }
                            else if(instrument.length()>30)
                            {
                                Toast.makeText(m_activity, "Error: Instrument must be 30 characters or less",
                                        Toast.LENGTH_LONG).show();
                            }
                            else if(!isValidEmail(email) || email.length()>200)
                            {
                                Toast.makeText(m_activity, "Error: Please enter a valid email address with less than 200 characters",
                                        Toast.LENGTH_LONG).show();
                            }
                            else{
                                boolean email_exists = false;
                                for(String e : current_emails){
                                    String check_email = e.toLowerCase();
                                    if(check_email.equals(email.toLowerCase())){
                                        email_exists = true;
                                    }
                                }
                                if(email_exists){
                                    Toast.makeText(m_activity, "Error: Artist with that email already exists",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Artist new_artist = new Artist(first_name, last_name, instrument, email);
                                    Context context = getApplicationContext();
                                    Intent i = new Intent(AddArtist.this, Settings.class);
                                    AddArtistParams params = new AddArtistParams(new_artist, context, i, m_activity);
                                    new AddArtistTask().execute(params);
                                    new GetCurrentEmailsAsyncTask(AddArtist.this).execute(getApplicationContext());
                                }
                            }

                        }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Method entered");
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(AddArtist.this, Settings.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onEmailsRetrieved(HashSet<String> emails) {
        this.current_emails = emails;
    }


    //Inserts new artist information and sends toast to user with new artist details after insertion
    static class AddArtistTask extends AsyncTask<AddArtistParams, Void, Void> {

        private static final String TAG = "AddArtistTask";
        private Artist output_artist;
        private Intent i;

        @Override
        protected Void doInBackground(AddArtistParams... params) {
            Log.d(TAG, "doInBackground: Method Entered");
            SQLiteDatabase db;
            ArtistDBHelper dbHelper;
            dbHelper = new ArtistDBHelper(params[0].context);
            this.i = params[0].intent;
            ContentValues values;
            db = dbHelper.getWritableDatabase();
            values = new ContentValues();
            values.put(ArtistDBContract.Artists.COLUMN_FIRST_NAME, params[0].artist.getFirstName());
            values.put(ArtistDBContract.Artists.COLUMN_LAST_NAME, params[0].artist.getLastName());
            values.put(ArtistDBContract.Artists.COLUMN_INSTRUMENT, params[0].artist.getInstrument());
            values.put(ArtistDBContract.Artists.COLUMN_EMAIL, params[0].artist.getEmail());
            Long newRowId = db.insert(ArtistDBContract.Artists.TABLE_NAME, null, values);
            String selection = ArtistDBContract.Artists._ID + " = ?";
            String[] selection_args = {newRowId.toString()};
            Cursor cursor = db.query(
                    ArtistDBContract.Artists.TABLE_NAME,
                    null,
                    selection,
                    selection_args,
                    null,
                    null,
                    null);
            cursor.moveToNext();
            Long id = cursor.getLong(0);
            String first_name = cursor.getString(1);
            String last_name = cursor.getString(2);
            String instrument = cursor.getString(3);
            String email = cursor.getString(4);
            cursor.close();
            dbHelper.close();
            db.close();
            output_artist = new Artist(first_name, last_name, instrument, email);
            output_artist.setId(id);
            i.putExtra("toast_message", output_artist.toString());
            params[0].activity.startActivity(i);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    static class GetCurrentEmailsAsyncTask extends AsyncTask<Context, Void, HashSet<String>>{
        private GetCurrentEmailsListener listener;

        public  GetCurrentEmailsAsyncTask(GetCurrentEmailsListener listener){this.listener = listener;}

        @Override
        protected HashSet<String> doInBackground(Context... contexts) {
            HashSet<String> current_emails = new HashSet<>();
            SQLiteDatabase db;
            ArtistDBHelper dbHelper;
            dbHelper = new ArtistDBHelper(contexts[0]);
            db = dbHelper.getReadableDatabase();
            String[] columns = {ArtistDBContract.Artists.COLUMN_EMAIL};
            Cursor cursor = db.query(ArtistDBContract.Artists.TABLE_NAME,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    null);

            while (cursor.moveToNext()){
                current_emails.add(cursor.getString(0));
            }
            return current_emails;
        }

        @Override
        protected void onPostExecute(HashSet<String> emails) {
            listener.onEmailsRetrieved(emails);
            super.onPostExecute(emails);
        }
    }

    /**
     * AddArtist AsyncTask parameters
     * Activity object never reassigned from constructor args starts
     * intent after artist created
     */

    private static class AddArtistParams {
        Artist artist;
        Context context;
        Intent intent;
        Activity activity;

        AddArtistParams(Artist artist, Context context, Intent intent, Activity activity) {
            this.artist = artist;
            this.context = context;
            this.intent = intent;
            this.activity = activity;
        }
    }

    public static boolean isValidEmail(String email) {
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

interface GetCurrentEmailsListener{
    void onEmailsRetrieved(HashSet<String> emails);
}
