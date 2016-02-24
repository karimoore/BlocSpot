package com.karimoore.android.blocspot.Api.Model;

/**
 * Created by kari on 2/15/16.
 */
public class Point extends Model{
    private String name;
    private double latitude;
    private double longitude;

    public Point(long rowId, String name, double latitude, double longitude) {
        super(rowId);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }



}
