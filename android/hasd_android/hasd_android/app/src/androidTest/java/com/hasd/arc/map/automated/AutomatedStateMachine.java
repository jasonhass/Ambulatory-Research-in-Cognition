//
// AutomatedStateMachine.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.hasd.arc.map.automated;

import com.healthymedium.arc.study.StateMachine;
import com.healthymedium.test_suite.paths.FinishedScreen;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.PathSegmentData;

import java.util.ArrayList;
import java.util.List;

public class AutomatedStateMachine extends StateMachine {

    boolean yolo = false;

    public AutomatedStateMachine() {

    }

    @Override
    public void decidePath(){
        if(yolo){
            state.lifecycle = -1;
            state.currentPath = -1;

            List<BaseFragment> fragments = new ArrayList<>();
            fragments.add(new FinishedScreen());
            PathSegment segment = new PathSegment(fragments, PathSegmentData.class);
            cache.segments.add(segment);
        }
        yolo = true;
    }

}
