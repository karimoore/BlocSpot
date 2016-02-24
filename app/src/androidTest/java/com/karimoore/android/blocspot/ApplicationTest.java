package com.karimoore.android.blocspot;

import android.test.ApplicationTestCase;
import android.test.RenamingDelegatingContext;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<BlocSpotApplication> {
    public ApplicationTest() {
        super(BlocSpotApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setContext(new RenamingDelegatingContext(getContext(), "test_"));
        createApplication();
    }

    public void testApplicationHasDataSource(){
        BlocSpotApplication blocSpotApplication = getApplication();
        blocSpotApplication.onCreate();
        assertNotNull(blocSpotApplication.getSharedDataSource());
    }

}