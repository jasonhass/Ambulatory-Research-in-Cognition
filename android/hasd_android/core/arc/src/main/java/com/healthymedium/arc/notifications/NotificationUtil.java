//
// NotificationUtil.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.healthymedium.arc.notifications.types.NotificationImportance;


import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.types.NotificationType;

import java.util.ArrayList;
import java.util.List;

public class NotificationUtil {

    static private final String tag = "NotificationUtil";

    public static boolean areNotificationsEnabled(Context context){

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if(!notificationManager.areNotificationsEnabled()){
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return true;
        }
        for(NotificationChannel channel : getChannels(context)){
            if(channel.getImportance() == NotificationImportance.NONE){
                return false;
            }
        }
        return true;
    }

    public static void openNotificationSettings(Context context){

        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", context.getPackageName());
        intent.putExtra("app_uid", context.getApplicationInfo().uid);
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        context.startActivity(intent);
    }

    // channels ------------------------------------------------------------------------------------

    public static void createChannel(Context context, NotificationType type){


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(type.getChannelId(), type.getChannelName(), type.getImportance());
        channel.setDescription(type.getChannelDesc());
        channel.setVibrationPattern(new long[]{500,250,125,250});
        channel.setShowBadge(type.shouldShowBadge());
        channel.enableVibration(true);
        channel.enableLights(true);

        if(type.getSoundResource()!=-1){
            Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + type.getSoundResource());
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(sound,attributes);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public static void removeChannel(Context context, NotificationType type){


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        android.app.NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(type.getChannelId());
    }

    public static List<NotificationChannel> getChannels(Context context){


        List<NotificationChannel> channels = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            channels = notificationManager.getNotificationChannels();
        }

        return channels;
    }

    public static void removeUnusedChannels(Context context, List<NotificationType> types){


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        List<NotificationChannel> channels = notificationManager.getNotificationChannels();
        for(NotificationChannel channel : channels){

            boolean found = false;
            String id = channel.getId();
            for(NotificationType type : types){
                if(type.getChannelId().equals(id)){
                    found = true;
                    break;
                }
            }
            if(!found){
                notificationManager.deleteNotificationChannel(id);
            }

        }

    }

    // ---------------------------------------------------------------------------------------------

    public static Notification buildNotification(Context context, NotificationNode node, NotificationType type) {


        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        Intent main = packageManager.getLaunchIntentForPackage(packageName);

        if(type.hasExtra()){
            main.putExtra(type.getExtra(), true);
        }

        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,node.requestCode,main, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,type.getChannelId())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(type.getContent(node))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setVibrate(new long[]{500,250,125,250})
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context,R.color.primary))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification);

        int soundResource = type.getSoundResource();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && soundResource != -1) {
            Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + type.getSoundResource());
            builder.setSound(sound);
        }
        return builder.build();
    }

}
