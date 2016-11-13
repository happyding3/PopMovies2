package com.example.android.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popmovies.data.MovieContract.ReviewEntry;

import static android.R.attr.version;
import static com.example.android.popmovies.data.MovieContract.MovieEntry;
import static com.example.android.popmovies.data.MovieContract.TrailerEntry;

/**
 * Created by happyding3 on 2016/10/31.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movie.db";
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE="CREATE TABLE  " + MovieEntry.TABLE_NAME + " ("+
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieEntry.COLUMN_OVERVIEW+" TEXT, " +
                MovieEntry.COLUMN_TITLE + " TEXT, "+
                MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT, "+
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT, "+
                MovieEntry.COLUMN_POSTER_PATH + " TEXT, "+
                MovieEntry.COLUMN_ID + " TEXT,"+
                MovieEntry.COLUMN_POPULARITY + " INTEGER,"+
                MovieEntry.COLUMN_FAVORITE+" INTEGER NOT NULL DEFAULT 0,"+
                " UNIQUE (" + MovieEntry.COLUMN_ID + ") ON CONFLICT ABORT);";

        final String SQL_CREATE_TRAILER_TABLE="CREATE TABLE "+TrailerEntry.TABLE_NAME+" ("+
                TrailerEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                TrailerEntry.COLUMN_YOUTUBE_ID+ " TEXT, "+
                TrailerEntry.COLUMN_NAME+" TEXT, "+
                TrailerEntry.COLUMN_ID + " TEXT,"+
                TrailerEntry.COLUMN_LOC_KEY+" INTEGER,"+
               TrailerEntry.COLUMN_FAVORITE+" INTEGER NOT NULL DEFAULT 0,"+
               " FOREIGN KEY ("+TrailerEntry.COLUMN_LOC_KEY+ ") REFERENCES  "+
                MovieEntry.TABLE_NAME+" (" +MovieEntry.COLUMN_ID+ ") "+
                " UNIQUE ( "+TrailerEntry.COLUMN_YOUTUBE_ID+") ON CONFLICT ABORT);";

        final String SQL_CREATE_REVIEW_TABLE="CREATE TABLE "+ ReviewEntry.TABLE_NAME+" ("+
                ReviewEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ReviewEntry.COLUMN_AUTHOR+" TEXT, "+
                ReviewEntry.COLUMN_CONTENT+" TEXT, "+
                ReviewEntry.COLUMN_TOTAL_RESULTS+ " TEXT, "+
                ReviewEntry.COLUMN_ID + " TEXT,"+
                ReviewEntry.COLUMN_FAVORITE+" INTEGER NOT NULL DEFAULT 0,"+
                ReviewEntry.COLUMN_LOC_KEY+
                ", FOREIGN KEY ("+ReviewEntry.COLUMN_LOC_KEY+ ") REFERENCES "+
                MovieEntry.TABLE_NAME+" ("+MovieEntry.COLUMN_ID+ ") "+
               " UNIQUE ("+ReviewEntry.COLUMN_CONTENT+") ON CONFLICT ABORT);";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ReviewEntry.TABLE_NAME);
        onCreate(db);
    }
}
