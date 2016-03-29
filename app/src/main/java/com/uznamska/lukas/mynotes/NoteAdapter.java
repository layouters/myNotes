/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.uznamska.lukas.mynotes.contentprovider.NotesContentProviderProxy;
import com.uznamska.lukas.mynotes.items.Header;
import com.uznamska.lukas.mynotes.items.IBindActionTaker;
import com.uznamska.lukas.mynotes.items.INote;
import com.uznamska.lukas.mynotes.items.INoteItem;

import com.uznamska.lukas.mynotes.items.IReminder;
import com.uznamska.lukas.mynotes.items.IUpdateTextListener;
import com.uznamska.lukas.mynotes.items.ItemAdder;
import com.uznamska.lukas.mynotes.items.ItemReminder;
import com.uznamska.lukas.mynotes.items.ItemReminderAdder;
import com.uznamska.lukas.mynotes.items.ItemSeparator;
import com.uznamska.lukas.mynotes.items.Iterator;
import com.uznamska.lukas.mynotes.items.ListItem;
import com.uznamska.lukas.mynotes.items.ListNote;

import java.security.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lukasz on 2016-02-21.
 */
public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
                        implements CardItemTouchHelper.ItemTouchHelperAdapter,
                        View.OnClickListener {

    class ContextReminderListener implements PopupMenu.OnMenuItemClickListener {
        private int mYear;
        private int mMonth;
        private long mDay;
        private long mTime;
        private long mHour;
        private long mMinute;

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.date_setter:
                    showDatePicker();
                    return true;
                case R.id.location_setter:
                    showLocationPicker();
                    return true;
                default:
                    return false;
            }
        }

        private void  showDatePicker() {
            final View dialogView = View.inflate(mContext, R.layout.date_time_picker, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();

            dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                    TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
                    timePicker.setIs24HourView(true);
                    Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth(),
                            timePicker.getCurrentHour(),
                            timePicker.getCurrentMinute());

                    mTime = calendar.getTimeInMillis();
                    mDay = datePicker.getDayOfMonth();
                    mYear = datePicker.getYear();
                    mMonth = datePicker.getMonth();
                    mHour = timePicker.getCurrentHour();
                    mMinute = timePicker.getCurrentMinute();

                    alertDialog.dismiss();
                    updateReminder();
                    //setAlarm();
                }});
            alertDialog.setView(dialogView);
            alertDialog.show();
        }

        private void updateReminder() {
            IReminder reminder = new ItemReminder();
            reminder.setDate(
                    new StringBuilder()
                            // Month is 0 based so add 1
                            .append(mMonth + 1).append("-")
                            .append(mDay).append("-")
                            .append(mYear).append(" ").toString());
            reminder.setTime(new StringBuilder().append(mHour).append(":").append(mMinute).toString());
            reminder.set(true);
            //reminder.setNoteId(mNote.getId());
            mNote.addItemReminder((INoteItem)reminder);
            notifyDataSetChanged();
        }

    }
    private static final String TAG = "Note:NoteAdapter";
    static int counter = 0 ;
    private final Context mContext;
    private final int HEADER_ELEMENT_TYPE = 0;
    private final int LIST_ELEMENT_TYPE = 1;
    private final int ADDER_ELEMENT_TYPE = 2;
    private final int ITEM_REMINDER = 3;
    private final int ITEM_SEPARATOR = 4;
    private final int ITEM_REMINDER_ADDER = 5;

    HolderAbstractFactory factoryHolder;
    int mOverhead = 3;

    PresentationMode mDisplayMode;
    INote mNote;
    NotesContentProviderProxy proxyContentProvider;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private Integer mHour;
    private Integer mMinute;

    enum EditorType {
        EDIT,
        NEW
    }



//    void setAlarm() {
//        Log.d(TAG,"Alarm is being set!!");
//        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(mContext, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
//
//        // Set the alarm to start at 8:30 a.m.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, mHour);
//        calendar.set(Calendar.MINUTE, mMinute);
//
//// setRepeating() lets you specify a precise custom interval--in this case,
//// 20 minutes.
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                20000, alarmIntent);
//
//        Toast.makeText(mContext, "Alarm Set", Toast.LENGTH_SHORT).show();
//    }

    public void cancelAlarm() {
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(alarmIntent);
        Toast.makeText(mContext, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }


    private void showLocationPicker() {
        cancelAlarm();
        Log.d(TAG,"Location set");
    }

    enum DisplayMode {
        RICH_MODE,
        SIMPLIFIED_MODE
    }

    Map<Integer, INoteItem> resourceLayoutMap;

    abstract class PresentationMode {

        public PresentationMode() {
            resourceLayoutMap = new HashMap<>();
        }

        public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Log.d(TAG, "Procedure: On create viewholder");
            IBindActionTaker viewHolder = (IBindActionTaker)factoryHolder.getHolderOfType(parent, viewType);
            if(viewHolder != null) {
                viewHolder.setViewClickListener(NoteAdapter.this);
                return (RecyclerView.ViewHolder) viewHolder;
            }
            Log.e(TAG, "Holder is null, thats an error viewType: " + viewType);
            return null;
        }

        public  void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            Log.d(TAG, "Procedure: On bind view holder " + position);

            if(holder != null) {
                ((IBindActionTaker) holder).onBindViewHolder(position);
                //NoteAdapter.this.bindViewHolder(holder,position);
            }
        }

        public int getItemViewType(int position) {

           // Log.d(TAG, "Procedure:ItemViewType: " + position);
            INote note = mNote;
            if(note.getItem(position) instanceof Header) {
                //Log.d(TAG, "ItemViewType instance of header");
                resourceLayoutMap.put(HEADER_ELEMENT_TYPE, note.getItem(position));
                return HEADER_ELEMENT_TYPE;
            } else if(note.getItem(position) instanceof ItemAdder){
                //Log.d(TAG, "ItemViewType instance of adder");
                resourceLayoutMap.put(ADDER_ELEMENT_TYPE, note.getItem(position));
                return ADDER_ELEMENT_TYPE;
            } else if(note.getItem(position) instanceof ListItem) {
                //Log.d(TAG, "ItemViewType instance of listiten");/////////////////
                resourceLayoutMap.put(LIST_ELEMENT_TYPE, note.getItem(position));
                return LIST_ELEMENT_TYPE;
            } else if(note.getItem(position) instanceof ItemSeparator) {
                //Log.d(TAG, "ItemViewType instance of separator");
                resourceLayoutMap.put(ITEM_SEPARATOR, note.getItem(position));
                return ITEM_SEPARATOR;
            } else if(note.getItem(position) instanceof ItemReminder) {
               // Log.d(TAG, "ItemViewType instance of remninder");
                resourceLayoutMap.put(ITEM_REMINDER, note.getItem(position));
                return ITEM_REMINDER;
            } else if(note.getItem(position) instanceof ItemReminderAdder) {
                return ITEM_REMINDER_ADDER;
            }
            return -1;
        }

        public abstract int getItemCount();

        public abstract void onItemMove(int fromPosition, int toPosition);

        public abstract void onItemDismiss(int position);

        public abstract void onClick(View v);
    }

    class RichMode extends PresentationMode {
        EditorType mEdit;

        public RichMode(EditorType edit) {
            factoryHolder = new RichHolderFactory();
            mEdit = edit;
        }

        @Override
        public int getItemCount() {
           // Log.d(TAG, "this is size in  rich mode" + mNote.getSize());
           // Log.d(TAG, "this is note in  rich mode" + mNote);
           // Log.d(TAG, "Procedure: getitemcoun");
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
            Log.d(TAG, "item deleted pos: " + position);
            if(mEdit == EditorType.EDIT) {
                Log.d(TAG, "Delete also from database pos: " + position + " " + mNote.getItem(position));
                proxyContentProvider.deleteItem(mNote.getItem(position));
            }
            ((ListNote)mNote).removeListElement(position);
            notifyItemRemoved(position);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "clicked clicked");
            Object holder = v.getTag();
            if(holder instanceof AdderItemViewHolder) {
                AdderItemViewHolder tmpHolder = (AdderItemViewHolder)holder;
                Log.d(TAG, "Adder list has been clicked | view id:  " + tmpHolder.itemView.getId());
                int iditem = -1;
                ListItem emptyItem = new ListItem();
                if(mEdit == EditorType.EDIT) {
                    Log.d(TAG, "Add empty list element to database");
                    iditem = proxyContentProvider.saveListItem(mNote);
                    emptyItem.setId(iditem);
                } else {
                    Log.d(TAG, "New editor");
                }
                //emptyItem.setUpdateTextListener(tmpHolder);
                int pos = mNote.addElement(emptyItem);
                Log.d(TAG, "Position inserted " + pos);
                //tmpHolder.itemView.setVisibility(View.INVISIBLE);
                notifyItemInserted(pos);
            } else if(holder instanceof ReminderViewHolder) {
                ReminderViewHolder tmpHolder = (ReminderViewHolder)holder;
                Log.d(TAG, "Reminder clicked| view id:  " + tmpHolder.itemView.getId());

                //notifyDataSetChanged();
            } else if(holder instanceof ReminderAdderViewHolder) {
                ReminderAdderViewHolder tmpHolder = (ReminderAdderViewHolder)holder;
                Log.d(TAG, "Reminder Adder clicked| view id:  " + tmpHolder.itemView.getId());
                Toast.makeText(mContext, "Reminder Adder clicked", Toast.LENGTH_SHORT).show();
                PopupMenu popup = new PopupMenu(mContext, tmpHolder.itemView);
                popup.setOnMenuItemClickListener(new ContextReminderListener());
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.reminder_type_menu, popup.getMenu());
                popup.show();
            }
        }
    }

    class SimplifiedMode extends PresentationMode {
        public SimplifiedMode() {
            factoryHolder = new SimplifiedHolderFactory();
        }

        @Override
        public int getItemCount() {
            return mNote.getSimpleItemsIterator().getItemsNumber();
        }

        @Override
        public void onItemMove(int fromPosition, int toPremoveListElementosition) {
            Log.d(TAG, "Inside cards list is not interactive");
        }

        @Override
        public void onItemDismiss(int position) {
            Log.d(TAG, "Dismiss Simple mode");
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
            } else if(type == ITEM_REMINDER_ADDER) {
                Log.d(TAG, "Create reminder adder");
                return createReminderAdderHolder(parent, type);
            }
            return null;
        }

        abstract RecyclerView.ViewHolder createHeaderHolder(ViewGroup parent, int type);
        abstract RecyclerView.ViewHolder createListElementHolder(ViewGroup parent, int type);
        abstract RecyclerView.ViewHolder createAdderHolder(ViewGroup parent, int type);
        abstract RecyclerView.ViewHolder createSeparatorHolder(ViewGroup parent, int type);
        abstract RecyclerView.ViewHolder createReminderHolder(ViewGroup parent, int type);
        abstract RecyclerView.ViewHolder createReminderAdderHolder(ViewGroup parent, int type);
    }

    class SimplifiedHolderFactory extends HolderAbstractFactory {

        @Override
        RecyclerView.ViewHolder createHeaderHolder(ViewGroup parent, int type) {
            View v = null;
           // Log.d(TAG, "This is Header element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_note_header, parent, false);
            return  new SimplifiedHeaderViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createListElementHolder(ViewGroup parent, int type) {
            View v = null;
            //Log.d(TAG, "This is List element type");
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
           // Log.d(TAG, "This is separator type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.separator_view_item, parent, false);
            return new SeparatorViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createReminderHolder(ViewGroup parent, int type) {
            return null;
        }

        @Override
        RecyclerView.ViewHolder createReminderAdderHolder(ViewGroup parent, int type) {
            return null;
        }

    }

    class RichHolderFactory extends HolderAbstractFactory {

        @Override
        RecyclerView.ViewHolder createHeaderHolder(ViewGroup parent, int type) {
            View v = null;
            //Log.d(TAG,"This is Header element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_view_item, parent, false);
            return  new HeaderViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createListElementHolder(ViewGroup parent, int type) {
            View v = null;
           // Log.d(TAG, "This is List element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_list_view_item, parent, false);
            return new ListItemViewHolder(v, new ItemTextWatcher(), new ItemCheckedListener());
        }

        @Override
        RecyclerView.ViewHolder createAdderHolder(ViewGroup parent, int type) {
            View v = null;
            //RecyclerView.ViewHolder viewHolder = null;
           // Log.d(TAG,"This is Adder element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adder_view_item, parent, false);
            return new AdderItemViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createSeparatorHolder(ViewGroup parent, int type) {
            View v = null;
           // Log.d(TAG, "This is Separator element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.separator_view_item, parent, false);
            return new SeparatorViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createReminderHolder(ViewGroup parent, int type) {
            View v = null;
            //Log.d(TAG, "This is Reminder element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_view_item, parent, false);
            return new ReminderViewHolder(v);
        }

        @Override
        RecyclerView.ViewHolder createReminderAdderHolder(ViewGroup parent, int type) {
            View v = null;
            //Log.d(TAG, "This is Reminder element type");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_adder_view_item, parent, false);
            return new ReminderAdderViewHolder(v);
        }

    }

   //Class NoteAdapter starts here
    NoteAdapter(INote note, Context context, EditorType edit) {
        mContext = context;
        mNote = note;
        setDisplayMode(new RichMode(edit));
        proxyContentProvider = new NotesContentProviderProxy(mContext);
    }

    NoteAdapter(INote note, Context context, DisplayMode mode, EditorType edit) {
        mContext = context;
        mNote = note;
        if(mode == DisplayMode.RICH_MODE) {
            setDisplayMode(new RichMode(edit));
        } else if (mode == DisplayMode.SIMPLIFIED_MODE) {
            setDisplayMode(new SimplifiedMode());
        } else {
            Log.e(TAG, "Unsupported Display mode");
        }
        proxyContentProvider = new NotesContentProviderProxy(mContext);

    }

    public PresentationMode getDisplayMode() {
        return mDisplayMode;
    }

    public void setDisplayMode(PresentationMode mDisplayMode) {
        this.mDisplayMode = mDisplayMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getDisplayMode().onCreateViewHolder(parent, viewType);
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
        getDisplayMode().onItemDismiss(position);
    }

    @Override
    public void onClick(View view) {
        getDisplayMode().onClick(view);
    }

    public class ReminderAdderViewHolder extends RecyclerView.ViewHolder implements IMovementInformator,
            IBindActionTaker {

        public ReminderAdderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindViewHolder(int position) {
            //dateItem.setText(mNote.getReminder().getDate());
        }

        @Override
        public void setViewClickListener(View.OnClickListener listener) {
            Log.d(TAG, "Listener added to ReminderViewHolder");
            itemView.setOnClickListener(listener);
            itemView.setTag(this);
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


    public class ReminderViewHolder extends RecyclerView.ViewHolder implements IMovementInformator,
            IBindActionTaker {

        private final EditText dateItem;
        private final EditText timeItem;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            dateItem = (EditText) itemView.findViewById(R.id.text_reminder_date);
            timeItem = (EditText) itemView.findViewById(R.id.text_reminder_time);
        }

        @Override
        public void onBindViewHolder(int position) {
            Iterator it = mNote.getReminderIterator();
            if(it.hasNext()) {
                IReminder rem = ((IReminder) it.next());
                dateItem.setText(rem.getDate());
                timeItem.setText(rem.getTime());
            }
        }

        @Override
        public void setViewClickListener(View.OnClickListener listener) {
            Log.d(TAG, "Listener added to ReminderViewHolder");
            itemView.setOnClickListener(listener);
            itemView.setTag(this);
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

    public  class ListItemViewHolder extends RecyclerView.ViewHolder implements IMovementInformator,
                IBindActionTaker {

        TextView textItem;
        CheckBox ticked;
        ItemTextWatcher mTextWatcher;
        ItemCheckedListener mCheckListener;

        public ListItemViewHolder(View itemView, ItemTextWatcher itemTextListener,
                                  ItemCheckedListener checkListener) {
            super(itemView);
            textItem = (TextView) itemView.findViewById(R.id.edit_msg_text);
            ticked = (CheckBox)itemView.findViewById(R.id.edit_check);
            mTextWatcher = itemTextListener;
            this.textItem.addTextChangedListener(mTextWatcher);
            mCheckListener = checkListener;
            this.ticked.setOnCheckedChangeListener(mCheckListener);
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
            Log.e(TAG, "On bind view holder " + position + " TEXT " + ((ListNote) mNote).getListText(position));
            mTextWatcher.updatePosition(position);
            mCheckListener.updatePosition(position);
            this.textItem.setText(((ListNote) mNote).getListText(position));
            boolean isChecked = ((ListNote) mNote).getListTicked(position);
            this.ticked.setChecked(isChecked);
            int flag = 0;
            if(isChecked) {
                flag = Paint.STRIKE_THRU_TEXT_FLAG;
            }
            textItem.setPaintFlags(/*textItem.getPaintFlags() |*/ flag);
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
            //this.ticked.setChecked(((ListNote) mNote).getListTicked(position));

            boolean isChecked = ((ListNote) mNote).getListTicked(position);
            this.ticked.setChecked(isChecked);
            int flag = 0;
            if(isChecked) {
                flag = Paint.STRIKE_THRU_TEXT_FLAG;
            }
            textItem.setPaintFlags(textItem.getPaintFlags() |  flag);
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
                                    IBindActionTaker, IUpdateTextListener {

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

        @Override
        public void onUpdate() {
            itemView.setVisibility(View.VISIBLE);
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

    public class ItemTextWatcher implements TextWatcher {
        int pos;

        public void updatePosition(int pos) {
            this.pos = pos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ((ListItem)mNote.getItem(pos)).setText(s.toString());
            Log.d(TAG, "Saving " + s.toString() + " " + pos);
        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    }
    public class ItemCheckedListener implements CompoundButton.OnCheckedChangeListener {
        int pos;

        public void updatePosition(int pos) {
            this.pos = pos;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ListItem item =  ((ListItem) mNote.getItem(pos));
            boolean oldChecked =  item.isTicked();
            ((ListItem) mNote.getItem(pos)).setTicked(isChecked);
            if(oldChecked != isChecked)
                 notifyDataSetChanged();

        }
    }


}
