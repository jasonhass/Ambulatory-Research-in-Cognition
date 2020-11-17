//
// TimeChangeReceiver.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.BuildConfig;


import com.healthymedium.arc.notifications.Proctor;
import com.healthymedium.arc.study.Study;

//  This receiver catches when the user modifies their date/time. When this happens, the app can occasionally
//  get left in an odd state, so we basically just make sure that any existing tests are abandoned,
//  and have the Study State Machine re-run its path deciding methods.

public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        if(Study.getStateMachine().isCurrentlyInTestPath()) {

            return;
        }

        Proctor.pauseService(context);
        Study.getStateMachine().decidePath();
        Study.getStateMachine().save(true);
        Study.getParticipant().save();
        Proctor.resumeService(context);



        String flavor = BuildConfig.FLAVOR;
        if(flavor.equals(Config.FLAVOR_DEV) || flavor.equals(Config.FLAVOR_QA)) {
            Toast.makeText(context,"Time Change Acknowledged",Toast.LENGTH_LONG).show();
        }

    }

}
