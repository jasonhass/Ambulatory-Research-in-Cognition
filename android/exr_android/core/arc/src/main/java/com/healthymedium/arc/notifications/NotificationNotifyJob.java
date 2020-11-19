//
// NotificationNotifyJob.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import com.healthymedium.arc.utilities.Log;

// This is only used for notifications that are not proctored.

public class NotificationNotifyJob extends JobService {

    static private final String tag = "NotificationNotifyJob";

    @Override
    public boolean onStartJob(JobParameters params) {


        // extract parameters
        int id = params.getExtras().getInt(NotificationManager.NOTIFICATION_ID,0);
        int typeId = params.getExtras().getInt(NotificationManager.NOTIFICATION_TYPE,1);

        NotificationManager manager = NotificationManager.getInstance();

        // grab the node
        NotificationNode node = manager.getNode(typeId, id);
        if(node==null){

            return false;
        }

        manager.notifyUser(node);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }

    public static JobInfo build(Context context, int id, int type){

        PersistableBundle extras = new PersistableBundle();
        extras.putInt(NotificationManager.NOTIFICATION_ID, id);
        extras.putInt(NotificationManager.NOTIFICATION_TYPE, type);

        NotificationNode node = NotificationManager.getInstance().getNode(type, id);

        ComponentName serviceComponent = new ComponentName(context, NotificationNotifyJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(node.getNotifyId(), serviceComponent);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        builder.setPersisted(false);
        builder.setExtras(extras);

        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(false);
            builder.setRequiresStorageNotLow(false);
        }
        JobInfo info = builder.build();
        return info;
    }

}
