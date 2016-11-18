package com.example.android.popmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.android.popmovies.sync.MovieSyncAdapter;


public class MainActivity extends AppCompatActivity implements MovieFragment.OnMovieFragmentSelectedListener{
    private static final String DETAILFRAGMENT_TAG= "DFTAG";
    private boolean mTwoPane;

    public boolean getPane() {
        return mTwoPane;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.detail_container)!=null){
            mTwoPane=true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailFragment(),DETAILFRAGMENT_TAG)
                        .commit();}
        }else {
            mTwoPane=false;
        }
        MovieSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public void onMovieSelected(String movieId) {
//DetailFragment detailFragment= (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_container);
        if (mTwoPane){
           Bundle args=new Bundle();
            args.putString(DetailFragment.DETAIL_MOVIEID,movieId);
            DetailFragment detailFragment=new DetailFragment();
            detailFragment.setArguments(args);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

                    transaction.replace(R.id.detail_container,detailFragment,DETAILFRAGMENT_TAG);
            transaction.addToBackStack(null);
                  transaction.commit();

        }
        else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra(DetailFragment.DETAIL_MOVIEID, movieId);
           // intent.setData(ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id));
            startActivity(intent);
        }
    }
}
