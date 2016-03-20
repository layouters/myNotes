/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;


import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.uznamska.lukas.mynotes.database.NotesTable;

import java.util.ArrayList;

public class TextNote extends AbstractNote implements INote {
    private static final String TAG = "TextNote";
    private static int HEADER_POSITION = 0;

    public TextNote() {
        setItems(new ArrayList<INoteItem>());
        addItem(new Header());
        addItem(new ItemSeparator());
        addItem(new ItemReminder());
    }

    @Override
    public boolean hasList() {
        return false;
    }

    @Override
    public void move(int fromPosition, int toPosition) {
        Log.d(TAG, "There is nothing I can do");
    }

    @Override
    public int addElement(INoteItem item) {
        Log.d(TAG, "Nothing can be added");
        return 0;
    }

    @Override
    public Iterator getListItemIterator() {
        return null;
    }
}
