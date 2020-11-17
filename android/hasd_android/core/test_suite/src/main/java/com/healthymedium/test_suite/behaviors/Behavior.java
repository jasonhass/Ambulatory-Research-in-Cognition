//
// Behavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors;

import com.healthymedium.arc.core.BaseFragment;

public class Behavior {

    protected boolean extensionAllowed = false;
    protected BaseFragment fragment;

    public Behavior(){

    }

    public boolean allowExtensions(){
        return extensionAllowed;
    }

    public void onOpened(BaseFragment fragment){
        this.fragment = fragment;
        //test.takeScreenshot(fragment.getView(),fragment.getSimpleTag()+" Opened");
    }

    public void onPoppedBack(BaseFragment fragment){
        this.fragment = fragment;
        //test.takeScreenshot(fragment.getView(),fragment.getSimpleTag()+" PoppedBack");
    }

}



