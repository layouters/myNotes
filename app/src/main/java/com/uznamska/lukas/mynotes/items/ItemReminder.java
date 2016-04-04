/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.uznamska.lukas.mynotes.AlarmService;
import com.uznamska.lukas.mynotes.contentprovider.NotesContentProvider;
import com.uznamska.lukas.mynotes.database.NotesTable;
import com.uznamska.lukas.mynotes.database.PendingAlarmsTable;
import com.uznamska.lukas.mynotes.database.ReminderItemTable;


public class ItemReminder extends AbstractNoteItem implements IReminder {
    private static final String TAG = "MyNotes:ItemReminder";
    String date;
    String repeat;
    String mTime;
    boolean mIsSet = false;
    int mNoteId;

    @Override
    public int getNoteId() {
        return mNoteId;
    }

    @Override
    public void setNoteId(int mNoteId) {
        this.mNoteId = mNoteId;
    }

    @Override
    public String toString() {
        return "ItemReminder{" +
                "date='" + date + '\'' +
                ", repeat='" + repeat + '\'' +
                ", mTime='" + mTime + '\'' +
                ", mIsSet=" + mIsSet +
                ", mNoteId=" + mNoteId +
                "} " + super.toString();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setTime(String time) {
        mTime = time;
    }

    @Override
    public String getTime() {
        return mTime;
    }

    @Override
    public void setRepeat(String repeat) {

    }

    @Override
    public String getRepeat() {
        return "NONE";
    }

    @Override
    public boolean isSet() {
        return mIsSet;
    }

    @Override
    public void set(boolean isSet) {
        mIsSet = isSet;
    }

    @Override
    public void saveDb(Context context) {
        if (getId() == 0) {
            Log.d(TAG, "Insert reminder");
            int idnote = getNoteId();
            Log.d(TAG, "Id note " + idnote);
            ContentValues valuez = new ContentValues();
            valuez.put(ReminderItemTable.COLUMN_DATE, getDate());
            valuez.put(ReminderItemTable.COLUMN_TIME, getTime());
            valuez.put(ReminderItemTable.COLUMN_RPEAT, "NO REPEAT");
            valuez.put(ReminderItemTable.NOTE_ID, idnote);
            Uri uri = context.getContentResolver().insert(NotesContentProvider.REMINDER_CONTENT_URI, valuez);
            String remindId = uri.getLastPathSegment();
            Log.d(TAG, "Saving reminder id: " + remindId );

            Intent service = new Intent(context, AlarmService.class);
            service.putExtra(PendingAlarmsTable.COLUMN_REMINDER_ID, String.valueOf(remindId));
            service.setAction(AlarmService.CREATE);
            context.startService(service);


        } else {
            Log.d(TAG, "Update reminder");
            ContentValues valuez = new ContentValues();
            valuez.put(ReminderItemTable.COLUMN_DATE, getDate());
            valuez.put(ReminderItemTable.COLUMN_TIME, getTime());
            valuez.put(ReminderItemTable.COLUMN_RPEAT, "NO REPEAT");

            Uri toupdate = Uri.parse(NotesContentProvider.REMINDER_CONTENT_URI + "/" + getId());
            context.getContentResolver().update(toupdate, valuez, null, null);
        }
    }

    @Override
    public void loadFromDB(Context context) {
        Cursor cursorlist;
        String selection = ReminderItemTable.TABLE_REMINDERITEMS + "." + ReminderItemTable.COLUMN_ID +" = ?";
        Log.d(TAG, "Load reminder of id: " + getId());
        String[] selectionArgs = {String.valueOf(getId())};
        String[] projection = {
                ReminderItemTable.TABLE_REMINDERITEMS + "." + ReminderItemTable.COLUMN_DATE,
                ReminderItemTable.TABLE_REMINDERITEMS + "." + ReminderItemTable.COLUMN_TIME,
                ReminderItemTable.TABLE_REMINDERITEMS + "." + ReminderItemTable.NOTE_ID,
        };
        cursorlist = context.getContentResolver().query(NotesContentProvider.REMINDER_CONTENT_URI,
                projection, selection, selectionArgs, null);

        while (cursorlist.moveToNext()) {
            String reminderDate = cursorlist.getString(0);
            String reminderTime = cursorlist.getString(1);
            int noteId = cursorlist.getInt(2);
           // Log.d(TAG, "DateItem: " + reminderDate + " Note title " + noteTitle);
            setDate(reminderDate);
            setTime(reminderTime);
            setNoteId(noteId);
        }
    }

    @Override
    public void deleteFromDb(Context context) {
        //delete pendings
        Log.d(TAG, "Delete reminder " + getUri());
        context.getContentResolver().delete(getUri(), null, null);
    }

    private Uri getUri() {
        return Uri.parse(NotesContentProvider.REMINDER_CONTENT_URI + "/" + getId());
    }


}
