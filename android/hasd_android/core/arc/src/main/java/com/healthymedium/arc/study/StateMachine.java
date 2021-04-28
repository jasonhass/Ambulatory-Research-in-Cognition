//
// StateMachine.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import android.content.res.Resources;
import android.text.Html;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.tests.CognitiveTest;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.paths.availability.AvailabilityBed;
import com.healthymedium.arc.paths.availability.AvailabilityConfirm;
import com.healthymedium.arc.paths.battery_optimization.BatteryOptimizationOverview;
import com.healthymedium.arc.paths.battery_optimization.BatteryOptimizationPrompt;
import com.healthymedium.arc.paths.informative.DayProgressScreen;
import com.healthymedium.arc.paths.informative.EarningsPostTestLoadingScreen;
import com.healthymedium.arc.paths.informative.FinishedCycleScreen;
import com.healthymedium.arc.paths.informative.FinishedStudyScreen;
import com.healthymedium.arc.paths.informative.FinishedStudyTotalsScreen;
import com.healthymedium.arc.paths.informative.RebukedCommitmentScreen;
import com.healthymedium.arc.paths.notification.NotificationOverview;
import com.healthymedium.arc.paths.notification.NotificationTurnOn;
import com.healthymedium.arc.paths.questions.QuestionNonRemoteStudyCommitment;
import com.healthymedium.arc.paths.questions.QuestionRemoteStudyCommitment;
import com.healthymedium.arc.paths.templates.StateInfoTemplate;
import com.healthymedium.arc.paths.templates.TestInfoTemplate;
import com.healthymedium.arc.paths.tests.Grid2Letters;
import com.healthymedium.arc.paths.tests.Grid2Study;
import com.healthymedium.arc.paths.tests.Grid2Test;
import com.healthymedium.arc.paths.tests.TestIntro;
import com.healthymedium.arc.paths.tests.TestProgress;
import com.healthymedium.arc.time.TimeUtil;


import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.SimplePopupScreen;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.path_data.AvailabilityPathData;
import com.healthymedium.arc.path_data.ContextPathData;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.path_data.PriceTestPathData;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.path_data.SymbolsTestPathData;
import com.healthymedium.arc.path_data.WakePathData;
import com.healthymedium.arc.paths.availability.AvailabilityWake;
import com.healthymedium.arc.paths.questions.QuestionAdjustSchedule;
import com.healthymedium.arc.paths.setup.SetupAuthCode;
import com.healthymedium.arc.paths.questions.QuestionCheckBoxes;
import com.healthymedium.arc.paths.questions.QuestionDuration;
import com.healthymedium.arc.paths.questions.QuestionInteger;
import com.healthymedium.arc.paths.questions.QuestionPolar;
import com.healthymedium.arc.paths.questions.QuestionRadioButtons;
import com.healthymedium.arc.paths.questions.QuestionRating;
import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.paths.setup.SetupParticipant;
import com.healthymedium.arc.paths.setup.SetupParticipantConfirm;
import com.healthymedium.arc.paths.setup.SetupWelcome;
import com.healthymedium.arc.paths.tests.GridLetters;
import com.healthymedium.arc.paths.tests.GridStudy;
import com.healthymedium.arc.paths.tests.GridTest;
import com.healthymedium.arc.paths.tests.PriceTestCompareFragment;
import com.healthymedium.arc.paths.tests.PriceTestMatchFragment;
import com.healthymedium.arc.paths.tests.QuestionInterrupted;
import com.healthymedium.arc.paths.tests.SymbolTest;
import com.healthymedium.arc.paths.tests.TestBegin;
import com.healthymedium.arc.utilities.CacheManager;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.PriceManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StateMachine {

    public static final String TAG_STUDY_STATE_CACHE = "StudyStateCache";
    public static final String TAG_STUDY_STATE = "StudyState";
    public static final String TAG_TEST_COMPLETE = "TestCompleteFlag";
    public static int AUTOMATED_TESTS_RANDOM_SEED = -1;

    protected String tag = getClass().getSimpleName();

    protected State state;
    protected StateCache cache;
    protected boolean currentlyInTestPath = false;

    public StateMachine() {

    }

    protected void enableTransition(PathSegment segment, boolean animateEntry){
        int size = segment.fragments.size();
        if(size==0){
            return;
        }

        segment.fragments.get(0).setTransitionSet(TransitionSet.getSlidingDefault(animateEntry));
        for(int i=1;i<size;i++){
            segment.fragments.get(i).setTransitionSet(TransitionSet.getSlidingDefault());
        }
    }

    protected void enableTransitionGrids(PathSegment segment, boolean animateEntry){
        if (animateEntry) {
            segment.fragments.get(1).setTransitionSet(TransitionSet.getSlidingDefault());
            segment.fragments.get(2).setTransitionSet(TransitionSet.getSlidingDefault());
        }
    }

    public void initialize(){
        state = new State();
        cache = new StateCache();
    }

    public void load(){
        load(false);
    }

    public void load(boolean overwrite){

        if(state!=null && !overwrite){
            return;
        }
        state = PreferencesManager.getInstance().getObject(TAG_STUDY_STATE, State.class);
        cache = CacheManager.getInstance().getObject(TAG_STUDY_STATE_CACHE, StateCache.class);

        if(cache==null) {
            cache = new StateCache();
        }
        if(cache.segments==null) {
            cache.segments = new ArrayList<>();
        }
        if(cache.data==null) {
            cache.data = new ArrayList<>();
        }

    }

    public void save(){
        save(false);
    }

    public void save(boolean saveCache){

        PreferencesManager.getInstance().putObject(TAG_STUDY_STATE, state);
        CacheManager.getInstance().putObject(TAG_STUDY_STATE_CACHE,cache);
        if(saveCache){
            CacheManager.getInstance().save(TAG_STUDY_STATE_CACHE);
        }
    }

    public void decidePath(){

    }

    public void abandonTest(){
        Participant participant = Study.getParticipant();


        participant.getCurrentTestSession().markAbandoned();


        for(PathSegment segment : cache.segments){
            BaseData object = segment.collectData();
            if(object!=null){
                cache.data.add(object);
            }
        }

        loadTestDataFromCache();
        cache.segments.clear();
        cache.data.clear();

        RestClient client = Study.getRestClient();
        client.submitTest(participant.getCurrentTestSession());
        participant.moveOnToNextTestSession(true);
        save();
    }

    protected void setupPath(){

    }

    // this is where we can use the cache of segments
    protected void endOfPath(){

    }

    public boolean skipToNextSegment(){

        if(cache.segments.size() > 0) {
            BaseData object = cache.segments.get(0).collectData();
            if (object != null) {
                cache.data.add(object);
            }
            cache.segments.remove(0);

            NavigationManager.getInstance().clearBackStack();
            Study.getInstance().getParticipant().save();
            save();
        }

        if(cache.segments.size()>0){
            return openNext();
        } else {
            endOfPath();
            cache.data.clear();
            decidePath();
            setupPath();
            return openNext();
        }
    }

    public boolean openNext() {
        return openNext(0);
    }

    public boolean openNext(int skips){
        save();
        if(cache.segments.size()>0){
            if(cache.segments.get(0).openNext(skips)) {
                return true;
            } else {
                return endOfSegment();
            }
        } else {
            return moveOn();
        }
    }

    protected boolean endOfSegment(){
        // else at the end of segment
        BaseData object = cache.segments.get(0).collectData();
        if(object!=null){
            cache.data.add(object);
        }
        cache.segments.remove(0);

        NavigationManager.getInstance().clearBackStack();
        NavigationManager.getInstance().removeController();
        Study.getInstance().getParticipant().save();
        save();

        if(cache.segments.size()>0){
            return openNext();
        } else {
            endOfPath();
            return moveOn();
        }
    }

    protected boolean moveOn(){
        cache.data.clear();
        decidePath();
        setupPath();
        Study.getInstance().getParticipant().save();
        save();
        return openNext();
    }

    public boolean openPrevious() {
        return openPrevious(0);
    }

    public boolean openPrevious(int skips){
        if(cache.segments.size()>0){
            return cache.segments.get(0).openPrevious(skips);
        }
        return false;
    }

    // ------------------------------------------


    protected void setTestCompleteFlag(boolean complete){

        PreferencesManager.getInstance().putBoolean(TAG_TEST_COMPLETE,complete);
    }

    protected boolean isTestCompleteFlagSet(){
        return PreferencesManager.getInstance().getBoolean(TAG_TEST_COMPLETE,false);
    }

    public boolean isCurrentlyInTestPath(){
        return currentlyInTestPath;
    }

    public boolean hasValidFragments() {
        if(cache.segments.size() == 0) {
            return false;
        }

        for(int i = 0; i < cache.segments.size(); i++) {
            if(cache.segments.get(i).fragments.size() == 0) {
                return false;
            }
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    public void setPathSetupParticipant(int firstDigitCount, int secondDigitCount, int authDigitCount){
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new SetupWelcome());
        fragments.add(new SetupParticipant(firstDigitCount,secondDigitCount));

        if (Config.EXPECTS_2FA_TEXT) {
            fragments.add(new SetupParticipantConfirm(false,true,firstDigitCount,secondDigitCount));
            fragments.add(new SetupAuthCode(true, true, authDigitCount, ViewUtil.getString(R.string.login_enter_2FA)));
        }
        else {
            fragments.add(new SetupParticipantConfirm(false,false,firstDigitCount,secondDigitCount));
            fragments.add(new SetupAuthCode(true, false, authDigitCount, ViewUtil.getString(R.string.login_enter_ARCID)));
        }

        PathSegment segment = new PathSegment(fragments,SetupPathData.class);
        enableTransition(segment,false);
        cache.segments.add(segment);
    }

    // default
    public void setPathSetupParticipant(){
        setPathSetupParticipant(5,3,5);
    }

    // ---------------------------------------------------------------------------------------------

    public void setPathCommitment(){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        if (Config.IS_REMOTE) {
            fragments.add(new QuestionRemoteStudyCommitment(
                    false,
                    res.getString(R.string.onboarding_header),
                    res.getString(R.string.onboarding_body),
                    res.getString(R.string.radio_commit),
                    res.getString(R.string.radio_nocommit)
            ));
        }
        else {
            List<String> opts = new ArrayList<>();
            opts.add(res.getString(R.string.radio_commit));

            fragments.add(new QuestionNonRemoteStudyCommitment(
                false,
                res.getString(R.string.onboarding_header),
                res.getString(R.string.onboarding_body),
                opts,
                ""
            ));
        }

        fragments.add(new StateInfoTemplate(
                false,
                Html.fromHtml(res.getString(R.string.onboarding_commit_header)).toString(),
                null,
                res.getString(R.string.onboarding_commit_body),
                res.getString(R.string.button_next)));

        PathSegment segment = new PathSegment(fragments);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void setPathCommitmentRebuked(){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new RebukedCommitmentScreen(
                res.getString(R.string.onboarding_nocommit_landing_header),
                res.getString(R.string.onboarding_nocommit_landing_body),
                res.getString(R.string.button_next)
        ));

        fragments.add(new QuestionRemoteStudyCommitment(
                true,
                res.getString(R.string.onboarding_header),
                res.getString(R.string.onboarding_body),
                res.getString(R.string.radio_commit),
                res.getString(R.string.radio_nocommit)
        ));

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.onboarding_commit_header),
                null,
                res.getString(R.string.onboarding_commit_body),
                res.getString(R.string.button_next)));

        PathSegment segment = new PathSegment(fragments);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void setPathNotificationOverview(){
        List<BaseFragment> fragments = new ArrayList<>();

        fragments.add(new NotificationOverview());
        fragments.add(new NotificationTurnOn());

        PathSegment segment = new PathSegment(fragments);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void setPathBatteryOptimizationOverview(){
        List<BaseFragment> fragments = new ArrayList<>();

        fragments.add(new BatteryOptimizationOverview());
        fragments.add(new BatteryOptimizationPrompt());

        PathSegment segment = new PathSegment(fragments);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    // ---------------------------------------------------------------------------------------------

    public void setPathSetupAvailability(int minWakeTime, int maxWakeTime, boolean reschedule){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.availability_header),
                null,
                res.getString(R.string.availability_body),
                res.getString(R.string.button_beginsurvey)));

        fragments.add(new AvailabilityWake());
        fragments.add(new AvailabilityBed(minWakeTime,maxWakeTime));
        fragments.add(new AvailabilityConfirm(minWakeTime, maxWakeTime, reschedule, true));

        PathSegment segment = new PathSegment(fragments,AvailabilityPathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    // default
    public void setPathSetupAvailability(){
        setPathSetupAvailability(4,24,false);
    }

    // ---------------------------------------------------------------------------------------------

    public void addChronotypeSurvey(){
        
    }

    public void addWakeSurvey(){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.wakesurvey_header),
                res.getString(R.string.wakesurvey_subheader),
                res.getString(R.string.wakesurvey_body),
                res.getString(R.string.button_beginsurvey)));


        CircadianClock clock = Study.getParticipant().getCircadianClock();
        CircadianRhythm rhythm;
        String weekday;


        weekday = TimeUtil.getWeekday();
        if(!clock.hasWakeRhythmChanged(weekday)){
            weekday = TimeUtil.getWeekday(DateTime.now().minusDays(1));
        }
        rhythm = clock.getRhythm(weekday);
        LocalTime wakeTime = rhythm.getWakeTime();


        weekday = TimeUtil.getWeekday();
        if(!clock.hasBedRhythmChanged(weekday)){
            weekday = TimeUtil.getWeekday(DateTime.now().minusDays(1));
        }
        rhythm = clock.getRhythm(weekday);
        LocalTime bedTime = rhythm.getWakeTime();


        fragments.add(new QuestionTime(true, res.getString(R.string.wake_q1),"",bedTime));
        fragments.add(new QuestionDuration(true, res.getString(R.string.wake_q2)," "));
        fragments.add(new QuestionInteger(true, res.getString(R.string.wake_q3a), res.getString(R.string.wake_q3b),2));
        fragments.add(new QuestionTime(true, res.getString(R.string.wake_q4)," ",wakeTime));
        fragments.add(new QuestionTime(true, res.getString(R.string.wake_q5)," ",wakeTime));
        fragments.add(new QuestionRating(true, res.getString(R.string.wake_q6), "", res.getString(R.string.wake_poor), res.getString(R.string.wake_excellent)));

        PathSegment segment = new PathSegment(fragments,WakePathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void addContextSurvey(){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.context_header),
                res.getString(R.string.context_subheader),
                res.getString(R.string.context_body),
                res.getString(R.string.button_beginsurvey)));

        List<String> who = new ArrayList<>();
        who.add(res.getString(R.string.context_q1_a1));
        who.add(res.getString(R.string.context_q1_a2));
        who.add(res.getString(R.string.context_q1_a3));
        who.add(res.getString(R.string.context_q1_a4));
        who.add(res.getString(R.string.context_q1_a5));
        who.add(res.getString(R.string.context_q1_a6));
        who.add(res.getString(R.string.context_q1_a7));
        fragments.add(new QuestionCheckBoxes(true, res.getString(R.string.context_q1), res.getString(R.string.list_selectall), who, res.getString(R.string.context_q1_a1)));

        List<String> where = new ArrayList<>();
        where.add(res.getString(R.string.context_q2_a1));
        where.add(res.getString(R.string.context_q2_a2));
        where.add(res.getString(R.string.context_q2_a3));
        where.add(res.getString(R.string.context_q2_a4));
        where.add(res.getString(R.string.context_q2_a5));
        where.add(res.getString(R.string.context_q2_a6));
        where.add(res.getString(R.string.context_q2_a7));
        fragments.add(new QuestionRadioButtons(true, false, res.getString(R.string.context_q2), res.getString(R.string.list_selectone), where));

        fragments.add(new QuestionRating(true, res.getString(R.string.context_q3), "", res.getString(R.string.context_bad), res.getString(R.string.context_good)));
        fragments.add(new QuestionRating(true, res.getString(R.string.context_q4), "", res.getString(R.string.context_tired), res.getString(R.string.context_active)));

        List<String> what = new ArrayList<>();
        what.add(res.getString(R.string.context_q5_a1));
        what.add(res.getString(R.string.context_q5_a2));
        what.add(res.getString(R.string.context_q5_a3));
        what.add(res.getString(R.string.context_q5_a4));
        what.add(res.getString(R.string.context_q5_a5));
        what.add(res.getString(R.string.context_q5_a6));
        what.add(res.getString(R.string.context_q5_a7));
        what.add(res.getString(R.string.context_q5_a8));
        what.add(res.getString(R.string.context_q5_a9));
        what.add(res.getString(R.string.context_q5_a10));
        fragments.add(new QuestionRadioButtons(true, false, res.getString(R.string.context_q5), "", what));

        PathSegment segment = new PathSegment(fragments,ContextPathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void addTests(){

        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        TestIntro info = new TestIntro();
//        StateInfoTemplate info = new StateInfoTemplate(
//                false,
//                res.getString(R.string.testing_intro_header),
//                res.getString(R.string.testing_intro_subhead),
//                res.getString(R.string.testing_intro_body),
//                res.getString(R.string.button_next));
        //info.setEnterTransitions(R.anim.slide_in_right,R.anim.slide_in_left);
        fragments.add(info);
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);

        Integer[] orderArray = new Integer[]{1,2,3};
        List<Integer> order = Arrays.asList(orderArray);
        if(AUTOMATED_TESTS_RANDOM_SEED == -1){
            Collections.shuffle(order);
        }else{
            Collections.shuffle(order, new Random(AUTOMATED_TESTS_RANDOM_SEED));
        }

        for(int i =0;i<3;i++){
            switch(order.get(i)){
                case 1:
                    addSymbolsTest(i);
                    break;
                case 2:
                    addPricesTest(i);
                    break;
                case 3:
                    addGridTest(i);
                    break;
            }
        }
    }

    public void addPricesTest(int index){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        String testNumber = getTestNumberString(index);

        TestInfoTemplate info = new TestInfoTemplate(
                testNumber,
                ViewUtil.getHtmlString(R.string.prices_header),
                ViewUtil.getHtmlString(R.string.prices_body),
                "prices",
                ViewUtil.getHtmlString(R.string.button_begintest));
        fragments.add(info);

        fragments.add(new TestBegin());

        int size = PriceManager.getInstance().getPriceSet().size();
        for(int i=0;i<size;i++){
            fragments.add(new PriceTestCompareFragment(i));
        }

        fragments.add(new SimplePopupScreen(
                ViewUtil.getHtmlString(R.string.prices_overlay),
                ViewUtil.getHtmlString(R.string.button_begin),
                3000,
                15000,
                true));

        fragments.add(new PriceTestMatchFragment());
        fragments.add(new TestProgress(ViewUtil.getString(R.string.prices_complete), index));
        PathSegment segment = new PathSegment(fragments,PriceTestPathData.class);
        cache.segments.add(segment);
    }

    public void addSymbolsTest(int index){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        String testNumber = getTestNumberString(index);

        TestInfoTemplate info = new TestInfoTemplate(
                testNumber,
                ViewUtil.getHtmlString(R.string.symbols_header),
                ViewUtil.getHtmlString(R.string.symbols_body),
                "symbols",
                ViewUtil.getHtmlString(R.string.button_begintest));
        fragments.add(info);

        fragments.add(new TestBegin());

        fragments.add(new SymbolTest());
        fragments.add(new TestProgress(ViewUtil.getString(R.string.symbols_complete), index));
        PathSegment segment = new PathSegment(fragments,SymbolsTestPathData.class);
        cache.segments.add(segment);
    }

    public void addGridTest(int index){
        switch (Config.TEST_VARIANT_GRID) {
            case V1:
                addGrid1Test(index);
                break;
            case V2:
                addGrid2Test(index);
                break;
        }
    }

    public void addGrid1Test(int index){
        List<BaseFragment> fragments = new ArrayList<>();

        String testNumber = getTestNumberString(index);

        TestInfoTemplate info0 = new TestInfoTemplate(
                testNumber,
                ViewUtil.getHtmlString(R.string.grids_header),
                ViewUtil.getHtmlString(R.string.grids_body),
                "grids",
                ViewUtil.getHtmlString(R.string.button_begintest));
        fragments.add(info0);

        fragments.add(new TestBegin());
        fragments.add(new GridStudy());
        fragments.add(new GridLetters());
        fragments.add(new GridTest());
        fragments.add(new GridStudy());
        fragments.add(new GridLetters());
        GridTest gridTestFragment = new GridTest();
        gridTestFragment.second = true;
        fragments.add(gridTestFragment);
        fragments.add(new TestProgress(ViewUtil.getString(R.string.grids_complete), index));
        PathSegment segment = new PathSegment(fragments,GridTestPathData.class);
        enableTransitionGrids(segment,true);
        cache.segments.add(segment);
    }

    public void addGrid2Test(int index){
        List<BaseFragment> fragments = new ArrayList<>();

        String testNumber = getTestNumberString(index);

        TestInfoTemplate info0 = new TestInfoTemplate(
                testNumber,
                ViewUtil.getHtmlString(R.string.grids_header),
                ViewUtil.getHtmlString(R.string.grids_body),
                "grids",
                ViewUtil.getHtmlString(R.string.button_begintest));
        fragments.add(info0);

        fragments.add(new TestBegin());
        fragments.add(new Grid2Study());
        fragments.add(new Grid2Letters());
        fragments.add(new Grid2Test());
        fragments.add(new Grid2Study());
        fragments.add(new Grid2Letters());
        fragments.add(new Grid2Test());

        fragments.add(new TestProgress(ViewUtil.getString(R.string.grids_complete), index));

        PathSegment segment = new PathSegment(fragments,GridTestPathData.class);
        enableTransitionGrids(segment,true);
        cache.segments.add(segment);
    }

    private String getTestNumberString(final int index) {
        String testNumberString = "invalid test index";
        switch(index) {
            case 0:
                testNumberString = ViewUtil.getHtmlString(R.string.testing_header_1);
                break;
            case 1:
                testNumberString = ViewUtil.getHtmlString(R.string.testing_header_2);
                break;
            case 2:
                testNumberString = ViewUtil.getHtmlString(R.string.testing_header_3);
                break;
            default:
                break;
        }

        return testNumberString;
    }

    public void addInterruptedPage(){

        Resources res = Application.getInstance().getResources();

        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new QuestionInterrupted(false, ViewUtil.getHtmlString(R.string.testing_interrupted_body),""));
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

//    public void addFinishedPage(){
//        List<BaseFragment> fragments = new ArrayList<>();
//
//        Participant participant = Study.getParticipant();
//
//        Resources res = Application.getInstance().getResources();
//
//        String header;
//        String subheader;
//        String body;
//
//        // Default
//        header = ViewUtil.getHtmlString(R.string.thankyou_header1);
//        subheader = ViewUtil.getHtmlString(R.string.thankyou_testcomplete_subhead1);
//        body = ViewUtil.getHtmlString(R.string.thankyou_testcomplete_body1);
//
//        // Finished with study
//        if(!participant.isStudyRunning()){
//            //at the end of the line
//            header = ViewUtil.getHtmlString(R.string.thankyou_header3);
//            subheader = ViewUtil.getHtmlString(R.string.thankyou_finished_subhead3);
//            body = ViewUtil.getHtmlString(R.string.thankyou_body3);
//        }
//        else {
//            TestCycle cycle = participant.getCurrentTestCycle();
//
//            // After the testCycles but before the next session
//            if (cycle.getNumberOfTestsLeft() == cycle.getNumberOfTests()) {
//
//                DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM d").withLocale(Locale.getCurrent());
//
//                //String format = ViewUtil.getString(com.healthymedium.arc.library.R.string.format_date);
//                header = ViewUtil.getHtmlString(R.string.thankyou_header2);
//                subheader = ViewUtil.getHtmlString(R.string.thankyou_cycle_subhead2);
//
//                String body2 = ViewUtil.getHtmlString(R.string.thankyou_cycle_body2);
//
//                // String startDate = testCycles.getActualStartDate().toString(format);
//                // String endDate = testCycles.getActualEndDate().toString(format);
//
//                String startDate = fmt.print(cycle.getActualStartDate());
//                String endDate = fmt.print(cycle.getActualEndDate().minusDays(1));
//
//                body2 = body2.replace("{DATE1}", startDate);
//                body2 = body2.replace("{DATE2}", endDate);
//
//                body = body2;
//            }
//            // After the 4th test of the day
//            else if (participant.getCurrentTestDay().getNumberOfTestsLeft() == 0) {
//                header = ViewUtil.getHtmlString(R.string.thank_you_header1);
//                subheader = ViewUtil.getHtmlString(R.string.thankyou_alldone_subhead1);
//                body = ViewUtil.getHtmlString(R.string.thankyou_alldone_body1);
//            }
//
//        }
//
//        InfoTemplate info = new InfoTemplate(
//                false,
//                header ,
//                subheader,
//                body,
//                ViewUtil.getDrawable(R.drawable.ic_home_active));
//        fragments.add(info);
//        PathSegment segment = new PathSegment(fragments);
//        cache.segments.add(segment);
//    }

    public void addSchedulePicker() {
        List<BaseFragment> fragments = new ArrayList<>();

        fragments.add(new QuestionAdjustSchedule(false, true, ViewUtil.getString(R.string.dateshift_picker), ""));

        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    public void setPathAdjustSchedule() {
        addSchedulePicker();
    }

    public void addPostTestProgressAndEarnings(){
        List<BaseFragment> fragments = new ArrayList<>();

        fragments.add(new DayProgressScreen());
        fragments.add(new EarningsPostTestLoadingScreen());

        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    public void addCycleFinishedScreen(){
        List<BaseFragment> fragments = new ArrayList<>();

        fragments.add(new FinishedCycleScreen());

        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    public void addStudyFinishedScreen(){
        List<BaseFragment> fragments = new ArrayList<>();

        fragments.add(new FinishedStudyScreen());
        fragments.add(new FinishedStudyTotalsScreen());

        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }


    // -----------------------

    public String getLifecycleName(int lifecycle){
        return "";
    }

    public String getPathName(int path){
        return "";
    }

    public State getState(){
        return state;
    }

    public StateCache getCache(){
        return cache;
    }

    // loadTestDataFromCache() is called from abandonTest().
    // Override this method to handle loading test data from cache.
    public void loadTestDataFromCache() {

    }

    public void loadCognitiveTestFromCache(){

        CognitiveTest cognitiveTest = new CognitiveTest();
        cognitiveTest.load(cache.data);
        Study.getInstance().getCurrentTestSession().addTestData(cognitiveTest);
    }

}
