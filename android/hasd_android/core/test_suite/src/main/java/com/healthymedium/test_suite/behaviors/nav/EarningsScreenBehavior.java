//
// EarningsScreenBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.nav;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.healthymedium.test_suite.utilities.Matchers.nthChildOf;



public class EarningsScreenBehavior extends Behavior {



    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(800);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");


        try {
            onView(nthChildOf(withId(R.id.goalLayout), 0)).perform(scrollTo());
            UI.sleep(2000);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "44_goal");

            onView(nthChildOf(withId(R.id.goalLayout), 1)).perform(scrollTo());
            UI.sleep(500);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "2day_goal");

            onView(nthChildOf(withId(R.id.goalLayout), 2)).perform(scrollTo());
            UI.sleep(500);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "21_goal");
        } catch (Exception e) {
            UI.sleep(500);
        }





        onView(withId(R.id.viewFaqButton)).perform(scrollTo());
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "faq");

        //go to details
        onView(withId(R.id.viewDetailsButton)).perform(scrollTo());
        UI.sleep(500);
        UI.click(ViewUtil.getString(R.string.button_viewdetails));


    }
}
