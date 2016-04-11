package com.karimoore.android.blocspot;

import android.support.v4.app.Fragment;

import com.karimoore.android.blocspot.Api.Model.Category;
import com.karimoore.android.blocspot.Api.Model.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kari on 3/24/16.
 */
public abstract class MyFragment extends Fragment{
    public MyFragment() {
        setCurrentPoints(MainActivity.getCurrentPoints());
        setCurrentCategories(MainActivity.getCurrentCategories());
    }

    List<Category> currentCategories = new ArrayList<>();
    List<Point> currentPoints = new ArrayList<>();

    public List<Category> getCurrentCategories() {
        return currentCategories;
    }

    public void setCurrentCategories(List<Category> currentCategories) {
        this.currentCategories.clear();
        this.currentCategories.addAll(currentCategories);
    }

    public List<Point> getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(List<Point> currentPoints) {
        this.currentPoints.clear();
        this.currentPoints.addAll(currentPoints);
    }


    public abstract void update (List<Point> points);
}
