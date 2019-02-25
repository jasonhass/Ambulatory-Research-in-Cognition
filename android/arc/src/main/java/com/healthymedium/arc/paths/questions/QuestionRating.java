//
// QuestionRating.java
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.healthymedium.arc.custom.Rating;
import com.healthymedium.arc.paths.templates.QuestionTemplate;

@SuppressLint("ValidFragment")
public class QuestionRating extends QuestionTemplate {

    float value = 0.5f;
    Rating rating;
    String high;
    String low;

    public QuestionRating(boolean allowBack, String header, String subheader, String low, String high) {
        super(allowBack,header,subheader);
        this.high = high;
        this.low = low;
        type = "slider";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        rating = new Rating(getContext());
        rating.setLowText(low);
        rating.setHighText(high);
        rating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                response_time = System.currentTimeMillis();
            }
        });

        content.addView(rating);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        value = rating.getValue();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(rating !=null) {
            rating.setValue(value);
        }
    }

    @Override
    public Object onValueCollection(){
        if(rating!=null){
            return rating.getValue();
        }
        return null;
    }

}
