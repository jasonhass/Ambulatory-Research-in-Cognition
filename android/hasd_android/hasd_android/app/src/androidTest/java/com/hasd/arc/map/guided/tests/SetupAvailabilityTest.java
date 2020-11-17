//
// SetupAvailabilityTest.java
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
import com.healthymedium.test_suite.core.TestBehavior;

public class SetupAvailabilityTest extends GuidedTestTemplate {

    @Override
    public void setup() {
        super.setup();
        TestBehavior.state.lifecycle = StateMachine.LIFECYCLE_INIT;
        TestBehavior.state.path = StateMachine.PATH_SETUP_AVAILABILITY;
    }

}

