package com.example.android.popmovies;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements MovieFragment.OnMovieFragmentSelectedListener{
    private static final String DETAILFRAGMENT_TAG= "DFTAG";
    private boolean mTwoPane;
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

        }}
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    public boolean isOnline1() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c" + "www.themoviedb.org");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
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
