/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

/**
 * Created by Anna on 2016-02-24.
 */
public class ItemAdder implements INoteItem {
    @Override
    public String toString() {
        return "ItemAdder{}";
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
