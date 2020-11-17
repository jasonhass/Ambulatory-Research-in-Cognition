//
// QuestionTimeBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.questions;

import android.support.test.espresso.ViewAction;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.test_suite.utilities.ViewActions;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;

import static android.support.test.espresso.action.ViewActions.click;


public class QuestionTimeBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        ViewAction setTime = ViewActions.setTime(8,0);
        UI.getTimePicker().perform(setTime);
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "setTime");
        UI.sleep(500);
        UI.click(R.id.buttonNext);
    }

}
