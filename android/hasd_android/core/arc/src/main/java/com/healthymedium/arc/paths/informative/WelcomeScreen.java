//
// WelcomeScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.informative;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.healthymedium.arc.ui.RadioButton;
import com.healthymedium.arc.paths.templates.InfoTemplate;

@SuppressLint("ValidFragment")
public class WelcomeScreen extends InfoTemplate {

    RadioButton understandButton;

    public WelcomeScreen(boolean allowBack, String header, String body, @Nullable String buttonText) {
        super(allowBack,header,body, buttonText);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        button.setEnabled(false);

        understandButton = new RadioButton(getContext());
        understandButton.setText("I understand");
        understandButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!button.isEnabled()){
                    button.setEnabled(true);
                    button.setText("NEXT");
                }
            }
        });

        content.addView(understandButton);

        return view;
    }
}
