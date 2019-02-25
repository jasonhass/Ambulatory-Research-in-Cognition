//
// TestSubmission.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.models;

import java.util.List;

public class TestSubmission {

    public String app_version;         // version of the app
    public String model_version = "0"; // the model version of this data object. For now, just set this to "0". Although this isn't used currently, if we ever need to make a meaningful change to the structure of this data, this will help us differentiate between versions)

    public String device_id;           // the unique id for this device
    public String device_info;         // format "OS name|device model|OS version", ie "iOS|iPhone8,4|10.1.1"
    public String participant_id;      // the user's participant id

    public String session_id;          // an identifier for this specific session w/r/t the entire test. On iOS, we're just using the sessions "index", so to speak)
    public String id;                  // a copy of the session_id
    public Double session_date;        // the  date/time when this session is scheduled to start
    public Double start_time;          // (optional) the date/time when the user began the test
    public Integer week;               // 0-indexed week that this session takes place in
    public Integer day;                // 0-indexed day within the current week
    public Integer session;            // 0-indexed session within the current day
    public Integer missed_session;     // 1 or 0, whether or not the user never started the test
    public Integer finished_session;   // 1 or 0, whether or not the user finished the test
    public Integer interrupted;        // 1 or 0, whether or not the user never started the test

    public List tests;                  // test data objects

}
