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
import android.os.Build;


public class AbandonmentJobService extends JobService {

    static private final String tag = "AbandonmentJobService";
    public static final int jobId = 8800;

    @Override
    public boolean onStartJob(JobParameters jobParameters){


        // First, check that we actually have a test running, and that we've hit our timeout for abandonment
        // Then, mark it abandoned, and send the data

        Participant participant = Study.getParticipant();
        StateMachine stateMachine = Study.getStateMachine();

        if(participant.isCurrentlyInTestSession() && participant.checkForTestAbandonment()){

            stateMachine.abandonTest();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters){
        return false;
    }

    public static void scheduleSelf(Context context){


        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);

        ComponentName serviceComponent = new ComponentName(context, AbandonmentJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);       // device should be idle
        builder.setRequiresCharging(false);         // we don't care if the device is charging or not
        builder.setPersisted(false);                // set persistent across reboots
        builder.setMinimumLatency(300000);          // minimum of 5 minutes before this service gets called
        builder.setOverrideDeadline(600000);        // maximum of 10 minutes before this service gets called
        builder.setBackoffCriteria(300000, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setRequiresBatteryNotLow(false);
            builder.setRequiresStorageNotLow(false);
        }

        jobScheduler.schedule(builder.build());
    }

    public static void unscheduleSelf(Context context){

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);
    }
}
