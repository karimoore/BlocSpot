package com.karimoore.android.blocspot.Api.Model;

/**
 * Created by kari on 4/25/16.
 */
public class YelpPoint extends Point {

    private String displayAddress;

    public String getDisplayAddress() {
        return displayAddress;
    }

    public YelpPoint(long rowId, String name, double latitude, double longitude, String displayAddress, boolean visited, long catId) {
        super(rowId, name, latitude, longitude, visited, catId);
        this.displayAddress = displayAddress;
    }
}
