//
// ClickTheStringBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.generic;

import android.support.annotation.StringRes;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

public class ClickTheStringBehavior extends Behavior {


    @StringRes int id;
    String actionName = null;
    long ms;

    public ClickTheStringBehavior(@StringRes  int id){
        this.id = id;
        this.ms = 0;
        this.actionName = null;
    }
    public ClickTheStringBehavior(@StringRes  int id, long ms){
        this.id = id;
        this.ms = ms;
        this.actionName = null;
    }

    public ClickTheStringBehavior(@StringRes  int id, long ms, String name){
        this.id = id;
        this.ms = ms;
        this.actionName = name;
    }

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(ms);
        if(actionName != null && !actionName.trim().isEmpty()) {
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), actionName);
        }
        UI.click(ViewUtil.getString(id));
    }

}
