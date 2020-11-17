//
// IdleBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.generic;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class IdleBehavior extends Behavior {

   ArrayList<Long> ms;
   ArrayList<String> actionName;


    public IdleBehavior(){
        this.ms =  new ArrayList<>();
        this.ms.add(0L);
        this.actionName = new ArrayList<>();
        this.actionName.add(null);
    }

    public IdleBehavior(long ms){
        this.ms =  new ArrayList<>();
        this.ms.add(ms);
        this.actionName = new ArrayList<>();
        this.actionName.add(null);
    }

    public IdleBehavior(long ms, String name){
        this.ms =  new ArrayList<>();
        this.ms.add(ms);
        this.actionName = new ArrayList<>();
        this.actionName.add(name);
    }

    public IdleBehavior(long[] ms, String[] name){
        this.ms =  new ArrayList<>();
        for (long m : ms) {
            this.ms.add(m);
        }
        this.actionName = new ArrayList<>();
        this.actionName.addAll(Arrays.asList(name));
    }


    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

        if( ms.size() == actionName.size()){

            for(int i = 0; i < ms.size(); i++){
                UI.sleep(ms.get(i));
                String currAction = actionName.get(i);
                if(currAction != null && !currAction.trim().isEmpty()) {
                    Capture.takeScreenshot(fragment, getClass().getSimpleName(), currAction);
                }
            }
        }
    }
}
