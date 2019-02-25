//
// Scheduler.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import android.util.Log;

import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.time.JodaUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;

import java.util.List;
import java.util.Random;

import static java.lang.Math.floor;

public class Scheduler {

    public Scheduler() {

    }

    private void unscheduleNotifications(Visit visit) {
        if(visit==null){
            return;
        }
        int testCount = visit.getNumberOfTests();
        List<TestSession> testSessions = visit.getTestSessions();
        for(TestSession session: testSessions) {
                NotificationManager.getInstance().removeNotification(session.getIndex(), NotificationManager.TEST_TAKE);
                NotificationManager.getInstance().removeNotification(testCount+session.getIndex(), NotificationManager.TEST_MISSED);
        }
    }

    public void scheduleNotifications(Visit visit) {
        if(visit==null){
            return;
        }
        int testCount = visit.getNumberOfTests();
        List<TestSession> testSessions = visit.getTestSessions();
        for (TestSession session : testSessions) {
            if (session.getScheduledTime().isAfterNow()) {
                NotificationManager.getInstance().scheduleNotification(session.getIndex(), NotificationManager.TEST_TAKE, session.getScheduledTime());
                NotificationManager.getInstance().scheduleNotification(testCount+session.getIndex(), NotificationManager.TEST_MISSED, session.getExpirationTime());
            }
        }
        visit.confirmNotificationsScheduled();
    }

    public void scheduleTests(DateTime now, Participant participant){
        ParticipantState state = participant.getState();
        if(state.visits.size()==0){
            initializeVisits(now,participant);
        }

        int size = state.visits.size();
        for(int i=state.currentVisit;i<size;i++){
                scheduleTestsForVisit(participant.getCircadianClock(), state, state.visits.get(i));
                state.visits.get(i).logTests();
        }

        participant.state.hasValidSchedule = true;
        participant.state.isStudyRunning = true;
        participant.save();
    }

    protected void initializeVisits(DateTime now, Participant participant) {
        List<Visit> visits = participant.state.visits;
        DateTime midnight = JodaUtil.setMidnight(now);

        Visit visit = new Visit(0,midnight,midnight.plusDays(1));
        TestSession testSession = new TestSession(0,0,0);
        testSession.setScheduledTime(midnight);
        visit.getTestSessions().add(testSession);
        visits.add(visit);
    }

    protected void scheduleTestsForVisit(CircadianClock clock, ParticipantState state, Visit visit) {
        Random random = new Random(System.currentTimeMillis());
        List<TestSession> testSessions = visit.testSessions;

        DateTime startDate = visit.getActualStartDate();
        List<CircadianInstant> orderedPairs = clock.getRhythmInstances(startDate.toLocalDate());
        boolean isCurrentVisit = visit.getId()==state.currentVisit;
        int currentDayIndex = getDayIndex(state.currentVisit,state.currentTestSession);
        int numDays = visit.getNumberOfDays();
        int index = 0;

        for(int i=0;i<numDays;i++){
            DateTime wake = orderedPairs.get(i).getWakeTime();
            DateTime bed = orderedPairs.get(i).getBedTime();

            boolean isCurrentDay = isCurrentVisit && (index==currentDayIndex) && LocalDate.now().equals(wake.toLocalDate());

            if(isCurrentDay){
                wake = wake.withTime(LocalTime.now());
            }

            int gap = Seconds.secondsBetween(wake,bed).getSeconds();
            int numTests = visit.getNumberOfTests(i);

            DateTime begin = wake;

            if(gap > 6*60*60) {
                gap -= 6*60*60;
                int period = gap;
                if(numTests>1){
                    period = gap / (numTests-1);
                }
                if (period <= 0) {
                    period = 10;
                }
                for (int j = 0; j < numTests; j++) {
                    if(!isCurrentDay || index>=state.currentTestSession) {
                        begin = begin.plusSeconds(random.nextInt(period));
                        testSessions.get(index).setScheduledTime(begin);
                        begin = begin.plusHours(2);
                    }
                    index++;
                }
            } else {
                int period = gap / (numTests+1);
                if (period <= 0) {
                    period = 10;
                }
                for (int j = 0; j < numTests; j++) {
                    if(!isCurrentDay || index>=state.currentTestSession) {
                        begin = begin.plusSeconds(period);
                        testSessions.get(index).setScheduledTime(begin);
                    }
                    index++;
                }

            }
            startDate = startDate.plusDays(1);
        }
    }

    // The following functions can feel a little misplaced.
    // Usually, any manipulation of the participant state should be left in the participant class.
    // I'd like to decrease any confusion as to when to use what class.
    // The participant class depends on member variables for the most part while this class is stateless.
    //
    // Minimizing the use of the study singleton makes it easier to unit test these classes at the moment.
    // ^ This is the driving force behind this decision.
    // As a compromise, this will generate an object that the participant can then consume.
    //
    // At some point, I would like to create a study class strictly for unit tests to help when these cases occur.
    // Any suggestions are welcome

    public ParticipantState getExistingParticipantState(DateTime startDate, int week, int dayIndex, int dailyIndex) {
        ParticipantState state = new ParticipantState();
        state.studyStartDate = startDate;
        state.currentVisit = getVisitIndex(week,dayIndex,dailyIndex);
        state.currentTestSession = getTestIndex(week,dayIndex,dailyIndex);
        state.currentTestSession++;
        if(isAtEndOfVisit(week,state.currentTestSession)){
            state.currentVisit++;
            state.currentTestSession = 0;
        }
        return state;
    }

    public int getVisitIndex(int week, int dayIndex, int dailyIndex){
        return 0;
    }

    public int getTestIndex(int week, int dayIndex, int dailyIndex){
        return 0;
    }

    public int getDayIndex(int visit, int test){
        return 0;
    }

    protected boolean isAtEndOfVisit(int visit, int test){
        return false;
    }

}
