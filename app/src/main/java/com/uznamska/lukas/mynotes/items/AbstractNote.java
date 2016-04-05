/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.uznamska.lukas.mynotes.contentprovider.INoteSaver;
import com.uznamska.lukas.mynotes.contentprovider.NoteCreator;
import com.uznamska.lukas.mynotes.contentprovider.NoteUpdater;
import com.uznamska.lukas.mynotes.contentprovider.NotesContentProvider;
import com.uznamska.lukas.mynotes.database.NotesTable;
import com.uznamska.lukas.mynotes.database.ReminderItemTable;

import java.util.List;

public abstract class AbstractNote  implements INote {

    private class SimpleItemsIterator implements Iterator {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public INoteItem next() {
            return null;
        }

        @Override
        public int getItemsNumber() {
            return getSimpleItemsNumber();
        }
    }

    private class ItemsIterator implements Iterator {
        java.util.Iterator it;

        public ItemsIterator () {
            it =  getItems().iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public INoteItem next() {
            return (INoteItem)it.next();
        }

        @Override
        public int getItemsNumber() {
            return getSize();
        }
    }

    private class ReminderItemIterator implements Iterator {
        int startPos;
        int lastPos;

        public ReminderItemIterator() {
            startPos =  AbstractNote.this.findFirstItemOfType(ItemReminder.class.getName());
            lastPos = getItemsIterator().getItemsNumber()- 1;
        }

        @Override
        public boolean hasNext() {
            if(startPos < 0) {
                return false;
            }
            return startPos <= lastPos;
        }

        @Override
        public INoteItem next() {
            if(this.hasNext()) {
                return getItems().get(startPos++);
            }
            return null;
        }

        @Override
        public int getItemsNumber() {
            return startPos - lastPos;
        }
    }

    private static final String TAG = "Note:AbstractNote";
    private static int HEADER_POSITION = 0;

    private int id;
    private int order;
    private int simpleItems;
    INoteSaver saver;

    protected List<INoteItem> items;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "AbstractNote{" +
                "id=" + id +
                ", order=" + order +
                ", items=" + items +
                '}';
    }


    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getSimpleItemsNumber() {
        return simpleItems;
    }

    private void countSimpleItems() {
        ++simpleItems;
    }
    private void uncountSimple() {
        --simpleItems;
    }

    private int findFirstItemOfType(String className) {
        Log.e(TAG, "Search for " + className);
        Iterator it = getItemsIterator();
        int pos = 0;
        while(it.hasNext()) {
            String typeName = it.next().getClass().getName();
            if (typeName.equals(className)) {
                Log.d(TAG, className + " has been found on pos " + pos
                                      + "this what i found  "  + typeName);
                return pos;
            }

            pos++;
        }
        Log.e(TAG, className + " has not been found this is the pattern "
                + getItems().get(0).getClass().getName());
        return -1;
    }

    @Override
    public int getListOrder(){
        return order;
    }

    @Override
    public void setListOrder(int order){
        this.order = order;
    }


    protected void addItem(INoteItem item) {
        if(item.isSimple()) {
            countSimpleItems();
        }
        items.add(item);
    }

    protected void removeItem(int pos) {
        if(items.get(pos).isSimple()) {
            uncountSimple();
        }
        items.remove(pos);
    }

    @Override
    public void addItemReminder(INoteItem item) {
        int pos = findFirstItemOfType(ItemReminderAdder.class.getName());
        if(pos > 0) {
            Log.d(TAG, "Removing item from pos " + pos);
            removeItem(pos);
        }
        //TODO: FIX THIS METHOD
        //countReminders++;
        addItem(item);
    }

    protected List<INoteItem> getItems() {
        return items;
    }
    protected void setItems(List<INoteItem> it) {
        items = it;
    }

    @Override
    public INoteItem getItem(int pos) {
        if(items.size() > pos) {
            return items.get(pos);
        }
        return null;
    }

    @Override
    public void setTitle(String title) {
        ((Header) items.get(HEADER_POSITION)).setTitle(title);
    }

    @Override
    public String getTitle() {
        return ((Header) items.get(HEADER_POSITION)).getTitle();
    }

    @Override
    public void setText(String text) {
        ((Header) items.get(HEADER_POSITION)).setText(text);
    }
    @Override
    public String  getText() {
        return ((Header) items.get(HEADER_POSITION)).getText();
    }

    @Override
    public int getSize() {
        return getItems().size();
    }

    @Override
    public Iterator getSimpleItemsIterator() {
        return new SimpleItemsIterator();
    }

    @Override
    public Iterator getItemsIterator() {
        return new ItemsIterator();
    }

    @Override
    public Iterator getReminderIterator() {
        return new ReminderItemIterator();
    }


    @Override
    public void loadItems(Context context){

    }

    @Override
    public Uri getUri() {
        return Uri.parse(NotesContentProvider.NOTES_CONTENT_URI + "/" + getId());
    }

    protected void cleanUpAfterNote(Context context) {
        Log.d(TAG, "Clean up aftre note");
        Iterator it  = getItemsIterator();
        while(it.hasNext()) {
            INoteItem item = it.next();
            Log.d(TAG, "Clean item " + item);
            item.deleteFromDb(context);
        }

    }

    @Override
    public void loadReminders(Context context) {
        Cursor cursorlist;
        String selection = NotesTable.TABLE_NAME + "." + NotesTable.COLUMN_ID +" = ?";
        String[] selectionArgs = {String.valueOf(getId())};
        String[] projection = {
                ReminderItemTable.TABLE_REMINDERITEMS + "." + ReminderItemTable.COLUMN_DATE,
                ReminderItemTable.TABLE_REMINDERITEMS + "." + ReminderItemTable.COLUMN_TIME,
                ReminderItemTable.TABLE_REMINDERITEMS + "." + ReminderItemTable.COLUMN_ID,
                ReminderItemTable.TABLE_REMINDERITEMS + "." + ReminderItemTable.NOTE_ID,
                NotesTable.COLUMN_TITLE
        };
        cursorlist = context.getContentResolver().query(NotesContentProvider.REMINDER_CONTENT_URI,
                projection, selection, selectionArgs, null);

        while (cursorlist.moveToNext()) {
            String reminderDate = cursorlist.getString(0);
            String reminderTime = cursorlist.getString(1);
            int remId = cursorlist.getInt(2);
            String noteTitle = cursorlist.getString(3);
            int noteId = cursorlist.getInt(4);
            Log.d(TAG, "DateItem: " + reminderDate + " Note title " + noteTitle);
            ItemReminder r = new ItemReminder();
            r.setDate(reminderDate);
            r.setTime(reminderTime);
            r.setId(remId);
            r.setNoteId(noteId);
            r.loadPendings(context);
            addItemReminder((AbstractNoteItem) r);
        }

        cursorlist.close();
    }

    protected void saveReminders(Context context) {
        Iterator it = getReminderIterator();
        while(it.hasNext()) {
            IReminder rem = ((IReminder)it.next());
            rem.setNoteId(getId());
            rem.saveDb(context);
        }
    }

    @Override
    public void saveDb(Context context, int order) {
        if (getTitle() == null || getTitle().equals("")) {
            Log.d(TAG, "Title is null");
            saver = null;
            return;
        }
        int listOrder = order;
        ContentValues values = new ContentValues();
        values.put(NotesTable.COLUMN_CATEGORY, "none");
        values.put(NotesTable.COLUMN_TITLE, getTitle());
        values.put(NotesTable.COLUMN_TEXT, getText());

        if (getId() == 0) {
            values.put(NotesTable.COLUMN_LIST_ORDER, listOrder);
            saver = new NoteCreator();
        } else {
            saver = new NoteUpdater();
        }
        saver.setValues(values);
        saver.setNote(this);
        saver.setContext(context);
    }
}
