//
// NotificationAlarmReceiver.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications;

import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.healthymedium.arc.utilities.Log;

import static com.healthymedium.arc.notifications.NotificationManager.NOTIFICATION_ID;
import static com.healthymedium.arc.notifications.NotificationManager.NOTIFICATION_TYPE;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        int id = intent.getIntExtra(NOTIFICATION_ID,0);
        int type = intent.getIntExtra(NOTIFICATION_TYPE,1);


        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(NotificationNotifyJob.build(context,id,type));
    }
}
