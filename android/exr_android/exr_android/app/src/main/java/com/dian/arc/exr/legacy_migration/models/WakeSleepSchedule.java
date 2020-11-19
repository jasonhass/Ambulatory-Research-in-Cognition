//
// WakeSleepSchedule.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration.models;

import java.util.ArrayList;
import java.util.List;

public class WakeSleepSchedule {

    public int version = 2;

    public String arcId;
    public String deviceId;
    public Double createdOn = new Double(0);
    public List<WakeSleepData> wakeSleepData = new ArrayList<>();

    public String getArcId() {
        return arcId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public List<WakeSleepData> getWakeSleepData() {
        return wakeSleepData;
    }

    public WakeSleepData get(int index){
        return wakeSleepData.get(index);
    }

    public WakeSleepData getSunday() {
        return wakeSleepData.get(0);
    }

    public WakeSleepData getMonday() {
        return wakeSleepData.get(1);
    }

    public WakeSleepData getTuesday() {
        return wakeSleepData.get(2);
    }

    public WakeSleepData getWednesday() {
        return wakeSleepData.get(3);
    }

    public WakeSleepData getThursday() {
        return wakeSleepData.get(4);
    }

    public WakeSleepData getFriday() {
        return wakeSleepData.get(5);
    }

    public WakeSleepData getSaturday() {
        return wakeSleepData.get(6);
    }

}
