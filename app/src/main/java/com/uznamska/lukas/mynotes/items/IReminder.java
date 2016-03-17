/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

/**
 * Created by Anna on 2016-03-17.
 */
public interface IReminder {
    void setDate(String date);
    String getDate();
    void setRepeat(String repeat);
    String getRepeat();
    boolean isSet();
    void set(boolean isSet);
}
