//
// QuestionIntegerBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.questions;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.KeyEvent;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;


public class QuestionIntegerBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(1000);
        ViewInteraction root = Espresso.onView(ViewMatchers.isRoot());
        root.perform(ViewActions.pressKey(KeyEvent.KEYCODE_1));
        root.perform(ViewActions.closeSoftKeyboard());
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "pressed");
        UI.sleep(500);
        UI.click(R.id.buttonNext);
    }

}
