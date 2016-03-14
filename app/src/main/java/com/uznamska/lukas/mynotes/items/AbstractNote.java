package com.uznamska.lukas.mynotes.items;

import java.util.List;

/**
 * Created by Anna on 2016-02-24.
 */
public abstract class AbstractNote  implements INote {
    private static final String TAG = "Note:AbstractNote";
    private static int HEADER_POSITION = 0;

    private int id;
    private int order;

    protected List<INoteItem> items;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "AbstractNote{" +
                "id=" + id +
                ", order=" + order +
                ", items=" + items +
                '}';
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getListOrder(){
        return order;
    }

    @Override
    public void setListOrder(int order){
        this.order = order;
    }


    protected List<INoteItem> getItems() {
        return items;
    }
    protected void setItems(List<INoteItem> it) {
        items = it;
    }

    @Override
    public INoteItem getItem(int pos) {
        if(items.size() > pos) {
            return items.get(pos);
        }
        return null;
    }

    @Override
    public void setTitle(String title) {
        ((Header) items.get(HEADER_POSITION)).setTitle(title);

    }

    @Override
    public String getTitle() {
        return ((Header) items.get(HEADER_POSITION)).getTitle();
    }

    @Override
    public void setText(String text) {
        ((Header) items.get(HEADER_POSITION)).setText(text);
    }
    @Override
    public String  getText() {
        return ((Header) items.get(HEADER_POSITION)).getText();
    }

    @Override
    public int getSize() {
        return getItems().size();
    }
}
