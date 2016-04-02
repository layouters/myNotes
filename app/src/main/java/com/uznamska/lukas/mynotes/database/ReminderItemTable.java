/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Anna on 2016-03-17.
 */
public class ReminderItemTable {
    public static final String TABLE_REMINDERITEMS = "reminders";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_RPEAT = "repeat";
    public static final String NOTE_ID = "note_id";
    public static final String COLUMN_TIME = "time";

    // Database creation SQL statement
    private static final String CREATE_TABLE = "create table "
            + TABLE_REMINDERITEMS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DATE + " text not null, "
            + COLUMN_TIME + " text not null, "
            + COLUMN_RPEAT + " text not null, "
            + NOTE_ID + " INT, "
            + "FOREIGN KEY(" + NOTE_ID + ") REFERENCES "
            + NotesTable.TABLE_NAME + "(_id) " + ")";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ReminderItemTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all the old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERITEMS);
        onCreate(database);
    }

}
