//
// GridTutorialBehavior.java
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
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.library.R;


public class GridTutorialBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

        UI.sleep(2000);
        UI.click(ViewUtil.getString(R.string.popup_gotit));
        UI.sleep(1000);
        UI.click(ViewUtil.getString(R.string.popup_tutorial_ready));
        UI.sleep(5000);
        UI.click(ViewUtil.getString(R.string.button_next));
        UI.sleep(1000);
        UI.click(R.id.tapThisF);
        UI.sleep(1000);
        UI.click(ViewUtil.getString(R.string.popup_tutorial_ready));
        UI.sleep(9000);
        UI.click(ViewUtil.getString(R.string.button_next));
        UI.sleep(1000);
        UI.click(ViewUtil.getString(R.string.popup_tutorial_ready));
        UI.sleep(1000);

        UI.click(R.id.image24);
        UI.sleep(1000);
        UI.click(R.id.image33);
        UI.sleep(1000);
        UI.click(R.id.image41);

        UI.sleep(2000);
        UI.click(R.id.endButton);

    }

}
