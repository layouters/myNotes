package com.uznamska.lukas.mynotes.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PendingAlarmsTable {
    public static final String TABLE_NAME = "pending_alarms";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_REMINDER_ID = "reminder_id";

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TIME + " integer not null, "
            + COLUMN_REMINDER_ID + " INT, "
            + "FOREIGN KEY(" + COLUMN_REMINDER_ID + ") REFERENCES "
            + ReminderItemTable.TABLE_REMINDERITEMS + "(_id) " + ")";


    public static final String ACTIVE = "A";
    public static final String EXPIRED = "E";
    public static final String CANCELLED = "C";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(PendingAlarmsTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all the old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
