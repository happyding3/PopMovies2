package com.example.android.popmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by happyding3 on 2016/10/31.
 */

public class MovieCursorAdapter extends CursorAdapter {
    public MovieCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        Log.e("posterPath1111111","MovieCursorAdapter run");
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.e("posterPath1111111","newView run");
        return LayoutInflater.from(context).inflate(R.layout.item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //ImageView mPoster= (ImageView) view.findViewById(R.id.movie_poster);
        ImageView mPoster= (ImageView) view.findViewById(R.id.movie_poster);
        int movieIdColumnIndex= cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID);
        Log.e("movieIdColumnIndex",String.valueOf(movieIdColumnIndex));
//        String movieID=cursor.getString(movieIdColumnIndex);
        String poster_path=
                cursor.getString
                        (cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        String posterPath="http://image.tmdb.org/t/p/w185"+poster_path;

       Picasso.with(context).load("http://image.tmdb.org/t/p/w185"+poster_path).into(mPoster);
        Log.e("posterPath1111111",posterPath);
    }



}
