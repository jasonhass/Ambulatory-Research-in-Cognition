//
// Capture.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.utilities;

import android.os.Environment;
import android.support.test.runner.screenshot.ScreenCapture;
import android.support.test.runner.screenshot.ScreenCaptureProcessor;
import android.support.test.runner.screenshot.Screenshot;

import com.healthymedium.arc.core.BaseFragment;

import com.healthymedium.test_suite.core.TestBehavior;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;


public class Capture {

    public static int INDEX = 0;


    public static void takeScreenshot(BaseFragment fragment, String behaviorName, String actionName){

        String dirName = TestBehavior.testName;
        String formattedIndex = String.format(Locale.ENGLISH, "%04d", INDEX);
        String captureName = dirName +  "/"+ formattedIndex+ "_" +fragment.getSimpleTag() + "_" + behaviorName + "_" + actionName;
        INDEX++;

        try {

            File dir = new File(Environment.getExternalStorageDirectory().toString() , "Pictures/screenshots/" + dirName);
            if (!dir.exists()) {
                if(!dir.mkdirs()){

                }
            }


            ScreenCapture screenCapture = Screenshot.capture();
            screenCapture.setName(captureName);

            HashSet<ScreenCaptureProcessor> processors = new HashSet<>();
            processors.add(new CustomScreenCaptureProcessor());
            screenCapture.process(processors);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
