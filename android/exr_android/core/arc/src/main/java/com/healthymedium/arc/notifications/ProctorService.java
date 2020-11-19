//
// ProctorService.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.core.MainActivity;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class ProctorService extends Service {

    private static final String tag = "ProctorService";

    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";
    public static final String ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE";
    public static final String ACTION_RESUME_SERVICE = "ACTION_RESUME_SERVICE";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_REFRESH_DATA = "ACTION_REFRESH_DATA";

    private ProctorServiceHandler serviceHandler;
    private Handler handler = new Handler();
    private boolean intentionalDestruction = false;

    public ProctorService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startForegroundService();
    }

    @Override
    public void onDestroy() {

        if(!intentionalDestruction){

            Proctor.startService(this);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            stopForegroundService();
            return START_STICKY;
        }

        String action = intent.getAction();


        switch (action) {
            case ACTION_START_SERVICE:

                if(serviceHandler==null){
                    serviceHandler = new ProctorServiceHandler(listener);

                    serviceHandler.refreshData();
                }

                if(!serviceHandler.isRunning()){
                    serviceHandler.start();

                }

                if(!ProctorWatchdogJob.isScheduled(this)) {
                    ProctorWatchdogJob.start(this);
                }
                break;

            case ACTION_PAUSE_SERVICE:
                if(serviceHandler==null) {
                    serviceHandler = new ProctorServiceHandler(listener);
                }
                serviceHandler.stop();
                break;

            case ACTION_RESUME_SERVICE:
                if(serviceHandler==null) {
                    serviceHandler = new ProctorServiceHandler(listener);
                }
                serviceHandler.refreshData(true);
                serviceHandler.start();

                if(!ProctorWatchdogJob.isScheduled(this)) {
                    ProctorWatchdogJob.start(this);
                }
                break;

            case ACTION_STOP_SERVICE:
                intentionalDestruction = true;
                if(serviceHandler!=null){
                    serviceHandler.stop();
                }
                stopForegroundService();
                ProctorWatchdogJob.stop(this);
                break;

            case ACTION_REFRESH_DATA:
                if(serviceHandler==null) {
                    serviceHandler = new ProctorServiceHandler(listener);
                }
                serviceHandler.stop();
                serviceHandler.refreshData();
                serviceHandler.start();
                break;
        }

        return START_STICKY;
    }

    private void startForegroundService() {


        NotificationUtil.createChannel(this,NotificationTypes.TestProctor);

        PackageManager packageManager = getPackageManager();
        String packageName = getPackageName();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);

        int notificationId = NotificationTypes.TestProctor.getId();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationTypes.TestProctor.getChannelId())
                .setContentTitle(ViewUtil.getString(R.string.notification_testproctor_header))
                .setContentText(ViewUtil.getString(R.string.notification_testproctor_body))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        Notification notification = builder.build();
        startForeground(notificationId, notification);
    }

    private void stopForegroundService() {

        stopForeground(true);
        stopSelf();
    }

    ProctorServiceHandler.Listener listener = new ProctorServiceHandler.Listener() {
        @Override
        public void onFinished() {

            Proctor.stopService(getApplicationContext());
        }

        @Override
        public void onNotify(final NotificationNode node) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    //noinspection deprecation
                    PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                            PowerManager.FULL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP,
                            tag+"_"+SystemClock.uptimeMillis());

                    wakeLock.acquire();
                    NotificationManager.getInstance().notifyUser(node);
                    wakeLock.release();
                }
            });
        }
    };

}
