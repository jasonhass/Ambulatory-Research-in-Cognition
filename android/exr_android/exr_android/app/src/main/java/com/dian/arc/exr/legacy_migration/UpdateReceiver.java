//
// UpdateReceiver.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.utilities.Log;

public class UpdateReceiver extends BroadcastReceiver {

    static private final String tag = "UpdateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {


        String action = intent.getAction();
        boolean isUpdate = action.equals(Intent.ACTION_MY_PACKAGE_REPLACED);

        // if intent is not from an update, nope out
        if (!isUpdate){
            return;
        }

        if(!LegacyMigration.hasBeenCompleted()){

            LegacyMigration.start(Application.getInstance());
        }
    }
}
