//
// GridTestBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.tests;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;

import static android.support.test.espresso.action.ViewActions.click;

import com.healthymedium.arc.library.R;


public class GridTestBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        try {
            UI.click(R.id.image11);
            UI.sleep(500);
            UI.click(R.id.image22);
            UI.sleep(500);
            UI.click(R.id.image33);
            UI.sleep(500);
        } catch (Exception e) {

        }

        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "clicked");
        UI.sleep(2000);
    }

}
