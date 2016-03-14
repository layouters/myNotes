/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.contentprovider;

import android.content.Context;

import com.uznamska.lukas.mynotes.items.INote;
import com.uznamska.lukas.mynotes.items.INoteItem;

import java.util.ArrayList;

/**
 * Created by Anna on 2016-03-12.
 */
public class ListContentProviderHandler  {


    private static final String TAG = "Notes:ListContentProviderHandler";
    Context mContext;

    public ListContentProviderHandler(Context context) {
        mContext = context;
    }

    //get me  all the items that belongs to this note  >> INote

    //get me Note with all his item
//    INote getNoteWithItem() {
//
//    }

}
