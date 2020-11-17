//
// Participant.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;



import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.notifications.Proctor;
import com.healthymedium.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;

public class Participant {

    public static final String TAG_PARTICIPANT_STATE = "ParticipantState";

    protected ParticipantState state;

    public void initialize(){
        state = new ParticipantState();
    }

    public void load() {
        load(false);
    }

    public void load(boolean overwrite){
        if(state!=null && !overwrite){
            return;
        }
        state = PreferencesManager.getInstance().getObject(TAG_PARTICIPANT_STATE,ParticipantState.class);
    }

    public void save(){
        PreferencesManager.getInstance().putObject(TAG_PARTICIPANT_STATE, state);
    }

    public boolean hasId(){
        return state.id!=null;
    }

    public String getId(){
        return state.id;
    }

    public boolean hasSchedule(){
        return state.hasValidSchedule;
    }

    public boolean hasCommittedToStudy(){
        return state.hasCommittedToStudy==1;
    }

    public boolean hasRebukedCommitmentToStudy(){
        return state.hasCommittedToStudy==0;
    }

    public void markCommittedToStudy(){
        state.hasCommittedToStudy = 1;
    }

    public void rebukeCommitmentToStudy(){
        state.hasCommittedToStudy = 0;
    }

    public boolean hasBeenShownNotificationOverview(){
        return state.hasBeenShownNotificationOverview;
    }

    public void markShownNotificationOverview(){
        state.hasBeenShownNotificationOverview = true;
    }

    public boolean hasBeenShownBatteryOptimizationOverview(){
        return state.hasBeenShownBatteryOptimizationOverview;
    }

    public void markShownBatteryOptimizationOverview(){
        state.hasBeenShownBatteryOptimizationOverview = true;
    }

    public void markPaused(){
        state.lastPauseTime = DateTime.now();
    }

    public void markResumed(){

        /*
        We're checking three situations here:
        - Are we currently in a test?
            If so check and see if we should abandon it
        - Should we be in a test, but the state machine is not set to a test path?
            If so let's skip to the next segment
        - Else are we currently in a test path?
            If so, have the state machine decide where to go next
         */
        if(isCurrentlyInTestSession()){
            if(checkForTestAbandonment())
            {
                Study.getInstance().abandonTest();
            }
        } else if(shouldCurrentlyBeInTestSession() && !Study.getStateMachine().isCurrentlyInTestPath()){
            Study.skipToNextSegment();
        }
        else if(Study.getStateMachine().isCurrentlyInTestPath()){
            Study.getStateMachine().decidePath();
            Study.getStateMachine().setupPath();
            Study.getStateMachine().openNext();
        }
    }

    public boolean checkForTestAbandonment(){
        return state.lastPauseTime.plusMinutes(5).isBeforeNow();
    }
    public boolean isCurrentlyInTestSession(){
        if(state.testCycles.size()==0){
            return false;
        }
        TestSession session = getCurrentTestSession();
        if(session==null) {
            return false;
        }
        return session.isOngoing();
    }

    public boolean shouldCurrentlyBeInTestSession(){
        if(state.testCycles.size()==0){
            return false;
        }
        TestSession session = getCurrentTestSession();
        if(session==null) {
            return false;
        }
        return session.getScheduledTime().isBeforeNow();
    }

    public boolean shouldCurrentlyBeInTestCycle() {
        TestCycle cycle = getCurrentTestCycle();
        if(cycle==null) {
            return false;
        }
        return cycle.getActualStartDate().isBeforeNow();
    }

    public TestCycle getCurrentTestCycle(){
        if(state.testCycles.size() > state.currentTestCycle) {
            return state.testCycles.get(state.currentTestCycle);
        }
        return null;
    }

    public TestDay getCurrentTestDay(){
        TestCycle cycle = getCurrentTestCycle();
        if(cycle==null) {
            return null;
        }
        return cycle.getTestDay(state.currentTestDay);
    }

    public TestSession getCurrentTestSession(){
        TestDay day = getCurrentTestDay();
        if(day==null){
            return null;
        }
        for(TestSession session : day.getTestSessions()) {
            if(session.getIndex()==state.currentTestSession){
                return session;
            }
        }
        return null;
    }

    public void moveOnToNextTestSession(boolean scheduleNotifications){


        state.currentTestSession++;

        if(getCurrentTestDay().isOver()){
            state.currentTestDay++;
            state.currentTestSession = 0;
        }

        if(getCurrentTestCycle().isOver()){
            Proctor.stopService(Application.getInstance());
            state.currentTestSession = 0;
            state.currentTestDay = 0;
            state.currentTestCycle++;
            if(state.currentTestCycle >=state.testCycles.size()){
                state.isStudyRunning = false;
            } else if(scheduleNotifications){
                Study.getScheduler().scheduleNotifications(getCurrentTestCycle(), false);
            }
        }
        save();
    }

    public void setCircadianClock(CircadianClock clock){
        state.circadianClock = clock;
    }

    public CircadianClock getCircadianClock(){
        return state.circadianClock;
    }

    public boolean isStudyRunning(){
        return state.isStudyRunning;
    }

    public void markStudyStarted(){
        state.isStudyRunning = true;
        save();
    }

    public void markStudyStopped(){
        state.isStudyRunning = false;
        save();
    }

    public ParticipantState getState(){
        return state;
    }

    public void setState(ParticipantState state){
        this.state = state;
        save();
    }

    public void setState(ParticipantState state, boolean shouldSave){
        this.state = state;
        if(shouldSave) {
            save();
        }
    }

    public Earnings getEarnings(){
        return state.earnings;
    }

    public DateTime getStartDate() {
        return state.studyStartDate;
    }

    public DateTime getFinishDate() {
        int size = state.testCycles.size();
        if(size>0) {
            return state.testCycles.get(size-1).getActualEndDate();
        }
        return null;
    }

    public TestSession getSessionById(int id) {
        for(TestCycle cycle : state.testCycles) {
            for(TestDay day : cycle.getTestDays()) {
                for(TestSession session : day.getTestSessions()) {
                    if(session.getId()==id){
                        return session;
                    }
                }
            }
        }
        return null;
    }

    public TestCycle getCycleBySessionId(int id) {
        for(TestCycle cycle : state.testCycles) {
            for(TestDay day : cycle.getTestDays()) {
                for(TestSession session : day.getTestSessions()) {
                    if(session.getId()==id){
                        return cycle;
                    }
                }
            }
        }
        return null;
    }

    public TestDay getDayBySessionId(int id) {
        for(TestCycle cycle : state.testCycles) {
            for(TestDay day : cycle.getTestDays()) {
                for(TestSession session : day.getTestSessions()) {
                    if(session.getId()==id){
                        return day;
                    }
                }
            }
        }
        return null;
    }

}
