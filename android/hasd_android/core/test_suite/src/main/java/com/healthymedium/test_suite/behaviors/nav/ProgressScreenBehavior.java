//
// ProgressScreenBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.nav;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;



public class ProgressScreenBehavior extends Behavior {


    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "session_week");
        onView(withId(R.id.viewFaqButton)).perform(scrollTo());
        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "study");
        UI.sleep(250);

        //The automated test will crash if testDay = 0 and testCycle = 0
        if ( Study.getParticipant().getState().currentTestDay == 0 && Study.getParticipant().getState().currentTestCycle == 0){
            Study.getParticipant().getState().currentTestDay = 1;
            Study.getParticipant().getState().currentTestCycle = 1;
        }



        UI.click(ViewUtil.getString(R.string.resources_nav_home));
    }
}
