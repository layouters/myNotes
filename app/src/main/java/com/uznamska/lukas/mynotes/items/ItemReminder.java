/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

/**
 * Created by Anna on 2016-03-14.
 */
public class ItemReminder extends AbstractNoteItem {
    @Override
    public String toString() {
        return "ItemReminder{}";
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
