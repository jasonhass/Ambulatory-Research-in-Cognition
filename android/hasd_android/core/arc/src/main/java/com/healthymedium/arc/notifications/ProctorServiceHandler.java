//
// ProctorServiceHandler.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import com.healthymedium.arc.core.Application;


import com.healthymedium.arc.notifications.types.NotificationType;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProctorServiceHandler {

    private static final String tag = "ProctorServiceHandler";

    private List<NotificationNode> nodes = new ArrayList<>();
    private NotificationTimer timer;

    private NotificationNode currentNode;
    private Listener listener;

    private boolean locked = false;

    ProctorServiceHandler(@NonNull Listener listener){
        this.listener = listener;
        if(listener==null){
            throw new UnsupportedOperationException("ProctorServiceHandler.Listener needs to not be null");
        }
    }

    public void refreshData(boolean resumed) {

        if(locked){
            return;
        }
        locked = true;
        nodes.clear();

        NotificationManager manager = NotificationManager.getInstance();
        NotificationNodes notificationNodes = NotificationManager.getInstance().getNodes();

        List<NotificationNode> allNodes = Collections.synchronizedList(notificationNodes.getAll());
        for(NotificationNode node : allNodes){

            NotificationType type = manager.getNotificationType(node.type);
            if(type==null){
               continue;
            }
            if(!type.isProctored()) {
                continue;
            }

            if(node.time.isAfterNow()){
                nodes.add(node);
            } else {
                if(!resumed) {
                    long now = System.currentTimeMillis();
                    analyzeTimeout("posthumous", now, node.time.getMillis(), node);
                }
                listener.onNotify(node);
            }
        }

        //  sort by time
        Collections.sort(nodes,new NotificationNode.TimeComparator());

        for(NotificationNode node : nodes){

        }

        locked = false;
    }

    public void refreshData() {
        refreshData(false);
    }

    public void start(){

        if(nodes.size()==0){
            listener.onFinished();
            return;
        }
        currentNode = nodes.remove(0);

        long time = currentNode.time.getMillis();
        long delay = time - DateTime.now().getMillis();
        timer = new NotificationTimer(delay,time);
        timer.start();

        int seconds = (int) (delay/1000);
        int minutes = seconds/60;
        int hours = minutes/60;

        seconds -= minutes*60;
        minutes -= hours*60;


    }

    public void stop(){

        if(timer!=null) {
            timer.cancel();
            timer = null;
        }
    }

    public boolean isRunning(){
        return timer != null;
    }

    private void onTimeout(){
        final NotificationNode node = currentNode;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(!isRunning()) {
                    listener.onNotify(node);
                    return;
                }
                stop();
                listener.onNotify(node);
                start();
            }
        });
    }

    public interface Listener {
        void onFinished();
        void onNotify(final NotificationNode node);
    }


    // there is an extra ten second buffer in this timer
    // just before onFinish would be called, countdown timers become reliant on sleep state
    // and perform the call after a device wakes up
    //
    // to get around this, we add a ten second buffer to the timer and check against system time on each interval
    // when the system time is after the non buffered time, we execute the timeout
    public class NotificationTimer extends CountDownTimer {

        private static final String tag = "NotificationTimer";
        private static final int tenSeconds = 10*1000;
        long time;

        public NotificationTimer(long delay, long time) {
            super(delay+tenSeconds, 500);
            this.time = time;
        }

        @Override
        public void onFinish() {

            long now = System.currentTimeMillis();
            analyzeTimeout("finished",now,time,currentNode);
            onTimeout();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long now = System.currentTimeMillis();
            if(now>time){

                analyzeTimeout("preemptive",now,time,currentNode);
                onTimeout();
//            } else { // uncomment for logging
//                int seconds = (int) ((millisUntilFinished-tenSeconds)/1000);
//                int minutes = seconds/60;
//                int hours = minutes/60;
//
//                seconds -= minutes*60;
//                minutes -= hours*60;
//
            }
        }

    }

    private static long five_min = 5*60*1000;
    private static long two_hr = 2*60*60*1000;

    private static void analyzeTimeout(String type, long now, long target, NotificationNode node) {
        long delta = Math.abs(target-now);
        if(delta>five_min){
            int seconds = (int) ((delta)/1000);
            int minutes = seconds/60;
            int hours = minutes/60;
            int days = hours/24;

            seconds -= minutes*60;
            minutes -= hours*60;
            hours -= days*24;

            String direction = (target < now) ? "late":"early";
            String difference = days+" day(s) "+hours+"hr "+minutes+"min "+seconds+"sec";

            DateTimeFormatter format = DateTimeFormat.mediumDateTime();

            JsonObject json = new JsonObject();
            json.addProperty("type",type);
            json.addProperty("direction",direction);
            json.addProperty("difference",difference);
            json.addProperty("actual",format.print(now));
            json.addProperty("actualTimestamp",now);
            json.addProperty("target",format.print(target));
            json.addProperty("targetTimestamp",target);

            if(node!=null){
                json.addProperty("nodeId",node.id);
                json.addProperty("nodeType",node.type);
                json.addProperty("nodeTypeName",NotificationTypes.getName(node.type));
                json.addProperty("nodeNotifyId",node.getNotifyId());

                NotificationType notificationType = NotificationTypes.fromId(node.type);
                if(notificationType!=null){
                    json.addProperty("nodeShown",notificationType.onNotifyPending(node));
                }
            }

            Context context = Application.getInstance();

            if(context != null) {
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

                String packageName = context.getPackageName();
                boolean isIgnoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(packageName);
                json.addProperty("isIgnoringBatteryOptimizations",isIgnoringBatteryOptimizations);
                json.addProperty("isDeviceInPowerSaveMode",powerManager.isPowerSaveMode());
                json.addProperty("isDeviceInteractive", powerManager.isInteractive());
                json.addProperty("isDeviceIdle",powerManager.isDeviceIdleMode());

                if(!isIgnoringBatteryOptimizations) {
                    ProctorDeviation deviation = ProctorDeviation.load();
                    deviation.markIncident();
                    deviation.save();
                }
            }

        }
    }

}
