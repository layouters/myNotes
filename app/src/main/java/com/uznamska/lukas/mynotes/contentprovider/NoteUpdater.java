package com.uznamska.lukas.mynotes.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.uznamska.lukas.mynotes.database.ListItemTable;
import com.uznamska.lukas.mynotes.database.ReminderItemTable;
import com.uznamska.lukas.mynotes.items.INote;
import com.uznamska.lukas.mynotes.items.Iterator;
import com.uznamska.lukas.mynotes.items.ListItem;

public class NoteUpdater implements INoteSaver {
    private static final String TAG = "NoteUpdater";
    INote mNote;
    Uri mUri;
    ContentValues values;
    Context mContext;



    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }

    public ContentValues getValues() {
        return values;
    }

    public void setValues(ContentValues values) {
        this.values = values;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public INote getNote() {
        return mNote;
    }

    public void setNote(INote mNote) {
        this.mNote = mNote;
       // mUri = mNote.getUri();
    }

    @Override
    public void storeAsTextNote() {
        mContext.getContentResolver().update(mNote.getUri(), values, null, null);
    }

    @Override
    public void storeAsListNote() {
        Log.d(TAG, "Store as list updater");
        mContext.getContentResolver().update(mNote.getUri(), values, null, null);
        Iterator it = mNote.getListItemIterator();
        Log.d(TAG, "Getting item iterator" + it.getItemsNumber());
        while (it.hasNext()) {
            ListItem item = (ListItem) it.next();
            if (item.getId() > 0) {
                Log.d(TAG, "Updating database " + item);
                ContentValues vals = buildValuesFromItem(item, mNote.getId());
                Uri toUpdateUri = Uri.parse(NotesContentProvider.LIST_CONTENT_URI + "/" + item.getId());
                mContext.getContentResolver().update(toUpdateUri, vals, null, null);
            }
        }
    }

    private ContentValues buildValuesFromItem(ListItem item, int noteid) {
        ContentValues val = new ContentValues();
        val.put(ListItemTable.COLUMN_TEXT, item.getText());
        val.put(ListItemTable.NOTE_ID, noteid);
        val.put(ListItemTable.COLUMN_IS_CHECKED, item.isTicked());
        val.put(ListItemTable.COLUMN_LIST_ORDER, 0);
        return val;
    }

//    @Override
//    public void saveReminder() {
//        Log.d(TAG, "save Reminder updater");
//        if (mUri != null && mNote.getReminder().isSet()) {
//            int reminderid = mNote.getReminder().getId();
//            Log.d(TAG, "GETTING REMINDER ID " + reminderid);
//            ContentValues valuez = new ContentValues();
//            valuez.put(ReminderItemTable.COLUMN_DATE, mNote.getReminder().getDate());
//            valuez.put(ReminderItemTable.COLUMN_RPEAT, "NO REPEAT");
//            //
//            if (reminderid > 0) {
//                Uri toupdate = Uri.parse(NotesContentProvider.REMINDER_CONTENT_URI + "/" + reminderid);
//                mContext.getContentResolver().update(toupdate, valuez, null, null);
//            } else {
//                valuez.put(ReminderItemTable.NOTE_ID, mNote.getId());
//                mContext.getContentResolver().insert(NotesContentProvider.REMINDER_CONTENT_URI, valuez);
//            }
//        }
//
//    }
}
