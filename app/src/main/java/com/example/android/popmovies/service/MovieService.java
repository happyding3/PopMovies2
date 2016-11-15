//package com.example.android.popmovies.service;
//
//import android.app.IntentService;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.net.Uri;
//import android.util.Log;
//
//import com.example.android.popmovies.R;
//import com.example.android.popmovies.data.MovieContract;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.nio.charset.Charset;
//
//import static com.example.android.popmovies.data.MovieProvider.LOG_TAG;
//
///**
// * Created by happyding3 on 2016/11/11.
// */
//
//public class MovieService extends IntentService {
//    public static final String SORT_BY_EXTRA = "sbe";
//    private final int UNFAVORITE = 0;
//
//    /**
//     * Creates an IntentService.  Invoked by your subclass's constructor.
//     *
//     * @param name Used to name the worker thread, important only for debugging.
//     */
//    public MovieService(String name) {
//        super(name);
//    }
//
//    public MovieService() {
//        super("MovieService");
//    }
//
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Log.e(LOG_TAG, "loadInBackground() RUN!!!!!");
//        String SORT_BY = intent.getStringExtra(SORT_BY_EXTRA);
//        try {
//            URL url = new URL(String.format(getString(R.string.basurl), SORT_BY));
//            Log.e(LOG_TAG, url.toString());
//            getMovieDataFromJson(makeHttpRequest(url));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private Void getMovieDataFromJson(String movieJsonStr)
//            throws JSONException {
//
//        final String RESULTS = "results";
//        final String TITLE = "title";
//        final String POSTER_PATH = "poster_path";
//        final String OVERVIEW = "overview";
//        final String VOTE_AVERAGE = "vote_average";
//        final String RELEASE_DATE = "release_date";
//        final String ID = "id";
//        final String POPULARITY = "popularity";
//        JSONArray movieArray;
//        JSONObject movieJson = new JSONObject(movieJsonStr);
//        movieArray = movieJson.getJSONArray(RESULTS);
//        for (int i = 0; i < movieArray.length(); i++) {
//            ContentValues values = new ContentValues();
//            JSONObject movie = movieArray.getJSONObject(i);
//            String title = movie.getString(TITLE);
//            String posterPath = movie.getString(POSTER_PATH);
//            String overView = movie.getString(OVERVIEW);
//            String voteAverage = movie.getString(VOTE_AVERAGE);
//            String releaseDate = movie.getString(RELEASE_DATE);
//            String id = movie.getString(ID);
//            int popularity = movie.getInt(POPULARITY);
//            Log.e("mMovie", id);
//            values.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
//            values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
//            values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overView);
//            values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
//            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
//            values.put(MovieContract.MovieEntry.COLUMN_ID, id);
//            values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
//            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
//            long movieEntry_id = ContentUris.parseId(uri);
//            fetchDetail(id);
//            Log.e("movie", " movieEntry_id" + movieEntry_id);
//            Log.e("mMovie", id + String.valueOf(uri));
//        }
//        return null;
//    }
//
//    public void fetchDetail(String movieID) {
//        String JSON_RESPONSE_TRAILER = null;
//        String JSON_RESPONSE_REVIEW = null;
//        String movieId = movieID;
//        String id_Review = movieId + getString(R.string.review_uri);
//        String id_Trailer = movieId + getString(R.string.trailer_uri);
//        URL review_url = null;
//        URL trailer_url = null;
//        try {
//            trailer_url = new URL(String.format(getString(R.string.basurl1), id_Trailer));
//            review_url = new URL(String.format(getString(R.string.basurl1), id_Review));
//            Log.e("Trailer URL", String.valueOf(trailer_url));
//            Log.e("Review URL", String.valueOf(review_url));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        try {
//            JSON_RESPONSE_TRAILER = makeHttpRequest(trailer_url);
//            JSON_RESPONSE_REVIEW = makeHttpRequest(review_url);
//        } catch (IOException e) {
//            Log.e("JSON_RESPONSE", "Error closing input stream", e);
//        }
//        try {
//            JSONObject root_trailer = new JSONObject(JSON_RESPONSE_TRAILER);
//            //String movieId=root_trailer.getString("id");
//            JSONArray results = root_trailer.getJSONArray("results");
//            for (int i = 0; i < results.length(); i++) {
//                JSONObject mFeature = results.getJSONObject(i);
//                String trailerName = mFeature.getString("name");
//                String trailerId = mFeature.getString("key");
//                ContentValues values = new ContentValues();
//                values.put(MovieContract.TrailerEntry.COLUMN_LOC_KEY, movieId);
//                values.put(MovieContract.TrailerEntry.COLUMN_NAME, trailerName);
//                values.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_ID, trailerId);
//                values.put(MovieContract.TrailerEntry.COLUMN_ID, movieId);
//                Uri insertUri = getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, values);
//                Log.e("TRAILER INSERTURI", String.valueOf(insertUri));
//                Log.e("TRAILER INSERTURI", "movieEntry_id" + movieId);
//            }
//        } catch (JSONException e) {
//            Log.e("QueryUtils", "Problem parsing the trailer JSON results", e);
//        }
//        try {
//            JSONObject root_review = new JSONObject(JSON_RESPONSE_REVIEW);
//            String reViewTotalResults = root_review.getString("total_results");
//            JSONArray results = root_review.getJSONArray("results");
//            for (int i = 0; i < results.length(); i++) {
//                JSONObject mFeature = results.getJSONObject(i);
//                String reViewAuthor = mFeature.getString("author");
//                String reViewContent = mFeature.getString("content");
//                ContentValues values = new ContentValues();
//                values.put(MovieContract.ReviewEntry.COLUMN_LOC_KEY, movieId);
//                values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, reViewAuthor);
//                values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, reViewContent);
//                values.put(MovieContract.ReviewEntry.COLUMN_TOTAL_RESULTS, reViewTotalResults);
//                values.put(MovieContract.ReviewEntry.COLUMN_ID, movieId);
//                // values.put(MovieContract.ReviewEntry.COLUMN_FAVORITE,UNFAVORITE);
//                Uri insertUri = getContentResolver().insert(MovieContract.ReviewEntry.CONTENT_URI, values);
//                Log.e("REVIEW INSERTURI", String.valueOf(insertUri));
//            }
//        } catch (JSONException e) {
//            Log.e("QueryUtils", "Problem parsing the review JSON results", e);
//        }
//
//    }
//
//    private static String makeHttpRequest(URL url) throws IOException {
//        String jsonResponse = "";
//        if (url == null) {
//            Log.e("input url is null", "");
//            return jsonResponse;
//        }
//        HttpURLConnection urlConnection = null;
//        InputStream inputStream = null;
//        try {
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//            if (urlConnection.getResponseCode() == 200) {
//                inputStream = urlConnection.getInputStream();
//                jsonResponse = readFromStream(inputStream);
//            } else {
//                Log.e("status code is ", String.valueOf(urlConnection.getResponseCode()));
//            }
//
//        } catch (IOException e) {
//            Log.e("makeHttpRequest", "IOException", e);
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (inputStream != null) {
//                inputStream.close();
//            }
//        }
//        return jsonResponse;
//    }
//
//    private static String readFromStream(InputStream inputStream) throws IOException {
//        StringBuilder output = new StringBuilder();
//        if (inputStream != null) {
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
//            BufferedReader reader = new BufferedReader(inputStreamReader);
//            String line = reader.readLine();
//            while (line != null) {
//                output.append(line);
//                line = reader.readLine();
//            }
//        }
//        return output.toString();
//    }
//}
