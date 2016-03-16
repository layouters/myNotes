/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

/**
 * Created by Anna on 2016-03-16.
 */
public interface Iterator {
    boolean hasNext();
    INoteItem next();
    int getItemsNumber();
}
