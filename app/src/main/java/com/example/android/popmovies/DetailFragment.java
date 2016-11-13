package com.example.android.popmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popmovies.data.MovieContract.ReviewEntry;
import com.example.android.popmovies.data.MovieContract.TrailerEntry;
import com.squareup.picasso.Picasso;

import static com.example.android.popmovies.data.MovieContract.MovieEntry;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MovieLoader = 0;
    private static final int ReviewLoader = 1;
    private static final int TrailerLoader = 2;
    static final String DETAIL_MOVIEID = "movieID";
    TextView mTitle;
    ImageView mPoster;
    TextView mReleaseDate;
    TextView mVoteRate;
    TextView mOverView;
    TextView mPopularity;
    Button mTrailerButton;
    Button mReviewButton;
    ListView mTrailerListView;
    ListView mReviewListView;
    String movieID;
    CheckBox mFavoriteCheckBox;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private TrailerCursorAdapter trailerCursorAdapter;
    private ReviewCursorAdapter reviewCursorAdapter;
    final int FAVORITE = 1;
    final int UNFAVORITE = 0;

    public DetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootViewlist;
        rootViewlist = inflater.inflate(R.layout.fragment_detail, container, false);
        View rootView = inflater.inflate(R.layout.headview, null, false);
        View emptyView = rootViewlist.findViewById(R.id.empty_view);
        mTitle = (TextView) rootView.findViewById(R.id.title);
        mPoster = (ImageView) rootView.findViewById(R.id.poster);
        mReleaseDate = (TextView) rootView.findViewById(R.id.releaseDate);
        mVoteRate = (TextView) rootView.findViewById(R.id.voteRate);
        mOverView = (TextView) rootView.findViewById(R.id.overView);
        mPopularity = (TextView) rootView.findViewById(R.id.popularity);
        mReviewButton = (Button) rootView.findViewById(R.id.review);
        mTrailerButton = (Button) rootView.findViewById(R.id.trailer);
        mTrailerListView = (ListView) rootViewlist.findViewById(R.id.trailer_listView);
        mTrailerListView.addHeaderView(rootView);
        if (movieID == null) {
            mTrailerListView.setEmptyView(emptyView);
        }

        mReviewListView = (ListView) rootViewlist.findViewById(R.id.review_listView);
        mReviewListView.addHeaderView(rootView);
        mFavoriteCheckBox = (CheckBox) rootView.findViewById(R.id.favorite);

        Bundle arguments = getArguments();
        if (arguments != null) {
            movieID = arguments.getString(DETAIL_MOVIEID);
        } else {
            movieID = getActivity().getIntent().getStringExtra("movieID");
        }
        trailerCursorAdapter = new TrailerCursorAdapter(getContext(), null);
        reviewCursorAdapter = new ReviewCursorAdapter(getContext(), null);
        if (movieID != null) {
            getLoaderManager().initLoader(TrailerLoader, null, this);
            getLoaderManager().initLoader(MovieLoader, null, this);
            getLoaderManager().initLoader(ReviewLoader, null, this);
        }
        mFavoriteCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mMoveId = movieID;
                String[] selectionArgs = {mMoveId};
                ContentValues values = new ContentValues();
                Uri uri_movie = MovieEntry.CONTENT_URI.buildUpon().appendPath(mMoveId).build();
                Uri uri_trailer = TrailerEntry.CONTENT_URI.buildUpon().appendPath(mMoveId).build();
                Uri uri_review = ReviewEntry.CONTENT_URI.buildUpon().appendPath(mMoveId).build();
                if (mFavoriteCheckBox.isChecked()) {
                    values.put(MovieEntry.COLUMN_FAVORITE, FAVORITE);

                } else {
                    values.put(MovieEntry.COLUMN_FAVORITE, UNFAVORITE);
                }
                getContext().getContentResolver().update(uri_movie, values, MovieEntry.COLUMN_ID, selectionArgs);
                getContext().getContentResolver().update(uri_review, values, ReviewEntry.COLUMN_ID, selectionArgs);
                getContext().getContentResolver().update(uri_trailer, values, TrailerEntry.COLUMN_ID, selectionArgs);
            }
        });
        mTrailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrailerButton.setTextColor(getResources().getColor(R.color.checked));
                mReviewButton.setTextColor(getResources().getColor(R.color.unChecked));
                mTrailerListView.setVisibility(View.VISIBLE);
                mReviewListView.setVisibility(View.GONE);
            }
        });
        mTrailerButton.performClick();
        mReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrailerButton.setTextColor(getResources().getColor(R.color.unChecked));
                mReviewButton.setTextColor(getResources().getColor(R.color.checked));
                mTrailerListView.setVisibility(View.GONE);
                mReviewListView.setVisibility(View.VISIBLE);
            }
        });

        mTrailerListView.setAdapter(trailerCursorAdapter);
        mReviewListView.setAdapter(reviewCursorAdapter);
        return rootViewlist;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {MovieEntry._ID, MovieEntry.COLUMN_TITLE, MovieEntry.COLUMN_POSTER_PATH, MovieEntry.COLUMN_OVERVIEW, MovieEntry.COLUMN_VOTE_AVERAGE, MovieEntry.COLUMN_RELEASE_DATE, MovieEntry.COLUMN_ID, MovieEntry.COLUMN_FAVORITE, MovieEntry.COLUMN_POPULARITY};
        String[] ReviewProjection = {ReviewEntry._ID, ReviewEntry.COLUMN_LOC_KEY, ReviewEntry.COLUMN_TOTAL_RESULTS, ReviewEntry.COLUMN_AUTHOR, ReviewEntry.COLUMN_CONTENT, ReviewEntry.COLUMN_ID, MovieEntry.COLUMN_FAVORITE};
        String[] TrailerProjection = {TrailerEntry._ID, TrailerEntry.COLUMN_LOC_KEY, TrailerEntry.COLUMN_ID, TrailerEntry.COLUMN_NAME, TrailerEntry.COLUMN_YOUTUBE_ID, MovieEntry.COLUMN_FAVORITE};
        switch (id) {
            case MovieLoader:
                return new CursorLoader(
                        getContext(),
                        MovieEntry.CONTENT_URI.buildUpon().appendPath(movieID).build(),
                        projection,
                        null,
                        null,
                        null
                );
            case ReviewLoader:
                return new CursorLoader(
                        getContext(),
                        ReviewEntry.CONTENT_URI.buildUpon().appendPath(movieID).build(),
                        ReviewProjection,
                        null,
                        null,
                        null
                );
            case TrailerLoader:
                return new CursorLoader(
                        getContext(),
                        TrailerEntry.CONTENT_URI.buildUpon().appendPath(movieID).build(),
                        TrailerProjection,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        switch (loader.getId()) {
            case MovieLoader:
                Log.e("cursor.moveToFirst()", String.valueOf(cursor.moveToFirst()));
                if (cursor.moveToFirst()) {
                    int titleColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_TITLE);
                    int posterPathColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH);
                    int releaseDateColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE);
                    int voteAVERAGEColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE);
                    int overViewColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW);
                    int movieIdColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_ID);
                    int popColumnIndex = cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_POPULARITY);
                    int favoriteColumnIndex = cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_FAVORITE);
                    if (cursor.getInt(favoriteColumnIndex) == FAVORITE) {
                        mFavoriteCheckBox.setChecked(true);
                    }
                    Log.e("movieIdColumnIndex", String.valueOf(movieIdColumnIndex));
                    final String movieID = cursor.getString(movieIdColumnIndex);
                    mTitle.setText(cursor.getString(titleColumnIndex));
                    Log.e("TITLE", cursor.getString(titleColumnIndex));
                    Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185" + cursor.getString(posterPathColumnIndex)).into(mPoster);
                    mReleaseDate.setText(cursor.getString(releaseDateColumnIndex));
                    mVoteRate.setText(cursor.getString(voteAVERAGEColumnIndex) + "/10");
                    mOverView.setText(cursor.getString(overViewColumnIndex));
                    mPopularity.setText(String.valueOf(cursor.getInt(popColumnIndex)));
                }
                break;
            case TrailerLoader:
                if (cursor != null) {
                    trailerCursorAdapter.swapCursor(cursor);
                    if (mPosition != ListView.INVALID_POSITION) {
                        mTrailerListView.smoothScrollToPosition(mPosition);
                    }
                    mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                            int idIndex = cursor.getColumnIndexOrThrow(TrailerEntry._ID);
                            int movieEntryId = cursor.getInt(idIndex);
                            Log.e("TRAILER_id=" + movieEntryId, ",long id=" + id);
                            int youtubeIdIndex = cursor.getColumnIndex(TrailerEntry.COLUMN_YOUTUBE_ID);
                            Log.e("youtubeIdIndex", "=" + youtubeIdIndex);
                            String youtubeId = cursor.getString(youtubeIdIndex);
                            watchYoutubeVideo(youtubeId);
                            mPosition = position;
                        }
                    });
                }
                Log.e("trailerCursorAdapter;", "RUN" + cursor.moveToFirst());
                break;
            case ReviewLoader:
                String reviewList = String.format(getString(R.string.review), String.valueOf(cursor.getCount()));
                mReviewButton.setText(reviewList);
                reviewCursorAdapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

}
