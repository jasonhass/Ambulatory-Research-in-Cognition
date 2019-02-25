//
// Application.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.sample;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.study.Study;

public class Application extends  com.healthymedium.arc.core.Application{

    @Override
    public void onCreate() {

        // Core
        Config.REST_ENDPOINT = "https://thinkhealthymedium.com/";
        Config.REST_BLACKHOLE = true;

        super.onCreate();
    }

    // register different behaviors here
    public void registerStudyComponents() {
        //Study.getInstance().registerParticipantBehavior();
        //Study.getInstance().registerMigrationBehavior();
        //Study.getInstance().registerSchedulerBehavior();
        //Study.getInstance().registerRestClient();
        Study.getInstance().registerStateMachine(SampleStateMachine.class);
    }

}
