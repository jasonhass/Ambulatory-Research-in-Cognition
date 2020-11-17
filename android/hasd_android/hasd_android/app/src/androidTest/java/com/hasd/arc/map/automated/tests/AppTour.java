//
// AppTour.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.hasd.arc.map.automated.tests;


import com.hasd.arc.map.automated.AutomatedTestTemplate;
import com.healthymedium.arc.paths.home.HomeScreen;
import com.healthymedium.test_suite.behaviors.tutorials.AppTourHomeScreenBehavior;
import com.healthymedium.test_suite.core.TestBehavior;

public class AppTour extends AutomatedTestTemplate {
    @Override
    public void setup() {
        super.setup();

        TestBehavior.STEPS = 1;
        TestBehavior.screenMap.put(HomeScreen.class, new AppTourHomeScreenBehavior());
    }

}

