package com.karimoore.android.blocspot;

import android.app.Application;
import android.location.Location;

import com.karimoore.android.blocspot.Api.DataSource;

/**
 * Created by kari on 2/15/16.
 */
public class BlocSpotApplication extends Application {

    private static BlocSpotApplication sharedInstance;
    private DataSource dataSource;
    private Location mCurrentLocation;

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Location mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }


    public static BlocSpotApplication getSharedInstance() {
        return sharedInstance;
    }

    public static DataSource getSharedDataSource() {
        return BlocSpotApplication.getSharedInstance().getDataSource();
    }

    private DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedInstance = this;
        dataSource = new DataSource();
    }
}
