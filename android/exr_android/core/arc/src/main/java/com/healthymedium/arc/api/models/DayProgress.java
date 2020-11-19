//
// DayProgress.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.models;

import java.util.List;

public class DayProgress {

    public Long start_date;
    public Long end_date;
    public Integer day;
    public Integer cycle;
    public List<SessionProgress> sessions;

}
