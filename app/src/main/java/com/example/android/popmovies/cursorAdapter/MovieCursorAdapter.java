package com.example.android.popmovies.cursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.example.android.popmovies.R;
import com.example.android.popmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by happyding3 on 2016/10/31.
 */

public class MovieCursorAdapter extends CursorAdapter {
    private final int MOVIEADAPTER = 0;
    public MovieCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        Log.e("posterPath1111111", "MovieCursorAdapter run");
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, MOVIEADAPTER);
        view.setTag(viewHolder);
        return view;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int movieIdColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID);
        Log.e("movieIdColumnIndex", String.valueOf(movieIdColumnIndex));
        String poster_path =
                cursor.getString
                        (cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        String posterPath = "http://image.tmdb.org/t/p/w185" + poster_path;
        Picasso.with(context).load(posterPath).into(viewHolder.mPoster);
        Log.e("posterPath1111111", posterPath);
    }


}
