/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

public interface IReminder {
    void setDate(String date);
    String getDate();
    void setRepeat(String repeat);
    String getRepeat();
    boolean isSet();
    void set(boolean isSet);
    int getId();
    void setId(int id);
}
