//
// StateMachineAlpha.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.notifications.NotificationUtil;
import com.healthymedium.arc.paths.home.LandingScreen;
import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.questions.QuestionSignature;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class StateMachineAlpha extends StateMachine {

    public static final int PATH_SETUP_PARTICIPANT = 0;         //
    public static final int PATH_COMMITMENT = 9;
    public static final int PATH_COMMITMENT_REBUKED = 10;
    public static final int PATH_NOTIFICATIONS_OVERVIEW = 11;
    public static final int PATH_BATTERY_OPTIMIZATION_OVERVIEW = 12;

    public static final int PATH_SETUP_AVAILABILITY = 1;        //

    public static final int PATH_TEST_FIRST_OF_BASELINE = 2;    // first test of the baseline
    public static final int PATH_TEST_BASELINE = 3;             // every other test in the baseline

    public static final int PATH_TEST_NONE = 4;                 // no tests available
    public static final int PATH_TEST_FIRST_OF_VISIT = 5;       // first test of a visit, this trumps the first day path
    public static final int PATH_TEST_FIRST_OF_DAY = 6;         // first test on a given day
    public static final int PATH_TEST_OTHER = 7;                // every test that doesn't listed above

    public static final int PATH_STUDY_OVER = 8;                //

    public static final int LIFECYCLE_INIT = 0;                 //
    public static final int LIFECYCLE_BASELINE = 1;             //
    public static final int LIFECYCLE_IDLE = 3;                 //
    public static final int LIFECYCLE_ARC = 4;                  //
    public static final int LIFECYCLE_OVER = 5;                 //

    @Override
    public void initialize() {
        super.initialize();
        state.lifecycle = LIFECYCLE_INIT;
        state.currentPath = PATH_SETUP_PARTICIPANT;
    }

    // deciding paths ------------------------------------------------------------------------------

    @Override
    public void decidePath(){



        switch (state.lifecycle) {
            case LIFECYCLE_INIT:
                decidePathInit();
                break;
            case LIFECYCLE_BASELINE:
                decidePathBaseline();
                break;
            case LIFECYCLE_ARC:
                decidePathArc();
                break;
            case LIFECYCLE_IDLE:
                decidePathIdle();
                break;
            case LIFECYCLE_OVER:
                decidePathOver();
                break;
        }
    }

    private void decidePathInit(){
        cache.segments.clear();
        Participant participant = Study.getParticipant();

        if(!participant.hasId()){
            state.currentPath = PATH_SETUP_PARTICIPANT;
            return;
        }

        if(participant.hasRebukedCommitmentToStudy()){
            state.currentPath = PATH_COMMITMENT_REBUKED;
            return;
        }

        if(!participant.hasCommittedToStudy()){
            state.currentPath = PATH_COMMITMENT;
            return;
        }

        if(!participant.hasBeenShownNotificationOverview()){
            state.currentPath = PATH_NOTIFICATIONS_OVERVIEW;
            return;
        }

        if(!NotificationUtil.areNotificationsEnabled(Application.getInstance())){
            state.currentPath = PATH_NOTIFICATIONS_OVERVIEW;
            return;
        }

        if(!participant.hasBeenShownBatteryOptimizationOverview()){
            state.currentPath = PATH_BATTERY_OPTIMIZATION_OVERVIEW;
            return;
        }

        if(!participant.hasSchedule()){
            state.currentPath = PATH_SETUP_AVAILABILITY;
            return;
        }

        if(participant.getState().currentTestCycle == 0){

            state.lifecycle = LIFECYCLE_BASELINE;
        } else if(participant.getCurrentTestCycle() == null) {

            state.lifecycle = LIFECYCLE_OVER;
        } else {

            state.lifecycle = LIFECYCLE_IDLE;

        }
        decidePath();
    }

    private void decidePathBaseline(){
        Participant participant = Study.getInstance().getParticipant();

        if(!NotificationUtil.areNotificationsEnabled(Application.getInstance())){

            state.currentPath = PATH_NOTIFICATIONS_OVERVIEW;
            return;
        }

        if(participant.getCurrentTestCycle() == null) {
            state.lifecycle = LIFECYCLE_OVER;
            decidePath();
            return;
        }

        if (participant.getCurrentTestSession().isOngoing()) {

            abandonTest();
            decidePath();
            return;
        }

        cache.segments.clear();

        if(participant.getCurrentTestCycle().getActualStartDate().isAfterNow()){

            state.lifecycle = LIFECYCLE_IDLE;
            decidePath();
            return;
        }

        if (participant.getCurrentTestSession().getScheduledTime().minusMinutes(5).isAfterNow()) {

            state.currentPath = PATH_TEST_NONE;
            return;
        }

        if (participant.getCurrentTestSession().getExpirationTime().isBeforeNow()) {

            participant.getCurrentTestSession().markMissed();
            loadTestDataFromCache();
            cache.data.clear();

            RestClient client = Study.getRestClient();
            client.submitTest(participant.getCurrentTestSession());
            participant.moveOnToNextTestSession(true);
            save();
            decidePath();
            return;
        }

        currentlyInTestPath = true;

        if (!participant.getCurrentTestCycle().hasThereBeenAFinishedTest()){

            state.currentPath = PATH_TEST_FIRST_OF_BASELINE;
            return;
        }

        if (!participant.getCurrentTestDay().hasThereBeenAFinishedTest()) {

            state.currentPath = PATH_TEST_FIRST_OF_DAY;
            return;
        }


        state.currentPath = PATH_TEST_BASELINE;
    }

    private void decidePathArc(){
        Participant participant = Study.getInstance().getParticipant();

        if(!NotificationUtil.areNotificationsEnabled(Application.getInstance())){

            state.currentPath = PATH_NOTIFICATIONS_OVERVIEW;
            return;
        }

        if(participant.getCurrentTestCycle() == null) {
            state.lifecycle = LIFECYCLE_OVER;
            decidePath();
            return;
        }

        TestCycle cycle = participant.getCurrentTestCycle();
        TestDay day = participant.getCurrentTestDay();

        if(!participant.isStudyRunning()){

            state.lifecycle = LIFECYCLE_OVER;
            decidePath();
            return;
        }

        if (participant.getCurrentTestSession().isOngoing()) {
            abandonTest();
            decidePath();
            return;
        }

        cache.segments.clear();

        if(cycle.getActualStartDate().isAfterNow()){

            state.lifecycle = LIFECYCLE_IDLE;
            decidePath();
            return;
        }

        if (participant.getCurrentTestSession().getScheduledTime().minusMinutes(5).isAfterNow()) {

            state.currentPath = PATH_TEST_NONE;
            return;
        }

        if (participant.getCurrentTestSession().getExpirationTime().isBeforeNow()) {

            participant.getCurrentTestSession().markMissed();

            RestClient client = Study.getRestClient();
            client.submitTest(participant.getCurrentTestSession());
            participant.moveOnToNextTestSession(true);
            participant.save();
            decidePath();
            return;
        }

        currentlyInTestPath = true;

        if (!cycle.hasThereBeenAFinishedTest()){

            state.currentPath = PATH_TEST_FIRST_OF_VISIT;
            return;
        }

        if (!day.hasThereBeenAFinishedTest()) {

            state.currentPath = PATH_TEST_FIRST_OF_DAY;
            return;
        }


        state.currentPath = PATH_TEST_OTHER;
    }

    private void decidePathIdle() {
        TestCycle cycle = Study.getCurrentTestCycle();

        if(!NotificationUtil.areNotificationsEnabled(Application.getInstance())){
            state.currentPath = PATH_NOTIFICATIONS_OVERVIEW;
            return;
        }

        if (cycle.getActualStartDate().isBeforeNow()) {
            state.lifecycle = LIFECYCLE_ARC;
            decidePath();
        } else {
            state.currentPath = PATH_TEST_NONE;
        }
    }

    private void decidePathOver() {
        state.currentPath = PATH_STUDY_OVER;
    }

    // setting up paths ----------------------------------------------------------------------------

//    @Override
//    protected void setupPath(){
//
//    }

    @Override
    protected void endOfPath(){




        switch (state.lifecycle) {
            case LIFECYCLE_INIT:
                switch (state.currentPath){
                    case PATH_SETUP_PARTICIPANT:
                        break;
                    case PATH_SETUP_AVAILABILITY:
                        break;
                }
                break;
            case LIFECYCLE_BASELINE:
            case LIFECYCLE_ARC:
                switch (state.currentPath){
                    case PATH_TEST_NONE:
                        break;
                    case PATH_NOTIFICATIONS_OVERVIEW:
                        break;
                    default:

                        // set up a loading dialog in case this takes a bit
                        LoadingDialog dialog = new LoadingDialog();
                        dialog.show(NavigationManager.getInstance().getFragmentManager(),"LoadingDialog");

                        TestSession currentTest = Study.getCurrentTestSession();
                        currentTest.markCompleted();
                        if(Study.getCurrentTestDay().getNumberOfTestsAvailableNow()==0){
                            setTestCompleteFlag(true);
                        }
                        loadTestDataFromCache();

                        RestClient client = Study.getRestClient();
                        client.submitTest(Study.getCurrentTestSession());
                        Study.getParticipant().moveOnToNextTestSession(true);
                        save();

                        dialog.dismiss();

                        break;
                }
                break;
            case LIFECYCLE_IDLE:
                break;
            case LIFECYCLE_OVER:
                break;
        }
        currentlyInTestPath = false;
    }

    // state machine helpers ---------------------------------------------------------------------

    public void addWelcome() {
//        List<BaseFragment> fragments = new ArrayList<>();
//
//        if(Config.IS_REMOTE) {
//            // I commit or I'm not able to commit
//            fragments.add(new QuestionRemoteStudyCommitment(
//                    true,
//                    ViewUtil.getString(R.string.testing_commitment),
//                    ViewUtil.getString(R.string.onboarding_body),
//                    ViewUtil.getString(R.string.radio_commit),
//                    ViewUtil.getString(R.string.radio_nocommit)
//            ));
//
//        } else {
//            // I understand
//            fragments.add(new QuestionSingleButton(
//                    false,
//                    ViewUtil.getString(R.string.onboarding_header),
//                    ViewUtil.getString(R.string.onboarding_body),
//                    ViewUtil.getString(R.string.button_continue),
//                    ViewUtil.getString(R.string.radio_understand)));
//        }
//
//        PathSegment segment = new PathSegment(fragments);
//        enableTransition(segment,true);
//        cache.segments.add(segment);
    }

    public void checkForLandingPage(){
        if(Config.OPENED_FROM_NOTIFICATION) {
            Config.OPENED_FROM_NOTIFICATION = false;

            // In a visit
            // Try to start a test
            Study.getParticipant().getState().lastPauseTime = DateTime.now();
            Study.getCurrentTestSession().markStarted();
        } else {
            addTestLandingPage();
        }
    }

    public void addTestLandingPage(){
        List<BaseFragment> fragments = new ArrayList<>();

        // Default
        fragments.add(new LandingScreen());
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    public void checkForSignaturePage(boolean allowHelp){
        if(Config.ENABLE_SIGNATURES) {
            addSignaturePage(allowHelp);
        }
    }

    public void addSignaturePage(boolean allowHelp){
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new QuestionSignature(
                false,
                allowHelp,
                ViewUtil.getString(R.string.idverify_header),
                ViewUtil.getString(R.string.testing_id_header)));
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    // --------------------------------------------------------------------------

    public void setPathFirstOfBaseline(){
        checkForLandingPage();
        checkForSignaturePage(true);
        addChronotypeSurvey();
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }

    public void setPathBaselineTest(){
        checkForLandingPage();
        checkForSignaturePage(true);
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }


    public void setPathNoTests(){
        //  leave empty for now
    }

    public void setPathTestFirstOfVisit(){
        checkForLandingPage();
        checkForSignaturePage(true);
        addChronotypeSurvey();
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }

    public void setPathTestFirstOfDay(){
        checkForLandingPage();
        checkForSignaturePage(true);
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }

    public void setPathTestOther(){
        checkForLandingPage();
        checkForSignaturePage(true);
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }

    public void setPathOver(){
        setPathNoTests();
    }


    // utility functions ---------------------------------------------------------------------------

    @Override
    public String getLifecycleName(int lifecycle){
        switch (lifecycle){
            case LIFECYCLE_INIT:
                return "INIT";
            case LIFECYCLE_BASELINE:
                return "BASELINE";
            case LIFECYCLE_IDLE:
                return "IDLE";
            case LIFECYCLE_ARC:
                return "ARC";
            case LIFECYCLE_OVER:
                return "OVER";
            default:
                return "INVALID";
        }
    }

    @Override
    public String getPathName(int path){
        switch (path){
            case PATH_SETUP_PARTICIPANT:
                return "SETUP_PARTICIPANT";
            case PATH_SETUP_AVAILABILITY:
                return "SETUP_AVAILABILITY";
            case PATH_TEST_FIRST_OF_BASELINE:
                return "TEST_FIRST_OF_BASELINE";
            case PATH_TEST_BASELINE:
                return "TEST_BASELINE";
            case PATH_TEST_NONE:
                return "TEST_NONE";
            case PATH_TEST_FIRST_OF_VISIT:
                return "TEST_FIRST_OF_VISIT";
            case PATH_TEST_FIRST_OF_DAY:
                return "TEST_FIRST_OF_DAY";
            case PATH_TEST_OTHER:
                return "TEST_OTHER";
            case PATH_STUDY_OVER:
                return "STUDY_OVER";
            case PATH_COMMITMENT:
                return "PATH_COMMITMENT";
            case PATH_COMMITMENT_REBUKED:
                return "PATH_COMMITMENT_REBUKED";
            case PATH_NOTIFICATIONS_OVERVIEW:
                return "PATH_NOTIFICATIONS_OVERVIEW";
            case PATH_BATTERY_OPTIMIZATION_OVERVIEW:
                return "PATH_BATTERY_OPTIMIZATION_OVERVIEW";
            default:
                return "INVALID";
        }
    }

    @Override
    public void loadTestDataFromCache() {
        loadCognitiveTestFromCache();
    }
}
