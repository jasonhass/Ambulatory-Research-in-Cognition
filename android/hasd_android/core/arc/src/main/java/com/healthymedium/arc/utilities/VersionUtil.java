//
// VersionUtil.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


import com.healthymedium.arc.library.BuildConfig;

public class VersionUtil {

    private static final String tag = "VersionUtil";

    private static long library_code;
    private static String library_name;
    private static long app_code;
    private static String app_name;

    private VersionUtil(){
        library_name = new String();
        app_name = new String();
    }

    public static void initialize(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            app_name = packageInfo.versionName;
            app_code = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        library_name = BuildConfig.VERSION_NAME;
        library_code = BuildConfig.VERSION_CODE;




    }

    public static long getAppVersionCode(){
        return app_code;
    }

    public static String getAppVersionName(){
        return app_name;
    }

    public static long getLibraryVersionCode(){
        return library_code;
    }

    public static String getLibraryVersionName(){
        return library_name;
    }

    public static long getVersionCode(int major,int minor, int patch, int build){
        return major * 1000000 + minor * 10000 + patch * 100 + build;
    }

    public static long getVersionCode(int major,int minor, int patch){
        return getVersionCode(major,minor,patch,0);
    }

    public static long getVersionCode(int major,int minor){
        return getVersionCode(major,minor,0,0);
    }

    public static long getVersionCode(int major){
        return getVersionCode(major,0,0,0);
    }

}
