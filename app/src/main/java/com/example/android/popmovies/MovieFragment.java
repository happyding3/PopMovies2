package com.example.android.popmovies;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
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
import android.widget.Toast;

import com.example.android.popmovies.service.MovieService;

import static com.example.android.popmovies.data.MovieContract.MovieEntry;


public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    MovieCursorAdapter adapter;
    private static final int cursorLoader = 2;
    private static final int FAVORITELOADER = 1;
    String mSort_By;
    private GridView gridView;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTOR_KEY = "selector key";
    OnMovieFragmentSelectedListener mCallback;
    private int favoriteOrNormal = 0;

    public MovieFragment() {

    }

    public interface OnMovieFragmentSelectedListener {
        void onMovieSelected(String movieId);
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
                getLoaderManager().initLoader(FAVORITELOADER, null, this);
                Log.e("case cursorLoader:", "RUN");
                favoriteOrNormal = 1;
            } else {
                getLoaderManager().restartLoader(cursorLoader, null, this);
                favoriteOrNormal = 0;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void fetchMovie() {
        String SORT_BY = null;
        if (PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString("sort", "Popular").equals("Popular")) {
            mSort_By = "Popular";
            SORT_BY = "popular";
        } else if (PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString("sort", "Popular").equals("Top rated")) {
            SORT_BY = "top_rated";
            mSort_By = "Top rated";
        } else {
            Log.d("sort", "sort by not found:" +
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getString("sort", "Popular"));
        }
        if (isOnline()){
            Intent intent = new Intent(getActivity(), MovieService.class);
            intent.putExtra(MovieService.SORT_BY_EXTRA,
                    SORT_BY);
            getActivity().startService(intent);
        }else {
            Toast.makeText(getActivity(),"there is no internet",Toast.LENGTH_LONG).show();
        }

    }

    public String setmSort_By() {
        String mSort_by = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString("sort", "Popular");
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
        if (Sort_by != null && !Sort_by.equals(mSort_By)) {
            onSortByChanged();
            Log.e("onSortByChanged();", "RUN");
        }
    }
    void onSortByChanged() {
        fetchMovie();
        getLoaderManager().restartLoader(cursorLoader, null, this);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new MovieCursorAdapter(getActivity(), null);
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
                mCallback.onMovieSelected(movie_id);
            }
        });
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
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(cursorLoader, null, this);
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
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}



