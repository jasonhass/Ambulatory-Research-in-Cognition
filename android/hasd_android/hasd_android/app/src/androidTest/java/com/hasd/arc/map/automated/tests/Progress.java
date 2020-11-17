//
// Progress.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.hasd.arc.map.automated.tests;

import com.hasd.arc.map.automated.AutomatedTestTemplate;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.study.Study;
import com.healthymedium.test_suite.behaviors.Behaviors;
import com.healthymedium.test_suite.core.TestBehavior;

import java.util.HashMap;

import static com.healthymedium.arc.paths.home.HomeScreen.HINT_TOUR;

public class Progress extends AutomatedTestTemplate {



    @Override
    public void setup() {
        super.setup();

        Hints.load();
        Hints.markShown(HINT_TOUR);

        Study.getParticipant().getState().studyStartDate = TEST_DATE_TIME;
        Study.getParticipant().getState().currentTestSession = 1;
        Study.getParticipant().getState().currentTestDay = 1;
        Study.getParticipant().getState().currentTestCycle = 1;


        TestBehavior.STEPS = 24;
        TestBehavior.screenMap = new HashMap<>();
        TestBehavior.screenMap.putAll(Behaviors.getProgress(TEST_DATE_TIME));
    }
}

