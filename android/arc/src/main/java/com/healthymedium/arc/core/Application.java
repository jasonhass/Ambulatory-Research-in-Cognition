//
// Application.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.core;

import android.content.res.Configuration;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.VersionUtil;

import net.danlew.android.joda.JodaTimeAndroid;
import java.util.ArrayList;
import java.util.List;

public class Application extends android.app.Application {

    static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        VersionUtil.initialize(this);
        JodaTimeAndroid.init(this);
        PreferencesManager.initialize(this);
        Device.initialize(this);
    }

    // register different behaviors here
    public void registerStudyComponents() {
        //Study.getInstance().registerParticipantBehavior();
        //Study.getInstance().registerMigrationBehavior();
        //Study.getInstance().registerSchedulerBehavior();
        //Study.getInstance().registerRestClient();
        //Study.getInstance().registerStudyBehavior();
    }

    // list all locale options offered by the app
    protected List<Locale> getLocaleOptions() {
        List<Locale> locales = new ArrayList<>();
        locales.add(new Locale(Locale.COUNTRY_UNITED_STATES,Locale.LANGUAGE_ENGLISH));
        return locales;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(PreferencesManager.getInstance() != null){
            if(PreferencesManager.getInstance().contains("language")){
                String language = PreferencesManager.getInstance().getString("language","en");
                String country = PreferencesManager.getInstance().getString("country","US");
                newConfig.setLocale(new java.util.Locale(language,country));
                getBaseContext().getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
            }
        }
    }

    public static Application getInstance(){
        return instance;
    }

}
