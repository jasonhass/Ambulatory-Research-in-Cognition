//
// AvailabilityBed.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.availability;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.LocalTime;

@SuppressLint("ValidFragment")
public class AvailabilityBed extends QuestionTime {

    CircadianClock clock;
    int minWakeTime = 4;
    int maxWakeTime = 24;

    public AvailabilityBed() {
        super(true, ViewUtil.getString(R.string.availability_stop),"",null);
    }

    public AvailabilityBed(int minWakeTime, int maxWakeTime) {
        super(true, ViewUtil.getString(R.string.availability_stop),"",null);
        this.minWakeTime = minWakeTime;
        this.maxWakeTime = maxWakeTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        clock = Study.getParticipant().getCircadianClock();
        if(time==null && clock.hasBedRhythmChanged("Monday")) {
            time = clock.getRhythm("Monday").getBedTime();
        }

        LocalTime wakeTime = clock.getRhythm("Monday").getWakeTime();
        timeInput.placeRestrictions(wakeTime, minWakeTime, maxWakeTime);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clock.getRhythm("Monday").setBedTime(timeInput.getTime());

                // Set all of the remaining days to the same wake and sleep times
                LocalTime bedTime = clock.getRhythm("Monday").getBedTime();
                clock.getRhythm("Tuesday").setBedTime(bedTime);
                clock.getRhythm("Wednesday").setBedTime(bedTime);
                clock.getRhythm("Thursday").setBedTime(bedTime);
                clock.getRhythm("Friday").setBedTime(bedTime);
                clock.getRhythm("Saturday").setBedTime(bedTime);
                clock.getRhythm("Sunday").setBedTime(bedTime);

                LocalTime wakeTime = clock.getRhythm("Monday").getWakeTime();
                clock.getRhythm("Tuesday").setWakeTime(wakeTime);
                clock.getRhythm("Wednesday").setWakeTime(wakeTime);
                clock.getRhythm("Thursday").setWakeTime(wakeTime);
                clock.getRhythm("Friday").setWakeTime(wakeTime);
                clock.getRhythm("Saturday").setWakeTime(wakeTime);
                clock.getRhythm("Sunday").setWakeTime(wakeTime);

                Study.openNextFragment();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        Study.getParticipant().save();
        super.onPause();
    }

    @Override
    public Object onDataCollection(){
        return null;
    }

}
