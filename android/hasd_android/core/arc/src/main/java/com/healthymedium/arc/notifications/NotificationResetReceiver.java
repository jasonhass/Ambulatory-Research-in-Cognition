//
// NotificationResetReceiver.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;

public class NotificationResetReceiver extends BroadcastReceiver {

    static private final String tag = "NotificationResetReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {


        String action = intent.getAction();
        boolean isUpdate = action.equals(Intent.ACTION_MY_PACKAGE_REPLACED);
        boolean isReboot = action.equals(Intent.ACTION_BOOT_COMPLETED);

        // if intent is not from either a reboot or an update, nope out
        if (!(isReboot||isUpdate)){
            return;
        }

        NotificationManager.getInstance().scheduleAllNotifications();
        TestCycle cycle = Study.getCurrentTestCycle();
        if(cycle ==null){
            return;
        }
        if(cycle.getActualStartDate().isBeforeNow() && cycle.getActualEndDate().isAfterNow()){

            Proctor.startService(Application.getInstance());
        }
    }
}
