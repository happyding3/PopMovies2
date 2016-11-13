package com.example.android.popmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.popmovies.data.MovieContract;

/**
 * Created by happyding3 on 2016/11/6.
 */

public class TrailerCursorAdapter extends CursorAdapter {
    public TrailerCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
        Log.e("TrailerCursorAdapter","run");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.e("TrailerCursorAdapter","newView run");
        return LayoutInflater.from(context).inflate(R.layout.trailer_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView trailerName= (TextView) view.findViewById(R.id.trailer_name);
        int trailerNameColumnIndex=cursor.getColumnIndexOrThrow(MovieContract.TrailerEntry.COLUMN_NAME);
        String mTrailerName=cursor.getString(trailerNameColumnIndex);
        Log.e("mTrailerName",mTrailerName);
        trailerName.setText(mTrailerName);
        int youtubeIdIndex=cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_ID);
        Log.e("youtubeIdIndex","="+youtubeIdIndex);
        String youtubeId=cursor.getString(youtubeIdIndex);
    }
}
