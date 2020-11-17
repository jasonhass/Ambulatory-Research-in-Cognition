//
// ContextPathData.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.path_data;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.api.tests.data.ContextSurvey;
import com.healthymedium.arc.api.tests.data.ContextSurveySection;
import com.healthymedium.arc.study.PathSegmentData;

import java.util.ArrayList;
import java.util.Map;

public class ContextPathData extends PathSegmentData {

    public ContextPathData(){
        super();
    }

    @Override
    protected BaseData onProcess() {

        ContextSurvey survey = new ContextSurvey();
        survey.questions = new ArrayList<>();

        int size = objects.size();
        for (int i=0;i<size;i++) {
            Map<String, Object> response = (Map<String, Object>) objects.get(i);
            ContextSurveySection surveySection = processHashMap(response,ContextSurveySection.class);

            if(i==0){
                survey.start_date = surveySection.display_time;
            } else if(survey.start_date > surveySection.display_time){
                survey.start_date = surveySection.display_time;
            }

            surveySection.question_id = "context_" + (i+1);
            survey.questions.add(surveySection);
        }

        return survey;
    }
}
