//
// FaqSubBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.nav;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.healthymedium.test_suite.utilities.Matchers.nthChildOf;


public class FaqSubBehavior extends Behavior {


    int size;

    public FaqSubBehavior(int size){
        this.size = size;
    }

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        for(int i =0; i < size; i++){
            onView(nthChildOf(withId(R.id.bottomLinearLayout), i)).perform(scrollTo());
            UI.sleep(2000);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "scrolled_" + i);
            onView(nthChildOf(withId(R.id.bottomLinearLayout), i)).perform(click());

            UI.sleep(500);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "clicked_" + i);


            onView(withId(R.id.textViewBack)).perform(scrollTo());
            UI.sleep(500);
            UI.click(R.id.textViewBack);

        }
        //returning to FaqHome
        UI.sleep(500);
        onView(withId(R.id.textViewBack)).perform(scrollTo());
        UI.sleep(500);
        UI.click(R.id.textViewBack);
    }
}
