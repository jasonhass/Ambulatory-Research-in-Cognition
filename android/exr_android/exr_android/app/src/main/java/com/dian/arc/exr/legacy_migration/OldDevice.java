//
// OldDevice.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

public class OldDevice {

    public String id;
    public String info;
    public String name;

    public OldDevice(){
        id = new String();
        info = new String();
        name = new String();
    }

    protected String getId(Context context){
        if(id.isEmpty()){
            id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return id;
    }

    public String getId(){
        return id;
    }

    public String getInfo(){
        if(info.isEmpty()){
            info = "Android|"+name+"|"+ Build.VERSION.RELEASE+"|"+Build.VERSION.SDK_INT+"|"+Build.FINGERPRINT;
        }
        return info;
    }

    protected String getName(){
        if(name.isEmpty()) {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                name = capitalize(model);
            } else {
                name = capitalize(manufacturer) + " " + model;
            }
        }
        return name;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

}
