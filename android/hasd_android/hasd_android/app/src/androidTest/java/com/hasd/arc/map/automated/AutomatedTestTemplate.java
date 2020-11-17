//
// AutomatedTestTemplate.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.hasd.arc.map.automated;


import com.hasd.arc.map.Application;
import com.healthymedium.arc.core.MainActivity;
import com.hasd.arc.map.StateMachine;
import com.healthymedium.arc.study.Study;
import com.healthymedium.test_suite.core.GenericTest;
import com.healthymedium.test_suite.core.TestBehavior;
import com.healthymedium.test_suite.behaviors.Behaviors;
import com.healthymedium.arc.core.Config;
import com.healthymedium.test_suite.data.ParticipantStates;
import com.healthymedium.test_suite.utilities.Capture;


import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Ignore;


@Ignore
public class AutomatedTestTemplate extends GenericTest {

    public static String TEST_DATE = "2021-01-01";
    public static DateTime TEST_DATE_TIME;



    
    @Override
    public void setup() {
        super.setup();
        Config.CHOOSE_LOCALE = false;
        Config.REST_BLACKHOLE = true;
        Config.REST_HEARTBEAT = false;

        TestBehavior.locales = Application.getInstance().getLocaleOptions();
        TestBehavior.testName = this.getClass().getSimpleName();
        Capture.INDEX = 0;

        TestBehavior.classes.activity = MainActivity.class;
        TestBehavior.classes.stateMachine = StateMachine.class;
        TestBehavior.state.lifecycle = StateMachine.LIFECYCLE_BASELINE;
        TestBehavior.state.path = StateMachine.PATH_TEST_NONE;

        StateMachine.AUTOMATED_TESTS_RANDOM_SEED = 1000;

        TEST_DATE_TIME = DateTime.parse(TEST_DATE);
        DateTimeUtils.setCurrentMillisFixed(TEST_DATE_TIME.getMillis());
        Study.getParticipant().setState(ParticipantStates.getDefault(Study.getScheduler(), TEST_DATE_TIME));
        Study.getParticipant().getState().currentTestSession = 0;
        Study.getParticipant().getState().currentTestDay = 1;

        TestBehavior.screenMap = Behaviors.getDefault();
    }

}

