/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

/**
 * Created by Anna on 2016-02-24.
 */
public class ListItem extends AbstractNoteItem {
    String  text;
    boolean ticked;
    IUpdateTextListener listener;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isTicked() {
        return ticked;
    }

    public void setTicked(boolean ticked) {
        this.ticked = ticked;
    }

    public ListItem() {
      //  text = "empty text";

    }

    public ListItem(String txt) {
        text = txt;
    }

    @Override
    public String toString() {
        return "ListItem{" +
                "ID= " + getId()+
                "text='" + text + '\'' +
                ", ticked=" + ticked +
                '}';
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    public void setUpdateTextListener(IUpdateTextListener list) {
        listener = list;
    }

    public void onUpdateText() {
        if(listener != null) {
            listener.onUpdate();
            listener = null;
        }
    }
}
