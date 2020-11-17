//
// CircadianClocks.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.data;

import com.healthymedium.arc.study.CircadianClock;
import org.joda.time.LocalTime;

public class CircadianClocks {

    public static CircadianClock get(LocalTime wake, LocalTime bed){
        CircadianClock clock = new CircadianClock();
        clock.setRhythms(wake,bed);
        return clock;
    }

    public static CircadianClock get(String wake, String bed){
        CircadianClock clock = new CircadianClock();
        clock.setRhythms(wake,bed);
        return clock;
    }

    public static CircadianClock getDefault(){
        return get("08:00","19:00");
    }

}
