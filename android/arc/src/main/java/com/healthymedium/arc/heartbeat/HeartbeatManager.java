//
// HeartbeatManager.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.heartbeat;

import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.models.Response;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;

public class HeartbeatManager {

    static public final String HEARTBEAT_CHANNEL_CREATED = "HEARTBEAT_CHANNEL_CREATED";
    static public final String LAST_HEARTBEAT = "LAST_HEARTBEAT";

    public static final int CHANNEL_HEARTBEAT = 9000;
    static public final String CHANNEL_HEARTBEAT_ID = "HEARTBEAT";
    static public final String CHANNEL_HEARTBEAT_NAME = "Heartbeat";
    static public final String CHANNEL_HEARTBEAT_DESC = "Used to phone home once a day between arcs. Will cease once the study is complete.";

    private static HeartbeatManager instance;
    private Context context;

    private long lastHeartbeat;

    //
    android.app.NotificationManager notificationManager;
    Listener listener;


    private HeartbeatManager(Context context) {
        this.context = context;
        if(PreferencesManager.getInstance()==null){
            PreferencesManager.initialize(context);
        }

        if(!PreferencesManager.getInstance().contains(HEARTBEAT_CHANNEL_CREATED)){
            NotificationManager.createNotificationChannel(context,CHANNEL_HEARTBEAT_ID,CHANNEL_HEARTBEAT_NAME,CHANNEL_HEARTBEAT_DESC);
            PreferencesManager.getInstance().putBoolean(HEARTBEAT_CHANNEL_CREATED,true);
        }

        lastHeartbeat = PreferencesManager.getInstance().getLong(LAST_HEARTBEAT,0);
        Log.i("HeartbeatManager", "last heartbeat = "+lastHeartbeat);
    }

    public static synchronized void initialize(Context context) {
        instance = new HeartbeatManager(context);
    }

    public static synchronized HeartbeatManager getInstance() {
        return instance;
    }

    public void tryHeartbeat(RestClient restClient, String participantId, final Listener listener){
        this.listener = listener;

        if(Config.REST_BLACKHOLE || !Config.REST_HEARTBEAT){
            if(listener!=null){
                listener.onSuccess(false);
            }
            return;
        }

        Log.i("HeartbeatManager","try heartbeat");
        DateTime time = new DateTime(lastHeartbeat*1000L).plusDays(1);
        if(time.isBeforeNow()) {
            restClient.sendHeartbeat(participantId, restListener);
        } else if(listener!=null){
            Log.i("HeartbeatManager","too soon, skipping api call");
            listener.onSuccess(false);
        }

    }

    public void startNotification(){
        Log.i("HeartbeatManager","start notification");
        if(NotificationManager.getInstance()==null) {
            NotificationManager.initialize(context);
        }
        Notification notification = NotificationManager.getInstance().buildDataNotification("",CHANNEL_HEARTBEAT_NAME);
        notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(CHANNEL_HEARTBEAT, notification);
    }

    public void stopNotification(){
        Log.i("HeartbeatManager","stop notification");
        if(notificationManager!=null){
            notificationManager.cancel(CHANNEL_HEARTBEAT);
        }
    }

    public void scheduleHeartbeat(){
        Log.i("HeartbeatManager","schedule heartbeat job");
        ComponentName serviceComponent = new ComponentName(context, HeartbeatJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(CHANNEL_HEARTBEAT, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPeriodic(6*60*60*1000); // six hours
        //builder.setPeriodic(15*60*1000); // fifteen minutes
        builder.setRequiresDeviceIdle(false); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        builder.setPersisted(true); // set persistant across reboots

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(false);
            builder.setRequiresStorageNotLow(false);
        }

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public void unscheduleHeartbeat(){
        Log.i("HeartbeatManager","unschedule heartbeat job");
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(CHANNEL_HEARTBEAT);
    }

    public interface Listener{
        void onSuccess(boolean tried);
        void onFailure();
    }

    RestClient.Listener restListener = new RestClient.Listener() {
        @Override
        public void onSuccess(Response response) {
            lastHeartbeat = (DateTime.now().getMillis() / 1000L);
            PreferencesManager.getInstance().putLong(LAST_HEARTBEAT,lastHeartbeat);
            Log.i("HeartbeatManager", "new heartbeat = "+lastHeartbeat);

            if(listener!=null){
                listener.onSuccess(true);
            }
        }

        @Override
        public void onFailure(Response response) {
            if(listener!=null){
                listener.onFailure();
            }
        }
    };

}
