//
// PricesTest.java
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
import com.healthymedium.test_suite.core.TestBehavior;


import static com.healthymedium.arc.paths.home.HomeScreen.HINT_TOUR;
import static com.healthymedium.arc.paths.templates.TestInfoTemplate.HINT_PRICES_TUTORIAL;
import static com.healthymedium.arc.paths.templates.TestInfoTemplate.HINT_REPEAT_TUTORIAL;

public class PricesTest extends AutomatedTestTemplate {

    @Override
    public void setup() {
        super.setup();

        Hints.load();
        Hints.markShown(HINT_TOUR);
        Hints.markShown(HINT_PRICES_TUTORIAL);
        Hints.markShown(HINT_REPEAT_TUTORIAL);

        TestBehavior.STEPS = 6;
        TestBehavior.classes.stateMachine = AutomatedStateMachine_PricesTest.class;
    }

    public static class AutomatedStateMachine_PricesTest extends StateMachine {

        boolean pass = true;

        @Override
        public void decidePath(){

        }

        @Override
        public void setupPath(){
            if(pass){
                addPricesTest(1);
                pass = false;
            }
            else {
                setPathNoTests();
            }
        }

        @Override
        public void endOfPath(){

        }

    }

}

