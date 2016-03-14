package com.uznamska.lukas.mynotes.items;

/**
 * Created by Anna on 2016-02-25.
 */
public interface INoteForList extends INote {
    String getListText(int itemPos);
    void setListText(int itemPos, String txt);
    boolean getListTicked(int itemPos);
    void setListTicked(int itemPos, boolean isticked);
    void removeListElement(int pos);
}
