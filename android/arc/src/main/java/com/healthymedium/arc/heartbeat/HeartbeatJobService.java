//
// HeartbeatJobService.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.heartbeat;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.StudyStateMachine;
import com.healthymedium.arc.utilities.PreferencesManager;

import java.util.Locale;

import static com.healthymedium.arc.study.Study.getRestClient;

public class HeartbeatJobService extends JobService {

    JobParameters params;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.params = jobParameters;
        Log.i("HeartbeatJobService","onStartJob");

        Log.i("HeartbeatJobService","initializing manager");
        if(HeartbeatManager.getInstance()==null){
            HeartbeatManager.initialize(this);
        }

        Log.i("HeartbeatJobService","initializing study");
        Study.initialize(getApplicationContext());
        Application.getInstance().registerStudyComponents();
        Study.getInstance().load();

        if(!Study.getStateMachine().isIdle()){
            return false;
        }

        Log.i("HeartbeatJobService","setting locale");
        String language = PreferencesManager.getInstance().getString("language","en");
        String country = PreferencesManager.getInstance().getString("country","US");
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language,country));
        res.updateConfiguration(conf, res.getDisplayMetrics());

        HeartbeatManager.getInstance().startNotification();

        if(!Config.REST_HEARTBEAT){
            checkUploadQueue();
            return true;
        }

        Log.i("HeartbeatJobService","trying heartbeat");
        String participantId = Study.getParticipant().getId();
        HeartbeatManager.getInstance().tryHeartbeat(getRestClient(), participantId, new HeartbeatManager.Listener() {
            @Override
            public void onSuccess(boolean tried) {
                checkUploadQueue();
            }

            @Override
            public void onFailure() {
                HeartbeatManager.getInstance().stopNotification();
                jobFinished(params,false);
            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("HeartbeatJobService","onStopJob");
        return false;
    }

    private void checkUploadQueue(){
        Log.i("HeartbeatJobService","checkUploadQueue");
        RestClient client = Study.getRestClient();
        if(client.isUploadQueueEmpty()){
            checkState();
            HeartbeatManager.getInstance().stopNotification();
            jobFinished(params,false);
        } else {
            client.setUploadListener(new RestClient.UploadListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onStop() {
                    Study.getRestClient().removeUploadListener();
                    checkState();
                    HeartbeatManager.getInstance().stopNotification();
                    jobFinished(params,false);
                }
            });
            client.popQueue();
        }
    }

    private void checkState(){
        Log.i("HeartbeatJobService","checkState");
        StudyStateMachine stateMachine = Study.getStateMachine();
        stateMachine.decidePath();
        stateMachine.save();
    }

}
