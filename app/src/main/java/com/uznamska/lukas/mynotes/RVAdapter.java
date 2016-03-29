/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.uznamska.lukas.mynotes.contentprovider.NotesContentProvider;
import com.uznamska.lukas.mynotes.contentprovider.NotesContentProviderProxy;
import com.uznamska.lukas.mynotes.items.INote;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CardListingViewHolder> implements
        View.OnLongClickListener, View.OnClickListener, CardItemTouchHelper.ItemTouchHelperAdapter {

    private static final String TAG ="MyNote:RvAdapter" ;
    private final List<INote> mNote;
    private int lastPosition = -1;
    public static final int LAST_POSITION = -1;
    private Context mContext;
    LinearLayoutManager mLinearLayoutManager;
    private NoteAdapter mAdapter;
    PosToIdConventer converter = new PosToIdConventer();
    NotesContentProviderProxy proxyContentProvider;

    class PosToIdConventer {
        int convert(int index) {
            return mNote.get(index).getId();
        }
    }
    /**
     * Represents a listener that will be notified of headline selections.
     */
    public interface OnCardClickListener {
        public void onItemDeleted();
        public void onItemMoved();
    }

    public void setCardClickListener(OnCardClickListener mCardClickListener) {
        this.mCardClickListener = mCardClickListener;
    }

    OnCardClickListener mCardClickListener = null;

    RVAdapter(List<INote> notes, Context context){
        mContext = context;
        proxyContentProvider = new NotesContentProviderProxy(mContext);
        mNote = notes;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "moving from " + fromPosition + " to " + toPosition);
        //NoteRepository.getInstance().move(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        int newId = converter.convert(position);
        Log.d(TAG, "delete " + position + " (" + newId + ")");

       // Uri toDeleteUri = Uri.parse(NotesContentProvider.NOTES_CONTENT_URI + "/" + newId);
       // proxyContentProvider.deleteNote(toDeleteUri);
        mNote.get(position).deleteFromDb(mContext);
        mNote.remove(position);
        if(mCardClickListener != null) {
            mCardClickListener.onItemDeleted();
        }
        notifyItemRemoved(position);
    }

    //TODO: fix add and remove methods
    public void add(String s,int position) {
        position = position == LAST_POSITION ? getItemCount()  : position;
        //NoteRepository.getInstance().add(new Person("Sylwerek", "29", R.drawable.emma));
        //notifyItemInserted(position);
    }

    public void remove(int position){
//        if (position == LAST_POSITION && getItemCount()>0)
//            position = getItemCount() -1 ;
//
//        if (position > LAST_POSITION && position < getItemCount()) {
//            PersonRepo.getInstance().remove(position);
//            notifyItemRemoved(position);
//        }
    }

    @Override
    public boolean onLongClick(View view) {
        CardListingViewHolder holder = (CardListingViewHolder) view.getTag();
        if (view.getId() == holder.itemView.getId()) {

        }
        return false;
    }

    @Override
    public void onClick(View view) {

//        CardListingViewHolder holder = (CardListingViewHolder) view.getTag();
//        Log.d(TAG, "Item " + holder.getLayoutPosition() + "has been clicked");
//       // if (view.getId() == holder.itemView.getId()) {
//            RVAdapter.this.mCardClickListener.onCardClicked(holder.getLayoutPosition());
//            Log.d(TAG, "Item " + holder.getLayoutPosition() + "has been clicked");
//       // }
    }

    @Override
    public CardListingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_item, viewGroup, false);
        // Set the view to the ViewHolder
        CardListingViewHolder pvh = new CardListingViewHolder(v);
     //   pvh.itemView.setOnClickListener(RVAdapter.this);
       // pvh.itemView.setOnLongClickListener(RVAdapter.this);
      //  pvh.itemView.setTag(pvh);
        return pvh;
    }

    @Override
     public void onBindViewHolder(CardListingViewHolder cardListingViewHolder, int i) {
        Log.d(TAG, "Element " + i + " set.");
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        cardListingViewHolder.mRecycleView.setLayoutManager(mLinearLayoutManager);
        //adapterList.addAll(listItem);
        mAdapter = new NoteAdapter(mNote.get(i), mContext,
                NoteAdapter.DisplayMode.SIMPLIFIED_MODE, NoteAdapter.EditorType.NEW);
        cardListingViewHolder.mRecycleView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

   //TODO: configure some serious animation
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mNote.size();
    }

    public class CardListingViewHolder extends RecyclerView.ViewHolder implements IMovementInformator{
        //private final LinearLayoutManager mLayoutManager;
        RecyclerView mRecycleView;

        public CardListingViewHolder(View itemView) {
            super(itemView);
            mRecycleView = (RecyclerView) itemView.findViewById(R.id.item_mode);
        }


        @Override
        public boolean canBeDragged() {
            return true;
        }

        @Override
        public boolean canBeSwiped() {
            return true;
        }
    }

}
