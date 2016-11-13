package com.example.android.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.android.popmovies.data.MovieContract.CONTENT_AUTHORITY;
import static com.example.android.popmovies.data.MovieContract.MovieEntry;
import static com.example.android.popmovies.data.MovieContract.ReviewEntry;
import static com.example.android.popmovies.data.MovieContract.TrailerEntry;

/**
 * Created by happyding3 on 2016/11/1.
 */

public class MovieProvider extends ContentProvider {
    public static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private MovieDbHelper movieDbHelper;
    private static final int MOVIES = 100;
    private static final int MOVIE_ID = 101;
    private static final int TRAILER = 200;
    private static final int TRAILERWITHMOVIEID = 201;
    private static final int REVIEW = 300;
    private static final int REVIEWWITHMOVIEID = 301;
    private static final UriMatcher mUriMatcher = buildUriMatcher();

//    private static final SQLiteQueryBuilder mMovieTrailerReviewBuilder;
//    static {
//        mMovieTrailerReviewBuilder()=new SQLiteQueryBuilder();
//        mMovieTrailerReviewBuilder().setTables(TrailerEntry.TABLE_NAME+" INNER JOIN "+
//                MovieEntry.TABLE_NAME+" ON "+TrailerEntry.TABLE_NAME+"."+
//                TrailerEntry.COLUMN_LOC_KEY+" = "+MovieEntry.TABLE_NAME+"."+MovieEntry._ID
//        );}


    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        matcher.addURI(authority, MovieEntry.TABLE_NAME, MOVIES);
        matcher.addURI(CONTENT_AUTHORITY, MovieEntry.TABLE_NAME + "/*", MOVIE_ID);
        matcher.addURI(authority, TrailerEntry.TABLE_NAME, TRAILER);
        matcher.addURI(authority, TrailerEntry.TABLE_NAME + "/*", TRAILERWITHMOVIEID);
        matcher.addURI(authority, ReviewEntry.TABLE_NAME, REVIEW);
        matcher.addURI(authority, ReviewEntry.TABLE_NAME + "/*", REVIEWWITHMOVIEID);
        // 3) Return the new matcher!
        return matcher;
    }

    //    private static final UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
//    static {
//        mUriMatcher.addURI(CONTENT_AUTHORITY, MovieEntry.TABLE_NAME,MOVIES);
//        mUriMatcher.addURI(CONTENT_AUTHORITY, MovieEntry.TABLE_NAME+"/#",MOVIE_ID);
//    }
    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = movieDbHelper.getReadableDatabase();
        Log.e("QUERY URI", "=" + uri);
        Cursor cursor;
        int match = mUriMatcher.match(uri);
        Log.e("QUERY match", "=" + match);
        String mSelection;
        switch (match) {

            case MOVIES:
                if (selection!=null){
                   mSelection=selection+"=?";
                }else {
                   mSelection=selection;
                }
                Log.e("MOVIES QUERY URI", "=" + uri);
                cursor = database.query(MovieEntry.TABLE_NAME, projection, mSelection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case MOVIE_ID:
                selection = MovieEntry.COLUMN_ID + "=?";
                Log.e("MOVIE_ID:QUERY URI", "=" + uri);
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(MovieEntry.TABLE_NAME, projection,selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TRAILERWITHMOVIEID:
                Log.e("MOVIE_ID:QUERY URI", "=" + uri);
                selection = TrailerEntry.COLUMN_LOC_KEY + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                Log.e(selection, selectionArgs[0]);
                cursor = database.query(TrailerEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case REVIEWWITHMOVIEID:

                selection = ReviewEntry.COLUMN_LOC_KEY + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ReviewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        Uri returnUri;
        switch (match) {
            case MOVIES:
                long id = database.insert(MovieEntry.TABLE_NAME, null, values);
                returnUri = ContentUris.withAppendedId(uri, id);
                break;
            case TRAILER:
                long TrailerId = database.insert(TrailerEntry.TABLE_NAME, null, values);
                returnUri = ContentUris.withAppendedId(uri, TrailerId);
                break;
            case REVIEW:
                long ReviewId = database.insert(ReviewEntry.TABLE_NAME, null, values);
                returnUri = ContentUris.withAppendedId(uri, ReviewId);
                break;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = movieDbHelper.getReadableDatabase();
        Log.e("update URI", "=" + uri);
        int match = mUriMatcher.match(uri);
        Log.e("update match", "=" + match);
        int updateRows;
        String mSelection=selection+"=?";
        switch (match) {
            case MOVIE_ID:
                updateRows = database.update(MovieEntry.TABLE_NAME, values, mSelection, selectionArgs);
                Log.e("update TABLE MOVIE", "=" + match);
                break;
            case TRAILERWITHMOVIEID:

                updateRows = database.update(TrailerEntry.TABLE_NAME, values, mSelection, selectionArgs);
                Log.e("update TABLE TRAILER", "=" + match);
                break;
            case REVIEWWITHMOVIEID:
                updateRows = database.update(ReviewEntry.TABLE_NAME, values, mSelection, selectionArgs);
                Log.e("update TABLE REVIEW", "=" + match);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return updateRows;
    }
}

