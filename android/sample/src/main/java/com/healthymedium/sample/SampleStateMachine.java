//
// SampleStateMachine.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.sample;

import android.transition.Slide;
import android.view.Gravity;

import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.StudyStateMachine;

public class SampleStateMachine extends StudyStateMachine {

    Slide slideIn;
    Slide slideOut;

    public SampleStateMachine() {
        slideIn = new Slide(Gravity.RIGHT);
        slideIn.setStartDelay(200);

        slideOut = new Slide(Gravity.LEFT);
        slideOut.setDuration(200);
    }

    private void enableTransition(PathSegment segment){
        int size = segment.fragments.size();
        for(int i=0;i<size;i++){
            segment.fragments.get(i).setEnterTransition(slideIn);
            segment.fragments.get(i).setEnterTransition(slideOut);
        }
    }

    @Override
    public void decidePath(){

    }

    @Override
    protected void setupPath(){
        addContextSurvey();
    }

}
