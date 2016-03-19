/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

import java.util.List;

/**
 * Created by Anna on 2016-02-24.
 */
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
            return getSize();
        }
    }

    class NoteReminder implements  IReminder {
        String date;
        String repeat;
        boolean mIsSet = false;

        @Override
        public String getDate() {
            return date;
        }

        @Override
        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public String getRepeat() {
            return repeat;
        }

        @Override
        public boolean isSet() {
            return mIsSet;
        }

        @Override
        public void set(boolean isSet) {
            mIsSet = isSet;
        }

        @Override
        public void setRepeat(String repeat) {
            this.repeat = repeat;
        }
    }

    private static final String TAG = "Note:AbstractNote";
    private static int HEADER_POSITION = 0;

    private int id;
    private int order;
    private int simpleItems;
    private IReminder mReminder;

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
    public IReminder getReminder() {
        if(mReminder == null) {
            mReminder = new NoteReminder();
        }
        return mReminder;
    }

    @Override
    public void setReminder(IReminder reminder) {
        mReminder = reminder;
    }
}
