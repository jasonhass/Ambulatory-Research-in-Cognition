//
// AvailabilityMondayWake.java
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

import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.Study;

public class AvailabilityMondayWake extends QuestionTime {

    CircadianClock clock;

    public AvailabilityMondayWake() {
        super(true,"When do you usually<br/><b>wake up</b> on <b>Monday</b>?","",null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        clock = Study.getParticipant().getCircadianClock();
        if(clock==null){
            clock = new CircadianClock();
        }

        if(time==null && clock.hasWakeRhythmChanged("Monday")) {
            time = clock.getRhythm("Monday").getWakeTime();
        }

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clock.getRhythm("Monday").setWakeTime(timeInput.getTime());
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
