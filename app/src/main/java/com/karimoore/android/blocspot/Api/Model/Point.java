package com.karimoore.android.blocspot.Api.Model;

import java.io.Serializable;

/**
 * Created by kari on 2/15/16.
 */
public class Point extends Model implements Serializable{
    private String name;
    private double latitude;
    private double longitude;
    private boolean visited;
    private long catId;



    public Point(long rowId, String name, double latitude, double longitude, boolean visited, long catId) {
        super(rowId);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visited = visited;
        this.catId = catId;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
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

    public long getCatId() {
        return catId;
    }

    public void setCatId(long catId) {
        this.catId = catId;
    }



}
