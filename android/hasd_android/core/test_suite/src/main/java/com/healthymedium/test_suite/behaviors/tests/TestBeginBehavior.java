//
// TestBeginBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.tests;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.GetTextViewMatcher;
import com.healthymedium.test_suite.utilities.UI;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class TestBeginBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "number_" + 3);
        int COUNT = 2;
        while(COUNT > 0){
            onView(withId(R.id.number)).check(matches(new GetTextViewMatcher()));
            int currNumber = Integer.parseInt(GetTextViewMatcher.value);
            if(currNumber == COUNT){
                UI.sleep(100);
                Capture.takeScreenshot(fragment, getClass().getSimpleName(), "number_" + COUNT);
                COUNT--;
            }
        }

        UI.sleep(900);


    }
}
