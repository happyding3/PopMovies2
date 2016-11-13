package com.example.android.popmovies;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popmovies.data.MovieDbHelper;

public class DetailActivity extends AppCompatActivity {
    TextView mTitle;
    ImageView mPoster;
    TextView mReleaseDate;
    TextView mVoteRate;
    TextView mOverView;
    private Uri mUri;
    MovieDbHelper movieDbHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            //Bundle arguments=new Bundle();

            getSupportFragmentManager().beginTransaction().add(R.id.detail_container,new DetailFragment())
                    .commit();
        }

    }}
