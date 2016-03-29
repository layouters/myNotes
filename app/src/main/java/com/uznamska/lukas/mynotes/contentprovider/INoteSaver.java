package com.uznamska.lukas.mynotes.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.uznamska.lukas.mynotes.items.INote;

public interface INoteSaver {
    Uri getUri();
    void setUri(Uri mUri);

    ContentValues getValues();
    void setValues(ContentValues values);

    Context getContext();
    void setContext(Context mContext);

    INote getNote();
    void setNote(INote mNote);

    void storeAsTextNote();
    void storeAsListNote();
}
