//
// TestInfoBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.tests;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.UI;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static org.hamcrest.Matchers.equalTo;

public class TestInfoBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

        UI.sleep(2000);

        // if showing hint, click on tutorial
        try {
            onView(withClassName(equalTo(HintPointer.class.getName()))).check(matches(isDisplayed()));
        } catch (Exception e) {
            UI.click(R.id.button);
            return;
        }
        UI.click(R.id.textViewTutorial);
    }

    @Override
    public void onPoppedBack(BaseFragment fragment) {
        super.onPoppedBack(fragment);

        UI.sleep(2000);

        try {
            UI.click(ViewUtil.getString(R.string.popup_gotit));
            UI.sleep(1000);
        } catch (Exception e) {

        }

        UI.click(R.id.button);

    }
}
