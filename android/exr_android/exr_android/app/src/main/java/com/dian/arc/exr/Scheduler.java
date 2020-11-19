//
// Scheduler.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr;

import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestDay;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.time.TimeUtil;
import com.healthymedium.arc.utilities.Log;

import org.joda.time.DateTime;
import java.util.List;

public class Scheduler extends com.healthymedium.arc.study.Scheduler {

    public Scheduler() {
        super();
    }

    @Override
    public void initializeCycles(DateTime now, Participant participant) {

        List<TestCycle> cycles = participant.getState().testCycles;
        DateTime midnight = TimeUtil.setMidnight(now);

        TestCycle baseline = initializeBaselineCycle(midnight,8);
        cycles.add(baseline);

        DateTime dateTime = midnight;

        for(int i=1;i<10;i++){
            dateTime = dateTime.plusDays(182);
            TestCycle visit = initializeCycle(i,4,dateTime,7);
            cycles.add(visit);
        }

        int id = 0;
        for(TestCycle cycle : cycles) {
            for(TestDay day : cycle.getTestDays()) {
                for(TestSession session : day.getTestSessions()) {
                    session.setId(id);
                    id++;
                }
            }

        }

    }

}

