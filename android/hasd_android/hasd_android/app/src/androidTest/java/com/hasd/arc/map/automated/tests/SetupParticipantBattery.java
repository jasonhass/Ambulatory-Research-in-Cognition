//
// SetupParticipantBattery.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.hasd.arc.map.automated.tests;

import com.hasd.arc.map.StateMachine;
import com.hasd.arc.map.automated.AutomatedTestTemplate;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.study.Study;
import com.healthymedium.test_suite.core.TestBehavior;
import com.healthymedium.test_suite.data.ParticipantStates;

import static com.healthymedium.arc.paths.home.HomeScreen.HINT_TOUR;

public class SetupParticipantBattery extends AutomatedTestTemplate {

    @Override
    public void setup() {
        super.setup();

        Hints.load();
        Hints.markShown(HINT_TOUR);

        TestBehavior.state.lifecycle = StateMachine.LIFECYCLE_INIT;
        TestBehavior.state.path = StateMachine.PATH_SETUP_PARTICIPANT;
        Study.getParticipant().setState(ParticipantStates.getInitState());


        TestBehavior.login.id = "654321";
        TestBehavior.login.idConfirm = "654321";
        TestBehavior.login.authCode = "123456";

        TestBehavior.availability.wake = "07:00";
        TestBehavior.availability.bed = "19:00";

        TestBehavior.STEPS = 9;
    }

}

