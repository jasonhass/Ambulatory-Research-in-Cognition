//
// SetupParticipantConfirmBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.setup;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;

import com.healthymedium.test_suite.core.TestConfig;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;

public class SetupParticipantConfirmBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment) {
        super.onOpened(fragment);

        String participantId = TestConfig.Participant.idConfirm;
        if(participantId==null){
            participantId = String.format("%09d",0);
        }

        ViewInteraction root = Espresso.onView(ViewMatchers.isRoot());
        int length = participantId.length();
        for(int i = 0; i<length; i++){
            char character = participantId.charAt(i);
            root.perform(ViewActions.pressKey(getKeyCode(character)));
            UI.sleep(100);
        }
        root.perform(ViewActions.closeSoftKeyboard());
        UI.click(R.id.buttonNext);
    }

    // unicode '0' = 48
    // keyEvent.Key_0 = 7
    int getKeyCode(char character){
        return Integer.valueOf(character)-41;
    }

}
