//
// EventBase.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.events;

import org.joda.time.DateTime;

public class EventBase {
    private DateTime timestamp;
    protected String type;
    protected String tag;
    protected String msg;

    public EventBase(String type, String tag, String msg){
        timestamp = DateTime.now();
        this.type = type;
        this.msg = msg;
        this.tag = tag;
    }
}
