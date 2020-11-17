//
// AvailabilitySundayBed.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.availability;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.notifications.Proctor;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

@SuppressLint("ValidFragment")
public class AvailabilitySundayBed extends QuestionTime {

    CircadianClock clock;
    int minWakeTime = 4;
    int maxWakeTime = 24;
    boolean reschedule = false;

    public AvailabilitySundayBed() {
        super(true, ViewUtil.getString(R.string.availability_stop),"",null);
    }

    public AvailabilitySundayBed(boolean reschedule, int minWakeTime, int maxWakeTime) {
        super(true, ViewUtil.getString(R.string.availability_stop),"",null);
        this.minWakeTime = minWakeTime;
        this.maxWakeTime = maxWakeTime;
        this.reschedule = reschedule;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        clock = Study.getParticipant().getCircadianClock();
        if(time==null && !clock.hasBedRhythmChanged("Sunday")){
            time = clock.getRhythm("Saturday").getBedTime();
        } else if(time==null){
            time = clock.getRhythm("Sunday").getBedTime();
        }

        LocalTime wakeTime = clock.getRhythm("Sunday").getWakeTime();
        timeInput.placeRestrictions(wakeTime, minWakeTime, maxWakeTime);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNext.setEnabled(false);
                clock.getRhythm("Sunday").setBedTime(timeInput.getTime());
                AsyncLoader loader = new AsyncLoader();
                loader.execute();
            }
        });

        return view;
    }

    private class AsyncLoader extends AsyncTask<Void, Void, Void> {
        LoadingDialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = new LoadingDialog();
            loadingDialog.show(getFragmentManager(),"LoadingDialog");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Study.getParticipant().setCircadianClock(clock);
            Study.getParticipant().save();
            Study.getRestClient().submitWakeSleepSchedule();

            DateTime start = Study.getParticipant().getState().studyStartDate;
            if(start==null){
                start = DateTime.now();
            } else {
                start = start.withTime(LocalTime.now());
            }

            Study.getScheduler().scheduleTests(start,Study.getInstance().getParticipant());
            Study.getRestClient().submitTestSchedule();
            Study.getScheduler().scheduleNotifications(Study.getCurrentTestCycle(), reschedule);

            if(reschedule) {
                Proctor.refreshData(getContext());
            } else {
                Proctor.startService(getContext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void etc) {
            loadingDialog.dismiss();
            Study.openNextFragment();
        }
    }

    @Override
    public Object onDataCollection(){
        return null;
    }

}
