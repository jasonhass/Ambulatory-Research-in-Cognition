//
// SampleSuite.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.hasd.arc.map.automated.suites;

import com.hasd.arc.map.automated.tests.SetupParticipantBattery;
import com.hasd.arc.map.automated.tests.TestFirstOfBaseline;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// this suite is for tests that simply follow a set path
// this suite runs the following tests in the order specified

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SetupParticipantBattery.class,
        TestFirstOfBaseline.class
})
public class SampleSuite {
    // leave empty
}
