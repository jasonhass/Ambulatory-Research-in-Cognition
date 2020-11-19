//
// CycleProgress.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.models;

import java.util.List;

public class CycleProgress {

    public Integer cycle;
    public Long start_date;
    public Long end_date;
    public Integer day_count;
    public Integer current_day;
    public List<DayProgress> days;

}
