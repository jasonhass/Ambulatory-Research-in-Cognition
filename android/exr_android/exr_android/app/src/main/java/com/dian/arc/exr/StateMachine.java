//
// StateMachine.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.TwoBtnPopupScreen;
import com.healthymedium.arc.paths.home.LandingScreen;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.StateMachineAlpha;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.Log;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StateMachine extends StateMachineAlpha {

    @Override
    protected void setupPath(){



        switch (state.currentPath){
            case PATH_SETUP_PARTICIPANT:
                setPathSetupParticipant(6, 0, 6);
                break;
            case PATH_COMMITMENT:
                setPathCommitment();
                break;
            case PATH_COMMITMENT_REBUKED:
                setPathCommitmentRebuked();
                break;
            case PATH_NOTIFICATIONS_OVERVIEW:
                setPathNotificationOverview();
                break;
            case PATH_SETUP_AVAILABILITY:
                setPathSetupAvailability(8, 18, false);
                break;
            case PATH_TEST_FIRST_OF_BASELINE:
                setPathFirstOfBaseline();
                break;
            case PATH_TEST_BASELINE:
                setPathBaselineTest();
                break;
            case PATH_TEST_FIRST_OF_DAY:
                setPathTestFirstOfDay();
                break;
            case PATH_TEST_FIRST_OF_VISIT:
                setPathTestFirstOfVisit();
                break;
            case PATH_TEST_OTHER:
                setPathTestOther();
                break;
            case PATH_TEST_NONE:
                setPathNoTests();
                break;
            case PATH_STUDY_OVER:
                setPathOver();
                break;
        }
    }

    @Override
    public void setPathSetupAvailability(){
        setPathSetupAvailability(8,18,false);
    }

    @Override
    public void setPathNoTests(){
        List<BaseFragment> fragments = new ArrayList<>();

        String language = PreferencesManager.getInstance().getString("language", "en");
        String country = PreferencesManager.getInstance().getString("country", "US");
        Locale locale = new Locale(language, country);


        if(isTestCompleteFlagSet()){

            ParticipantState state = Study.getParticipant().getState();

            if(state.currentTestCycle==10){
                addStudyFinishedScreen();
            } else if(state.currentTestDay==0 && state.currentTestSession==0) {
                addPostTestProgressAndEarnings();
                addCycleFinishedScreen();
                Config.OPENED_FROM_VISIT_NOTIFICATION = true;
            } else {
                addPostTestProgressAndEarnings();
            }

//            addFinishedPage();
            setTestCompleteFlag(false);
        }

        if (Config.OPENED_FROM_VISIT_NOTIFICATION) {
            Config.OPENED_FROM_VISIT_NOTIFICATION = false;

            DateTime currVisitStart = Study.getInstance().getCurrentTestCycle().getActualStartDate();
            DateTime currVisitEnd = Study.getInstance().getCurrentTestCycle().getActualEndDate();
            if (!(currVisitStart.isBeforeNow() && currVisitEnd.isAfterNow())) {
                //addAdjustSchedulePopup();

                DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM d").withLocale(locale);

                String start = fmt.print(currVisitStart);
                String end = fmt.print(currVisitEnd.minusDays(1));

                String headerText = Application.getInstance().getResources().getString(R.string.overlay_nextcycle).replace("{DATE1}", start);
                headerText = headerText.replace("{DATE2}", end);

                fragments.add(new TwoBtnPopupScreen(headerText, "", ViewUtil.getString(R.string.button_confirm), ViewUtil.getString(R.string.button_adjustschedule)));

            }
        }

        fragments.add(new LandingScreen());
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

}
