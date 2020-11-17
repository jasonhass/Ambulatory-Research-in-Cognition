//
// BatteryOptimizationReminderBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.setup;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

public class BatteryOptimizationReminderBehavior extends Behavior {
    @Override

    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        UI.click(R.id.button);
        UI.sleep(4000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "dialog_opened");
        UI.sleep(1000);

    }
}
