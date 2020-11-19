//
// CircadianInstant.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import org.joda.time.DateTime;

public class CircadianInstant {

    private DateTime bed;
    private DateTime wake;

    CircadianInstant(){
        bed = DateTime.now();
        wake = DateTime.now();
    }

    public DateTime getBedTime() {
        return bed;
    }

    public void setBedTime(DateTime bed) {
        this.bed = bed;
    }

    public DateTime getWakeTime() {
        return wake;
    }

    public void setWakeTime(DateTime wake) {
        this.wake = wake;
    }

    public boolean isNocturnal(){
        return (wake.isAfter(bed));
    }

}
