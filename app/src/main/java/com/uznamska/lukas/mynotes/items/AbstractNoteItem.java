/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

/**
 * Created by Anna on 2016-03-18.
 */
public abstract class AbstractNoteItem implements INoteItem{

    private int mId;

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public void setId(int id) {
        mId = id;
    }
}
