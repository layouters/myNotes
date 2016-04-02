package com.uznamska.lukas.mynotes.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Anna on 2016-03-06.
 */
public class NotesTable {
    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_LIST_ORDER = "list_order";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CATEGORY + " text not null, "
            + COLUMN_TYPE + " text not null, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_TEXT  + " text not null, "
            + COLUMN_LIST_ORDER  + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(NotesTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all the old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
