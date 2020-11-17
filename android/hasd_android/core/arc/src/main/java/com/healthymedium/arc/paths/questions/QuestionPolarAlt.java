//
// QuestionPolarAlt.java
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
import android.widget.CompoundButton;

import com.healthymedium.arc.ui.RadioButton;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.AltQuestionTemplate;
import com.healthymedium.arc.utilities.ViewUtil;

// a yes or no question
@SuppressLint("ValidFragment")
public class QuestionPolarAlt extends AltQuestionTemplate {

    RadioButton yesButton;
    RadioButton noButton;
    protected boolean answerIsYes;
    protected boolean answered;

    String yesText;
    String noText;

    public QuestionPolarAlt(boolean allowBack, String header, String subheader) {
        super(allowBack,header,subheader);
        type = "choice";
    }

    public QuestionPolarAlt(boolean allowBack, String header, String subheader, String yesAnswer, String noAnswer) {
        super(allowBack,header,subheader);
        type = "choice";
        yesText = yesAnswer;
        noText = noAnswer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        yesButton = new RadioButton(getContext());

        if (yesText == null) {
            yesButton.setText(ViewUtil.getString(R.string.radio_yes));
        } else {
            yesButton.setText(yesText);
        }

        yesButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                answered = true;
                if(b){
                    response_time = System.currentTimeMillis();
                    answerIsYes = true;
                    noButton.setChecked(false);
                }

                if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                    onNextButtonEnabled(true);
                    buttonNext.setText(ViewUtil.getString(R.string.button_next));
                }
            }
        });

        noButton = new RadioButton(getContext());

        if (noText == null) {
            noButton.setText(ViewUtil.getString(R.string.radio_no));
        } else {
            noButton.setText(noText);
        }

        noButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                answered = true;
                if(b){
                    response_time = System.currentTimeMillis();
                    answerIsYes = false;
                    yesButton.setChecked(false);
                }

                if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                    onNextButtonEnabled(true);
                    buttonNext.setText(ViewUtil.getString(R.string.button_next));
                }
            }
        });

        content.addView(yesButton);
        content.addView(noButton);
        buttonNext.setText(ViewUtil.getString(R.string.button_confirm));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(answered){
            if(answerIsYes){
                yesButton.setChecked(true);
            } else {
                noButton.setChecked(true);
            }
        }
    }

    @Override
    public Object onValueCollection(){
        if(!answered){
            return -1;
        }
        return answerIsYes ? 1:0;
    }

    @Override
    public String onTextValueCollection(){
        if(answered){
            return answerIsYes ? "Yes":"No";
        }
        return null;
    }
}
