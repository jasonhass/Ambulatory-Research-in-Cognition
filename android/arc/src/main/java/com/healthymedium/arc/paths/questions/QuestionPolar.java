//
// QuestionPolar.java
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

import com.healthymedium.arc.custom.RadioButton;
import com.healthymedium.arc.paths.templates.QuestionTemplate;

// a yes or no question
@SuppressLint("ValidFragment")
public class QuestionPolar extends QuestionTemplate {

    RadioButton yesButton;
    RadioButton noButton;
    protected boolean answerIsYes;
    protected boolean answered;

    public QuestionPolar(boolean allowBack, String header, String subheader) {
        super(allowBack,header,subheader);
        type = "choice";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        yesButton = new RadioButton(getContext());
        yesButton.setText("Yes");
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
                    buttonNext.setText("NEXT");
                }
            }
        });

        noButton = new RadioButton(getContext());
        noButton.setText("No");
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
                    buttonNext.setText("NEXT");
                }
            }
        });

        content.addView(yesButton);
        content.addView(noButton);
        buttonNext.setText("CHOOSE ANSWER");

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
