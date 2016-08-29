package com.davidparkeredwards.fono;

import android.test.AndroidTestCase;

import com.davidparkeredwards.fono.data.EventDbHelper;

/**
 * Created by User on 7/30/2016.
 */
public class DbTest extends AndroidTestCase{



    void deleteTheDatabase() { mContext.deleteDatabase(EventDbHelper.DATABASE_NAME); }

    public void setUp() throws Exception { deleteTheDatabase(); }

    public void testCreateDb() throws Throwable {

    }


    public void testThatDemonstratesAssertions() throws Throwable {
        int a = 5;
        int b = 3;
        int c = 8;

        assertEquals("X should be equal", a+b, c);

    }

    @Override
    protected void tearDown() throws Exception {super.tearDown();}

}
