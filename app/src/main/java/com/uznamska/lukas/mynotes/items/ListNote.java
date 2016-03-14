package com.uznamska.lukas.mynotes.items;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Anna on 2016-02-23.
 */
public class ListNote extends AbstractNote implements INoteForList {

    private static final String TAG = "Note:ListNote";

    public ListNote() {
        setItems(new ArrayList<INoteItem>());
        getItems().add(new Header());
        getItems().add(new ListItem());
        getItems().add(new ItemAdder());
        getItems().add(new ItemSeparator());
        getItems().add(new ItemReminder());
    }

    private int getFirstListIndex() {
        return 1;
    }

    private int getLastListIndex() {
        return getSize() - 3;
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
        toPosition = Math.max(getLastListIndex() ,toPosition);
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
        getItems().add(item);
        moveAboveAdder();

        return getItems().size() - 4;
    }
    private void moveAboveAdder() {
        Collections.swap(getItems(), getItems().size() - 1, getItems().size() - 2);
        Collections.swap(getItems(), getItems().size() - 2, getItems().size() - 3);
        Collections.swap(getItems(), getItems().size() - 3, getItems().size() - 4);
    }

    @Override
    public void removeListElement(int pos) {

    }

    @Override
    public boolean hasList() {
        return true;
    }
}
