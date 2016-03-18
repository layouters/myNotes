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
import com.uznamska.lukas.mynotes.items.IReminder;
import com.uznamska.lukas.mynotes.items.ListItem;
import com.uznamska.lukas.mynotes.items.ListNote;
import com.uznamska.lukas.mynotes.items.NoteFactory;
import com.uznamska.lukas.mynotes.items.TextNote;

import java.util.ArrayList;
import java.util.List;

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
                //Uri noteListUri = NotesContentProvider.LIST_NOTES_CONTENT_URI;
                String selection = NotesTable.TABLE_NOTES + "." + NotesTable.COLUMN_ID +" = ?";
                String[] selectionArgs = {String.valueOf(note.getId())};
                String[] projectionl = {
                        ListItemTable.TABLE_LISTITEM + "." + ListItemTable.COLUMN_TEXT,
                        ListItemTable.COLUMN_IS_CHECKED,
                        NotesTable.COLUMN_TITLE
                };
                cursorlist = mContext.getContentResolver().query(NotesContentProvider.LIST_NOTES_CONTENT_URI,
                                                                 projectionl, selection, selectionArgs, null);
                    while (cursorlist.moveToNext()) {

                        String textitem = cursorlist.getString(0);
                        String noteTitle = cursorlist.getString(2);
                        Log.d(TAG, "TextItem: " + textitem + " Note title " + noteTitle);
                        ListItem item = new ListItem();
                        item.setText(textitem);
                        note.addElement(item);

                    }

                cursorlist.close();
              //  loadItems((ListNote)note);

            }
            loadReminders(note);



        }
        Log.d(TAG, "Return note " + note);
        return note;
    }

    class NoteLoader {

    }

    interface INoteSaver {
        void storeAsTextNote ();
        void storeAsListNote();
        void saveReminder();
    }

    class NoteStorer {

        class NoteCreator implements INoteSaver {
            @Override
            public void storeAsTextNote() {
                values.put(NotesTable.COLUMN_TYPE, "TextNote");
                mUri = mContext.getContentResolver().insert(NotesContentProvider.NOTES_CONTENT_URI,values);
            }

            @Override
            public void storeAsListNote() {
                values.put(NotesTable.COLUMN_TYPE, "ListNote");
                ContentValues val;
                mUri = mContext.getContentResolver().insert(NotesContentProvider.NOTES_CONTENT_URI,
                        values);
                String idx = mUri.getLastPathSegment();
                //TODO: use list iterator here!!!!
                for(int i = 1; i < mNote.getSize() - 3; i++) {
                    val = new ContentValues();
                    val.put(ListItemTable.COLUMN_TEXT, ((ListNote) mNote).getListText(i));
                    val.put(ListItemTable.NOTE_ID, idx);
                    val.put(ListItemTable.COLUMN_IS_CHECKED, 0);
                    val.put(ListItemTable.COLUMN_LIST_ORDER, 0);
                    mContext.getContentResolver().insert(NotesContentProvider.LIST_CONTENT_URI, val);
                }
            }

            @Override
            public void saveReminder() {
                if(mUri != null && mNote.getReminder().isSet()) {
                    String idnote = mUri.getLastPathSegment();
                    ContentValues valuez = new ContentValues();
                    valuez.put(ReminderItemTable.COLUMN_DATE, mNote.getReminder().getDate());
                    valuez.put(ReminderItemTable.COLUMN_RPEAT, "NO REPEAT");
                    valuez.put(ListItemTable.NOTE_ID, idnote);
                    mContext.getContentResolver().insert(NotesContentProvider.REMINDER_CONTENT_URI, valuez);
                }

            }
        }

        class NoteUpdater implements INoteSaver {

            @Override
            public void storeAsTextNote() {
                mContext.getContentResolver().update(mUri, values, null, null);
            }

            @Override
            public void storeAsListNote() {

            }

            @Override
            public void saveReminder() {

            }
        }

        private INoteSaver saver;
        INote mNote;
        Uri mUri;
        int mOrder;
        ContentValues values;

        void configure(INote note, Uri uri, int listOrder) {
            mNote = note;
            mUri = uri;
           // mOrder = listOrder;

            values = new ContentValues();
            values.put(NotesTable.COLUMN_CATEGORY, "none");
            values.put(NotesTable.COLUMN_TITLE, note.getTitle());
            values.put(NotesTable.COLUMN_TEXT, note.getText());
            if(uri == null) {
                values.put(NotesTable.COLUMN_LIST_ORDER, listOrder);
                saver = new NoteCreator();
            } else {
                saver = new NoteUpdater();
            }
        }

         void saveNote() {
            if(mNote instanceof TextNote) {
                saver.storeAsTextNote();
            } else if (mNote instanceof ListNote) {
                saver.storeAsListNote();
            }
             saver.saveReminder();

        }

        Uri getUri() {
            return mUri;
        }

    }

    @Override
    public Uri saveNote(INote note, int listOrder, Uri uri) {
        if(note.getTitle() == null) {
            return null;
        }
        NoteStorer storer = new NoteStorer();
        storer.configure(note, uri, listOrder);
        storer.saveNote();
        return storer.getUri();
    }

    @Override
    public void deleteNote(Uri uri) {
        mContext.getContentResolver().delete(uri, null, null);
        //TODO: Delete all notes and reminders associated with the note.
    }

    @Override
    public void deleteItem(Uri uri) {
        mContext.getContentResolver().delete(uri,null,null);
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
        String[] projection = { NotesTable.COLUMN_TYPE,
                                NotesTable.COLUMN_TITLE,
                                NotesTable.COLUMN_TEXT,
                                NotesTable.COLUMN_ID,
                                NotesTable.COLUMN_LIST_ORDER};
        Cursor tmp_cursor = mContext.getContentResolver().query(NotesContentProvider.NOTES_CONTENT_URI, projection, null,
                null, NotesTable.COLUMN_LIST_ORDER + " ASC");
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
        //Uri noteListUri = NotesContentProvider.LIST_NOTES_CONTENT_URI;
        String selection = NotesTable.TABLE_NOTES + "." + NotesTable.COLUMN_ID +" = ?";
        String[] selectionArgs = {String.valueOf(note.getId())};
        String[] projectionl = {
                ListItemTable.TABLE_LISTITEM + "." + ListItemTable.COLUMN_TEXT,
                ListItemTable.COLUMN_IS_CHECKED,
                NotesTable.COLUMN_TITLE
        };
        cursorlist = mContext.getContentResolver().query(NotesContentProvider.LIST_NOTES_CONTENT_URI,
                                                         projectionl, selection, selectionArgs, null);
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

    private void loadReminders(INote note) {
        Cursor cursorlist;
        String selection = NotesTable.TABLE_NOTES + "." + NotesTable.COLUMN_ID +" = ?";
        String[] selectionArgs = {String.valueOf(note.getId())};
        String[] projection = {
                ReminderItemTable.TABLE_REMINDERITEMS + "." + ReminderItemTable.COLUMN_DATE,
                NotesTable.COLUMN_TITLE
        };
        cursorlist = mContext.getContentResolver().query(NotesContentProvider.REMINDER_CONTENT_URI,
                projection, selection, selectionArgs, null);

        while (cursorlist.moveToNext()) {
            String reminderDate = cursorlist.getString(0);
            String noteTitle = cursorlist.getString(1);
            Log.d(TAG, "DateItem: " + reminderDate + " Note title " + noteTitle);
            IReminder r = note.getReminder();
            r.setDate(reminderDate);
        }
        cursorlist.close();
    }


}
