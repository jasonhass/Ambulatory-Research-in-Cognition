//
// AvailabilityPathData.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.path_data;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.study.PathSegmentData;

public class AvailabilityPathData extends PathSegmentData {

    public boolean weekdaySame; // are all weekday wake and sleep times the same

    public AvailabilityPathData(){
        super();
    }

    @Override
    protected BaseData onProcess() {
        return null;
    }
}
