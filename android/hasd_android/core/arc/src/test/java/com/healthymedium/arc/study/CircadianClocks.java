//
// CircadianClocks.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import com.healthymedium.arc.study.CircadianClock;
import org.junit.Assert;

public class CircadianClocks {

    public static CircadianClock getDefault(){
        CircadianClock clock = new CircadianClock();
        clock.getRhythm(0).setTimes("08:00:00","17:00:00"); // Sunday
        clock.getRhythm(1).setTimes("08:00:00","17:00:00"); // Monday
        clock.getRhythm(2).setTimes("08:00:00","17:00:00"); // Tuesday
        clock.getRhythm(3).setTimes("08:00:00","17:00:00"); // Wednesday
        clock.getRhythm(4).setTimes("08:00:00","17:00:00"); // Thursday
        clock.getRhythm(5).setTimes("08:00:00","17:00:00"); // Friday
        clock.getRhythm(6).setTimes("08:00:00","17:00:00"); // Saturday
        Assert.assertTrue(clock.isValid());
        return clock;
    }

    public static CircadianClock getDayShift(){
        CircadianClock clock = new CircadianClock();
        clock.getRhythm(0).setTimes("08:30:00","20:30:00"); // Sunday
        clock.getRhythm(1).setTimes("07:00:00","20:15:00"); // Monday
        clock.getRhythm(2).setTimes("07:30:00","21:00:00"); // Tuesday
        clock.getRhythm(3).setTimes("07:00:00","21:00:00"); // Wednesday
        clock.getRhythm(4).setTimes("07:30:00","21:00:00"); // Thursday
        clock.getRhythm(5).setTimes("07:00:00","20:15:00"); // Friday
        clock.getRhythm(6).setTimes("09:30:00","23:15:00"); // Saturday
        Assert.assertTrue("day shift is invalid",clock.isValid());
        return clock;
    }

}
