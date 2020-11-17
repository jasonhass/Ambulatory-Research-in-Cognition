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

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.Rating;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class QuestionRating extends QuestionTemplate {

    private static final String HINT_QUESTION_RATING = "HINT_QUESTION_RATING";

    HintPointer pointer;
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
                if(pointer!=null){
                    pointer.dismiss();
                    pointer = null;
                }
            }
        });

        content.addView(rating);

        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        if(!Hints.hasBeenShown(HINT_QUESTION_RATING)){
            pointer = new HintPointer(getMainActivity(),rating.getSeekBar(),true,true);
            pointer.setText(ViewUtil.getString(R.string.popup_drag));
            pointer.show();
            Hints.markShown(HINT_QUESTION_RATING);
        }

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
