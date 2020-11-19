//
// SuiteEvent.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.events;

import org.junit.runner.Description;

public class SuiteEvent extends EventBase {

    String error;

    public SuiteEvent(String tag, Description description, String error) {
        super(EventTypes.SUITE,tag, description.getDisplayName());
        this.error = error;
    }

    public SuiteEvent(String tag, Description description) {
        super(EventTypes.SUITE,tag, description.getDisplayName());
    }

    public SuiteEvent(String tag, String msg) {
        super(EventTypes.SUITE,tag, msg);
    }

}
