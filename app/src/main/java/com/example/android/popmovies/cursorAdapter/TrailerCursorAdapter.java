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

/**
 * Created by happyding3 on 2016/11/6.
 */

public class TrailerCursorAdapter extends CursorAdapter {
    private final int TRAILERADAPTER = 2;

    public TrailerCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        Log.e("TrailerCursorAdapter", "run");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.e("TrailerCursorAdapter", "newView run");
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, TRAILERADAPTER);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int trailerNameColumnIndex = cursor.getColumnIndexOrThrow(MovieContract.TrailerEntry.COLUMN_NAME);
        String mTrailerName = cursor.getString(trailerNameColumnIndex);
        Log.e("mTrailerName", mTrailerName);
        viewHolder.trailerName.setText(mTrailerName);
        int youtubeIdIndex = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_ID);
        Log.e("youtubeIdIndex", "=" + youtubeIdIndex);
        String youtubeId = cursor.getString(youtubeIdIndex);
    }
}
