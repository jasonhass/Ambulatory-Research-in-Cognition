package com.dian.arc.cs;

import android.content.res.Configuration;
import com.dian.arc.libs.utilities.Config;

public class Application extends com.dian.arc.libs.Application {

    @Override
    public void onCreate() {
        Config.DEBUG_DIALOGS = false;
        Config.REST_BLACKHOLE = true;
        Config.REST_ENDPOINT = "";
        Config.ARC_INTERVALS = new int[]{1};

        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
}
