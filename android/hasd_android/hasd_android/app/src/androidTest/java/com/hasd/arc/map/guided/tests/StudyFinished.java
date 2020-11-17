//
// StudyFinished.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.hasd.arc.map.guided.tests;

import com.hasd.arc.map.StateMachine;
import com.hasd.arc.map.guided.GuidedTestTemplate;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.test_suite.core.TestBehavior;
import com.healthymedium.test_suite.data.CircadianClocks;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

public class StudyFinished extends GuidedTestTemplate {

    @Override
    public void setup() {
        super.setup();

        TestBehavior.classes.stateMachine = GuidedStateMachine_StudyFinished.class;

        DateTime now = DateTime.now();
        DateTime startDate = DateTime.now().minusYears(5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis());

        setupDefaultParticipant();

        Study.getParticipant().getState().studyStartDate = startDate;
        Study.getParticipant().getState().currentTestCycle = Study.getParticipant().getState().testCycles.size();

        DateTimeUtils.setCurrentMillisFixed(now.getMillis());
    }

    public static class GuidedStateMachine_StudyFinished extends StateMachine {

        @Override
        public void decidePath(){

        }

        @Override
        public void setupPath(){
            addStudyFinishedScreen();
            addTestLandingPage();
        }

        @Override
        public void endOfPath(){

        }

    }


}

