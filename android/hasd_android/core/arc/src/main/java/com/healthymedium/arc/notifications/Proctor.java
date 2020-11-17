//
// Proctor.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;



public class Proctor {

    private static final String tag = "Proctor";



    public static void startService(Context context) {

        Intent intent = new Intent(context, ProctorService.class);
        intent.setAction(ProctorService.ACTION_START_SERVICE);
        ContextCompat.startForegroundService(context, intent);
    }

    public static void pauseService(Context context) {

        Intent intent = new Intent(context, ProctorService.class);
        intent.setAction(ProctorService.ACTION_PAUSE_SERVICE);
        ContextCompat.startForegroundService(context,intent);
    }

    public static void resumeService(Context context) {

        Intent intent = new Intent(context, ProctorService.class);
        intent.setAction(ProctorService.ACTION_RESUME_SERVICE);
        ContextCompat.startForegroundService(context,intent);
    }

    public static void stopService(Context context) {

        Intent intent = new Intent(context, ProctorService.class);
        intent.setAction(ProctorService.ACTION_STOP_SERVICE);
        ContextCompat.startForegroundService(context,intent);
    }

    public static void refreshData(Context context) {

        Intent intent = new Intent(context, ProctorService.class);
        intent.setAction(ProctorService.ACTION_REFRESH_DATA);
        ContextCompat.startForegroundService(context,intent);
    }

}
