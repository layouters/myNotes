package com.uznamska.lukas.mynotes;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.uznamska.lukas.mynotes.database.PendingAlarmsTable;
import com.uznamska.lukas.mynotes.items.ItemReminder;

import java.util.HashMap;
import java.util.Map;


public class AlarmService extends IntentService {

    private static final String TAG = "MyNotes:AlarmService";

    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";
    public static final String REFRESH_ALL = "REFRESH ALL";

    private IntentFilter matcher;
    Map<String, ICommand> mCmdMap = new HashMap<>();


    interface ICommand {
        void execute(String ...args);
    }

    class CreateCommand implements ICommand {

        @Override
        public void execute(String ...args) {
            Log.d(TAG, "I will be saving pending alarm to database");
            String reminderId = (args!=null && args.length>0) ? args[0] : null;
            ItemReminder reminder = new ItemReminder();
            reminder.setId(Integer.parseInt(reminderId));
            reminder.loadFromDB(getApplicationContext());
            Log.d(TAG, "Reminder " + reminder );

        }
    }

    class RefreshCommand implements ICommand{

        @Override
        public void execute(String ...args) {
            Log.d(TAG, "I will be refreshing all the alarms after device got rebooted");
        }
    }

    class CancelCommand implements ICommand {

        @Override
        public void execute(String ...args) {
            Log.d(TAG, "I will be cancelling alarms");
        }
    }

    public AlarmService() {
        super(TAG);
        matcher = new IntentFilter();
        matcher.addAction(CANCEL);
        matcher.addAction(CREATE);
        matcher.addAction(REFRESH_ALL);

        mCmdMap.put(CREATE, new CreateCommand());
        mCmdMap.put(CANCEL, new CancelCommand());
        mCmdMap.put(REFRESH_ALL, new RefreshCommand());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        String reminderId = intent.getStringExtra(PendingAlarmsTable.COLUMN_REMINDER_ID);
        Log.d(TAG, "Reminder id: " + reminderId);
        if (matcher.matchAction(action)) {
            mCmdMap.get(action).execute(reminderId);
        }

    }
}
