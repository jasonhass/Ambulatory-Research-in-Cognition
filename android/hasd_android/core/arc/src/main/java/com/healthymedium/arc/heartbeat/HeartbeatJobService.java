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

import com.healthymedium.arc.core.Application;


import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.StateMachine;

import static com.healthymedium.arc.study.Study.getRestClient;

public class HeartbeatJobService extends JobService {

    private static final String tag = "HeartbeatJobService";
    JobParameters params;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.params = jobParameters;


        if(!Config.REST_HEARTBEAT){
            if(!Application.getInstance().isVisible()) {
                checkUploadQueue();
            }
            return true;
        }


        String participantId = Study.getParticipant().getId();
        HeartbeatManager.getInstance().tryHeartbeat(getRestClient(), participantId, new HeartbeatManager.Listener() {
            @Override
            public void onSuccess(boolean tried) {
                if(!Application.getInstance().isVisible()) {
                    checkUploadQueue();
                }
            }

            @Override
            public void onFailure() {
                jobFinished(params,false);
            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }

    private void checkUploadQueue(){

        RestClient client = Study.getRestClient();
        if(client.isUploadQueueEmpty()){
            checkState();
            jobFinished(params,false);
            return;
        }

        client.addUploadListener(new RestClient.UploadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {
                Study.getRestClient().removeUploadListener(this);
                checkState();
                jobFinished(params,false);
            }
        });
        client.popQueue();
    }

    private void checkState(){

        StateMachine stateMachine = Study.getStateMachine();
        stateMachine.decidePath();
        stateMachine.save(true);
    }








}
