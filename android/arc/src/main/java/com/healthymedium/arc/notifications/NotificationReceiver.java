//
// NotificationReceiver.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;

import static com.healthymedium.arc.notifications.NotificationManager.NOTIFICATION_ID;
import static com.healthymedium.arc.notifications.NotificationManager.NOTIFICATION_TYPE;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra(NOTIFICATION_ID,0);
        int type = intent.getIntExtra(NOTIFICATION_TYPE,1);

        PersistableBundle extras = new PersistableBundle();
        extras.putInt(NOTIFICATION_ID, id);
        extras.putInt(NOTIFICATION_TYPE, type);

        ComponentName serviceComponent = new ComponentName(context, NotificationNotifyJob.class);
        JobInfo.Builder builder = new JobInfo.Builder((id+1)*type, serviceComponent);
        builder.setRequiresDeviceIdle(false); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        builder.setPersisted(false); // set persistant across reboots
        builder.setExtras(extras);

        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(false);
            builder.setRequiresStorageNotLow(false);
        }

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}
