//
// LandingTestReady.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.tests;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.paths.templates.LandingTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class LandingTestReady extends LandingTemplate {

    public LandingTestReady(String header) {
        super(header);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        Button button = new Button(getContext());
        button.setText("BEGIN");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Study.getCurrentTestSession().markStarted();
                Study.getParticipant().save();
                Study.getStateMachine().save();
                Study.openNextFragment();
            }
        });
        content.addView(button);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = ViewUtil.dpToPx(32);
        params.topMargin = ViewUtil.dpToPx(16);
        content.setLayoutParams(params);

        return view;
    }

}
