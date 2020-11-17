//
// TestBegin.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.tests;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

public class TestBegin extends BaseFragment {

    TextView begin;
    TextView number;

    int count = 3;
    boolean paused = false;
    boolean done = false;

    Handler handler;
    Runnable runnableCountdown = new Runnable() {
        @Override
        public void run() {
            count--;
            if (count >= 1) {
                number.animate()
                        .alpha(0)
                        .setDuration(100)
                        .withEndAction(runnableDisappear);
            } else if(paused) {
                done = true;
            } else {
                Study.openNextFragment();
            }
        }
    };

    Runnable runnableDisappear = new Runnable() {
        @Override
        public void run() {
            number.setAlpha(0);
            number.setText(String.valueOf(count));
            number.animate()
                    .alpha(1)
                    .setDuration(100)
                    .withEndAction(runnableReappear);
        }
    };

    Runnable runnableReappear = new Runnable() {
        @Override
        public void run() {
            number.setAlpha(1);
            handler.postDelayed(runnableCountdown,800);
        }
    };

    public TestBegin() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_begin, container, false);

        begin = view.findViewById(R.id.header);
        begin.setText(Html.fromHtml(ViewUtil.getString(R.string.testing_begin)));

        number = view.findViewById(R.id.number);
        number.setTypeface(Fonts.georgiaItalic);

        handler = new Handler();
        handler.postDelayed(runnableCountdown,900);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused && done) {
            Study.openNextFragment();
        } else {
            paused = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

}
