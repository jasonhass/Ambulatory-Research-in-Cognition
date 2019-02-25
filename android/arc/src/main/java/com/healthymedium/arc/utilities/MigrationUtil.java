//
// MigrationUtil.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import android.util.Log;

public class MigrationUtil {

    public void checkForUpdate(){

        // library migration

        long newLibVersion = VersionUtil.getLibraryVersionCode();
        long oldLibVersion = PreferencesManager.getInstance().getLong("libVersion", newLibVersion);

        Log.i("MigrationUtil", "old library version="+oldLibVersion);
        Log.i("MigrationUtil", "new library version="+newLibVersion);

        if(newLibVersion > oldLibVersion) {
            Log.i("MigrationUtil", "migrating library data from "+oldLibVersion+" to "+newLibVersion);
            if(migrateLibraryData(oldLibVersion,newLibVersion)) {
                PreferencesManager.getInstance().putLong("libVersion", newLibVersion);
            }
        }

        // app migration

        long newAppVersion = VersionUtil.getAppVersionCode();
        long oldAppVersion = PreferencesManager.getInstance().getLong("appVersion", newAppVersion);

        Log.i("MigrationUtil", "old app version="+oldAppVersion);
        Log.i("MigrationUtil", "new app version="+newAppVersion);

        if(newLibVersion > oldLibVersion) {
            Log.i("MigrationUtil", "migrating app data from "+oldAppVersion+" to "+newAppVersion);
            if(migrateAppData(oldAppVersion,newAppVersion)) {
                PreferencesManager.getInstance().putLong("appVersion", newAppVersion);
                Log.i("MigrationUtil", "migration successful");
            } else {
                Log.i("MigrationUtil", "migration failed");
            }
        }
    }


    protected boolean migrateAppData(long oldVersion, long newVersion){
        return true;
    }

    protected boolean migrateLibraryData(long oldVersion, long newVersion){
        return true;
    }

}
