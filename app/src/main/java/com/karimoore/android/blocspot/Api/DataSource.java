package com.karimoore.android.blocspot.Api;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.karimoore.android.blocspot.Api.Model.Point;
import com.karimoore.android.blocspot.Api.Model.database.DatabaseOpenHelper;
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
    public static interface Callback<Result>{
        public void onSuccess(Result result);
        public void onError(String errorMessage);
    }


    private DatabaseOpenHelper databaseOpenHelper;
    private PointTable pointTable;

    public DataSource(){

        pointTable = new PointTable();

        databaseOpenHelper = new DatabaseOpenHelper(BlocSpotApplication.getSharedInstance(),
                pointTable);

        new Thread(new Runnable() {
            @Override
            public void run() {

                //if (BuildConfig.DEBUG && true) {
                    BlocSpotApplication.getSharedInstance().deleteDatabase("blocspot_db");
                //}

                SQLiteDatabase writableDatabase = databaseOpenHelper.getWritableDatabase();
                // ask user for data?
                new PointTable.Builder()
                        .setName("restaurant1")
                        .setLatitude("41.1")
                        .setLongitude("-88")
                        .insert(writableDatabase);
                new PointTable.Builder()
                        .setName("restaurant2")
                        .setLatitude("41.2")
                        .setLongitude("-88")
                        .insert(writableDatabase);
                new PointTable.Builder()
                        .setName("restaurant3")
                        .setLatitude("41.3")
                        .setLongitude("-88")
                        .insert(writableDatabase);
                new PointTable.Builder()
                        .setName("restaurant4")
                        .setLatitude("41.4")
                        .setLongitude("-88")
                        .insert(writableDatabase);

            }
        }).start();
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

    // Given a cursor - get the point data

    static Point pointFromCursor(Cursor cursor){
        return new Point(Table.getRowId(cursor),PointTable.getName(cursor),PointTable.getLat(cursor), PointTable.getLong(cursor));

    }
}
