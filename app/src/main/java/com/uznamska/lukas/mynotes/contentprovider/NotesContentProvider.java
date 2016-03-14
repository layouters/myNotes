package com.uznamska.lukas.mynotes.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.uznamska.lukas.mynotes.database.ListItemTable;
import com.uznamska.lukas.mynotes.database.NotesDatabaseHelper;
import com.uznamska.lukas.mynotes.database.NotesTable;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Anna on 2016-03-06.
 */
public class NotesContentProvider extends ContentProvider {
    private static final String TAG = "Note:Content";
    // database
    private NotesDatabaseHelper database;

    // used for the UriMacher
    //all the notes
    private static final int NOTES = 10;
    //just one note
    private static final int NOTES_ID = 20;
    private static final int ITEMS = 30;
    private static final int ITEMS_ID = 40;
    private static final int NOTE_ITEMS = 50;
    private static final int NOTE_ITEMS_ID = 60;

    private static final String AUTHORITY = "com.uznamska.lukas.mynotes.contentprovider";
    private static final String NOTES_BASE_PATH = NotesTable.TABLE_NOTES;
    private static final String ITEM_BASE_PATH = ListItemTable.TABLE_LISTITEM;
    public static final Uri NOTES_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + NOTES_BASE_PATH);
    public static final Uri LIST_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + ITEM_BASE_PATH);
    public static final Uri LIST_NOTES_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + "listnote");
    //noteUri = Uri.parse(NotesContentProvider.NOTES_CONTENT_URI + "/" + index);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/notes";

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/note";
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, NOTES_BASE_PATH, NOTES);
        sURIMatcher.addURI(AUTHORITY, NOTES_BASE_PATH + "/#", NOTES_ID);
        sURIMatcher.addURI(AUTHORITY, ITEM_BASE_PATH, ITEMS);
        sURIMatcher.addURI(AUTHORITY, ITEM_BASE_PATH + "/#", ITEMS_ID);
        sURIMatcher.addURI(AUTHORITY, "listnote", NOTE_ITEMS);
        sURIMatcher.addURI(AUTHORITY, "listnote" + "/#", NOTE_ITEMS_ID);
    }


    @Override
    public boolean onCreate() {
        Log.d(TAG, "Content provider oncreate");
        database = new NotesDatabaseHelper(getContext());
        database.getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // check if the caller has requested a column which does not exists
        //checkColumns(projection);
        // Set the table

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case NOTES:
                queryBuilder.setTables(NotesTable.TABLE_NOTES);
                //return all notes
                Log.d(TAG, "notes " + queryBuilder);
                break;
            case NOTES_ID:
                queryBuilder.setTables(NotesTable.TABLE_NOTES);
                // adding the ID to the original query
                // so that you can return one specific note info
                queryBuilder.appendWhere(NotesTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                Log.d(TAG, "notes_id " + uri.toString());
                break;
            case NOTE_ITEMS:
                queryBuilder.setTables(ListItemTable.TABLE_LISTITEM//empl
                        + " INNER JOIN "
                        +  NotesTable.TABLE_NOTES //dep table
                        + " ON "
                        + ListItemTable.NOTE_ID
                        + " = "
                        + (NotesTable.TABLE_NOTES + "." + NotesTable.COLUMN_ID));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        Uri _uri = null;
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case NOTES:
                id = sqlDB.insert(NotesTable.TABLE_NOTES, null, values);
                Log.d(TAG, "Insert data  id " + id);
                if (id > 0) {
                    _uri = ContentUris.withAppendedId(NOTES_CONTENT_URI, id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            case ITEMS:
                id = sqlDB.insert(ListItemTable.TABLE_LISTITEM, null, values);
                if (id > 0) {
                    _uri = ContentUris.withAppendedId(LIST_CONTENT_URI, id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return _uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case NOTES:
                rowsDeleted = sqlDB.delete(NotesTable.TABLE_NOTES, selection,
                        selectionArgs);
                break;
            case NOTES_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "this is note_id " + id + "to delete");
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(NotesTable.TABLE_NOTES,
                            NotesTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(NotesTable.TABLE_NOTES,
                            NotesTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case ITEMS:
                break;
            case ITEMS_ID:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case NOTES:
                rowsUpdated = sqlDB.update(NotesTable.TABLE_NOTES,
                        values,
                        selection,
                        selectionArgs);
                break;
            case NOTES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(NotesTable.TABLE_NOTES,
                            values,
                            NotesTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(NotesTable.TABLE_NOTES,
                            values,
                            NotesTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { NotesTable.COLUMN_CATEGORY,
                NotesTable.COLUMN_TITLE, NotesTable.COLUMN_TEXT,
                NotesTable.COLUMN_ID,NotesTable.COLUMN_TYPE,NotesTable.COLUMN_LIST_ORDER };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
