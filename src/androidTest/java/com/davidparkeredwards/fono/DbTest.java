package com.davidparkeredwards.fono;

import android.test.AndroidTestCase;

/**
 * Created by User on 7/30/2016.
 */
public class DbTest extends AndroidTestCase{

    @Override
    protected void setUp() throws Exception { super.setUp(); }

    public void testThatDemonstratesAssertions() throws Throwable {
        int a = 5;
        int b = 3;
        int c = 8;

        assertEquals("X should be equal", a+b, c);

    }

    @Override
    protected void tearDown() throws Exception {super.tearDown();}

}
