package com.uznamska.lukas.mynotes.items;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Anna on 2016-02-24.
 */
public class NoteRepository {

    private static final String TAG = "Notes:Repository";
    private List<INote> mDataset;
    static NoteRepository repoInstance = null;
    NoteFactory factory = new NoteFactory();

    public static NoteRepository getInstance() {
        if(repoInstance == null) {
            repoInstance = new NoteRepository();
        }
        return repoInstance;
    }

    private NoteRepository() {
        this.mDataset = new ArrayList<>();
    }

    public void move(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDataset, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDataset, i, i - 1);
            }
        }
    }

    public void initNote(String type) {
        mDataset.add(factory.getNote(type));
    }
    public void populateWithGarbage() {

//        for(int i = 0 ; i < 10; i++) {
//            ListNote note = (ListNote) factory.getNote(NoteFactory.LIST_NOTE);
//            note.setTitle("Garbage " + String.valueOf(i));
//            for(int j = 0 ; j < 10; j++) {
//                note.addElement(new ListItem(String.valueOf(j)));
//            }
//            mDataset.add(note);
//
//            TextNote textNote = (TextNote) factory.getNote(NoteFactory.TEXT_NOTE);
//            textNote.setTitle("Garbage " + String.valueOf(i));
//            mDataset.add(textNote);
 //       }
    }

    public List<INote> getmDataset() {
        return mDataset;
    }

    public void setmDataset(List<INote> mDataset) {
        this.mDataset = mDataset;
    }

    public INote get(int index) {
        return mDataset.get(index);
    }

    public void add(INote note) {
        Log.d(TAG, "Adding note to database " + note.toString());
        if(note.hasList()) {
            for(int i = 0 ; i < note.getSize(); i++) {
                Log.d(TAG, note.getItem(i).toString());
            }
        }
        //mDataset.add(note);
    }
    public INote remove(int index) {
        return mDataset.remove(index);
    }

    public int size() {
        return mDataset.size();
    }
}
