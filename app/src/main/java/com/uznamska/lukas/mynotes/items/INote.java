/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

/**
 * Created by Anna on 2016-02-24.
 */
public interface INote {
    INoteItem getItem(int pos);
    void setTitle(String title);
    String  getTitle();

    void setText(String text);
    String  getText();

    int getSize();
    boolean hasList();

    int getId();
    void setId(int id);

    int getListOrder();
    void setListOrder(int order);

    void move(int fromPosition, int toPosition);
    public int addElement(INoteItem item);

    Iterator getSimpleItemsIterator();
    Iterator getItemsIterator();

    String getDateReminder();
    void setDateReminder(String reminder);
}
