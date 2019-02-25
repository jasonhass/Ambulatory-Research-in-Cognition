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

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.healthymedium.arc.utilities.PreferencesManager;

import java.util.Locale;

public class NotificationNotifyJob extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("NotificationNotifyJob","onStartJob");

        if(PreferencesManager.getInstance()==null){
            PreferencesManager.initialize(this);
        }

        if(PreferencesManager.getInstance().contains("language")) {
            String language = PreferencesManager.getInstance().getString("language", "en");
            String country = PreferencesManager.getInstance().getString("country", "US");
            Resources res = getResources();
            Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale(language, country));
            res.updateConfiguration(conf, res.getDisplayMetrics());
        }

        if(NotificationManager.getInstance()==null){
            NotificationManager.initialize(this);
        }
        int id = params.getExtras().getInt(NotificationManager.NOTIFICATION_ID,0);
        int type = params.getExtras().getInt(NotificationManager.NOTIFICATION_TYPE,1);

        if(type==NotificationManager.TEST_MISSED) {
            int count = PreferencesManager.getInstance().getInt("test_missed_count", 0);
            count++;
            if (count == 4) {
                NotificationManager.getInstance().notifyUser(id, type);
                count = 0;
            }
            NotificationManager.getInstance().removeNotification(id,type);
            PreferencesManager.getInstance().putInt("test_missed_count", count);
        } else {
            NotificationManager.getInstance().notifyUser(id, type);
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("NotificationNotifyJob","onStopJob");
        return false;
    }







}
