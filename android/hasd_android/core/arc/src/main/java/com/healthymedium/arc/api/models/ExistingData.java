//
// ExistingData.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.models;

public class ExistingData {

    public SessionInfo first_test;
    public SessionInfo latest_test;
    public WakeSleepSchedule wake_sleep_schedule;
    public TestSchedule test_schedule;


    public boolean isValid(){
        if(first_test==null){
            return false;
        }
        if(latest_test==null){
            return false;
        }
        if(wake_sleep_schedule==null){
            return false;
        }
        if(test_schedule==null){
            return false;
        }
        return true;
    }

}
