//
// UI.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.utilities;

import android.support.annotation.IdRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.widget.TimePicker;

import org.hamcrest.Matchers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;

public class UI {

    public static void click(@IdRes int id) {
        onView(ViewMatchers.withId(id)).perform(ViewActions.click());
    }

    // ---------------------------------------------------------------------------------------------

    public static ViewInteraction getTimePicker() {
        return onView(withClassName(Matchers.equalTo(TimePicker.class.getName())));
    }

    // ---------------------------------------------------------------------------------------------

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
