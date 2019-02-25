//
// QuestionDuration.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.custom.DurationInput;
import com.healthymedium.arc.paths.templates.QuestionTemplate;

import static java.lang.Math.floor;

@SuppressLint("ValidFragment")
public class QuestionDuration extends QuestionTemplate {

    DurationInput durationInput;
    int minutes = 5;
    boolean valid = true;

    public QuestionDuration(boolean allowBack, String header, String subheader) {
        super(allowBack,header,subheader,"SUBMIT TIME");
        type = "duration";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        durationInput = new DurationInput(getContext());
        durationInput.setListener(new DurationInput.Listener() {
            @Override
            public void onDurationChanged(int minutes) {
                response_time = System.currentTimeMillis();
                if(minutes==0){
                    buttonNext.setEnabled(false);
                    valid = false;
                } else if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                    valid = true;
                }
            }
        });
        content.addView(durationInput);
        content.setGravity(Gravity.CENTER);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        minutes = durationInput.getDuration();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(durationInput!=null){
            durationInput.setDuration(minutes);
            buttonNext.setEnabled(valid);
        }
    }

    @Override
    public Object onValueCollection(){
        if(durationInput==null){
            return null;
        }
        int hours = (int)floor(minutes/60);
        String value = minutes%60+" min";
        if(hours>0){
            value = String.valueOf(hours) +" hr " + value;
        }
        return value;
    }

}
