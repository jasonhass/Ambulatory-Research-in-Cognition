//
// AvailabilityOtherBed.java
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

import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.Study;

import org.joda.time.Hours;
import org.joda.time.LocalTime;

@SuppressLint("ValidFragment")
public class AvailabilityOtherBed extends QuestionTime {

    CircadianClock clock;
    String weekday;

    @SuppressLint("ValidFragment")
    public AvailabilityOtherBed(String weekday) {
        super(true,"When do you usually<br/><b>go to bed</b> on <b>"+weekday+"</b>?","",null);
        this.weekday = weekday;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        clock = Study.getParticipant().getCircadianClock();
        if(time==null && !clock.hasBedRhythmChanged(weekday)){
            int index = clock.getRhythmIndex(weekday)-1;
            time = clock.getRhythm(index).getBedTime();
        } else if(time==null){
            time = clock.getRhythm(weekday).getBedTime();
        }

        LocalTime wakeTime = clock.getRhythm(weekday).getWakeTime();
        timeInput.placeRestrictions(wakeTime, Hours.FOUR);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clock.getRhythm(weekday).setBedTime(timeInput.getTime());
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
