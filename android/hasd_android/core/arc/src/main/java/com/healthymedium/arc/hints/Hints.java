//
// Hints.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.hints;

import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.VersionUtil;

import org.joda.time.DateTime;

import java.util.HashMap;

public class Hints {

    private static final String tag = "Hints";
    static HashMap<String, HintDetails> map;

    private Hints(){

    }

    public static void save(){
        PreferencesManager.getInstance().putObject(tag, map.values().toArray());
    }

    public static void load(){
        map = new HashMap<>();

        if(!PreferencesManager.getInstance().contains(tag)){
            return;
        }

        HintDetails[] details = PreferencesManager.getInstance().getObject(tag, HintDetails[].class);
        map = new HashMap<>();
        for (HintDetails detail : details) {
            map.put(detail.name, detail);
        }
    }

    public static boolean hasBeenShown(String key){
        return map.containsKey(key);
    }

    public static boolean hasBeenShownSinceVersion(String key, long versionCode){
        if(!map.containsKey(key)){
            return false;
        }
        HintDetails details = map.get(key);
        return details.versionCode > versionCode;
    }

    public static boolean hasBeenShownSinceDate(String key, DateTime dateTime){
        if(!map.containsKey(key)){
            return false;
        }
        HintDetails details = map.get(key);
        return details.timestamp > (dateTime.getMillis()/1000);
    }

    public static void markShown(String key){
        HintDetails details = new HintDetails();
        details.name = key;
        details.timestamp = DateTime.now().getMillis()/1000;
        details.versionCode = VersionUtil.getAppVersionCode();
        map.put(key,details);
        save();
    }


    public static class HintDetails {
        long versionCode;
        long timestamp;
        String name;
    }

}
