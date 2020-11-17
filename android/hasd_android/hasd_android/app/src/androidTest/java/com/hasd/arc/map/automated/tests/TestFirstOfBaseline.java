//
// TestFirstOfBaseline.java
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
import com.healthymedium.arc.paths.home.HomeScreen;
import com.healthymedium.test_suite.behaviors.generic.ClickTheStringBehavior;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.core.TestBehavior;
import com.healthymedium.arc.study.Study;
import com.healthymedium.test_suite.data.ParticipantStates;


import org.joda.time.DateTimeUtils;

import static com.healthymedium.arc.paths.home.HomeScreen.HINT_TOUR;
import static com.healthymedium.arc.paths.templates.TestInfoTemplate.HINT_GRID_TUTORIAL;
import static com.healthymedium.arc.paths.templates.TestInfoTemplate.HINT_PRICES_TUTORIAL;
import static com.healthymedium.arc.paths.templates.TestInfoTemplate.HINT_REPEAT_TUTORIAL;
import static com.healthymedium.arc.paths.templates.TestInfoTemplate.HINT_SYMBOL_TUTORIAL;

public class TestFirstOfBaseline extends AutomatedTestTemplate {

    @Override
    public void setup() {
        super.setup();

        Hints.load();
        Hints.markShown(HINT_TOUR);

        Hints.load();
        Hints.markShown(HINT_GRID_TUTORIAL);
        Hints.markShown(HINT_SYMBOL_TUTORIAL);
        Hints.markShown(HINT_PRICES_TUTORIAL);
        Hints.markShown(HINT_REPEAT_TUTORIAL);


        TestBehavior.STEPS = 46;
        TestBehavior.screenMap.put(HomeScreen.class, new ClickTheStringBehavior(R.string.button_begin,4000, "opened"));
        TestBehavior.state.path = StateMachine.PATH_TEST_FIRST_OF_BASELINE;
        Study.getParticipant().setState(ParticipantStates.getDefault(Study.getScheduler(), TEST_DATE_TIME));
        //time is fixed, need to be forward 1 second to be in the study
        DateTimeUtils.setCurrentMillisFixed(TEST_DATE_TIME.getMillis()+1);

    }

}

