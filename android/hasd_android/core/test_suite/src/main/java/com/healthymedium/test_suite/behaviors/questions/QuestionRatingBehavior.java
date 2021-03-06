//
// QuestionRatingBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.questions;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.healthymedium.test_suite.utilities.ViewActions.setProgress;

public class QuestionRatingBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        // grab the seekbar inside the rating view and set it
       onView(withId(R.id.seekbarRating)).perform(setProgress(25));

        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "dragged");
        UI.sleep(500);
        UI.click(R.id.buttonNext);
    }

}
