//
// BootReceiver.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class BootReceiver extends BroadcastReceiver {

    static private final String tag = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {


        String action = intent.getAction();
        boolean isReboot = action.equals(Intent.ACTION_BOOT_COMPLETED);

        // if intent is not from a reboot, nope out
        if (!isReboot){
            return;
        }

        Participant participant = Study.getParticipant();
        StateMachine stateMachine = Study.getStateMachine();

        if(participant.isCurrentlyInTestSession() && participant.checkForTestAbandonment()){

            stateMachine.abandonTest();
        }

    }
}
