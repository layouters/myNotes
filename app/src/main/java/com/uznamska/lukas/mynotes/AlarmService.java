package com.uznamska.lukas.mynotes;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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

    abstract class AbstractCommand implements ICommand{
        protected void reSetAlarms(String ...args) {
            Intent i;
            PendingIntent pi;
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Cursor pendings = ItemPendingAlarm.getRelatedAlarms(getApplicationContext(), args);
            if (pendings != null) {
                while (pendings.moveToNext()) {
                    long now = System.currentTimeMillis();
                    long time, diff;
                    i = new Intent(getApplicationContext(), AlarmReceiver.class);
                    i.putExtra(PendingAlarmsTable.COLUMN_ID, pendings.getInt(pendings.getColumnIndex(PendingAlarmsTable.COLUMN_ID)));
                    i.putExtra(PendingAlarmsTable.COLUMN_REMINDER_ID,
                            pendings.getInt(pendings.getColumnIndex(PendingAlarmsTable.COLUMN_REMINDER_ID)));

                    pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                    time = pendings.getLong(pendings.getColumnIndex(PendingAlarmsTable.COLUMN_TIME));
                    diff = time - now + (long)DateUtils.MIN;
                    if (diff > 0 && diff < DateUtils.YEAR) {
                        am.set(AlarmManager.RTC_WAKEUP, time, pi);
                    }
                }
                pendings.close();
            }
        }
    }

    class CreateCommand extends AbstractCommand {

        @Override
        public void execute(String ...args) {
            Log.d(TAG, "I will be saving pending alarm to database");

            String reminderId = (args != null && args.length > 0) ? args[0] : null;
            ItemReminder reminder = new ItemReminder();
            reminder.setId(Integer.parseInt(reminderId));
            reminder.loadFromDB(getApplicationContext());

            savePendingAlarmForReminder(reminder);
            reSetAlarms(reminderId);
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

    class RefreshCommand extends AbstractCommand {

        @Override
        public void execute(String ...args) {
            Log.d(TAG, "I will be refreshing all the alarms after device got rebooted");
        }
    }

    class CancelCommand extends AbstractCommand {

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
