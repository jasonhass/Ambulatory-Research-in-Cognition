//
// QuestionRadioButtons.java
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

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class QuestionRadioButtons extends QuestionTemplate {

    List<RadioButton> buttons;
    List<String> options;
    String selection;
    int lastIndex = -1;

    public QuestionRadioButtons(boolean allowBack, String header, String subheader, List<String> options) {
        super(allowBack,header,subheader);
        this.options = options;
        type = "choice";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        buttons = new ArrayList<>();
        int index=0;
        for(String option : options){
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(option);

            final int radioButtonIndex = index;
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    response_time = System.currentTimeMillis();
                    if(b) {
                        if (radioButtonIndex != lastIndex) {
                            if (lastIndex != -1) {
                                buttons.get(lastIndex).setChecked(false);
                            }
                            //buttons.get(radioButtonIndex).setChecked(true);
                            selection = options.get(radioButtonIndex);
                            lastIndex = radioButtonIndex;


                        }
                        if (!buttonNext.isEnabled()) {
                            buttonNext.setEnabled(true);
                        }
                    }
                }
            });
            buttons.add(radioButton);
            content.addView(radioButton);
            index++;
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(buttons.size()>0 && lastIndex>-1) {
            buttons.get(lastIndex).setChecked(true);
        }
    }

    @Override
    public Object onValueCollection(){
        return lastIndex;
    }

    @Override
    public String onTextValueCollection(){
        return selection;
    }

}
