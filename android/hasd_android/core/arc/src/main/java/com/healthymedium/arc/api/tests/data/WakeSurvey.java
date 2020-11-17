//
// WakeSurvey.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.tests.data;

import java.util.List;

public class WakeSurvey extends BaseData {

    public List<WakeSurveySection> questions;
    public String type = "wake";
    public Double start_date;

    public int getProgress(){

        int displayCounts = 0;
        int expectedDisplayCounts = 6;

        for(WakeSurveySection section : questions){
            if(section.display_time==null){
                continue;
            }
            if(section.display_time>0){
                displayCounts++;
            }
        }

        return (int) (100*((float)displayCounts/expectedDisplayCounts));
    }
}
