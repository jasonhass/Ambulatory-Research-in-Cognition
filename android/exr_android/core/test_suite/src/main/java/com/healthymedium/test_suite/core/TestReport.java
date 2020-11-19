//
// TestReport.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.core;

import com.healthymedium.test_suite.events.EventBase;
import com.healthymedium.arc.core.Device;

import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestReport {

    private Description description;
    private String id;

    // device
    private String deviceId;
    private String deviceInfo;
    private String deviceName;

    private List<EventBase> events = new ArrayList<>();

    public TestReport(Description desc){
        id = UUID.randomUUID().toString();

        description = desc;
        deviceId = Device.getId();
        deviceInfo = Device.getInfo();
        deviceName = Device.getName();
    }

    void addEvent(EventBase event){
        events.add(event);
    }

}
