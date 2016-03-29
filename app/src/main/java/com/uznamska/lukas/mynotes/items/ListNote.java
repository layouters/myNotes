/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.uznamska.lukas.mynotes.contentprovider.NotesContentProvider;
import com.uznamska.lukas.mynotes.database.ListItemTable;
import com.uznamska.lukas.mynotes.database.NotesTable;

import java.util.ArrayList;
import java.util.Collections;


public class ListNote extends AbstractNote implements INoteForList {

    private class ListItemIterator implements Iterator {

        int index;
        int lastIndex;

        public ListItemIterator() {
            index = 1;
            lastIndex = getLastListItemPosition();
        }

        @Override
        public boolean hasNext() {
            return index <= lastIndex;
        }

        @Override
        public INoteItem next() {
            if(this.hasNext()) {
                return getItems().get(index++);
            }
            return null;
        }

        @Override
        public int getItemsNumber() {
            return lastIndex - index;
        }
    }

    private static final String TAG = "Note:ListNote";

    public ListNote() {
        setItems(new ArrayList<INoteItem>());
        addItem(new Header());
        addItem(new ItemAdder());
        addItem(new ItemSeparator());
        addItem(new ItemReminderAdder());
    }

    @Override
    public Iterator getListItemIterator() {
        return new ListItemIterator();
    }

    private int getFirstListIndex() {
        return 1;
    }

    private int getLastListItemPosition() {
        int start  = getItems().size() - 1;
        while(!(getItems().get(start) instanceof ListItem)) {
            --start;
            if(start < 0)
                break;
        }
        return start;
    }

    @Override
    public String getListText(int itemPos) {
        INoteItem item = getItems().get(itemPos);
        if(item instanceof ListItem) {
            return ((ListItem) item).text;
        }
        return null;
    }

    @Override
    public void setListText(int itemPos , String txt) {
        INoteItem item = getItems().get(itemPos);
        if(item instanceof ListItem) {
            ((ListItem) item).text = txt;
        }
    }

    @Override
    public boolean getListTicked(int itemPos) {
        INoteItem item = getItems().get(itemPos);
        if(item instanceof ListItem) {
            return ((ListItem) item).ticked;
        }
        return false;
    }

    @Override
    public void setListTicked(int itemPos, boolean isticked) {
        INoteItem item = getItems().get(itemPos);
        if(item instanceof ListItem) {
            Log.d(TAG, "Element is ticked:" + isticked);
            ((ListItem) item).ticked = isticked;
        }
    }

    @Override
    public void move(int fromPosition, int toPosition) {
        toPosition = Math.max(getLastListItemPosition() ,toPosition);
        toPosition = Math.min(getFirstListIndex() , toPosition);
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Log.d(TAG, i +" -->  "+  (i + 1));
                Collections.swap(getItems(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Log.d(TAG, i +" -->  "+  (i + 1));
                Collections.swap(getItems(), i, i - 1);
            }
        }
    }

    @Override
    public String toString() {
        return "ListNote{} " + super.toString();
    }

    @Override
    public int addElement(INoteItem item) {
        addItem(item);
        int jumps = moveAboveAdder();
        return getItems().size() - jumps;
    }

    private void swapitems(int swappedUp, int swappedDown, int jump ) {
        Collections.swap(getItems(), swappedUp, swappedDown);
    }

    private int moveAboveAdder() {
        int jumps = 0;
        int start = getItems().size() - 1;
        for(int i = start; i > 0; i-- ) {
            if(getItems().get(i-1) instanceof ItemAdder) {
                swapitems(i, i - 1, jumps ++);
                break;
            }
            swapitems(i, i - 1, jumps ++);
        }
        return jumps + 1;
    }

    @Override
    public void removeListElement(int pos) {
        removeItem(pos);
    }

    @Override
    public boolean hasList() {
        return true;
    }

    @Override
    public void loadItems(Context context){
        Cursor cursorlist;
        //Uri noteListUri = NotesContentProvider.LIST_NOTES_CONTENT_URI;
        String selection = NotesTable.TABLE_NOTES + "." + NotesTable.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(getId())};
        String[] projectionl = {
                ListItemTable.TABLE_LISTITEM + "." + ListItemTable.COLUMN_TEXT,
                ListItemTable.COLUMN_IS_CHECKED,
                NotesTable.COLUMN_TITLE,
                ListItemTable.TABLE_LISTITEM +"."+ ListItemTable.COLUMN_ID
        };
        //join query
        cursorlist = context.getContentResolver().query(NotesContentProvider.LIST_NOTES_CONTENT_URI,
                projectionl, selection, selectionArgs, null);
        Log.d(TAG, "cursorlist " + cursorlist);
        while (cursorlist.moveToNext()) {
            String textitem = cursorlist.getString(0);
            short ischecked = cursorlist.getShort(1);
            String noteTitle = cursorlist.getString(2);
            int id = cursorlist.getInt(3);
            Log.d(TAG, "Loading item TextItem: " + textitem + "note id " + id + " Note title " + noteTitle);
            ListItem item = new ListItem();
            item.setText(textitem);
            item.setId(id);
            if(ischecked == 1) {
                item.setTicked(true);
            } else {
                item.setTicked(false);
            }
            addElement(item);
        }
        cursorlist.close();
    }

    @Override
    public void saveDb(Context context, int order) {
        super.saveDb(context, order);
        if(saver != null) {
            saver.storeAsListNote();
        }
        saveReminders(context);
    }

    @Override
    public void deleteFromDb(Context context) {
        Uri toDeleteUri = Uri.parse(NotesContentProvider.NOTES_CONTENT_URI + "/" + getId());
        Uri tmpuri = NotesContentProvider.LIST_CONTENT_URI;
        String selection = ListItemTable.NOTE_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(getId())};
        context.getContentResolver().delete(tmpuri, selection, selectionArgs);
        context.getContentResolver().delete(toDeleteUri, null, null);
    }
}
