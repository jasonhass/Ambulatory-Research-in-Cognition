//
// AltQuestionTemplate.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.templates;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class AltQuestionTemplate extends AltStandardTemplate {

    private String question;
    private double display_time;
    protected double response_time;
    protected String type = "unknown";

    public AltQuestionTemplate(boolean allowBack, String header, String subheader) {
        super(allowBack,header,subheader);
        question = header.replace("<b>","").replace("</b>","");
    }

    public AltQuestionTemplate(boolean allowBack, String header, String subheader, String button) {
        super(allowBack,header,subheader,button);
        question = header.replace("<b>","").replace("</b>","");
    }

    @Override
    public void onStart(){
        super.onStart();
        display_time = System.currentTimeMillis();
    }

    @Override
    public Object onDataCollection(){

        Map<String, Object> response = new HashMap<>();
        response.put("display_time", display_time/(double)1000);
        response.put("response_time", response_time/(double)1000);
        response.put("question", question);
        response.put("type", type);

        Object value = onValueCollection();
        if(value!=null){
            response.put("value", value);
        }

        String textValue = onTextValueCollection();
        if(textValue!=null){
            response.put("text_value", textValue);
        }

        return response;
    }

    public Object onValueCollection(){
        return null;
    }

    public String onTextValueCollection(){
        return null;
    }

}
