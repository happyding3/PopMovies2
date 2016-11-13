package com.example.android.popmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by happyding3 on 2016/10/31.
 */

public class MovieContract {
    public static final  String CONTENT_AUTHORITY="com.example.android.popmovies";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns{
        public final static String TABLE_NAME = "movie";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String _ID=BaseColumns._ID;
        public final static String COLUMN_RESULTS = "results";
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_POSTER_PATH = "poster_path";
        public final static String COLUMN_OVERVIEW = "overview";
        public final static String COLUMN_VOTE_AVERAGE = "vote_average";
        public final static String COLUMN_RELEASE_DATE = "release_date";
        public final static String COLUMN_ID = "moveid";
        public final static String COLUMN_POPULARITY="popularity";
        public final static String COLUMN_FAVORITE="favorite";

    }
    public static final class TrailerEntry implements BaseColumns{
        public final static String TABLE_NAME="trailer";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public final static String _ID=BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_YOUTUBE_ID="id";
        public static final String COLUMN_LOC_KEY = "movieEntry_id";
        public final static String COLUMN_ID = "moveid";
        public final static String COLUMN_FAVORITE="favorite";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

    }
    public static final class ReviewEntry implements BaseColumns{
        public final static String TABLE_NAME="review";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public final static String _ID=BaseColumns._ID;
        public final static String COLUMN_AUTHOR="author";
        public final static String COLUMN_CONTENT="content";
        public static final String COLUMN_LOC_KEY = "movieEntry_id";
        public final static String COLUMN_ID = "moveid";
        public final static String COLUMN_TOTAL_RESULTS="total_results";
        public final static String COLUMN_FAVORITE="favorite";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
    }
}
