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
 * Created by happyding3 on 2016/11/8.
 */

public class ReviewCursorAdapter extends CursorAdapter {
    private final int REVIEWADAPTER = 1;
    public ReviewCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        Log.e("ReviewCursorAdapter", "run");
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.e("ReviewCursorAdapter", "run");
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, REVIEWADAPTER);
        view.setTag(viewHolder);
        return view;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int reviewAuthorIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        int reviewContentIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);
        String reviewAuthor = cursor.getString(reviewAuthorIndex);
        String reviewContent = cursor.getString(reviewContentIndex);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.mReviewAuthor.setText(reviewAuthor);
        viewHolder.mReviewContent.setText(reviewContent);
    }
}
