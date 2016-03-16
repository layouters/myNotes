/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.uznamska.lukas.mynotes.items.Header;
import com.uznamska.lukas.mynotes.items.IBindActionTaker;
import com.uznamska.lukas.mynotes.items.INote;
import com.uznamska.lukas.mynotes.items.ItemAdder;
import com.uznamska.lukas.mynotes.items.ItemReminder;
import com.uznamska.lukas.mynotes.items.ItemSeparator;
import com.uznamska.lukas.mynotes.items.ListItem;
import com.uznamska.lukas.mynotes.items.ListNote;

/**
 * Created by Lukasz on 2016-02-21.
 */
public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements CardItemTouchHelper.ItemTouchHelperAdapter,View.OnClickListener {
    private static final String TAG = "Note:NoteAdapter";
    private final Context mContext;
    private final int HEADER_ELEMENT_TYPE = 0;
    private final int LIST_ELEMENT_TYPE = 1;
    private final int ADDER_ELEMENT_TYPE = 2;
    private final int ITEM_REMINDER = 3;
    private final int ITEM_SEPARATOR = 4;
    HolderAbstractFactory factoryHolder;
    int mOverhead = 3;

    PresentationMode mDisplayMode;
    INote mNote;

    enum DisplayMode {
        RICH_MODE,
        SIMPLIFIED_MODE
    }

    abstract class PresentationMode {

        public abstract  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        public  void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if(holder != null) {
                ((IBindActionTaker) holder).onBindViewHolder(position);
            }
        }

        public int getItemViewType(int position){

            Log.d(TAG, "ItemViewType: " + position);
            INote note = mNote;
            if(note.getItem(position) instanceof Header){
                Log.d(TAG, "ItemViewType instance of header");
                return HEADER_ELEMENT_TYPE;
            } else if(note.getItem(position) instanceof ItemAdder){
                Log.d(TAG, "ItemViewType instance of adder");
                return ADDER_ELEMENT_TYPE;
            } else if(note.getItem(position) instanceof ListItem) {
                Log.d(TAG, "ItemViewType instance of listiten");
                return LIST_ELEMENT_TYPE;
            } else if(note.getItem(position) instanceof ItemSeparator) {
                Log.d(TAG, "ItemViewType instance of separatoe");
                return ITEM_SEPARATOR;
            } else if(note.getItem(position) instanceof ItemReminder) {
                Log.d(TAG, "ItemViewType instance of remninder");
                return ITEM_REMINDER;
            }
            return -1;
        }

        public abstract int getItemCount();

        public abstract void onItemMove(int fromPosition, int toPosition);

        public abstract void onItemDismiss(int position);

        public abstract void onClick(View v);
    }

    class RichMode extends PresentationMode {

        public RichMode() {
            factoryHolder = new RichHolderFactory();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "this is size in  rich mode" + mNote.getSize());
            Log.d(TAG, "this is note in  rich mode" + mNote);

            return mNote.getItemsIterator().getItemsNumber();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            Log.d(TAG, "moving from " + fromPosition + " to " + toPosition);
            //INote note = mNote;
            mNote.move(fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {

        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "clicked clicked");
            Object holder = v.getTag();
            if(holder instanceof AdderItemViewHolder) {
                AdderItemViewHolder tmpHolder = (AdderItemViewHolder)holder;
                Log.d(TAG, "Adder list has been clicked | view id:  " + tmpHolder.itemView.getId());
                int pos = mNote.addElement(new ListItem());
                Log.d(TAG, "Position inserted " + pos);
                notifyItemInserted(pos);
            }
        }
    }

    class SimplifiedMode extends PresentationMode {
        public SimplifiedMode() {
            factoryHolder = new SimplifiedHolderFactory();
        }

        public int getItemViewType(int position){
           if( mNote.hasList() && mNote.getTitle()== null && mNote.getText().isEmpty()) {
               return LIST_ELEMENT_TYPE;
           }
//            else if(mOverhead == 2) {
//                position +=1;
//            }

            Log.d(TAG, "SimplifiedMode: " + position + " text " + mNote.getTitle());
            INote note = mNote;
            if(note.getItem(position) instanceof Header){
                Log.d(TAG, "ItemViewType instance of header");
                return HEADER_ELEMENT_TYPE;
            } else if(note.getItem(position) instanceof ItemAdder){
                Log.d(TAG, "ItemViewType instance of adder");
                return ADDER_ELEMENT_TYPE;
            } else if(note.getItem(position) instanceof ListItem) {
                Log.d(TAG, "ItemViewType instance of listiten");
                return LIST_ELEMENT_TYPE;
            } else if(note.getItem(position) instanceof ItemSeparator) {
                Log.d(TAG, "ItemSeparator");
                return ITEM_SEPARATOR;
            } else if(note.getItem(position) instanceof ItemReminder) {
                Log.d(TAG, "ItemReminder instance of ItemReminder");
                return ITEM_REMINDER;
            }
            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public int getItemCount() {
            return mNote.getSimpleItemsIterator().getItemsNumber();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            Log.d(TAG, "Inside cards list is not interactive");
        }

        @Override
        public void onItemDismiss(int position) {

        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Nothing is clickable in Simplified mode");
            Log.d(TAG, "clicked clicked");
            Object holder = v.getTag();
            if(holder instanceof SimplifiedHeaderViewHolder) {
                SimplifiedHeaderViewHolder tmpHolder = (SimplifiedHeaderViewHolder)holder;
                Log.d(TAG, "SimplifiedHeaderViewHolder list has been clicked | view id:  " +
                        tmpHolder.getLayoutPosition());
            }

        }
    }

    abstract class HolderAbstractFactory {

        RecyclerView.ViewHolder  getHolderOfType(ViewGroup parent, int type){
            if (type == HEADER_ELEMENT_TYPE) {
                return createHeaderHolder(parent, type);
            } else if(type == LIST_ELEMENT_TYPE) {
                return createListElementHolder(parent, type);
            } else if (type == ADDER_ELEMENT_TYPE) {
                return createAdderHolder(parent, type);
            } else if(type == ITEM_REMINDER) {
                Log.d(TAG, "Create item reminder");
                return createReminderHolder(parent, type);
            } else if(type == ITEM_SEPARATOR) {
                Log.d(TAG, "Create item separator");
                return createSeparatorHolder(parent,type);
            }
            return null;
        }

        abstract RecyclerView.ViewHolder createHeaderHolder(ViewGroup parent, int type);
        abstract RecyclerView.ViewHolder createListElementHolder(ViewGroup parent, int type);
        abstract RecyclerView.ViewHolder createAdderHolder(ViewGroup parent, int type);
        abstract RecyclerView.ViewHolder createSeparatorHolder(ViewGroup parent, int type);
        abstract RecyclerView.ViewHolder createReminderHolder(ViewGroup parent, int type);
    }

    class SimplifiedHolderFactory extends HolderAbstractFactory {

        @Override
        RecyclerView.ViewHolder createHeaderHolder(ViewGroup parent, int type) {
            View v = null;
            Log.d(TAG, "This is Header element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_note_header, parent, false);
            return  new SimplifiedHeaderViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createListElementHolder(ViewGroup parent, int type) {
            View v = null;
            Log.d(TAG, "This is List element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simplified_list_view_item, parent, false);
            return new SimplifiedListItemViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createAdderHolder(ViewGroup parent, int type) {
            Log.e(TAG, "No Adder holder for Simplified view");
            return null;
        }

        @Override
        RecyclerView.ViewHolder createSeparatorHolder(ViewGroup parent, int type) {
            View v = null;
            Log.d(TAG, "This is separator type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.separator_view_item, parent, false);
            return new SeparatorViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createReminderHolder(ViewGroup parent, int type) {
            return null;
        }

    }
    class RichHolderFactory extends HolderAbstractFactory {

        @Override
        RecyclerView.ViewHolder createHeaderHolder(ViewGroup parent, int type) {
            View v = null;
            Log.d(TAG,"This is Header element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_view_item, parent, false);
            return  new HeaderViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createListElementHolder(ViewGroup parent, int type) {
            View v = null;
            Log.d(TAG, "This is List element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_list_view_item, parent, false);
            return new ListItemViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createAdderHolder(ViewGroup parent, int type) {
            View v = null;
            //RecyclerView.ViewHolder viewHolder = null;
            Log.d(TAG,"This is Adder element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adder_view_item, parent, false);
            return new AdderItemViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createSeparatorHolder(ViewGroup parent, int type) {
            View v = null;
            Log.d(TAG, "This is Separator element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.separator_view_item, parent, false);
            return new SeparatorViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createReminderHolder(ViewGroup parent, int type) {
            View v = null;
            Log.d(TAG, "This is Reminder element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_view_item, parent, false);
            return new ReminderViewHolder(v);
        }

    }

   //Class NoteAdapter starts here
    NoteAdapter(INote note, Context context) {
        mContext = context;
        mNote = note;
        setDisplayMode(new RichMode());
    }

    NoteAdapter(INote note, Context context, DisplayMode mode) {
        mContext = context;
        mNote = note;
        if(mode == DisplayMode.RICH_MODE) {
            setDisplayMode(new RichMode());
        } else if (mode == DisplayMode.SIMPLIFIED_MODE) {
            setDisplayMode(new SimplifiedMode());
        } else {
            Log.e(TAG, "Unsupported Display mode");
        }
    }

    public PresentationMode getDisplayMode() {
        return mDisplayMode;
    }

    public void setDisplayMode(PresentationMode mDisplayMode) {
        this.mDisplayMode = mDisplayMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IBindActionTaker viewHolder = (IBindActionTaker)factoryHolder.getHolderOfType(parent, viewType);
        if(viewHolder != null) {
            viewHolder.setViewClickListener(this);
            return (RecyclerView.ViewHolder) viewHolder;
        }
        Log.e(TAG, "Holder is null, thats an error viecType: " + viewType );
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        getDisplayMode().onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
       return getDisplayMode().getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return getDisplayMode().getItemCount();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
       getDisplayMode().onItemMove(fromPosition,toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        Log.d(TAG, "delete "+ position);
        //mNoteBuilder.removeElement(position);
    }

    @Override
    public void onClick(View view) {
        getDisplayMode().onClick(view);
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder implements IMovementInformator,
            IBindActionTaker {


        public ReminderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindViewHolder(int position) {

        }

        @Override
        public void setViewClickListener(View.OnClickListener listener) {

        }

        @Override
        public boolean canBeDragged() {
            return false;
        }

        @Override
        public boolean canBeSwiped() {
            return false;
        }
    }

    public class SeparatorViewHolder extends RecyclerView.ViewHolder implements IMovementInformator,
                                                                                IBindActionTaker {


        public SeparatorViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindViewHolder(int position) {

        }

        @Override
        public void setViewClickListener(View.OnClickListener listener) {

        }

        @Override
        public boolean canBeDragged() {
            return false;
        }

        @Override
        public boolean canBeSwiped() {
            return false;
        }
    }

    public abstract class AbstractHeaderHolder extends RecyclerView.ViewHolder implements IMovementInformator,
            IBindActionTaker {
        TextView title;
        TextView text;
        ImageView photo;

        public AbstractHeaderHolder(View itemView) {
            super(itemView);
        }

        @Override
        public boolean canBeDragged() {
            return false;
        }

        @Override
        public boolean canBeSwiped() {
            return false;
        }

        @Override
        public void setViewClickListener(View.OnClickListener listener) {
           // itemView.setOnClickListener(listener);
           // itemView.setTag(this);

        }
    }

    public class HeaderViewHolder extends AbstractHeaderHolder {

        HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.edit_title);
            text = (TextView)itemView.findViewById(R.id.edit_msg_text);
            photo = (ImageView)itemView.findViewById(R.id.p_photo);
        }

        @Override
        public void onBindViewHolder(final int position) {
            this.title.setText(mNote.getTitle());
            this.title.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mNote.setTitle(s.toString());
                }
            });
            this.text.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mNote.setText(s.toString());
                }
            });
            this.text.setText(mNote.getText());
        }

    }

    public class SimplifiedHeaderViewHolder extends AbstractHeaderHolder {

        SimplifiedHeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.simple_header_title_view);
            text = (TextView)itemView.findViewById(R.id.simple_header_text_view);
            //photo = (ImageView)itemView.findViewById(R.id.simple_header_image_view);
        }

        @Override
        public void onBindViewHolder(final int position) {
            if(title != null)
                this.title.setText(mNote.getTitle());
            if(text != null)
                this.text.setText(mNote.getText());
        }

    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder implements IMovementInformator,
            IBindActionTaker {
        TextView textItem;
        CheckBox ticked;
        public ListItemViewHolder(View itemView) {
            super(itemView);
            textItem = (TextView) itemView.findViewById(R.id.edit_msg_text);
            ticked = (CheckBox)itemView.findViewById(R.id.edit_check);
        }

        @Override
        public boolean canBeDragged() {
            return true;
        }

        @Override
        public boolean canBeSwiped() {
            return true;
        }

        @Override
        public void onBindViewHolder(final int position) {
            this.textItem.setText(((ListNote) mNote).getListText(position));
            this.textItem.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ((ListNote) mNote).setListText(position, s.toString());
                    Log.d(TAG, "Saving " + s.toString() + " on position " + position);
                }
            });
            this.ticked.setChecked(((ListNote) mNote).getListTicked(position));
            this.ticked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ((ListNote) mNote).setListTicked(position,isChecked);
                }
            });

        }

        @Override
        public void setViewClickListener(View.OnClickListener listener) {

        }
    }
    public class SimplifiedListItemViewHolder extends RecyclerView.ViewHolder implements IMovementInformator,
            IBindActionTaker {
        TextView textItem;
        CheckBox ticked;

        public SimplifiedListItemViewHolder(View itemView) {
            super(itemView);
            textItem = (TextView) itemView.findViewById(R.id.simple_list_element_text);
            ticked = (CheckBox)itemView.findViewById(R.id.simple_check);
        }

        @Override
        public void onBindViewHolder(int position) {
            this.textItem.setText(((ListNote) mNote).getListText(position));
            this.ticked.setChecked(((ListNote) mNote).getListTicked(position));
        }

        @Override
        public void setViewClickListener(View.OnClickListener listener) {

        }

        @Override
        public boolean canBeDragged() {
            return false;
        }

        @Override
        public boolean canBeSwiped() {
            return false;
        }
    }

    public class AdderItemViewHolder extends RecyclerView.ViewHolder implements IMovementInformator,
                                    IBindActionTaker {

        public AdderItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public boolean canBeDragged() {
            return false;
        }

        @Override
        public boolean canBeSwiped() {
            return false;
        }

        @Override
        public void onBindViewHolder(final int position) {
        }

        @Override
        public void setViewClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
            itemView.setTag(this);
        }
    }

    public  class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    }


}
