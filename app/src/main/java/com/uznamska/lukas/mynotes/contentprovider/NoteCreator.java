package com.uznamska.lukas.mynotes.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.uznamska.lukas.mynotes.database.ListItemTable;
import com.uznamska.lukas.mynotes.database.NotesTable;
import com.uznamska.lukas.mynotes.database.ReminderItemTable;
import com.uznamska.lukas.mynotes.items.INote;
import com.uznamska.lukas.mynotes.items.Iterator;
import com.uznamska.lukas.mynotes.items.ListItem;

/**
 * Created by Anna on 2016-03-27.
 */
public class NoteCreator implements INoteSaver {

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
    }

    @Override
    public void storeAsTextNote() {
        values.put(NotesTable.COLUMN_TYPE, "TextNote");
        mUri = mContext.getContentResolver().insert(NotesContentProvider.NOTES_CONTENT_URI, values);
    }

    @Override
    public void storeAsListNote() {
        //Log.d(TAG, "Store as list saver");
        values.put(NotesTable.COLUMN_TYPE, "ListNote");
        ContentValues val;
        mUri = mContext.getContentResolver().insert(NotesContentProvider.NOTES_CONTENT_URI,
                values);
        String idx = mUri.getLastPathSegment();
        mNote.setId(Integer.parseInt(idx));
        Iterator li = mNote.getListItemIterator();
        while (li.hasNext()) {
            ListItem item = (ListItem) li.next();
            val = new ContentValues();
            val.put(ListItemTable.COLUMN_TEXT, item.getText());
            val.put(ListItemTable.NOTE_ID, idx);
            val.put(ListItemTable.COLUMN_IS_CHECKED, item.isTicked());
            val.put(ListItemTable.COLUMN_LIST_ORDER, 0);
            mContext.getContentResolver().insert(NotesContentProvider.LIST_CONTENT_URI, val);
        }
    }

 //   public void saveReminder() {
//        if (mUri != null && mNote.getReminder().isSet()) {
//            String idnote = mUri.getLastPathSegment();
//            ContentValues valuez = new ContentValues();
//            valuez.put(ReminderItemTable.COLUMN_DATE, mNote.getReminder().getDate());
//            valuez.put(ReminderItemTable.COLUMN_RPEAT, "NO REPEAT");
//            valuez.put(ReminderItemTable.NOTE_ID, idnote);
//            mContext.getContentResolver().insert(NotesContentProvider.REMINDER_CONTENT_URI, valuez);
//        }
//
//    }
//
}
