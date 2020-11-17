//
// QuestionInterrupted.java
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

import com.healthymedium.arc.paths.questions.QuestionPolar;
import com.healthymedium.arc.study.Study;

// a yes or no question
@SuppressLint("ValidFragment")
public class QuestionInterrupted extends QuestionPolar {

    public QuestionInterrupted(boolean allowBack, String header, String subheader) {
        super(allowBack,header,subheader);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = super.onCreateView(inflater,container,savedInstanceState);

        return view;
    }

    @Override
    protected void onNextRequested() {
        Study.getCurrentTestSession().markInterrupted(answerIsYes);
        super.onNextRequested();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public Object onDataCollection(){
        return null;
    }
}
