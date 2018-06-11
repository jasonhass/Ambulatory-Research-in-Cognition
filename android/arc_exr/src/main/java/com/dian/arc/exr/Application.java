package com.dian.arc.exr;

import android.content.res.Configuration;
import com.dian.arc.libs.utilities.Config;

public class Application extends com.dian.arc.libs.Application {

    @Override
    public void onCreate() {

        // Core
        Config.CHOOSE_LOCALE = false;
        Config.FOLDER_NAME = "arc_exr"; // if a file fails to send, you can find it here
        Config.ARC_INTERVALS = new int[]{1,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,}; // each index is the number of weeks between visits
        Config.SKIP_BASELINE = true;

        // Rest API
        Config.REST_API_VERSION = 2;
        Config.REST_ENDPOINT = "";
        Config.REST_BLACKHOLE = false;
        Config.REST_HEARTBEAT = false; // heartbeat will fail if blackhole is enabled

        Config.AUTHENTICATION_TWO_FACTOR = true;
        Config.AUTHENTICATION_RATER_ID = false;

        Config.DEBUG_DIALOGS = false; // click the top left header on most screens a couple times and a debug dialog will appear
        Config.RECORD_PERFORMANCE = true; // setting this to false lets the emulator function properly, used for debug

        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
