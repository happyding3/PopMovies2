package com.example.android.popmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.popmovies.R;
import com.example.android.popmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;

/**
 * Created by happyding3 on 2016/11/14.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int SYNC_INTERVAL = 60 * 60 * 3;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            Log.e("input url is null", "");
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("status code is ", String.valueOf(urlConnection.getResponseCode()));
            }

        } catch (IOException e) {
            Log.e("makeHttpRequest", "IOException", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);


        ContentResolver.requestSync(getSyncAccount(context),
                "com.example.android.popmovies", bundle);
        Log.e("syncImmediately", "run!!!!!!!");

    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(
                context.getString(R.string.app_name), "com.example");

        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                Log.e("getSyncAccount null", "run!!!!!!!" + newAccount.toString());
                return null;
            }
            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        //MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, "com.example.android.popmovies", true);
        syncImmediately(context);
        Log.e("onAccountCreated", "run!!!!!!!");
    }

    public static void initializeSyncAdapter(Context context) {
        Log.e("initializeSyncAdapter", "run!!!!!!!");
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.e("onPerformSync", "loadInBackground() RUN!!!!!");
        String SORT_BY = null;
        if (PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString("sort", "Popular").equals("Popular")) {

            SORT_BY = "popular";
        } else if (PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString("sort", "Popular").equals("Top rated")) {
            SORT_BY = "top_rated";

        } else {
            Log.d("sort", "sort by not found:" +
                    PreferenceManager.getDefaultSharedPreferences(getContext())
                            .getString("sort", "Popular"));
        }
        try {
            URL url = new URL(String.format(getContext().getString(R.string.basurl), SORT_BY));
            Log.e("movie url", url.toString());
            getMovieDataFromJson(makeHttpRequest(url));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Void getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        final String RESULTS = "results";
        final String TITLE = "title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String ID = "id";
        final String POPULARITY = "popularity";
        JSONArray movieArray;
        JSONObject movieJson = new JSONObject(movieJsonStr);
        movieArray = movieJson.getJSONArray(RESULTS);
        Vector<ContentValues> cVVector = new Vector<>(movieArray.length());
        for (int i = 0; i < movieArray.length(); i++) {
            ContentValues values = new ContentValues();
            JSONObject movie = movieArray.getJSONObject(i);
            String title = movie.getString(TITLE);
            String posterPath = movie.getString(POSTER_PATH);
            String overView = movie.getString(OVERVIEW);
            String voteAverage = movie.getString(VOTE_AVERAGE);
            String releaseDate = movie.getString(RELEASE_DATE);
            String id = movie.getString(ID);
            int popularity = movie.getInt(POPULARITY);
            Log.e("mMovie", id);
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overView);
            values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            values.put(MovieContract.MovieEntry.COLUMN_ID, id);
            values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
            cVVector.add(values);
            //Uri uri = getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
            fetchDetail(id);
        }
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
        return null;
    }

//    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
//        Account account = getSyncAccount(context);
//        String authority = context.getString(R.string.content_authority);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // we can enable inexact timers in our periodic sync
//            SyncRequest request = new SyncRequest.Builder().
//                    syncPeriodic(syncInterval, flexTime).
//                    setSyncAdapter(account, authority).
//                    setExtras(new Bundle()).build();
//            ContentResolver.requestSync(request);
//        } else {
//            ContentResolver.addPeriodicSync(account,
//                    authority, new Bundle(), syncInterval);
//        }
//    }

    public void fetchDetail(String movieID) {
        String JSON_RESPONSE_TRAILER = null;
        String JSON_RESPONSE_REVIEW = null;
        String movieId = movieID;
        String id_Review = movieId + getContext().getString(R.string.review_uri);
        String id_Trailer = movieId + getContext().getString(R.string.trailer_uri);
        URL review_url = null;
        URL trailer_url = null;
        try {
            trailer_url = new URL(String.format(getContext().getString(R.string.basurl1), id_Trailer));
            review_url = new URL(String.format(getContext().getString(R.string.basurl1), id_Review));
            Log.e("Trailer URL", String.valueOf(trailer_url));
            Log.e("Review URL", String.valueOf(review_url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            JSON_RESPONSE_TRAILER = makeHttpRequest(trailer_url);
            JSON_RESPONSE_REVIEW = makeHttpRequest(review_url);
        } catch (IOException e) {
            Log.e("JSON_RESPONSE", "Error closing input stream", e);
        }
        try {
            JSONObject root_trailer = new JSONObject(JSON_RESPONSE_TRAILER);
            //String movieId=root_trailer.getString("id");
            JSONArray results = root_trailer.getJSONArray("results");
            Vector<ContentValues> contentValuesVector = new Vector<>(results.length());
            for (int i = 0; i < results.length(); i++) {
                JSONObject mFeature = results.getJSONObject(i);
                String trailerName = mFeature.getString("name");
                String trailerId = mFeature.getString("key");
                ContentValues values = new ContentValues();
                values.put(MovieContract.TrailerEntry.COLUMN_LOC_KEY, movieId);
                values.put(MovieContract.TrailerEntry.COLUMN_NAME, trailerName);
                values.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_ID, trailerId);
                values.put(MovieContract.TrailerEntry.COLUMN_ID, movieId);
                contentValuesVector.add(values);
            }
            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValuesArray);
                getContext().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, contentValuesArray);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the trailer JSON results", e);
        }
        try {
            JSONObject root_review = new JSONObject(JSON_RESPONSE_REVIEW);
            String reViewTotalResults = root_review.getString("total_results");
            JSONArray results = root_review.getJSONArray("results");
            Vector<ContentValues> contentValuesVector = new Vector<>(results.length());
            for (int i = 0; i < results.length(); i++) {
                JSONObject mFeature = results.getJSONObject(i);
                String reViewAuthor = mFeature.getString("author");
                String reViewContent = mFeature.getString("content");
                ContentValues values = new ContentValues();
                values.put(MovieContract.ReviewEntry.COLUMN_LOC_KEY, movieId);
                values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, reViewAuthor);
                values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, reViewContent);
                values.put(MovieContract.ReviewEntry.COLUMN_TOTAL_RESULTS, reViewTotalResults);
                values.put(MovieContract.ReviewEntry.COLUMN_ID, movieId);
                contentValuesVector.add(values);
            }
            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValuesArray);
                getContext().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, contentValuesArray);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the review JSON results", e);
        }

    }
}


