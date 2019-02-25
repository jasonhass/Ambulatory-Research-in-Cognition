//
// Config.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.core;

public class Config {

    public static String FLAVOR_DEV = "dev";
    public static String FLAVOR_QA = "qa";
    public static String FLAVOR_PROD = "prod";

    // Core
    public static boolean CHOOSE_LOCALE = false;

    // Rest API
    public static String REST_ENDPOINT = "http://thinkhealthymedium.com/"; // where we send the data
    public static boolean REST_BLACKHOLE = true; // used for debugging, keeps all rest calls from reaching the outside world
    public static boolean REST_HEARTBEAT = false; // heartbeat will fail if blackhole is enabled

    //Debug
    public static boolean DEBUG_DIALOGS = true; // click the header on most screens a couple times and a debug dialog will appear

    // Runtime
    public static boolean OPENED_FROM_NOTIFICATION = false; //

}
