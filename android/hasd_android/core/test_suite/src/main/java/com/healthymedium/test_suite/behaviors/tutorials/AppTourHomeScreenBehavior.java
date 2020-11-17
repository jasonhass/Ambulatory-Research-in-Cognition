//
// AppTourHomeScreenBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.tutorials;



import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

public class AppTourHomeScreenBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment) {
        super.onOpened(fragment);

        UI.sleep(5000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");

        UI.click(ViewUtil.getString(com.healthymedium.arc.library.R.string.popup_tour));
        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "popup_tour");
        UI.sleep(500);

        UI.click(ViewUtil.getString(com.healthymedium.arc.library.R.string.button_next));
        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "next_1");
        UI.sleep(500);

        UI.click(ViewUtil.getString(com.healthymedium.arc.library.R.string.button_next));
        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "next_2");
        UI.sleep(500);

        UI.click(ViewUtil.getString(com.healthymedium.arc.library.R.string.button_next));
        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "next_3");
        UI.sleep(500);

        UI.click(ViewUtil.getString(com.healthymedium.arc.library.R.string.button_next));
        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "next_4");
        UI.sleep(500);
    }
}
