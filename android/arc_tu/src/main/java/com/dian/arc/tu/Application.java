package com.dian.arc.tu;

import android.content.res.Configuration;
import com.dian.arc.libs.utilities.Config;

public class Application extends com.dian.arc.libs.Application {

    @Override
    public void onCreate() {

        // Core
        Config.CHOOSE_LOCALE = true;
        Config.FOLDER_NAME = "arc_tu"; // if a file fails to send, you can find it here
        Config.ARC_INTERVALS = new int[]{1,12,12,16,12,12,12,12,16,12,12,12,16,12,12,12,16,12,12,12,16,12,12,12,16,12,12,12,16}; // each index is the number of weeks between visits

        // Rest API
        Config.REST_API_VERSION = 2;
        Config.REST_ENDPOINT = ""; // where we send the data
        Config.REST_BLACKHOLE = false; // used for debugging, keeps all rest calls from reaching the outside world
        Config.REST_HEARTBEAT = true; // heartbeat will fail if blackhole is enabled

        // Authentication
        Config.AUTHENTICATION_TWO_FACTOR = false;
        Config.AUTHENTICATION_RATER_ID = true;

        // Misc
        Config.DEBUG_DIALOGS = false; // click the top left header on most screens a couple times and a debug dialog will appear
        Config.RECORD_PERFORMANCE = true;

        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
