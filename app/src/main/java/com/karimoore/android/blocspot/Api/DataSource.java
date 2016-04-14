package com.karimoore.android.blocspot.Api;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.karimoore.android.blocspot.Api.Model.Category;
import com.karimoore.android.blocspot.Api.Model.Point;
import com.karimoore.android.blocspot.Api.Model.database.DatabaseOpenHelper;
import com.karimoore.android.blocspot.Api.Model.database.table.CategoryTable;
import com.karimoore.android.blocspot.Api.Model.database.table.PointTable;
import com.karimoore.android.blocspot.Api.Model.database.table.Table;
import com.karimoore.android.blocspot.BlocSpotApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kari on 2/15/16.
 */
public class DataSource {

    // results after the db calls
    public static interface Callback<Result> {

        public void onSuccess(Result result);

        public void onError(String errorMessage);
    }
    // results after the db calls
    public static interface Callback2<Result, L>{
        public void onSuccess(Result result, L l);
        public void onError(String errorMessage);
    }


    private DatabaseOpenHelper databaseOpenHelper;
    private PointTable pointTable;
    private CategoryTable categoryTable;
    private Table[] tables;

    public DataSource(){

        pointTable = new PointTable();
        categoryTable = new CategoryTable();
        tables = new Table[]{pointTable, categoryTable};
        databaseOpenHelper = new DatabaseOpenHelper(BlocSpotApplication.getSharedInstance(),
                tables);

        new Thread(new Runnable() {
            @Override
            public void run() {

                //if (BuildConfig.DEBUG && true) {
                    boolean what = BlocSpotApplication.getSharedInstance().deleteDatabase("blocspot_db");
                //}

                SQLiteDatabase writableDatabase = databaseOpenHelper.getWritableDatabase();

                new CategoryTable.Builder()
                        .setName("Restaurants Category")
                        .setMarkerColor(String.valueOf(BitmapDescriptorFactory.HUE_RED))
                        .setBackgroundColor(String.valueOf(Color.RED))
                        .insert(writableDatabase);
                new CategoryTable.Builder()
                        .setName("Bars Category")
                        .setMarkerColor(String.valueOf(BitmapDescriptorFactory.HUE_YELLOW))
                        .setBackgroundColor(String.valueOf(Color.YELLOW))
                        .insert(writableDatabase);
                new CategoryTable.Builder()
                        .setName("Stores Category")
                        .setMarkerColor(String.valueOf(BitmapDescriptorFactory.HUE_GREEN))
                        .setBackgroundColor(String.valueOf(Color.GREEN))
                        .insert(writableDatabase);

                // ask user for data?
                new PointTable.Builder()
                        .setName("restaurant1")
                        .setLatitude("41.1")
                        .setLongitude("-88")
                        .setCategoryId("1")
                        .setVisited("0")
                        .insert(writableDatabase);
                new PointTable.Builder()
                        .setName("bar")
                        .setLatitude("41.2")
                        .setLongitude("-88")
                        .setCategoryId("2")
                        .setVisited("0")
                        .insert(writableDatabase);
                new PointTable.Builder()
                        .setName("store")
                        .setLatitude("41.3")
                        .setLongitude("-88")
                        .setCategoryId("3")
                        .setVisited("0")
                        .insert(writableDatabase);
                new PointTable.Builder()
                        .setName("restaurant2")
                        .setLatitude("41.4")
                        .setLongitude("-88")
                        .setCategoryId("1")
                        .setVisited("0")
                        .insert(writableDatabase);

            }
        }).start();
    }

    public void updateCategoryForPoint(int catId, int rowId){

        // update the roweId in Table Point and change the categoryId column to the new catId
        int updated = PointTable.updateCategoryColumn(databaseOpenHelper.getWritableDatabase(), catId, rowId);

    }



    public void fetchFilteredPoints(List<String> filterIds, final Callback<List<Point>> callback){
        // perform the db query on separate thread
        final List<Point> resultPoint = new ArrayList<Point>();
        Cursor cursor = PointTable.fetchFilteredCategoryPoints(databaseOpenHelper.getReadableDatabase(), filterIds);
        if (cursor.getCount() == 0) callback.onSuccess(resultPoint);
        else if (cursor.moveToFirst()) {
            do {
                resultPoint.add(pointFromCursor(cursor));

            } while (cursor.moveToNext());
            cursor.close();
            callback.onSuccess(resultPoint);
        }
    }

    public void fetchAllPoints( final Callback<List<Point>> callback){
        // perform the db query on separate thread
        final List<Point> resultPoint = new ArrayList<Point>();
        Cursor cursor = PointTable.fetchAllPoints(databaseOpenHelper.getReadableDatabase());
        if (cursor.moveToFirst()){
            do {
                resultPoint.add(pointFromCursor(cursor));

            } while (cursor.moveToNext());
            cursor.close();
            callback.onSuccess(resultPoint);
        }

    }
    public void fetchAllPointsAndCategories( final Callback2<List<Point>, List<Category>> callback2){
        // perform the db query on separate thread
        final List<Point> resultPoint = new ArrayList<Point>();
        final List<Category> catResult = new ArrayList<>();
        Cursor cursor = PointTable.fetchAllPoints(databaseOpenHelper.getReadableDatabase());
        if (cursor.moveToFirst()) {
            do {
                resultPoint.add(pointFromCursor(cursor));

            } while (cursor.moveToNext());
            cursor.close();
        }
        cursor = CategoryTable.fetchAllCategories(databaseOpenHelper.getReadableDatabase());
        if (cursor.moveToFirst()){
                do {
                    catResult.add(categoryFromCursor(cursor));

                } while (cursor.moveToNext());
            cursor.close();
            callback2.onSuccess(resultPoint, catResult);
        }

    }

    // Given a cursor - get the point data

    static Point pointFromCursor(Cursor cursor){
        return new Point(Table.getRowId(cursor),PointTable.getName(cursor),PointTable.getLat(cursor),
                PointTable.getLong(cursor), PointTable.getVisited(cursor), PointTable.getCategoryId(cursor));

    }
    public void fetchAllCategories( final Callback<List<Category>> callback){
        // perform the db query on separate thread
        final List<Category> resultCategories = new ArrayList<Category>();
        Cursor cursor = CategoryTable.fetchAllCategories(databaseOpenHelper.getReadableDatabase());
        if (cursor.moveToFirst()){
            do {
                resultCategories.add(categoryFromCursor(cursor));

            } while (cursor.moveToNext());
            cursor.close();
            callback.onSuccess(resultCategories);
        }

    }

    // Given a cursor - get the category data

    static Category categoryFromCursor(Cursor cursor){
        return new Category(Table.getRowId(cursor),CategoryTable.getName(cursor),
                        CategoryTable.getMarkerColor(cursor), CategoryTable.getBackgroundColor(cursor), false);

    }

    public void addCategory (Category category){
        SQLiteDatabase writableDatabase = databaseOpenHelper.getWritableDatabase();

        new CategoryTable.Builder()
                .setName(category.getName())
                .setMarkerColor(String.valueOf(category.getMarkerColor()))
                .setBackgroundColor(String.valueOf(category.getBackgroundColor()))
                .insert(writableDatabase);


    }
}
