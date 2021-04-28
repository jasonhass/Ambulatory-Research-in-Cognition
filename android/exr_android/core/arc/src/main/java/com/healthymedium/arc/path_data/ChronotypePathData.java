//
// ChronotypePathData.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.path_data;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.api.tests.data.ChronotypeSurvey;
import com.healthymedium.arc.api.tests.data.ChronotypeSurveySection;
import com.healthymedium.arc.study.PathSegmentData;

import java.util.ArrayList;
import java.util.Map;

public class ChronotypePathData extends PathSegmentData {

    public ChronotypePathData(){
        super();
    }

    @Override
    protected BaseData onProcess() {

        ChronotypeSurvey survey = new ChronotypeSurvey();
        survey.questions = new ArrayList<>();

        return survey;

    }
}
