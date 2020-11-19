//
// Application.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.hasd.arc.map;


import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestVariant;

public class Application extends com.healthymedium.arc.core.Application{

    @Override
    public void onCreate() {

        Config.CHOOSE_LOCALE = false;
        Config.CHECK_CONTACT_INFO = true;
        Config.CHECK_SESSION_INFO = true;
        Config.CHECK_PROGRESS_INFO = true;
        Config.ENABLE_VIGNETTES = true;
        Config.ENABLE_EARNINGS = true;
        Config.IS_REMOTE = false;
        Config.ENABLE_SIGNATURES = true;
        Config.USE_HELP_SCREEN = false;
        Config.EXPECTS_2FA_TEXT = false;
        Config.TEST_VARIANT_PRICE = TestVariant.Price.Revised;
        Config.REPORT_STUDY_INFO = true;

        if(BuildConfig.FLAVOR.equals(Config.FLAVOR_DEV)){
            Config.REST_ENDPOINT = "";
            Config.DEBUG_DIALOGS = true;
            Config.REST_HEARTBEAT = false;
            Config.REST_BLACKHOLE = true;

        } else if(BuildConfig.FLAVOR.equals(Config.FLAVOR_QA)){
            Config.REST_ENDPOINT = "";
            Config.DEBUG_DIALOGS = true;

        } else if(BuildConfig.FLAVOR.equals(Config.FLAVOR_PROD)){
            Config.REST_ENDPOINT = "";
        }

        super.onCreate();
    }

    // register different behaviors here
    @Override
    public void registerStudyComponents() {
        Study.getInstance().registerStateMachine(StateMachine.class);
        Study.getInstance().registerScheduler(Scheduler.class);
        Study.getInstance().registerPrivacyPolicy(PrivacyPolicy.class);
    }

}
