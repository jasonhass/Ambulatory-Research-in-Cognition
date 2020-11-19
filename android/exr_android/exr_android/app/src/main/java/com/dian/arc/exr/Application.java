//
// Application.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr;

import com.dian.arc.exr.api.RestClient;
import com.dian.arc.exr.legacy_migration.LegacyMigrationNotification;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.notifications.types.NotificationType;
import com.healthymedium.arc.study.Study;

import java.util.ArrayList;
import java.util.List;

public class Application extends  com.healthymedium.arc.core.Application{

    @Override
    public void onCreate() {

        Config.CHOOSE_LOCALE = true;
        Config.CHECK_CONTACT_INFO = true;
        Config.CHECK_SESSION_INFO = true;
        Config.CHECK_PROGRESS_INFO = true;
        Config.ENABLE_VIGNETTES = true;
        Config.ENABLE_EARNINGS = true;
        Config.IS_REMOTE = true;
        Config.ENABLE_SIGNATURES = true;
        Config.USE_HELP_SCREEN = false;

        if(BuildConfig.FLAVOR.equals(Config.FLAVOR_DEV)){
            Config.REST_ENDPOINT = "";
            Config.DEBUG_DIALOGS = true;
            Config.REST_HEARTBEAT = false;
            Config.REST_BLACKHOLE = false;
//            Config.ENABLE_LOGGING = true;

        } else if(BuildConfig.FLAVOR.equals(Config.FLAVOR_QA)){
            Config.REST_ENDPOINT = "";
            Config.DEBUG_DIALOGS = true;
            Config.ENABLE_LOGGING = true;

        } else if(BuildConfig.FLAVOR.equals(Config.FLAVOR_PROD)){
            Config.REST_ENDPOINT = "";
        }

        super.onCreate();
    }

    @Override
    public List<NotificationType> getNotificationTypes() {
        List<NotificationType> types = super.getNotificationTypes();
        types.add(new LegacyMigrationNotification());
        return types;
    }

    @Override
    public List<Locale> getLocaleOptions() {
        List<Locale> locales = new ArrayList<>();

        locales.add(new Locale(true, Locale.LANGUAGE_SPANISH, Locale.COUNTRY_ARGENTINA));
        locales.add(new Locale(true,  Locale.LANGUAGE_ENGLISH, Locale.COUNTRY_AUSTRALIA));
        locales.add(new Locale(true,  Locale.LANGUAGE_ENGLISH, Locale.COUNTRY_CANADA));
        locales.add(new Locale(true,  Locale.LANGUAGE_FRENCH,  Locale.COUNTRY_CANADA));
        locales.add(new Locale(true, Locale.LANGUAGE_SPANISH, Locale.COUNTRY_COLUMBIA));
        locales.add(new Locale(false, Locale.LANGUAGE_GERMAN,  Locale.COUNTRY_GERMANY));
        locales.add(new Locale(false, Locale.LANGUAGE_SPANISH, Locale.COUNTRY_SPAIN));
        locales.add(new Locale(false, Locale.LANGUAGE_JAPANESE,Locale.COUNTRY_JAPAN));
        locales.add(new Locale(true, Locale.LANGUAGE_SPANISH, Locale.COUNTRY_MEXICO));
        locales.add(new Locale(true,  Locale.LANGUAGE_ENGLISH, Locale.COUNTRY_UNITED_KINGDOM));
        locales.add(new Locale(true,  Locale.LANGUAGE_ENGLISH, Locale.COUNTRY_UNITED_STATES));

        return locales;
    }

    // register different behaviors here
    @Override
    public void registerStudyComponents() {
        Study.getInstance().registerStateMachine(StateMachine.class);
        Study.getInstance().registerScheduler(Scheduler.class);
        Study.getInstance().registerPrivacyPolicy(PrivacyPolicy.class);
        Study.getInstance().registerRestApi(RestClient.class);
    }
}
