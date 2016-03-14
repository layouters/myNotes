package com.uznamska.lukas.mynotes.items;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.uznamska.lukas.mynotes.contentprovider.NotesContentProviderProxy;
import com.uznamska.lukas.mynotes.database.NotesTable;

/**
 * Created by Lukasz on 2016-02-23.
 *
 */
public class NoteFactory {
    private static final String TAG = "MyNote:NoteFactory";
    public static String TEXT_NOTE = "Text Note";
    public static String LIST_NOTE = "List Note";

    //private Context mContext;

    //private NotesContentProviderProxy providerProxy;
    //public Context getContext() {
    //    return mContext;
    //}

    //public void setContext(Context mContext) {
    //    this.mContext = mContext;
    //}

    public INote getNote (String noteType) {
        if(noteType.equals(TEXT_NOTE)) {
            Log.d(TAG, "Creating text note");
            return new TextNote();
        } else if(noteType.equals(LIST_NOTE)) {
            Log.d(TAG, "Creating list note");
            return new ListNote();
        } else {
            if(noteType.toLowerCase().contains("text")) {
                return new TextNote();
            } else if(noteType.toLowerCase().contains("list")) {
                return new ListNote();
            } else {
                Log.d(TAG, "Unknown TYPE!!");
            }
        }
        return null;
    }

    public INote getNote(String noteType, Uri uri) {
        if (uri == null) {
            Log.d(TAG, "Uri is null return plain object!");
            return getNote(noteType);
        }

        return null;
    }
}
