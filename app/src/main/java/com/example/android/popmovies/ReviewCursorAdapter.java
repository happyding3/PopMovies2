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
 * Created by happyding3 on 2016/11/8.
 */

public class ReviewCursorAdapter extends CursorAdapter {
    public ReviewCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
        Log.e("ReviewCursorAdapter","run");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.e("ReviewCursorAdapter","run");
        return LayoutInflater.from(context).inflate(R.layout.review_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView mReviewAuthor= (TextView) view.findViewById(R.id.review_author);
        TextView mReviewContent= (TextView) view.findViewById(R.id.review_content);
        int reviewAuthorIndex=cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        int reviewContentIndex=cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);
        String reviewAuthor=cursor.getString(reviewAuthorIndex);
        String reviewContent=cursor.getString(reviewContentIndex);
        mReviewAuthor.setText(reviewAuthor);
        mReviewContent.setText(reviewContent);
        Log.e("ReviewCursorAdapter","run");
    }
}
