package com.dian.arc.libs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dian.arc.libs.utilities.Config;
import com.dian.arc.libs.utilities.NotificationManager;
import com.dian.arc.libs.utilities.PreferencesManager;

public class NotificationAutoStart extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            if(PreferencesManager.getInstance()==null){
                PreferencesManager.initialize(context);
            }
            if(NotificationManager.getInstance()==null){
                NotificationManager.initialize(context);
            }
            NotificationManager.getInstance().scheduleAllNotifications();

            if(Config.REST_HEARTBEAT){
                scheduleHeartbeatAlarm(context);
            }
            //scheduleUploadCheck(context);
        }
    }

    public void scheduleHeartbeatAlarm(Context context){
        Intent notificationIntent = new Intent(context, HeartbeatReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,AlarmManager.INTERVAL_HALF_DAY, AlarmManager.INTERVAL_HALF_DAY,pendingIntent);
    }
}