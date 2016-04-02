package com.uznamska.lukas.mynotes.items;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.uznamska.lukas.mynotes.AlarmService;
import com.uznamska.lukas.mynotes.contentprovider.NotesContentProvider;
import com.uznamska.lukas.mynotes.database.PendingAlarmsTable;
import com.uznamska.lukas.mynotes.database.ReminderItemTable;

public class ItemPendingAlarm  extends AbstractNoteItem {

    private static final String TAG = "MyNotes:ItemsPendingAlarm" ;
    private long mDateTime;
    private int mReminderId;
    private String mStatus;

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        this.mStatus = status;
    }

    public int getReminderId() {
        return mReminderId;
    }

    public void setReminderId(int mReminderId) {
        this.mReminderId = mReminderId;
    }

    public long getDateTime() {
        return mDateTime;
    }

    public void setDateTime(long mDateTime) {
        this.mDateTime = mDateTime;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public void saveDb(Context context) {
        if (getId() == 0) {
            Log.d(TAG, "Insert pending alarm");
            ContentValues valuez = new ContentValues();
            valuez.put(PendingAlarmsTable.COLUMN_TIME, getDateTime());
            valuez.put(PendingAlarmsTable.COLUMN_REMINDER_ID, getReminderId());
            valuez.put(PendingAlarmsTable.COLUMN_STATUS, getStatus() == null ?
                    PendingAlarmsTable.ACTIVE : getStatus());
            Uri uri = context.getContentResolver().insert(NotesContentProvider.PENDING_ALARM_CONTENT_URI, valuez);
            String pendingId = uri.getLastPathSegment();
            Log.d(TAG, "Saving pending alarm id: " + pendingId);
        }
    }

    @Override
    public void deleteFromDb(Context context) {

    }
}
