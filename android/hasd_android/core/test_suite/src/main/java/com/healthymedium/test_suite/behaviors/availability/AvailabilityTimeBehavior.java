//
// AvailabilityTimeBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.availability;

import android.support.test.espresso.ViewAction;

import com.healthymedium.arc.paths.availability.AvailabilityBed;
import com.healthymedium.arc.paths.availability.AvailabilityWake;
import com.healthymedium.test_suite.core.TestBehavior;
import com.healthymedium.test_suite.data.CircadianClocks;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.ViewActions;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.CircadianClock;

import org.junit.Assert;

import org.joda.time.LocalTime;

public class AvailabilityTimeBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");

        CircadianClock clock = CircadianClocks.get(TestBehavior.availability.wake,TestBehavior.availability.bed);
        LocalTime time = null;

        boolean wake = false;

        Class tClass = fragment.getClass();

        if(tClass.equals(AvailabilityWake.class)){
            wake = true;

        } else if(tClass.equals(AvailabilityBed.class)){
            wake = false;
        }

        if(wake){
            time = clock.getRhythm(0).getWakeTime();
        } else {
            time = clock.getRhythm(0).getBedTime();
        }

        Assert.assertNotNull("invalid local time",time);

        ViewAction setTime = ViewActions.setTime(time.getHourOfDay(),time.getMinuteOfHour());
        UI.getTimePicker().perform(setTime);
        UI.sleep(400);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "setTime");
        UI.click(R.id.buttonNext);
    }

}
