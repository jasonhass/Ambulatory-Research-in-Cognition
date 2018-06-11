package com.dian.arc.libs;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.NotificationManager;
import com.dian.arc.libs.utilities.PreferencesManager;

public class HeartbeatReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
        if(PreferencesManager.getInstance()==null){
            PreferencesManager.initialize(context);
        }
        Thread.sleep(100);

        if(NotificationManager.getInstance()==null){
           NotificationManager.initialize(context);
        }
        if (ArcManager.getInstance() == null) {
            ArcManager.initialize(context);
        }
        int lifecycle = ArcManager.getInstance().getLifecycleStatus();
        if(lifecycle==ArcManager.LIFECYCLE_IDLE){
            ArcManager.getInstance().tryHeartbeat();
        } else if(lifecycle==ArcManager.LIFECYCLE_OVER){
            ArcManager.getInstance().cancelHeartbeatAlarm();
        }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}