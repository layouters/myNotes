/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.uznamska.lukas.mynotes.database.PendingAlarmsTable;
import com.uznamska.lukas.mynotes.items.ItemPendingAlarm;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // For our recurring task, we'll just display a message
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        Log.d("CC", "Receiver!!!!");
        int pendingAlarmId = intent.getIntExtra(PendingAlarmsTable.COLUMN_ID, -1);
        int reminderId = intent.getIntExtra(PendingAlarmsTable.COLUMN_REMINDER_ID, -1);

        ItemPendingAlarm pendingAlarm = new ItemPendingAlarm();
        pendingAlarm.setId(pendingAlarmId);
        pendingAlarm.setStatus(PendingAlarmsTable.EXPIRED);
        pendingAlarm.saveDb(context);
        Intent notIntent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 1, notIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.emma)
                .setContentTitle("Title")
                .setTicker("this is ticker text")
                .setContentIntent(pi);

        Notification n = builder.build();
        n.defaults |= Notification.DEFAULT_VIBRATE;
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(11, n);
    }
}
