//
// AvailabilityWeekdayConfirm.java
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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.AvailabilityPathData;
import com.healthymedium.arc.paths.questions.QuestionPolar;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.CircadianRhythm;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.LocalTime;

public class AvailabilityWeekdayConfirm extends QuestionPolar {

    CircadianClock clock;
    AvailabilityPathData data;

    public AvailabilityWeekdayConfirm() {
        super(true,"","");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        clock = Study.getParticipant().getCircadianClock();
        data = (AvailabilityPathData) Study.getInstance().getCurrentSegmentData();

        CircadianRhythm monday = clock.getRhythm("Monday");
        String wakeTime = monday.getWakeTime().toString("h:mm a");
        String bedTime = monday.getBedTime().toString("h:mm a");

        String header = "";
//        String header = ViewUtil.getString(R.string.availability_same_tuesday_friday).replace("{TIME1}", wakeTime);
//        header = header.replace("{TIME2}", bedTime);



        textViewHeader.setText(Html.fromHtml(header));

        buttonNext.setText(ViewUtil.getString(R.string.button_submittime));
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.weekdaySame = answerIsYes;
                if(answerIsYes){
                    LocalTime bedTime = clock.getRhythm("Monday").getBedTime();
                    clock.getRhythm("Tuesday").setBedTime(bedTime);
                    clock.getRhythm("Wednesday").setBedTime(bedTime);
                    clock.getRhythm("Thursday").setBedTime(bedTime);
                    clock.getRhythm("Friday").setBedTime(bedTime);

                    LocalTime wakeTime = clock.getRhythm("Monday").getWakeTime();
                    clock.getRhythm("Tuesday").setWakeTime(wakeTime);
                    clock.getRhythm("Wednesday").setWakeTime(wakeTime);
                    clock.getRhythm("Thursday").setWakeTime(wakeTime);
                    clock.getRhythm("Friday").setWakeTime(wakeTime);
                }
                Study.setCurrentSegmentData(data);

                if(data.weekdaySame){
                    Study.openNextFragment(8);
                } else {
                    Study.openNextFragment();
                }
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
    protected void onNextButtonEnabled(boolean enabled){
        buttonNext.setText(ViewUtil.getString(R.string.button_next));
    }

    @Override
    public Object onDataCollection(){
        return null;
    }

}
