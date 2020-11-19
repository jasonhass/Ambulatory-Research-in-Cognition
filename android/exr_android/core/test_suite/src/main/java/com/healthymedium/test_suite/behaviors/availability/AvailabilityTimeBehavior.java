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

import com.healthymedium.test_suite.core.TestConfig;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.ViewActions;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.availability.AvailabilityOtherBed;
import com.healthymedium.arc.paths.availability.AvailabilityOtherWake;
import com.healthymedium.arc.paths.availability.AvailabilitySaturdayBed;
import com.healthymedium.arc.paths.availability.AvailabilitySaturdayWake;
import com.healthymedium.arc.paths.availability.AvailabilitySundayBed;
import com.healthymedium.arc.paths.availability.AvailabilitySundayWake;
import com.healthymedium.arc.study.CircadianClock;

import junit.framework.Assert;

import org.joda.time.LocalTime;

public class AvailabilityTimeBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

        CircadianClock clock = TestConfig.circadianClock;
        LocalTime time = null;

        boolean wake = false;
        String weekday = null;

        Class tClass = fragment.getClass();

//        if(tClass.equals(AvailabilityMondayWake.class)){
//            weekday = "Monday";
//            wake = true;
//
//        } else if(tClass.equals(AvailabilityMondayBed.class)){
//            weekday = "Monday";
//
//        } else
        if(tClass.equals(AvailabilityOtherWake.class)){
            weekday = ((AvailabilityOtherWake)fragment).getWeekday();
            wake = true;

        } else if(tClass.equals(AvailabilityOtherBed.class)){
            weekday = ((AvailabilityOtherBed)fragment).getWeekday();

        } else if(tClass.equals(AvailabilitySaturdayWake.class)){
            weekday = "Saturday";
            wake = true;

        } else if(tClass.equals(AvailabilitySaturdayBed.class)){
            weekday = "Saturday";

        } else if(tClass.equals(AvailabilitySundayWake.class)){
            weekday = "Sunday";
            wake = true;

        } else if(tClass.equals(AvailabilitySundayBed.class)){
            weekday = "Sunday";
        }

        Assert.assertNotNull("invalid weekday",weekday);

        if(wake){
            time = clock.getRhythm(weekday).getWakeTime();
        } else {
            time = clock.getRhythm(weekday).getBedTime();
        }

        Assert.assertNotNull("invalid local time",time);

        ViewAction setTime = ViewActions.setTime(time.getHourOfDay(),time.getMinuteOfHour());
        UI.getTimePicker().perform(setTime);
        UI.sleep(400);
        UI.click(R.id.buttonNext);
    }

}
