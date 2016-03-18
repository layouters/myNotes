/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

/**
 * Created by Anna on 2016-02-24.
 */
public class Header  extends AbstractNoteItem {
    public void setTitle(String title) {
        this.title = title;
    }

    String title;

    String text;

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Header{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean isSimple() {
        return true;
    }
}
