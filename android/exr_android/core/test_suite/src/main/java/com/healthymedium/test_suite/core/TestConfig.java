//
// TestConfig.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.core;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.arc.study.CircadianClock;

import java.util.Map;

public class TestConfig {

    public static final String TAG_ = "";

    public static Map<Class, Behavior> behaviorMap;
    public static CircadianClock circadianClock;   // if null, grabs default
    public static boolean needsToSchedule = false;

    public static class Classes {
        public static Class stateMachine;
        public static Class participant;
        public static Class scheduler;
        public static Class restClient;
        public static Class restApi;
    }

    public static class Participant {
        public static String id;             // if null, fills with zeros
        public static String idConfirm;      // if null, fills with zeros
        public static String raterId;                   // if null, fills with zeros
    }

    public static class StudyState {
        public static int lifecycle;                    // if null, fills with zeros
        public static int path;                  // if null, fills with zeros
    }

}
