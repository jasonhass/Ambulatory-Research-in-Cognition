//
// AvailabilitySaturdayWake.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.availability;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.AvailabilityPathData;
import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

public class AvailabilitySaturdayWake extends QuestionTime {

    CircadianClock clock;
    AvailabilityPathData data;

    public AvailabilitySaturdayWake() {
        super(true, ViewUtil.getString(R.string.availability_start),"",null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        clock = Study.getParticipant().getCircadianClock();
        data = (AvailabilityPathData) Study.getCurrentSegmentData();

        if(time==null && !clock.hasWakeRhythmChanged("Saturday")){
            time = clock.getRhythm("Friday").getWakeTime();
        } else if(time==null){
            time = clock.getRhythm("Saturday").getWakeTime();
        }

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clock.getRhythm("Saturday").setWakeTime(timeInput.getTime());
                Study.getInstance().setCurrentSegmentData(data);
                Study.openNextFragment();
            }
        });

        return view;
    }

    @Override
    protected void onBackRequested() {

        if(data.weekdaySame){
            Study.getInstance().openPreviousFragment(8);
        } else {
            Study.getInstance().openPreviousFragment();
        }
    }

    @Override
    public void onPause() {
        Study.getParticipant().save();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        int skips = (data.weekdaySame) ? 8:0;
        getMainActivity().enableBackPress(true,true,skips);
    }

    @Override
    public Object onDataCollection(){
        return null;
    }

}
