package com.pnd.future_bosses.plannedanddone;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by sapuzar on 2/13/19.
 */

public class NotificationBuilder  extends BroadcastReceiver {

    final public int notificationID = 13;

    @TargetApi(26)
    @Override
        public void onReceive(Context context, Intent intent) {

            long when = System.currentTimeMillis();

            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            long[] vibrate = new long[]{100, 250, 100, 500};

            Uri table = Uri.parse("content://hr.math.provider.contprov/task");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, 1);
            String year = String.valueOf(c.get(Calendar.YEAR));
            String mon;
            int month = c.get(Calendar.MONTH)+1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            if (month < 10){
                mon = "0" +  String.valueOf(month);
            }
            else{
                mon = String.valueOf(month);
            }

            String d;
            if (day < 10){
                d = "0" +  String.valueOf(day);
            }
            else{
                d = String.valueOf(day);
            }
            String filter = year + mon + d;


            String where = DataBase.TASK_TIME + " LIKE '" + filter + "%'";
            Cursor curs = context.getContentResolver().query(table, new String[]{DataBase.TASK_ID, DataBase.TASK_NAME}, where, null, null);
            int num = curs.getCount();
            final NotificationCompat.Builder notif;
            String NOTIFICATION_CHANNEL_ID = "my_channel_01";
            CharSequence channelName = "hr.math.karga.MYNOTIF";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(vibrate);
            NotificationManager nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(notificationChannel);
        Notification notif1;

        if(num > 0){
            notif1 = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setTicker("Reminder: Planned and done")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setShowWhen(true)
                    .setContentTitle("Check your plans for tomorrow.")
                    .setContentText("You have " + num + " plan" + (num == 1? "":"s") +  ". Take some time and prepare yourself to swtich them from planned to done ;).")
                    .setContentIntent(pendingIntent)
                    .setVibrate(vibrate)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setAutoCancel(true)
                    .build();
            }else{
            notif1 = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setTicker("Reminder: Planned and done")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setShowWhen(true) .setContentTitle("Make some plans for tomorrow.")
                    .setContentText("You don't have any plans for tomorraw. Take some time and make your plans from planned to done ;).")
                    .setContentIntent(pendingIntent)
                    .setVibrate(vibrate)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setAutoCancel(true)
                    .build();

        }
            nm.notify(notificationID, notif1);

        }

    }

