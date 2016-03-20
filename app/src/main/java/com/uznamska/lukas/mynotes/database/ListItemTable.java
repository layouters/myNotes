/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ListItemTable {
    public static final String TABLE_LISTITEM = "listitems";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IS_CHECKED = "ischecked";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_LIST_ORDER = "list_order";
    public static final String NOTE_ID = "note_id";

    public static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_LISTITEM + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_TEXT + " TEXT, "
            + COLUMN_IS_CHECKED + " BOOLEAN, "
            + NOTE_ID + " INT, "
            + COLUMN_LIST_ORDER  + " integer not null, "
            + "FOREIGN KEY(" + NOTE_ID + ") REFERENCES "
            + NotesTable.TABLE_NOTES + "(_id) " + ")";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(NotesTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all the old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTITEM);
        onCreate(database);
    }

}
