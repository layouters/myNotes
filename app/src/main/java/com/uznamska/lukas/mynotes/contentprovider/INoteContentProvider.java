/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.contentprovider;

import android.net.Uri;

import com.uznamska.lukas.mynotes.items.INote;

/**
 * Created by Anna on 2016-03-10.
 */
public interface INoteContentProvider {
    INote getNoteFromUri(Uri uri);
    void deleteItem(Uri uri);

    Uri saveNote(INote note , int listOder, Uri uri);
    void deleteNote(Uri uri);
}
