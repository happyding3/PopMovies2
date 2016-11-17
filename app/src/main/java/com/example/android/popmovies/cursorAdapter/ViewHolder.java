package com.example.android.popmovies.cursorAdapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popmovies.R;

/**
 * Created by happyding3 on 2016/11/17.
 */

public class ViewHolder {
    private final int MOVIEADAPTER = 0;
    private final int REVIEWADAPTER = 1;
    private final int TRAILERADAPTER = 2;
    ImageView mPoster;
    TextView mReviewAuthor;
    TextView mReviewContent;
    TextView trailerName;

    public ViewHolder(View view, int i) {
        switch (i) {
            case MOVIEADAPTER:
                mPoster = (ImageView) view.findViewById(R.id.movie_poster);
                return;
            case REVIEWADAPTER:
                mReviewAuthor = (TextView) view.findViewById(R.id.review_author);
                mReviewContent = (TextView) view.findViewById(R.id.review_content);
                return;
            case TRAILERADAPTER:
                trailerName = (TextView) view.findViewById(R.id.trailer_name);
        }
    }
}
