//
// TestSchedule.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.models;

import java.util.List;

public class TestSchedule {

    public String app_version;                  // version of the app
    public String device_info;                  // a string with format
    public String participant_id;               // the user's participant id
    public String device_id;                    // the unique id for this device
    public String model_version = "0";          // the model version of this data object
    public List<TestScheduleSession> sessions;  // an array of objects that define each session

}
