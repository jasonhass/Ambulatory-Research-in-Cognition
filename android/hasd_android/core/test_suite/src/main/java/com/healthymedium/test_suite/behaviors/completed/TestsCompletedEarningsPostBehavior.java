//
// TestsCompletedEarningsPostBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.completed;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.healthymedium.test_suite.utilities.Matchers.nthChildOf;


public class TestsCompletedEarningsPostBehavior extends Behavior {


    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        //In order to prevent crash, testSession reset to 1
        Study.getParticipant().getState().currentTestSession =1;
        UI.sleep(2000);


        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        for(int i = 0; i < 3; i++){
            onView(nthChildOf(withId(R.id.goalLayout), i)).perform(scrollTo());
            UI.sleep(1500);
        }

        onView(withId(R.id.linearLayoutMainBody))
                .perform(swipeUp());
        UI.sleep(1500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "scrolled");
        UI.sleep(500);
        UI.click(R.id.buttonNext);

    }

}
