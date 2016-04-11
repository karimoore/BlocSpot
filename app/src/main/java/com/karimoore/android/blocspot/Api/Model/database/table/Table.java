package com.karimoore.android.blocspot.Api.Model.database.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kari on 2/15/16.
 */
public abstract class Table {

    protected static final String COLUMN_ID = "id";

    public abstract String getName();

    public abstract String getCreateStatement();


    public void onUpgrade(SQLiteDatabase writableDatabase, int oldVersion, int newVersion) {
        // Nothing
    }

    public Cursor fetchRow(SQLiteDatabase readOnlyDatabase, long rowId){
        return readOnlyDatabase.query(true,getName(), null,COLUMN_ID + " = ?",new String[]{String.valueOf(rowId)},
                                        null, null, null, null);
    }

    public static long getRowId(Cursor cursor){
        return getLong(cursor, COLUMN_ID);
    }

    protected static long getLong(Cursor cursor, String column) {
        int colIndex = cursor.getColumnIndex(column);
        if (colIndex == -1) {
            return -1l;
        }
        return cursor.getLong(colIndex);
    }

    protected static int getInt(Cursor cursor, String column){
        int colIndex = cursor.getColumnIndex(column);
        if (colIndex == -1) {
            return -1;
        }
        return cursor.getInt(colIndex);
    }

    protected static double getDouble(Cursor cursor, String column){
        int colIndex = cursor.getColumnIndex(column);
        if (colIndex == -1) {
            return -1;
        }
        return cursor.getDouble(colIndex);
    }
    protected static String getString(Cursor cursor, String column){
        int colIndex = cursor.getColumnIndex(column);
        if (colIndex == -1) {
            return "";
        }
        return cursor.getString(colIndex);
    }
    protected static boolean getBoolean(Cursor cursor, String column) {
        int colIndex = cursor.getColumnIndex(column);
        if (colIndex == -1) {
            return false;
        }
        return (cursor.getString(colIndex).equals("1")) ? true : false;
    }


        public static interface Builder {

        public long insert(SQLiteDatabase writableDB);
    }

}