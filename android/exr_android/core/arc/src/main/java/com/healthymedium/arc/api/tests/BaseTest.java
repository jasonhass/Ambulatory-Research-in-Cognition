//
// BaseTest.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.tests;

import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.api.tests.data.BaseData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class BaseTest {
    protected String type;

    public BaseTest(){
        type = "default";
    }

    public void load(List<BaseData> segments){

        Field[] fields = getClass().getFields();
        for(int i=0;i<fields.length;i++){
            if( Modifier.isPublic(fields[i].getModifiers())){
                for(Object segment : segments){
                    Class segmentClass = segment.getClass();
                    Class fieldClass = fields[i].getType();
                    if(fieldClass==segmentClass){

                        try {
                            fields[i].setAccessible(true);
                            fields[i].set(this,segment);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public int getProgress(boolean testCompleted){
        return 0;
    }

}
