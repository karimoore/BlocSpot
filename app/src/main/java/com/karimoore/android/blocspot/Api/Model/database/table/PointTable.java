package com.karimoore.android.blocspot.Api.Model.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by kari on 2/15/16.
 */
public class PointTable extends Table {

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LAT = "latitude";
    private static final String COLUMN_LONG = "longitude";
    private static final String COLUMN_VISIT = "visited";
    private static final String COLUMN_CATEGORYID = "category_id";
    private static final String COLUMN_NOTE = "note";
    private static final String NAME = "point";


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCreateStatement() {
        return "CREATE TABLE " + getName() + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT UNIQUE, "
                + COLUMN_LAT + " TEXT, "
                + COLUMN_LONG + " TEXT, "
                + COLUMN_VISIT + " TEXT, "
                + COLUMN_CATEGORYID + " TEXT, "
                + COLUMN_NOTE + " TEXT)";
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
        public Builder setVisited(String visited) {
            values.put(COLUMN_VISIT, visited);
            return this;
        }
        public Builder setCategoryId(String catId) {
            values.put(COLUMN_CATEGORYID, catId);
            return this;
        }
        public Builder setNote(String note) {
            values.put(COLUMN_NOTE, note);
            return this;
        }


        @Override
        public long insert(SQLiteDatabase writableDB) {
            return writableDB.insert(NAME, null, values);
        }
    }

/*
    ContentValues cv = new ContentValues();
    cv.put(KEY_CUR_LEVEL, level);

    mDb.update(DATABASE_TABLE, cv, "? = ?", new String[] { KEY_NAME, "Default"});


First make a ContentValues object :

ContentValues cv = new ContentValues();
cv.put("Field1","Bob"); //These Fields should be your String values of actual column names
cv.put("Field2","19");
cv.put("Field2","Male");
Then use the update method, it should work now:

myDB.update(TableName, cv, "_id="+id, null);
*/
public static int updateCategoryColumn(SQLiteDatabase writableDatabase, int categoryId, int rowId){
    ContentValues cv = new ContentValues();
    cv.put(COLUMN_CATEGORYID, String.valueOf(categoryId));
    String whereClause = COLUMN_ID+ "=" + rowId;
    return writableDatabase.update(NAME, cv, whereClause, null);

}
    public static int updateNoteColumn(SQLiteDatabase writableDatabase, int rowId, String note){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOTE, note);
        String whereClause = COLUMN_ID+ "=" + rowId;
        return writableDatabase.update(NAME, cv, whereClause, null);

    }
    public static int updateVisitedColumn(SQLiteDatabase writableDatabase, int rowId, boolean visited){
        ContentValues cv = new ContentValues();
        int i = visited? 1:0;
        cv.put(COLUMN_VISIT, String.valueOf(i));
        String whereClause = COLUMN_ID+ "=" + rowId;
        return writableDatabase.update(NAME, cv, whereClause, null);

    }

    public static Cursor fetchAllPoints( SQLiteDatabase readOnlyDatabase){
        return readOnlyDatabase.rawQuery("SELECT * FROM " + NAME + " ORDER BY ?", new String[]{COLUMN_NAME});
    }

    public static Cursor fetchFilteredCategoryPoints( SQLiteDatabase readOnlyDatabase, List<String> categoryIds){
        // create (a set of categoryId's)
        StringBuilder sb = new StringBuilder();
        if (categoryIds.size()==1){
            sb.append(categoryIds.get(0));
        } else {
            for (int i = 0; i < categoryIds.size(); i++){
                sb.append(categoryIds.get(i));
                if (i != categoryIds.size()-1){
                    sb.append(",");
                }
            }
        }

        return readOnlyDatabase.rawQuery("SELECT * FROM " + NAME + " WHERE " + COLUMN_CATEGORYID + " IN (" + sb + ") " + " ORDER BY ?", new String[]{COLUMN_NAME});
    }
/*

    */
/*    public Cursor query (boolean distinct, String table, String[] columns, String selection,
                         String[] selectionArgs, String groupBy, String having,
                         String orderBy, String limit)*//*

    public Cursor fetchArchivedItems(SQLiteDatabase readOnlyDatabase, long feed){
        if (feed == -1) {
            // a 1 means that we want all archived
            return readOnlyDatabase.query(true, NAME, new String[]{COLUMN_TITLE}, COLUMN_ARCHIVED + " = ?",
                    new String[]{"1"}, null, null, null, null);
        } else {
            return readOnlyDatabase.query(true, NAME, new String[]{COLUMN_TITLE}, COLUMN_ARCHIVED + " = ? and " + COLUMN_RSS_FEED + "= ?",
                    new String[]{"1", String.valueOf(feed)}, null, null, null, null);

        }
    }
    public Cursor fetchFavoritedItems(SQLiteDatabase readOnlyDatabase, long feed){
        if (feed == -1) {
            return readOnlyDatabase.query(true, NAME, new String[]{COLUMN_TITLE}, COLUMN_FAVORITE + " = ?",
                    new String[]{"1"}, null, null, null, null);
        } else {
            return readOnlyDatabase.query(true, NAME, new String[]{COLUMN_TITLE}, COLUMN_FAVORITE + " = ? and " + COLUMN_RSS_FEED + "= ?",
                    new String[]{"1", String.valueOf(feed)}, null, null, null, null);
        }
    }
    public Cursor fetchAllItems(SQLiteDatabase readOnlyDatabase, long feed){
        return readOnlyDatabase.query(true, NAME, new String[]{COLUMN_TITLE}, COLUMN_RSS_FEED + " = ?",
                new String[]{String.valueOf(feed)}, null, null, null, null);
    }

*/
    public static String getName(Cursor cursor){ return getString(cursor, COLUMN_NAME);}
    public static String getNote(Cursor cursor){ return getString(cursor, COLUMN_NOTE);}
    public static double getLat(Cursor cursor){ return getDouble(cursor, COLUMN_LAT);}
    public static double getLong(Cursor cursor){ return getDouble(cursor, COLUMN_LONG);}
    public static boolean getVisited(Cursor cursor){ return getBoolean(cursor, COLUMN_VISIT);}
    public static long getCategoryId(Cursor cursor){ return getLong(cursor, COLUMN_CATEGORYID);}
}

