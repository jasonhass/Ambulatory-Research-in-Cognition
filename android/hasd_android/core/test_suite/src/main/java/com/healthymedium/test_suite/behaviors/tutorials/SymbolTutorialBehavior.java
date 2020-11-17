//
// SymbolTutorialBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.tutorials;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.UI;

public class SymbolTutorialBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

        UI.sleep(2000);
        UI.click(ViewUtil.getString(R.string.button_next));
        UI.sleep(1000);
        UI.click(ViewUtil.getString(R.string.button_next));
        UI.sleep(1000);
        UI.click(ViewUtil.getString(R.string.button_next));


        UI.sleep(1000);
        UI.click(R.id.symbolbutton_bottom1);
        UI.sleep(1000);
        UI.click(ViewUtil.getString(R.string.button_next));


        UI.sleep(1000);
        UI.click(R.id.symbolbutton_bottom2);
        UI.sleep(1000);
        UI.click(ViewUtil.getString(R.string.button_next));


        UI.sleep(1000);
        UI.click(R.id.symbolbutton_bottom1);

        // and wait for the end
        UI.sleep(2000);
        UI.click(R.id.endButton);

    }

}
