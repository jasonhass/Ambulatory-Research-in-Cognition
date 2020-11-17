//
// TestProgress.java
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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.ui.CircleProgressView;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class TestProgress extends BaseFragment {

    CircleProgressView circleProgressView;

    TextView textViewHeader;
    String headerText;

    TextView textViewSubHeader;
    String subheaderText;

    TextView textViewThree;
    TextView textViewTextNumber;
    String testNumber;

    int percentageFrom;
    int percentageTo;

    boolean wasPaused = false;
    boolean ready = false;

    public TestProgress(String header, int index) {
        this.headerText = header;
        this.testNumber = String.valueOf(index+1);
        switch (index){
            case 0:
                percentageFrom = 0;
                percentageTo = 33;
                subheaderText = ViewUtil.getString(R.string.testing_loading);
                break;
            case 1:
                percentageFrom = 33;
                percentageTo = 66;
                subheaderText = ViewUtil.getString(R.string.testing_loading);
                break;
            case 2:
                percentageFrom = 66;
                percentageTo = 100;
                subheaderText = ViewUtil.getString(R.string.testing_done);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_progress, container, false);

        textViewTextNumber = view.findViewById(R.id.textViewTextNumber);
        textViewTextNumber.setTypeface(Fonts.georgia);
        textViewTextNumber.setText(testNumber);

        textViewThree = view.findViewById(R.id.textViewThree);
        textViewThree.setTypeface(Fonts.georgia);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setTypeface(Fonts.robotoBold);
        textViewHeader.setText(Html.fromHtml(headerText));

        textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        textViewSubHeader.setTypeface(Fonts.georgiaItalic);
        textViewSubHeader.setText(Html.fromHtml(subheaderText));

        circleProgressView = view.findViewById(R.id.circleProgressView);
        circleProgressView.setProgress(percentageFrom,false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(wasPaused) {
            checkExit();
            return;
        }

        circleProgressView.setProgress(percentageTo,true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ready = true;
                checkExit();
            }
        },5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        wasPaused = true;
    }

    private void checkExit() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ready && isVisible()) {
                    Study.openNextFragment();
                }
            }
        },100);
    }


}
