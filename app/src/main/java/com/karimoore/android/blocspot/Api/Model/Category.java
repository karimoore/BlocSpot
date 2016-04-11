package com.karimoore.android.blocspot.Api.Model;

import java.io.Serializable;

/**
 * Created by kari on 3/11/16.
 */
public class Category extends Model implements Serializable{
    private long markerColor;
    private int backgroundColor;
    private String name;
    private boolean isFilter;


    public Category(long rowId,  String name, long markerColor, int backColor, boolean isFilter) {
        super(rowId);
        this.markerColor = markerColor;
        this.backgroundColor = backColor;
        this.name = name;
        this.isFilter = isFilter;
    }



    public long getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(long color) {
        this.markerColor = color;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backColor) {
        this.backgroundColor = backColor;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public void setIsFilter(boolean isFilter) {
        this.isFilter = isFilter;
    }


}
