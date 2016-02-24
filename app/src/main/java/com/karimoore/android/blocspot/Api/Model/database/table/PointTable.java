package com.karimoore.android.blocspot.Api.Model.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kari on 2/15/16.
 */
public class PointTable extends Table {

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LAT = "latitude";
    private static final String COLUMN_LONG = "longitude";
    private static final String NAME = "point";


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCreateStatement() {
        return "CREATE TABLE " + getName() + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_LAT + " TEXT, "
                + COLUMN_LONG + " TEXT)";
    }

    @Override
    public void onUpgrade(SQLiteDatabase writableDatabase, int oldVersion, int newVersion) {
        super.onUpgrade(writableDatabase, oldVersion, newVersion);
    }

    public static class Builder implements Table.Builder {

        ContentValues values = new ContentValues();


        public Builder setName(String name) {
            values.put(COLUMN_NAME, name);
            return this;
        }

        public Builder setLatitude(String latitude) {
            values.put(COLUMN_LAT, latitude);
            return this;
        }

        public Builder setLongitude(String longitude) {
            values.put(COLUMN_LONG, longitude);
            return this;
        }


        @Override
        public long insert(SQLiteDatabase writableDB) {
            return writableDB.insert(NAME, null, values);
        }
    }

    public static Cursor fetchAllPoints( SQLiteDatabase readOnlyDatabase){
        return readOnlyDatabase.rawQuery("SELECT * FROM " + NAME+ " ORDER BY ?", new String[]{COLUMN_NAME});
    }

    public static String getName(Cursor cursor){ return getString(cursor, COLUMN_NAME);}
    public static double getLat(Cursor cursor){ return getDouble(cursor, COLUMN_LAT);}
    public static double getLong(Cursor cursor){ return getDouble(cursor, COLUMN_LONG);}
}

