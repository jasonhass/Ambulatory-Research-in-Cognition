//
// ParticipantStates.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.data;

import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.Scheduler;

import org.joda.time.DateTime;

public class ParticipantStates {

    public static ParticipantState getDefault(Scheduler scheduler) {
       return getDefault(scheduler, DateTime.now());
    }

    public static ParticipantState getInitState(){
        Participant participant = new Participant();
        participant.initialize();
        return participant.getState();
    }

    public static ParticipantState getDefault(Scheduler scheduler, DateTime time) {
        Participant participant = new Participant();
        participant.initialize();
        participant.setCircadianClock(CircadianClocks.getDefault());
        participant.getState().id = "123456";
        scheduler.scheduleTests(time,participant);
        return participant.getState();
    }
}
