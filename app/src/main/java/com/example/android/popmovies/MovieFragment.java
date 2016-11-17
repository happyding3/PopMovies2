package com.example.android.popmovies;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.android.popmovies.cursorAdapter.MovieCursorAdapter;
import com.example.android.popmovies.sync.MovieSyncAdapter;

import static android.R.attr.id;
import static com.example.android.popmovies.data.MovieContract.MovieEntry;


public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int cursorLoader = 2;
    private static final int FAVORITELOADER = 1;
    private static final String SELECTOR_KEY = "selector key";
    private static final String FAVORITE_KEY = "favorite key";
    private static final String MovieId_KEY = "movieId key";
    MovieCursorAdapter adapter;
    String mSort_By;
    OnMovieFragmentSelectedListener mCallback;
    Cursor cursor;
    Cursor favoriteCoursor;
    String mMovieID;
    LinearLayout linlaHeaderProgress;
    private GridView gridView;
    private int mPosition = GridView.INVALID_POSITION;
    private int favoriteOrNormal = 0;
    public MovieFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fetchMovie();
        Log.e("fetchMovie();", "yunxingle");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        } else if (id == R.id.favorite_list) {
            if (favoriteOrNormal == 0) {
                favoriteOrNormal = 1;
                getLoaderManager().initLoader(FAVORITELOADER, null, this);
                Log.e("case cursorLoader:", "RUN");

            } else {
                favoriteOrNormal = 0;
                getLoaderManager().restartLoader(cursorLoader, null, this);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchMovie() {
        MovieSyncAdapter.syncImmediately(getActivity());
        Log.e("fetchMovie()", "RUN");
    }

    public String setmSort_By() {
        String mSort_by = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString("sort", "Popular");
        mSort_By = mSort_by;
        String sortOrder;
        if (mSort_by.equals("Popular")) {
            sortOrder = MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            sortOrder = MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }
        return sortOrder;
    }

    @Override
    public void onResume() {
        super.onResume();
        String Sort_by = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString("sort", "Popular");
        Log.e("onResume();", "RUN");
        if (Sort_by != null && !Sort_by.equals(mSort_By)) {
            onSortByChanged();
            Log.e("onSortByChanged();", "RUN");
        }
    }

    void onSortByChanged() {
        fetchMovie();
        if (favoriteOrNormal == 1) {
            getLoaderManager().restartLoader(FAVORITELOADER, null, null);
        } else {
            getLoaderManager().restartLoader(cursorLoader, null, this);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new MovieCursorAdapter(getActivity(), null);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                int IdColumnIndex = cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_ID);
                int idIndex = cursor.getColumnIndexOrThrow(MovieEntry._ID);
                String movie_id = cursor.getString(IdColumnIndex);
                int movieEntryId = cursor.getInt(idIndex);
                Log.e("movieEntryId=" + movie_id, ",long id=" + id);
                mPosition = position;
                mMovieID = movie_id;
                mCallback.onMovieSelected(movie_id);
            }
        });
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTOR_KEY)) {
                mPosition = savedInstanceState.getInt(SELECTOR_KEY);
            }
            if (savedInstanceState.containsKey(FAVORITE_KEY)) {
                favoriteOrNormal = savedInstanceState.getInt(FAVORITE_KEY);
            }
            if (savedInstanceState.containsKey(MovieId_KEY)) {
                mMovieID = savedInstanceState.getString(MovieId_KEY);
            }
        }
        Log.e("favoriteOrNormal", "=" + favoriteOrNormal);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnMovieFragmentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieFragmentSelectedListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTOR_KEY, mPosition);
        }
        outState.putInt(FAVORITE_KEY, favoriteOrNormal);
        outState.putString(MovieId_KEY, mMovieID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (favoriteOrNormal == 1) {
            getLoaderManager().restartLoader(FAVORITELOADER, null, this);
            Log.e("case cursorLoader:", "RUN");

        } else {
            getLoaderManager().restartLoader(cursorLoader, null, this);

        }
        super.onActivityCreated(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("onCreateLoader", "RUN");
        String[] projection = {MovieEntry._ID,
                MovieEntry.COLUMN_POSTER_PATH, MovieEntry.COLUMN_TITLE, MovieEntry.COLUMN_ID};
        String sortOrder = setmSort_By();
        String selector = MovieEntry.COLUMN_FAVORITE;
        String[] selectorArgs = {"1"};
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        switch (id) {
            case cursorLoader:
                Log.e("case cursorLoader:", "RUN");
                return new CursorLoader(
                        getActivity(),
                        MovieEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder
                );
            case FAVORITELOADER:
                Log.e("FAVORITELOADER", "FAVORITELOADER RUN");
                return new CursorLoader(
                        getActivity(),
                        MovieEntry.CONTENT_URI,
                        projection,
                        selector,
                        selectorArgs,
                        sortOrder
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        linlaHeaderProgress.setVisibility(View.GONE);
        if (loaderId == cursorLoader && favoriteOrNormal == 0) {
            cursor = data;
            adapter.swapCursor(data);
            if (mMovieID == null) {
                if (data.moveToFirst()) {
                    int IdColumnIndex = cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_ID);
                    String movie_id = cursor.getString(IdColumnIndex);
                    Log.e("movieEntryId=" + movie_id, ",long id=" + id);
                    mCallback.onMovieSelected(movie_id);
                }
            }
        } else if (loaderId == FAVORITELOADER && favoriteOrNormal == 1) {
            favoriteCoursor = data;
            adapter.swapCursor(data);

        }
        if (mPosition != GridView.INVALID_POSITION) {
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public interface OnMovieFragmentSelectedListener {
        void onMovieSelected(String movieId);
    }

}



