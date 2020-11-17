//
// GuidedTestTemplate.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.hasd.arc.map.guided;

import com.healthymedium.arc.core.MainActivity;
import com.hasd.arc.map.StateMachine;
import com.healthymedium.test_suite.core.GuidedTest;
import com.healthymedium.test_suite.core.TestBehavior;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.study.Study;
import com.healthymedium.test_suite.data.ParticipantStates;

import org.junit.Ignore;

@Ignore
public class GuidedTestTemplate extends GuidedTest {

    @Override
    public void setup() {
        super.setup();
        Config.CHOOSE_LOCALE = false;
        Config.REST_BLACKHOLE = true;

        TestBehavior.classes.activity = MainActivity.class;
        TestBehavior.classes.stateMachine = com.hasd.arc.map.guided.GuidedStateMachine.class;
        TestBehavior.state.lifecycle = StateMachine.LIFECYCLE_INIT;
        TestBehavior.state.path = StateMachine.PATH_SETUP_PARTICIPANT;

    }

    protected void setupDefaultParticipant(){
        Study.getParticipant().setState(ParticipantStates.getDefault(Study.getScheduler()));
    }

}

