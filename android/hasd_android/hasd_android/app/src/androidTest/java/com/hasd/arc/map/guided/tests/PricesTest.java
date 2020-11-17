//
// PricesTest.java
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

public class PricesTest extends GuidedTestTemplate {

    @Override
    public void setup() {
        super.setup();

        TestBehavior.classes.stateMachine = GuidedStateMachine_PricesTest.class;
        setupDefaultParticipant();
    }

    public static class GuidedStateMachine_PricesTest extends StateMachine {

        @Override
        public void decidePath(){

        }

        @Override
        public void setupPath(){
            addPricesTest(0);
        }

        @Override
        public void endOfPath(){

        }
    }


}

