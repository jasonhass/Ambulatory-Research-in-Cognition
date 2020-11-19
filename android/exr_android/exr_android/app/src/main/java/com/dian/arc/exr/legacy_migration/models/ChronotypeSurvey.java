//
// ChronotypeSurvey.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration.models;

public class ChronotypeSurvey {

    public int numWorkDays;
    public int wasShiftWorker;
    public String workFreeSleepTime;
    public String workFreeWakeTime;
    public String workSleepTime;
    public String workWakeTime;

    public String cpuLoad;
    public String deviceMemory;

    public ChronotypeSurvey(){
        workFreeSleepTime = new String();
        workFreeWakeTime = new String();
        workSleepTime = new String();
        workWakeTime = new String();
        cpuLoad = new String();
        deviceMemory = new String();
    }
}
