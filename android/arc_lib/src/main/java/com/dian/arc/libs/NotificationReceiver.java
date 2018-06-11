package com.dian.arc.libs;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.NotificationManager;
import com.dian.arc.libs.utilities.PreferencesManager;

import java.util.Locale;

public class NotificationReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "wakeLock");
        wakeLock.acquire();
        try {
        if(PreferencesManager.getInstance()==null){
            PreferencesManager.initialize(context);
        }
            if(PreferencesManager.getInstance().contains("language")) {
                String language = PreferencesManager.getInstance().getString("language", "es");
                String country = PreferencesManager.getInstance().getString("country", "ES");
                Resources res = context.getResources();
                Configuration conf = res.getConfiguration();
                conf.setLocale(new Locale(language, country));
                res.updateConfiguration(conf, res.getDisplayMetrics());
            }

        Thread.sleep(100);

        if(NotificationManager.getInstance()==null){
           NotificationManager.initialize(context);
        }
        int id = intent.getIntExtra(NotificationManager.NOTIFICATION_ID,0);
        int type = intent.getIntExtra(NotificationManager.NOTIFICATION_TYPE,1);

        if(type==NotificationManager.TEST_NEXT) {
            if (ArcManager.getInstance() == null) {
                ArcManager.initialize(context);
            }
        }

        if(type==NotificationManager.TEST_MISSED) {
            int count = PreferencesManager.getInstance().getInt("test_missed_count", 0);
            count++;
            if (count == 4) {
                NotificationManager.getInstance().notifyUser(id, type);
                count = 0;
            }
            NotificationManager.getInstance().removeNotification(id,type);
            PreferencesManager.getInstance().putInt("test_missed_count", count);
        } else {
            NotificationManager.getInstance().notifyUser(id, type);
        }
        Thread.sleep(2900);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wakeLock.release();
    }
}