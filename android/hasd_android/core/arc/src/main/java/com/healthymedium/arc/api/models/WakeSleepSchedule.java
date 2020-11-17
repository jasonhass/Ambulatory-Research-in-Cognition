//
// WakeSleepSchedule.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.models;

import java.util.List;

public class WakeSleepSchedule {

    public String app_version;          // "app_version" : (string, version of the app),
    public String device_id;            // "device_id" : (string, the unique id for this device),
    public String device_info;          // "device_info": (a string with format "OS name|device model|OS version", ie "iOS|iPhone8,4|10.1.1")
    public String participant_id;       // "participant_id" : (string, the user's participant id),

    public List<WakeSleepData> wakeSleepData;
    public String model_version = "0";

    public String timezone_name;       // name of timezone ie "Central Standard Time"
    public String timezone_offset;     // offset from utc ie "UTC-05:00"

}
