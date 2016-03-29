/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

import android.content.Context;

public interface IReminder {
    void setDate(String date);
    String getDate();

    void setTime(String time);
    String getTime();

    void setRepeat(String repeat);
    String getRepeat();

    boolean isSet();
    void set(boolean isSet);

    int getId();
    void setId(int id);

    void saveDb(Context context);
    void loadFromDB(Context context);

    public int getNoteId();
    public void setNoteId(int mNoteId);
}
