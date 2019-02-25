//
// AbandonmentJobService.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.font.FontFactory;
import com.healthymedium.arc.heartbeat.HeartbeatJobService;
import com.healthymedium.arc.notifications.NotificationManager;

public class AbandonmentJobService extends JobService {

    public static final int CHANNEL_ABANDONMENT = 8800;
    static public final String CHANNEL_ABANDONMENT_ID = "ABANDONMENT";
    static public final String CHANNEL_ABANDONMENT_NAME = "Abandonment";
    static public final String CHANNEL_ABANDONMENT_DESC = "Checks to see if the current test session has been abandoned by the participant.";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Log.i("AbandonmentJobService","Running abandonment check");

        // First, check that we actually have a test running, and that we've hit our timeout for abandonment
        // Then, mark it abandoned, and send the data

        if(Study.isValid() == false)
        {
            Context context = Application.getInstance();

            // Most of this was copied from the SplashScreen.
            Study.initialize(context);
            NotificationManager.initialize(context);
            Application.getInstance().registerStudyComponents();
            Study.getInstance().load();
        }

        Participant participant = Study.getParticipant();
        StudyStateMachine stateMachine = Study.getStateMachine();
        if(participant.isCurrentlyInTestSession() && participant.checkForTestAbandonment())
        {
            Log.i("AbandonmentJobService", participant.getCurrentTestSession().getTestData().toString());
            stateMachine.abandonTest();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void scheduleSelf(Context context)
    {
        Log.i("AbandonmentJobService","Schedule abandonment job");
        Log.i("AbandonmentJobService", context.getClass().toString());
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(CHANNEL_ABANDONMENT);

        ComponentName serviceComponent = new ComponentName(context, AbandonmentJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(CHANNEL_ABANDONMENT, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        builder.setPersisted(false); // set persistant across reboots
        builder.setMinimumLatency(300000);  // minimum of 5 minutes before this service gets called
        builder.setOverrideDeadline(600000); // maximum of 10 minutes before this service gets called
        builder.setBackoffCriteria(300000, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(false);
            builder.setRequiresStorageNotLow(false);
        }

        jobScheduler.schedule(builder.build());
    }

    public static void unscheduleSelf(Context context)
    {
        Log.i("AbandonmentJobService","Cancelling abandonment job");
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(CHANNEL_ABANDONMENT);
    }
}
