//
// CustomScreenCaptureProcessor.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.utilities;

import android.support.test.runner.screenshot.BasicScreenCaptureProcessor;

/** Custom processor that does not include any additional suffix with the filename */
public class CustomScreenCaptureProcessor extends BasicScreenCaptureProcessor {


    protected String getFilename(String prefix) {
        return prefix;
    }
}
