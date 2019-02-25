//
// StudyStateMachine.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import android.os.Bundle;
import android.util.Log;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.SimplePopupScreen;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.AvailabilityPathData;
import com.healthymedium.arc.path_data.ChronotypePathData;
import com.healthymedium.arc.path_data.ContextPathData;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.path_data.PriceTestPathData;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.path_data.SinglePageData;
import com.healthymedium.arc.path_data.SymbolsTestPathData;
import com.healthymedium.arc.path_data.WakePathData;
import com.healthymedium.arc.paths.availability.AvailabilityMondayBed;
import com.healthymedium.arc.paths.availability.AvailabilityMondayWake;
import com.healthymedium.arc.paths.availability.AvailabilityOtherBed;
import com.healthymedium.arc.paths.availability.AvailabilityOtherWake;
import com.healthymedium.arc.paths.availability.AvailabilitySaturdayBed;
import com.healthymedium.arc.paths.availability.AvailabilitySaturdayWake;
import com.healthymedium.arc.paths.availability.AvailabilitySundayBed;
import com.healthymedium.arc.paths.availability.AvailabilitySundayWake;
import com.healthymedium.arc.paths.availability.AvailabilityWeekdayConfirm;
import com.healthymedium.arc.paths.templates.InfoTemplate;
import com.healthymedium.arc.paths.questions.QuestionCheckBoxes;
import com.healthymedium.arc.paths.questions.QuestionDuration;
import com.healthymedium.arc.paths.questions.QuestionInteger;
import com.healthymedium.arc.paths.questions.QuestionPolar;
import com.healthymedium.arc.paths.questions.QuestionRadioButtons;
import com.healthymedium.arc.paths.questions.QuestionRating;
import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.paths.setup.SetupParticipant;
import com.healthymedium.arc.paths.setup.SetupParticipantConfirm;
import com.healthymedium.arc.paths.setup.SetupSite;
import com.healthymedium.arc.paths.setup.SetupWelcome;
import com.healthymedium.arc.paths.tests.GridLetters;
import com.healthymedium.arc.paths.tests.GridStudy;
import com.healthymedium.arc.paths.tests.GridTest;
import com.healthymedium.arc.paths.tests.PriceTestCompareFragment;
import com.healthymedium.arc.paths.tests.PriceTestMatchFragment;
import com.healthymedium.arc.paths.tests.QuestionInterrupted;
import com.healthymedium.arc.paths.tests.SymbolTest;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.PriceManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StudyStateMachine {

    protected StudyState state;
    protected boolean notificationAcknowledged = false;
    protected boolean currentlyInTestPath = false;

    public StudyStateMachine() {

    }

    protected void enableTransition(PathSegment segment, boolean animateEntry){
        int size = segment.fragments.size();
        int first = 0;
        int last = size-1;

        for(int i=0;i<size;i++){
            if(i!=first || animateEntry){
                segment.fragments.get(i).setEnterTransitionRes(R.anim.slide_in_right,R.anim.slide_in_left);
            }
            segment.fragments.get(i).setExitTransitionRes(R.anim.slide_out_left,R.anim.slide_out_right);
        }
    }

    public void initialize(){
        state = new StudyState();
    }

    public void load(){
        state = PreferencesManager.getInstance().getObject("StudyState",StudyState.class);
    }


    public void save(){
        PreferencesManager.getInstance().putObject("StudyState", state);
    }

    public void decidePath(){

    }

    public void abandonTest(){
        Participant participant = Study.getParticipant();

        Log.i("StudyStateMachine", "loading in the middle of an indexed test, marking it abandoned");
        participant.getCurrentTestSession().markAbandoned();

        Log.i("StudyStateMachine", "collecting data from each existing segment");
        for(PathSegment segment : state.segments){
            BaseData object = segment.collectData();
            if(object!=null){
                state.cache.add(object);
            }
        }

        loadTestDataFromCache();
        state.segments.clear();
        state.cache.clear();

        Study.getRestClient().submitTest(participant.getCurrentTestSession());
        participant.moveOnToNextTestSession(true);
        save();
    }

    protected void setupPath(){

    }

    // this is where we can use the cache of segments
    protected void endOfPath(){

    }

    public boolean skipToNextSegment(){
        BaseData object = state.segments.get(0).collectData();
        if(object!=null){
            state.cache.add(object);
        }
        state.segments.remove(0);

        NavigationManager.getInstance().clearBackStack();
        Study.getInstance().getParticipant().save();
        save();

        if(state.segments.size()>0){
            return openNext();
        } else {
            endOfPath();
            state.cache.clear();
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
        if(state.segments.size()>0){
            if(state.segments.get(0).openNext(skips)) {
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
        BaseData object = state.segments.get(0).collectData();
        if(object!=null){
            state.cache.add(object);
        }
        state.segments.remove(0);

        NavigationManager.getInstance().clearBackStack();
        Study.getInstance().getParticipant().save();
        save();

        if(state.segments.size()>0){
            return openNext();
        } else {
            endOfPath();
            return moveOn();
        }
    }

    protected boolean moveOn(){
        state.cache.clear();
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
        if(state.segments.size()>0){
            return state.segments.get(0).openPrevious(skips);
        }
        return false;
    }

    // ------------------------------------------


    protected void setTestCompleteFlag(boolean complete){
        Log.i("StudyStateMachine", "setTestCompleteFlag("+complete+")");
        PreferencesManager.getInstance().putBoolean("TestCompleteFlag",complete);
    }

    protected boolean isTestCompleteFlagSet(){
        return PreferencesManager.getInstance().getBoolean("TestCompleteFlag",false);
    }

    public boolean isCurrentlyInTestPath(){
        return currentlyInTestPath;
    }

    public boolean isIdle(){
        return false;
    }

    // -----------------------------------------------------------------


    public void setPathSetupParticipant(){
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new SetupWelcome());
        fragments.add(new SetupParticipant());
        fragments.add(new SetupParticipantConfirm());
        fragments.add(new SetupSite());

        PathSegment segment = new PathSegment(fragments,SetupPathData.class);
        enableTransition(segment,false);
        state.segments.add(segment);
    }

    public void setPathSetupParticipant(int firstDigits, int secondDigits) {
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new SetupWelcome());

        SetupParticipant setupParticipantFragment = new SetupParticipant();
        Bundle setupDigitsBundle = new Bundle();
        setupDigitsBundle.putInt("firstDigits", firstDigits);
        setupDigitsBundle.putInt("secondDigits", secondDigits);
        setupParticipantFragment.setArguments(setupDigitsBundle);
        fragments.add(setupParticipantFragment);

        SetupParticipantConfirm setupParticipantConfirmFragment = new SetupParticipantConfirm();
        setupParticipantConfirmFragment.setArguments(setupDigitsBundle);
        fragments.add(setupParticipantConfirmFragment);

        fragments.add(new SetupSite());

        PathSegment segment = new PathSegment(fragments,SetupPathData.class);
        enableTransition(segment,false);
        state.segments.add(segment);
    }

    public void setPathSetupAvailability(){
        List<BaseFragment> fragments = new ArrayList<>();

        fragments.add(new InfoTemplate(
                false,
                "Availability" ,
                "A Few Questions",
                "The following section will ask you questions in regard to your sleep and wake behavior on weekdays and weekends." + "\n\n"
                        + "Please estimate an average of your normal sleep behavior over the past 6 weeks when you were able to follow your usual routines.",
                "BEGIN"));

        fragments.add(new AvailabilityMondayWake());
        fragments.add(new AvailabilityMondayBed());
        fragments.add(new AvailabilityWeekdayConfirm());
        fragments.add(new AvailabilityOtherWake("Tuesday"));
        fragments.add(new AvailabilityOtherBed("Tuesday"));
        fragments.add(new AvailabilityOtherWake("Wednesday"));
        fragments.add(new AvailabilityOtherBed("Wednesday"));
        fragments.add(new AvailabilityOtherWake("Thursday"));
        fragments.add(new AvailabilityOtherBed("Thursday"));
        fragments.add(new AvailabilityOtherWake("Friday"));
        fragments.add(new AvailabilityOtherBed("Friday"));
        fragments.add(new AvailabilitySaturdayWake());
        fragments.add(new AvailabilitySaturdayBed());
        fragments.add(new AvailabilitySundayWake());
        fragments.add(new AvailabilitySundayBed());

        PathSegment segment = new PathSegment(fragments,AvailabilityPathData.class);
        enableTransition(segment,true);
        state.segments.add(segment);
    }

    public void addWakeSurvey(){
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new InfoTemplate(
                false,
                "Wake Survey" ,
                "Six Questions",
                "The following questions will ask you about sleep specific to last night and waking this morning.",
                "BEGIN"));

        CircadianClock clock;
        String weekday;
        LocalTime wakeTime = null;
        LocalTime bedTime = null;

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar calendar = Calendar.getInstance();
        weekday = dayFormat.format(calendar.getTime());

        clock = Study.getParticipant().getCircadianClock();

        // Get previously entered wake time for today
        if(wakeTime==null && !clock.hasWakeRhythmChanged(weekday)){
            int index = clock.getRhythmIndex(weekday)-1;
            wakeTime = clock.getRhythm(index).getWakeTime();
        } else if(wakeTime==null){
            wakeTime = clock.getRhythm(weekday).getWakeTime();
        }

        // Get previously entered bed time for today
        if(bedTime==null && !clock.hasBedRhythmChanged(weekday)){
            int index = clock.getRhythmIndex(weekday)-1;
            bedTime = clock.getRhythm(index).getBedTime();
        } else if(bedTime==null){
            bedTime = clock.getRhythm(weekday).getBedTime();
        }

        fragments.add(new QuestionTime(true,"What time did you get in bed last night?","",bedTime));
        fragments.add(new QuestionDuration(true,"How long did it take you to fall asleep last night?"," "));
        fragments.add(new QuestionInteger(true,"How many times did you wake up for 5 minutes or longer?","Number of times",2));
        fragments.add(new QuestionTime(true,"What time did you wake up this morning?"," ",wakeTime));
        fragments.add(new QuestionTime(true,"What time did you get out of bed this morning?"," ",wakeTime));
        fragments.add(new QuestionRating(true,"How would you rate the quality of your sleep?","On a scale of poor to excellent.","Poor","Excellent"));

        PathSegment segment = new PathSegment(fragments,WakePathData.class);
        enableTransition(segment,true);
        state.segments.add(segment);
    }

    public void addContextSurvey(){
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new InfoTemplate(
                false,
                "Context Survey" ,
                "Five Questions",
                "These questions will briefly ask you about what is happening around you right now in this moment.",
                "BEGIN"));

        List<String> who = new ArrayList<>();
        who.add("Nobody");
        who.add("Partner / Spouse");
        who.add("Family");
        who.add("Friends");
        who.add("Co-Workers");
        who.add("Pet");
        who.add("Other");
        fragments.add(new QuestionCheckBoxes(true,"Who is with you right now?","Select all that apply",who,"Nobody"));

        List<String> where = new ArrayList<>();
        where.add("My Home");
        where.add("My Work");
        where.add("School");
        where.add("Another Person’s Home");
        where.add("Vehicle");
        where.add("Outside");
        where.add("Other");
        fragments.add(new QuestionRadioButtons(true,"Where are you right now?","Select One",where));

        fragments.add(new QuestionRating(true,"How would you rate <b>your overall mood</b> right now?","On a scale of bad to good.","Bad","Good"));
        fragments.add(new QuestionRating(true,"How would you rate <b>how you feel</b> right now?","On a scale of sleepy/tired to active/alert.","Sleepy/Tired","Active/Alert"));

        List<String> what = new ArrayList<>();
        what.add("Sitting at a Desk");
        what.add("Watching Television");
        what.add("Eating");
        what.add("Exercising");
        what.add("Household Chores (Cooking, Cleaning)");
        what.add("Driving");
        what.add("Socializing");
        what.add("Personal Care");
        what.add("Resting");
        what.add("Other");
        fragments.add(new QuestionRadioButtons(true,"In the past 5 minutes, what were you mostly doing?","Select One",what));

        PathSegment segment = new PathSegment(fragments,ContextPathData.class);
        enableTransition(segment,true);
        state.segments.add(segment);
    }

    public void addTests(){
        List<BaseFragment> fragments = new ArrayList<>();
        InfoTemplate info = new InfoTemplate(
                false,
                "Testing" ,
                "You Will Now Complete 3 Quick Tests",
                "You cannot pause a test once it has been started. You cannot go back to the previous screen.",
                "NEXT");
        //info.setEnterTransitionRes(R.anim.slide_in_right,R.anim.slide_in_left);
        fragments.add(info);
        PathSegment segment = new PathSegment(fragments);
        state.segments.add(segment);

        Integer[] orderArray = new Integer[]{1,2,3};
        List<Integer> order = Arrays.asList(orderArray);
        Collections.shuffle(order);
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

        InfoTemplate info = new InfoTemplate(
                false,
                "Test "+(index+1)+" of 3" ,
                "Prices",
                "We’ll show you some shopping items and prices.\n\nPlease decide if the displayed price is a good bargain for that item. The pair will only remain on the screen for a short time, so please make your decision quickly and pay close attention.",
                "BEGIN");
        fragments.add(info);

        int size = PriceManager.getInstance().getPriceSet().size();
        for(int i=0;i<size;i++){
            fragments.add(new PriceTestCompareFragment(i));
        }

        fragments.add(new SimplePopupScreen(
                "You will now start the test. You will see an item and two prices. Please select the price that matches the item you studied.",
                "BEGIN",
                3000,
                15000,
                true));

        fragments.add(new PriceTestMatchFragment());

        PathSegment segment = new PathSegment(fragments,PriceTestPathData.class);
        state.segments.add(segment);
    }

    public void addSymbolsTest(int index){
        List<BaseFragment> fragments = new ArrayList<>();

        InfoTemplate info = new InfoTemplate(
                false,
                "Test "+(index+1)+" of 3" ,
                "Symbols",
                "You will see three pairs of symbols at the top of the screen and two pairs at the bottom.\n\nAs quickly as you can, tap the pair at the bottom of the screen that matches one of the pairs at the top.",
                "BEGIN");
        fragments.add(info);

        fragments.add(new SymbolTest());

        PathSegment segment = new PathSegment(fragments,SymbolsTestPathData.class);
        state.segments.add(segment);
    }

    public void addGridTest(int index){
        List<BaseFragment> fragments = new ArrayList<>();

        InfoTemplate info0 = new InfoTemplate(
                false,
                "Test "+(index+1)+" of 3" ,
                "Grids",
                "In this test you will see a 5 x 5 grid with three items placed in that grid.\n\nStudy the grid and try to remember the location of the three items.",
                "NEXT");
        fragments.add(info0);

        InfoTemplate info1 = new InfoTemplate(
                true,
                "Test "+(index+1)+" of 3" ,
                "Grids",
                "You will then do a different task where you will touch all the letter “F”s that you find as quickly as you can.",
                "NEXT");
        fragments.add(info1);

        InfoTemplate info2 = new InfoTemplate(
                true,
                "Test "+(index+1)+" of 3" ,
                "Grids",
                "Finally, you will be shown a blank grid. Tap the boxes where the items were previously located.\n\nPress Begin to start the test.",
                "BEGIN");
        fragments.add(info2);

        fragments.add(new GridStudy());
        fragments.add(new GridLetters());
        fragments.add(new GridTest());
        fragments.add(new GridStudy());
        fragments.add(new GridLetters());
        GridTest gridTestFragment = new GridTest();
        gridTestFragment.second = true;
        fragments.add(gridTestFragment);

        PathSegment segment = new PathSegment(fragments,GridTestPathData.class);
        state.segments.add(segment);
    }

    public void addInterruptedPage(){
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new QuestionInterrupted(false,"Thanks!<br><br>Were you interrupted or did you have to stop while taking any of these tests?",""));
        PathSegment segment = new PathSegment(fragments);
        state.segments.add(segment);
    }

    public void addFinishedPage(){
        List<BaseFragment> fragments = new ArrayList<>();
        String header;
        String subheader;
        String body;

        // Default
        header = "Thank You";
        subheader = "Test Complete";
        body = "You'll get a notification later today when your next test is available.";

        // Finished with study
        if(!Study.getParticipant().isStudyRunning()){
            //at the end of the line
            header = "Congratulations";
            subheader = "You've Finished the Study!";
            body = "There are no more tests to take.";
        }
        else {
            ParticipantState participantState = Study.getParticipant().getState();
            Visit visit = participantState.visits.get(participantState.currentVisit);

            // After the cycle but before the next session
            if (visit.getNumberOfTestsLeft() == visit.getNumberOfTests()) {
                String format = ViewUtil.getString(com.healthymedium.arc.library.R.string.format_date);
                String date = visit.getActualStartDate().toString(format);
                header = "Great Job";
                subheader = "You've Finished This Cycle!";
                body = "There are no tests available at this time. You'll receive a notification on "+date+", when the next testing cycle begins.";
            }
            // After the 4th test of the day
            else if (visit.getNumberOfTestsLeftForToday() == 0) {
                header = "Thank You";
                subheader = "All Done for Today!";
                body = "Thanks for your hard work. We'll notify you tomorrow with your next test.";
            }

        }

        InfoTemplate info = new InfoTemplate(
                false,
                header ,
                subheader,
                body,
                "RETURN TO HOME");
        fragments.add(info);
        PathSegment segment = new PathSegment(fragments);
        state.segments.add(segment);
    }


    // -----------------------

    public String getLifecycleName(int lifecycle){
        return "";
    }

    public String getPathName(int path){
        return "";
    }

    public StudyState getState(){
        return state;
    }

    // loadTestDataFromCache() is called from abandonTest().
    // Override this method to handle loading test data from cache.
    public void loadTestDataFromCache() {

    }


}
