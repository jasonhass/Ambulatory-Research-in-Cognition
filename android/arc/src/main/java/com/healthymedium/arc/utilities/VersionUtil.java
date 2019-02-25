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
import android.os.Build;
import android.util.Log;

import com.healthymedium.arc.library.BuildConfig;

public class VersionUtil {

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
           // if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
              //  app_code = packageInfo.getLongVersionCode();
           // } else {
                app_code = packageInfo.versionCode;
           // }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        library_name = BuildConfig.VERSION_NAME;
        library_code = BuildConfig.VERSION_CODE;

        Log.i("VersionUtil","library | code="+library_code+", name="+library_name);
        Log.i("VersionUtil","app     | code="+app_code+", name="+app_name);

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

}
