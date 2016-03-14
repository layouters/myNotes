package com.uznamska.lukas.mynotes;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Created by Lukasz on 2016-02-19.
 */

public class CardItemTouchHelper extends ItemTouchHelper.Callback {

    public interface ItemTouchHelperAdapter {

        void onItemMove(int fromPosition, int toPosition);

        void onItemDismiss(int position);
    }

    private final ItemTouchHelperAdapter mAdapter;
    private final static String TAG = "Touch Helper";

    public CardItemTouchHelper(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        int swipeFlags = 0;
        IMovementInformator info =(IMovementInformator) viewHolder;

        if(info.canBeDragged()) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        if(info.canBeSwiped()) {
            swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
       // if(!(viewHolder instanceof NoteAdapter.HeaderViewHolder)) {
            Log.d(TAG, "not note instance!!!!");
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
       // } else {
       //     Log.d(TAG, "This is note instance!!!!");
       //     return false;
      //  }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

}

