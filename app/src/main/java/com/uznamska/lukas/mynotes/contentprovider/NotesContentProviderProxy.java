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
import com.uznamska.lukas.mynotes.items.INote;
import com.uznamska.lukas.mynotes.items.ListItem;
import com.uznamska.lukas.mynotes.items.ListNote;
import com.uznamska.lukas.mynotes.items.NoteFactory;
import com.uznamska.lukas.mynotes.items.TextNote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anna on 2016-03-10.
 */
public class NotesContentProviderProxy  implements INoteContentProvider {
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
                NotesTable.COLUMN_TEXT, NotesTable.COLUMN_TYPE,NotesTable.COLUMN_ID };


        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String type = cursor.getString(cursor
                    .getColumnIndexOrThrow(NotesTable.COLUMN_TYPE));
            //Log.d(TAG, "This is type " + type);

            note = factory.getNote(type);

            note.setTitle(cursor.getString(cursor
                    .getColumnIndexOrThrow(NotesTable.COLUMN_TITLE)));
            note.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(NotesTable.COLUMN_TEXT)));
            note.setId(cursor.getInt(cursor
                    .getColumnIndexOrThrow(NotesTable.COLUMN_ID)));

            cursor.close();
            if (note instanceof ListNote) {
                Cursor cursorlist;
                   // SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

                Uri noteListUri = NotesContentProvider.LIST_NOTES_CONTENT_URI;
                String selection = NotesTable.TABLE_NOTES+"."+NotesTable.COLUMN_ID +" = ?";
                String[] selectionArgs = {String.valueOf(note.getId())};
                String[] projectionl = {
                        ListItemTable.TABLE_LISTITEM + "." + ListItemTable.COLUMN_TEXT,
                        ListItemTable.COLUMN_IS_CHECKED,
                        NotesTable.COLUMN_TITLE
                };
                cursorlist = mContext.getContentResolver().query(NotesContentProvider.LIST_NOTES_CONTENT_URI, projectionl, selection, selectionArgs,
                        null);
                Log.d(TAG, "cursorlist " + cursorlist);
                    while (cursorlist.moveToNext()) {

                        String textitem = cursorlist.getString(0);
                        String noteTitle = cursorlist.getString(2);
                        Log.d(TAG, "TextItem: " + textitem + " Note title " + noteTitle);
                        ListItem item = new ListItem();
                        item.setText(textitem);
                        note.addElement(item);

                    }

                cursorlist.close();
            }

            // always close the cursor
        }
        Log.d(TAG, "Return note " + note);

        return note;
    }

    @Override
    public Uri saveNote(INote note, int listOrder, Uri uri) {
        String title = note.getTitle();
        String text = note.getText();
        int id = note.getId();

        if (title == null && text.length() == 0) {
            return null;
        }
       // Log.d(TAG, "SAVING DATA");

        ContentValues values = new ContentValues();
        values.put(NotesTable.COLUMN_CATEGORY, "none");
        values.put(NotesTable.COLUMN_TITLE, title);
        values.put(NotesTable.COLUMN_TEXT, text);
        if(uri == null)
            values.put(NotesTable.COLUMN_LIST_ORDER, listOrder);
        if(note instanceof TextNote) {
            values.put(NotesTable.COLUMN_TYPE, "TextNote");

            if (uri == null) {
                // New note
                uri = mContext.getContentResolver().insert(NotesContentProvider.NOTES_CONTENT_URI, values);
            } else {
                // Update note
                mContext.getContentResolver().update(uri, values, null, null);
            }
        } else if(note instanceof ListNote) {
            values.put(NotesTable.COLUMN_TYPE, "ListNote");
            if (uri == null) {
                // New note
                ContentValues val;
                uri = mContext.getContentResolver().insert(NotesContentProvider.NOTES_CONTENT_URI, values);
                String idx = uri.getLastPathSegment();
                for(int i =1; i < note.getSize()-1; i++) {
                    val = new ContentValues();
                    val.put(ListItemTable.COLUMN_TEXT, ((ListNote) note).getListText(i));
                    val.put(ListItemTable.NOTE_ID, idx);
                    val.put(ListItemTable.COLUMN_IS_CHECKED, 0);
                    val.put(ListItemTable.COLUMN_LIST_ORDER,0);
                    mContext.getContentResolver().insert(NotesContentProvider.LIST_CONTENT_URI, val);
                }
            }

        }


        return uri;
    }

    @Override
    public void deleteNote(Uri uri) {
        mContext.getContentResolver().delete(uri, null, null);
        //TODO: Fix all order fields
        //TODO: Decrease all the order fields below deleted item  by one
    }


    public List<INote> loadListOfNotes() {
        List<INote> list = new ArrayList<INote>();
        String[] projection = { NotesTable.COLUMN_TYPE,
                                NotesTable.COLUMN_TITLE,
                                NotesTable.COLUMN_TEXT,
                                NotesTable.COLUMN_ID,
                                NotesTable.COLUMN_LIST_ORDER};
        Cursor tmp_cursor = mContext.getContentResolver().query(NotesContentProvider.NOTES_CONTENT_URI, projection, null,
                null, NotesTable.COLUMN_LIST_ORDER +  " ASC");
        if(tmp_cursor != null) {

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
                INote note  = factory.getNote(type);
                Log.d(TAG, "type: " + type);
                note.setTitle(titler);
                note.setText(text);
                note.setId(id);
                note.setListOrder(listOrder);
                if(note instanceof ListNote) {
                    loadItems((ListNote)note);
                }
                list.add(note);
            }
        }
        int next = 0;
        if(list.size() > 0) {
            next = list.get(list.size() - 1).getListOrder() + 1;
        }

        return list;
    }

    private void loadItems(ListNote note) {

        Cursor cursorlist;
        // SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        Uri noteListUri = NotesContentProvider.LIST_NOTES_CONTENT_URI;
        String selection = NotesTable.TABLE_NOTES+"."+NotesTable.COLUMN_ID +" = ?";
        String[] selectionArgs = {String.valueOf(note.getId())};
        String[] projectionl = {
                ListItemTable.TABLE_LISTITEM + "." + ListItemTable.COLUMN_TEXT,
                ListItemTable.COLUMN_IS_CHECKED,
                NotesTable.COLUMN_TITLE
        };
        cursorlist = mContext.getContentResolver().query(NotesContentProvider.LIST_NOTES_CONTENT_URI, projectionl, selection, selectionArgs,
                null);
        Log.d(TAG, "cursorlist " + cursorlist);
        while (cursorlist.moveToNext()) {

            String textitem = cursorlist.getString(0);
            String noteTitle = cursorlist.getString(2);
            Log.d(TAG, "TextItem: " + textitem + " Note title " + noteTitle);
            ListItem item = new ListItem();
            item.setText(textitem);
            note.addElement(item);

        }

        cursorlist.close();
    }

}
