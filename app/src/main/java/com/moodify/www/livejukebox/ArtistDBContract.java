package com.moodify.www.livejukebox;

import android.provider.BaseColumns;

final class ArtistDBContract {
    private ArtistDBContract(){}

    static class Artists implements BaseColumns{
        static final String TABLE_NAME = "Artists";
        static final String COLUMN_FIRST_NAME = "FirstName";
        static final String COLUMN_LAST_NAME = "LastName";
        static final String COLUMN_INSTRUMENT = "Instrument";
        static final String COLUMN_EMAIL = "Email";
    }

    static class Songs implements BaseColumns{
        static final String TABLE_NAME = "Songs";
        static final String COLUMN_ARTIST_ID = "ArtistId";
        static final String COLUMN_TITLE = "Title";
        static final String COLUMN_ORIGINAL_ARTIST = "OriginalArtist";
        static final String COLUMN_URL = "URL";
    }

    static class Users implements BaseColumns{
        static final String TABLE_NAME = "Users";
        static final String COLUMN_EMAIL = "Email";
        static final String COLUMN_INSTAGRAM_HANDLE = "InstagramHandle";
        static final String COLUMN_PHONE_NUMBER = "PhoneNumber";
    }
}
