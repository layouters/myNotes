package com.uznamska.lukas.mynotes.items;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
            //return Integer.parseInt(pendingId);
        } else {
            ContentValues cv = new ContentValues();
            if (getReminderId() > 0)
                cv.put(PendingAlarmsTable.COLUMN_REMINDER_ID, getReminderId());
            if (getDateTime() > 0)
                cv.put(PendingAlarmsTable.COLUMN_TIME, getDateTime());
            if (getStatus()  != null)
                cv.put(PendingAlarmsTable.COLUMN_STATUS, getStatus() );
            Uri uri = Uri.parse(NotesContentProvider.PENDING_ALARM_CONTENT_URI + "/" + this.getId());
            int updated = context.getContentResolver().update(uri, cv, null, null);
           //return getId();
        }
    }

    @Override
    public void deleteFromDb(Context context) {

    }

    public static  Cursor getRelatedAlarms(Context context, String... args) {
        String[] columns = { PendingAlarmsTable.COLUMN_ID,
                             PendingAlarmsTable.COLUMN_REMINDER_ID,
                             PendingAlarmsTable.COLUMN_TIME,
                             PendingAlarmsTable.COLUMN_STATUS };
        String selection = "1 = 1";
        selection += (args!=null && args.length>0 && args[0]!=null) ? " AND "+
                        PendingAlarmsTable.COLUMN_REMINDER_ID +" = "+args[0] : "";
        selection += (args!=null && args.length>1 && args[1]!=null) ? " AND "+
                        PendingAlarmsTable.COLUMN_TIME +" >= "+args[1] : "";
        selection += (args!=null && args.length>2 && args[2]!=null) ? " AND "+
                        PendingAlarmsTable.COLUMN_TIME +" <= "+args[2] : "";
        selection += (args!=null && args.length>3 && args[3]!=null) ? " AND "+
                        PendingAlarmsTable.COLUMN_STATUS +" = '"+args[3]+"'" : "";
       return  context.getContentResolver().query(NotesContentProvider.PENDING_ALARM_CONTENT_URI,
                                                    columns, selection, null, null);

    }
}
