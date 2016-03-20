/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uznamska.lukas.mynotes.contentprovider.NotesContentProvider;
import com.uznamska.lukas.mynotes.contentprovider.NotesContentProviderProxy;
import com.uznamska.lukas.mynotes.database.NotesTable;
import com.uznamska.lukas.mynotes.items.INote;
import com.uznamska.lukas.mynotes.items.INoteItem;
import com.uznamska.lukas.mynotes.items.ListNote;
import com.uznamska.lukas.mynotes.items.NoteFactory;
import com.uznamska.lukas.mynotes.items.TextNote;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Lukasz 2016-02-13.
 */
public class NoteFragment extends Fragment implements MainActivity.BackPressedListener {

    private static final String TAG = "Notes:NoteFragment";
    public static final String NEXT_POSITION = "com.uznamska.lukas.position";
    public static final String FRAGMENT_NAME = "Note Fragment";
    private final int SELECT_PHOTO = 1;
    private ImageView imageView;
    int mNextIndex = -1;
    public static final String NOTE_TYPE = "com.uznamska.lukas.notetype";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private NoteAdapter mAdapter;
    private String mNoteType;
    INote mNote = null;
    Uri noteUri;
    ImageView mImageView;
    NoteFactory factory = new NoteFactory();
    NotesContentProviderProxy proxyContentProvider;
    NoteAdapter.EditorType mEditType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "On create");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        proxyContentProvider = new NotesContentProviderProxy(getContext());
        //factory.setContext(this.getContext());
        mNextIndex = getIndex();
        mNoteType = getType();
        noteUri = getUri();
    }

    private int getIndex() {
        return this.getArguments().getInt(NEXT_POSITION);
    }
    private String getType() {
        return this.getArguments().getString(NOTE_TYPE);
    }

    private Uri getUri() {
        return this.getArguments().getParcelable(NotesContentProvider.CONTENT_ITEM_TYPE);
    }

    public void syncToolbar(Bundle bundle) {
        //Log.d(TAG, NoteRepository.getInstance().get(mNextIndex).toString());
      // ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NoteRepository.getInstance().get(mNextIndex).getTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
      //  ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        Bundle b = getActivity().getIntent().getExtras();

        mEditType = NoteAdapter.EditorType.NEW;
        if(noteUri == null) {
            mNote = factory.getNote(mNoteType);
            mEditType = NoteAdapter.EditorType.NEW;
            Log.d(TAG, "Uri is null");
        } else {
            mNote = proxyContentProvider.getNoteFromUri(noteUri);
            mEditType = NoteAdapter.EditorType.EDIT;
            Log.d(TAG, "Note from uri " + mNote);
        }
        Log.d(TAG, "Note from uri " + mNote);
        syncToolbar(b);
        View rootView = inflater.inflate(R.layout.recycle_view_frag, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NoteAdapter(mNote, this.getContext(),mEditType);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        ItemTouchHelper.Callback callback =
                new CardItemTouchHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "On load menu");
        menu.clear();
        inflater.inflate(R.menu.frag_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "On load prepare menu");
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        saveState();
        super.onPause();
    }

    private void saveState() {
        noteUri = proxyContentProvider.saveNote(mNote, mNextIndex, noteUri);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "On resume");
        super.onResume();
        ActionBar ab =((AppCompatActivity) getActivity()).getSupportActionBar();
//        if(ab != null) {
//            ab.setDisplayHomeAsUpEnabled(true);
//            ab.setDisplayShowHomeEnabled(true);
//        }
        Log.e(TAG, "Action bar can not be obtained - home can not be set !!!");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Item clicked: "  + item.getItemId() );
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_load_image:
                Log.d(TAG, "Action Load Image");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent,SELECT_PHOTO);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                // if(resultCode == RESULT_OK){
                try {
                    final Uri imageUri = imageReturnedIntent.getData();
                    final InputStream imageStream = ((AppCompatActivity) getActivity()).
                            getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // }
        }
    }


    @Override
    public void OnBackPressured() {

    }
}
