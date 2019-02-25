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

import com.healthymedium.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class Participant {

    protected ParticipantState state;

    public void initialize(){
        state = new ParticipantState();
    }

    public void load(){
        state = PreferencesManager.getInstance().getObject("ParticipantState",ParticipantState.class);
    }

    public void save(){
        PreferencesManager.getInstance().putObject("ParticipantState", state);
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

    public void markPaused(){
        state.lastPauseTime = DateTime.now();
    }

    public void markResumed(){
        if(isCurrentlyInTestSession()){
            if(checkForTestAbandonment())
            {
                Study.getInstance().abandonTest();
            }
        } else if(shouldCurrentlyBeInTestSession()){
            if(!Study.getStateMachine().isCurrentlyInTestPath()){
                Study.skipToNextSegment();
            }
        }
        else if(Study.getStateMachine().isCurrentlyInTestPath()){
            Study.getStateMachine().decidePath();
            Study.getStateMachine().setupPath();
            Study.getStateMachine().openNext();
        }
    }

    protected boolean checkForTestAbandonment(){
        return state.lastPauseTime.plusMinutes(5).isBeforeNow();
    }
    public boolean isCurrentlyInTestSession(){
        if(state.visits.size()==0){
            return false;
        }

        return getCurrentTestSession().isOngoing();
    }

    public boolean shouldCurrentlyBeInTestSession(){
        if(state.visits.size()==0){
            return false;
        }
        return getCurrentTestSession().getScheduledTime().isBeforeNow();
    }

    public Visit getCurrentVisit(){
        if(state.visits.size()>0) {
            return state.visits.get(state.currentVisit);
        }
        return null;
    }

    public void moveOnToNextTestSession(boolean scheduleNotifications){
        state.currentTestSession++;
        if(state.currentTestSession>=state.visits.get(state.currentVisit).testSessions.size()){
            state.currentTestSession = 0;
            state.currentVisit++;
            if(state.currentVisit>=state.visits.size()){
                state.isStudyRunning = false;
            } else if(scheduleNotifications){
                Study.getScheduler().scheduleNotifications(getCurrentVisit());
            }
        }
        save();
    }

    public TestSession getCurrentTestSession(){
        return getCurrentVisit().getTestSessions().get(state.currentTestSession);
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

}
