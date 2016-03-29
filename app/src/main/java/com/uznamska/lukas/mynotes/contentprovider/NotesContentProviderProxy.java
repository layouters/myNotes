/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.uznamska.lukas.mynotes.database.ListItemTable;
import com.uznamska.lukas.mynotes.database.NotesTable;
import com.uznamska.lukas.mynotes.database.ReminderItemTable;
import com.uznamska.lukas.mynotes.items.AbstractNote;
import com.uznamska.lukas.mynotes.items.INote;
import com.uznamska.lukas.mynotes.items.INoteItem;
import com.uznamska.lukas.mynotes.items.IReminder;
import com.uznamska.lukas.mynotes.items.Iterator;
import com.uznamska.lukas.mynotes.items.ListItem;
import com.uznamska.lukas.mynotes.items.ListNote;
import com.uznamska.lukas.mynotes.items.NoteFactory;
import com.uznamska.lukas.mynotes.items.TextNote;

import java.util.ArrayList;
import java.util.List;

public class NotesContentProviderProxy implements INoteContentProvider {
    private static final String TAG = "Notes:ProxyContentProvider";
    Context mContext;
    NoteFactory factory;

    public NotesContentProviderProxy(Context context) {
        mContext = context;
        factory = new NoteFactory();
    }

    @Override
    public INote getNoteFromUri(Uri uri) {
        Log.d(TAG, "Get Note from uri " + uri);
        INote note = null;
        String[] projection = {NotesTable.COLUMN_TITLE,
                NotesTable.COLUMN_TEXT, NotesTable.COLUMN_TYPE, NotesTable.COLUMN_ID};

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String type = cursor.getString(cursor
                    .getColumnIndexOrThrow(NotesTable.COLUMN_TYPE));
            note = factory.getNote(type);

            note.setTitle(cursor.getString(cursor
                    .getColumnIndexOrThrow(NotesTable.COLUMN_TITLE)));
            note.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(NotesTable.COLUMN_TEXT)));
            note.setId(cursor.getInt(cursor
                    .getColumnIndexOrThrow(NotesTable.COLUMN_ID)));

            cursor.close();
            note.loadItems(mContext);
            note.loadReminders(mContext);
        }
        Log.d(TAG, "Return note " + note);
        return note;
    }

    class NoteLoader {

    }

    @Override
    public void deleteNote(Uri uri) {
        mContext.getContentResolver().delete(uri, null, null);
        //TODO: Delete all notes and reminders associated with the note.
    }

    public void deleteNote(INote note) {
        int id = note.getId();
        Uri toDeleteUri = Uri.parse(NotesContentProvider.NOTES_CONTENT_URI + "/" + id);


        mContext.getContentResolver().delete(toDeleteUri, null, null);
        //TODO: Delete all notes and reminders associated with the note.

    }

    @Override
    public void deleteItem(Uri uri) {
        mContext.getContentResolver().delete(uri, null, null);
    }

    public void deleteItem(INoteItem item) {
        Log.d(TAG, "delete item " + item);
        Uri toDeleteUri = Uri.parse(NotesContentProvider.LIST_CONTENT_URI + "/" + item.getId());
        mContext.getContentResolver().delete(toDeleteUri, null, null);
    }

    //@Override
    public int saveListItem(INote note) {
//        String idnote = mUri.getLastPathSegment();
        //Adding list items to the note with id = 1//
        int noteId = note.getId();
        ContentValues values = new ContentValues();
        values.put(ListItemTable.COLUMN_TEXT, "");
        values.put(ListItemTable.COLUMN_IS_CHECKED, "FALSE");
        values.put(ListItemTable.COLUMN_LIST_ORDER, 0);
        values.put(ListItemTable.NOTE_ID, noteId);
        Uri muri = mContext.getContentResolver().insert(NotesContentProvider.LIST_CONTENT_URI, values);
        String idnote = muri.getLastPathSegment();
        int idx = Integer.parseInt(idnote);
        return idx;
    }

    public List<INote> loadListOfNotes() {
        List<INote> list = new ArrayList<INote>();
        String[] projection = {NotesTable.COLUMN_TYPE,
                NotesTable.COLUMN_TITLE,
                NotesTable.COLUMN_TEXT,
                NotesTable.COLUMN_ID,
                NotesTable.COLUMN_LIST_ORDER};
        Cursor tmp_cursor = mContext.getContentResolver().query(NotesContentProvider.NOTES_CONTENT_URI, projection, null,
                null, NotesTable.COLUMN_LIST_ORDER + " ASC");
        if (tmp_cursor != null) {
            while (tmp_cursor.moveToNext()) {
                String titler = tmp_cursor.getString(tmp_cursor.getColumnIndexOrThrow
                        (NotesTable.COLUMN_TITLE));
                String text = tmp_cursor.getString(tmp_cursor.getColumnIndexOrThrow
                        (NotesTable.COLUMN_TEXT));
                String type = tmp_cursor.getString(tmp_cursor.getColumnIndexOrThrow
                        (NotesTable.COLUMN_TYPE));
                int id = tmp_cursor.getInt(tmp_cursor.getColumnIndexOrThrow
                        (NotesTable.COLUMN_ID));
                int listOrder = tmp_cursor.getInt(tmp_cursor.getColumnIndexOrThrow
                        (NotesTable.COLUMN_LIST_ORDER));
                INote note = factory.getNote(type);
                Log.d(TAG, "type: " + type);
                note.setTitle(titler);
                note.setText(text);
                note.setId(id);
                note.setListOrder(listOrder);
                note.loadItems(mContext);
                list.add(note);
            }
        }
        int next = 0;
        if (list.size() > 0) {
            next = list.get(list.size() - 1).getListOrder() + 1;
        }
        return list;
    }
}
