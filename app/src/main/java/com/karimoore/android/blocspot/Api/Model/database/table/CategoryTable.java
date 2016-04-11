package com.karimoore.android.blocspot.Api.Model.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kari on 3/16/16.
 */
public class CategoryTable extends Table {
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_MARKER_COLOR = "markercolor";
    private static final String COLUMN_BACK_COLOR = "backcolor";
    private static final String NAME = "category";


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCreateStatement() {
        return "CREATE TABLE " + getName() + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_MARKER_COLOR + " TEXT, "
                + COLUMN_BACK_COLOR + " TEXT)";

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

        public Builder setMarkerColor(String color) {
            values.put(COLUMN_MARKER_COLOR, color);
            return this;
        }
        public Builder setBackgroundColor(String color) {
            values.put(COLUMN_BACK_COLOR, color);
            return this;
        }


        @Override
        public long insert(SQLiteDatabase writableDB) {
            return writableDB.insert(NAME, null, values);
        }
    }

    public static Cursor fetchAllCategories( SQLiteDatabase readOnlyDatabase){
        return readOnlyDatabase.rawQuery("SELECT * FROM " + NAME + " ORDER BY ?", new String[]{COLUMN_NAME});
    }


    public static String getName(Cursor cursor){ return getString(cursor, COLUMN_NAME);}
    public static long getMarkerColor(Cursor cursor){ return getLong(cursor, COLUMN_MARKER_COLOR);}
    public static int getBackgroundColor(Cursor cursor){ return getInt(cursor, COLUMN_BACK_COLOR);}
}