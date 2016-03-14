package com.uznamska.lukas.mynotes;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.uznamska.lukas.mynotes.contentprovider.NotesContentProvider;
import com.uznamska.lukas.mynotes.contentprovider.NotesContentProviderProxy;
import com.uznamska.lukas.mynotes.database.NotesTable;
import com.uznamska.lukas.mynotes.items.INote;
import com.uznamska.lukas.mynotes.items.NoteFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukasz on 2016-02-12.
 */
public class RecyclerViewFragment extends Fragment implements RVAdapter.OnCardClickListener {
    private static final String TAG = "Notes:ListFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private List<INote> mNotesList;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mCurrentLayoutManagerType;
    private NoteFactory factory = new NoteFactory();

    private RVAdapter mAdapter;
    // The listener we are to notify when a card is selected
    OnCardsSelectedListener mCardsSelectedListener = null;
    OnListSyncedListener mListSyncListener = null;
    private NotesContentProviderProxy proxyContentProvider;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    public interface OnCardsSelectedListener {
        public void onCardSelected(int index);
    }
    public interface OnListSyncedListener {
        public void onListSynced(int size);
    }


    public void onCardClicked(int index) {
        mCardsSelectedListener.onCardSelected(index);
    }

    public RVAdapter getAdapter() {
        return mAdapter;
    }

    public RecyclerViewFragment() {
    }

    @Override
    public void onItemDeleted() {
        mNotesList = loadListOfNotes();
    }

    @Override
    public void onItemMoved() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On create");
        proxyContentProvider = new NotesContentProviderProxy(getContext());
        mNotesList = loadListOfNotes();
        setHasOptionsMenu(true);
    }

    private List<INote> loadListOfNotes() {
        List<INote> list = new ArrayList<INote>();
        //String[] projection = {NotesTable.COLUMN_TYPE, NotesTable.COLUMN_TITLE, NotesTable.COLUMN_TEXT,
//                                NotesTable.COLUMN_ID, NotesTable.COLUMN_LIST_ORDER};
//        Cursor tmp_cursor = getContext().getContentResolver().query(NotesContentProvider.NOTES_CONTENT_URI, projection, null,
//                null, NotesTable.COLUMN_LIST_ORDER +  " ASC");
//        if(tmp_cursor != null) {
//            //tmp_cursor.moveToFirst();
//
//            while (tmp_cursor.moveToNext()) {
//                String titler = tmp_cursor.getString(tmp_cursor.getColumnIndexOrThrow
//                        (NotesTable.COLUMN_TITLE));
//                String text = tmp_cursor.getString(tmp_cursor.getColumnIndexOrThrow
//                        (NotesTable.COLUMN_TEXT));
//                String type = tmp_cursor.getString(tmp_cursor.getColumnIndexOrThrow
//                        (NotesTable.COLUMN_TYPE));
//                int id = tmp_cursor.getInt(tmp_cursor.getColumnIndexOrThrow
//                        (NotesTable.COLUMN_ID));
//                int listOrder = tmp_cursor.getInt(tmp_cursor.getColumnIndexOrThrow
//                        (NotesTable.COLUMN_LIST_ORDER));
//                INote note  = factory.getNote(type);
//                Log.d(TAG,"type: " + type);
//                note.setTitle(titler);
//                note.setText(text);
//                note.setId(id);
//                note.setListOrder(listOrder);
//                list.add(note);
//            }
//        }
//
        list = proxyContentProvider.loadListOfNotes();
        int next = 0;
        if(list.size() > 0) {
            next = list.get(list.size() - 1).getListOrder() + 1;
        }
        mListSyncListener.onListSynced(next);
        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        //refresh list
        mNotesList = loadListOfNotes();
        View rootView = inflater.inflate(R.layout.recycle_view_frag, container, false);
        rootView.setTag(TAG);


        //get RecycleView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        int id = mNotesList.get(position).getId();
                        onCardClicked(id);
                    }
                })
        );
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        //create adapter
        //add adapter to recycview
        mAdapter = new RVAdapter(mNotesList, this.getContext());
        mAdapter.setCardClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback =
                new CardItemTouchHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
        return rootView;
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCardsSelectedListener = (OnCardsSelectedListener) activity;
            mListSyncListener = (OnListSyncedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == R.id.action_change) {
            Log.d(TAG, "Action changed!");
            if(mCurrentLayoutManagerType.equals(LayoutManagerType.LINEAR_LAYOUT_MANAGER)) {
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
            } else {
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
            }
            setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initDataset() {
       //NoteRepository.getInstance().
    }

}
