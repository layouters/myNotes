package com.uznamska.lukas.mynotes;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.uznamska.lukas.mynotes.database.PendingAlarmsTable;
import com.uznamska.lukas.mynotes.items.ItemPendingAlarm;
import com.uznamska.lukas.mynotes.items.ItemReminder;
import com.uznamska.lukas.mynotes.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AlarmService extends IntentService {

    private static final String TAG = "MyNotes:AlarmService";

    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";
    public static final String REFRESH_ALL = "REFRESH ALL";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-M-d";
    private IntentFilter matcher;
    Map<String, ICommand> mCmdMap = new HashMap<>();


    interface ICommand {
        void execute(String ...args);

    }

    class CreateCommand implements ICommand {

        @Override
        public void execute(String ...args) {
            Log.d(TAG, "I will be saving pending alarm to database");

            String reminderId = (args!=null && args.length > 0) ? args[0] : null;
            ItemReminder reminder = new ItemReminder();
            reminder.setId(Integer.parseInt(reminderId));
            reminder.loadFromDB(getApplicationContext());

            savePendingAlarmForReminder(reminder);
        }

        private void savePendingAlarmForReminder(ItemReminder reminder) {
            String time = reminder.getTime();
            String date = reminder.getDate();

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            ItemPendingAlarm alarm =  new ItemPendingAlarm();
            long now = System.currentTimeMillis();

            try {
                Date alarmDate = DateUtils.getDateFromString(date);
                cal.setTime(alarmDate);

                cal = DateUtils.getCalendarWithTime(time, cal);

                alarm.setReminderId(reminder.getId());
                alarm.setDateTime(cal.getTimeInMillis());

                if (alarm.getDateTime() < now - DateUtils.MIN) {
                    alarm.setStatus(PendingAlarmsTable.EXPIRED);
                }
                alarm.saveDb(getApplicationContext());
            } catch (ParseException e) {
                Log.e(TAG, "Invalid date format: " + date);
            }
        }
    }

    class RefreshCommand implements ICommand {

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
