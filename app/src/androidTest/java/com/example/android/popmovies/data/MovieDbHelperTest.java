package com.example.android.popmovies.data;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by happyding3 on 2016/11/4.
 */
public class MovieDbHelperTest extends AndroidTestCase{
    public void testOnCreate() throws Exception {
        SQLiteDatabase db=new MovieDbHelper(this.getContext()).getReadableDatabase();
        assertEquals(true, db.isOpen());
    }

}