//
// TestData.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.data;

import com.healthymedium.arc.api.tests.BaseTest;

public class TestData extends BaseTest {

    private int progress = 0;
    public TestData(int p){
        progress = p;
    }

    @Override
    public int getProgress(boolean testCompleted) {
        if (testCompleted) {
            return 100;
        } else {
            return progress;
        }
    }
}
