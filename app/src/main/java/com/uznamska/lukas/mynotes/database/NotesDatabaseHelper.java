package com.uznamska.lukas.mynotes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * C
 */
public class NotesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notestables.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "Notes:DatabaseHelper";
    SQLiteDatabase db;

    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create database");
        NotesTable.onCreate(db);
        ListItemTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        NotesTable.onUpgrade(db, oldVersion, newVersion);
        ListItemTable.onUpgrade(db, oldVersion, newVersion);
    }
}
