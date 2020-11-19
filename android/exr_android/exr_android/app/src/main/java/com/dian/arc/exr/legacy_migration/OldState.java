//
// OldState.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration;

import com.dian.arc.exr.legacy_migration.models.TestSession;
import com.dian.arc.exr.legacy_migration.models.Visit;
import com.dian.arc.exr.legacy_migration.models.WakeSleepSchedule;

import java.util.ArrayList;
import java.util.List;

public class OldState {

    public static final int SETUP_INIT = 0;
    public static final int SETUP_NEW = 1;
    public static final int SETUP_EXISTING = 2;
    public static final int PATH_FIRST_DAY = 3;
    public static final int PATH_FIRST_BURST = 4;
    public static final int PATH_LAST_BURST = 5;
    public static final int PATH_OTHER = 6;
    public static final int PATH_BETWEEN = 7;
    public static final int PATH_NONE = 8;
    public static final int PATH_BASELINE = 9;
    public static final int PATH_AVAILABILITY = 10;
    public static final int PATH_OVER = 11;
    public static final int PATH_CONFIRM = 12;

    public static final int LIFECYCLE_INIT = 0;
    public static final int LIFECYCLE_TRIAL = 1;
    public static final int LIFECYCLE_BASELINE= 2;
    public static final int LIFECYCLE_IDLE = 3;
    public static final int LIFECYCLE_ARC = 4;
    public static final int LIFECYCLE_OVER = 5;

    public String arcId;
    public String raterId;

    public int currentPathStepsTaken;
    public int currentPath;
    public int nextPath;

    public int currentTestSession;
    public int currentVisit;

    public int lifecycle;

    public List<Visit> visits;
    private WakeSleepSchedule wakeSleepSchedule;

    public OldState(){
        currentPath = SETUP_INIT;
        lifecycle = LIFECYCLE_INIT;
        nextPath = PATH_BETWEEN;
        arcId = new String();
        raterId = new String();
        visits = new ArrayList<>();
    }

    public Visit getCurrentVisit(){
        return visits.get(currentVisit);
    }

    public TestSession getCurrentTestSession(){
        return getCurrentVisit().getTestSessions().get(currentTestSession);
    }


}
