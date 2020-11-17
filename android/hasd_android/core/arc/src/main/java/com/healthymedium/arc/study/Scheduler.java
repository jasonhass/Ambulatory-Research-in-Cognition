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

import com.healthymedium.arc.api.models.ExistingData;
import com.healthymedium.arc.api.models.TestScheduleSession;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.notifications.NotificationTypes;
import com.healthymedium.arc.time.TimeUtil;


import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Scheduler {

    protected static String tag = "Scheduler";

    Random random = new Random(System.currentTimeMillis());

    public Scheduler() {

    }

    public void unscheduleNotifications(TestCycle cycle) {
        if(cycle ==null){
            return;
        }

        NotificationManager notificationManager = NotificationManager.getInstance();
        int visitId = cycle.getId();

        if(Config.ENABLE_VIGNETTES){
            notificationManager.unscheduleNotification(visitId, NotificationTypes.VisitNextMonth);
            notificationManager.unscheduleNotification(visitId, NotificationTypes.VisitNextWeek);
            notificationManager.unscheduleNotification(visitId, NotificationTypes.VisitNextDay);
        }

        List<TestSession> testSessions = cycle.getTestSessions();
        for(TestSession session: testSessions) {
            int sessionId = session.getId();

            notificationManager.unscheduleNotification(sessionId, NotificationTypes.TestTake);
            notificationManager.unscheduleNotification(sessionId, NotificationTypes.TestMissed);
        }
    }

    public void scheduleNotifications(TestCycle cycle, boolean rescheduleDuringVisit) {
        if(cycle ==null){
            return;
        }

        NotificationManager notificationManager = NotificationManager.getInstance();
        int visitId = cycle.getId();

        if (Config.ENABLE_VIGNETTES && visitId != 0 && !rescheduleDuringVisit) {

            DateTime startDate = cycle.getActualStartDate();

            // one month out, noon
            DateTime monthVignette = startDate.minusMonths(1).plusHours(12);
            if (monthVignette.isAfterNow()) {
                notificationManager.scheduleNotification(visitId, NotificationTypes.VisitNextMonth, monthVignette);
            }

            // one week out, noon
            DateTime weekVignette = startDate.minusWeeks(1).plusHours(12);
            if (weekVignette.isAfterNow()) {
                notificationManager.scheduleNotification(visitId, NotificationTypes.VisitNextWeek, weekVignette);
            }

            // one day out, noon
            DateTime dayVignette = startDate.minusDays(1).plusHours(12);
            if (dayVignette.isAfterNow()) {
                notificationManager.scheduleNotification(visitId, NotificationTypes.VisitNextDay, dayVignette);
            }
        }

        List<TestSession> testSessions = cycle.getTestSessions();
        for (TestSession session : testSessions) {
            int sessionId = session.getId();


            if(session.isOver()) {
                continue;
            }

            if (session.getScheduledTime().isAfterNow()) {
                notificationManager.scheduleNotification(sessionId, NotificationTypes.TestTake, session.getScheduledTime());
            }

            if(session.getExpirationTime().isAfterNow()){
                notificationManager.scheduleNotification(sessionId, NotificationTypes.TestMissed, session.getExpirationTime());
            }
        }
    }

    public void scheduleTests(DateTime now, Participant participant){
        ParticipantState state = participant.getState();
        if(state.testCycles.size()==0){
            initializeCycles(now,participant);
        }

        for(TestCycle cycle : state.testCycles){
            scheduleTestsForCycle(participant.getCircadianClock(), state, cycle, now);
            logCycle(cycle);
        }

        state.hasValidSchedule = true;
        state.isStudyRunning = true;

    }

    public void initializeCycles(DateTime now, Participant participant) {
        List<TestCycle> cycles = participant.getState().testCycles;
        DateTime midnight = TimeUtil.setMidnight(now);

        TestCycle cycle = new TestCycle(0,midnight,midnight.plusDays(1));
        TestSession testSession = new TestSession(0,0,0);
        testSession.setPrescribedTime(midnight);
        cycle.getTestSessions().add(testSession);
        cycles.add(cycle);
    }

    protected TestCycle initializeCycle(int id, int testsPerDay, DateTime start, int numDays) {

        DateTime end = start.plusDays(numDays);
        TestCycle cycle = new TestCycle(id,start,end);

        for(int i=0;i<numDays;i++) {

            List<TestSession> sessions = new ArrayList<>();
            for(int j=0;j<testsPerDay;j++) {
                TestSession testSession = new TestSession(i,j,0);
                sessions.add(testSession);
            }
            cycle.getTestDays().add(new TestDay(i,sessions));
        }
        return cycle;
    }

    protected TestCycle initializeBaselineCycle(DateTime start, int numDays){

        DateTime end = start.plusDays(numDays);
        TestCycle cycle = new TestCycle(0,start,end);

        int index = 0;

        List<TestSession> initialSessions = new ArrayList<>();
        TestSession initialSession = new TestSession(0,index,0);
        initialSessions.add(initialSession);
        cycle.getTestDays().add(new TestDay(0,initialSessions));
        index++;

        // ----------------------------------------------

        for(int i=1;i<numDays;i++) {
            List<TestSession> sessions = new ArrayList<>();
            for(int j=0;j<4;j++) {
                TestSession testSession = new TestSession(i,j,0);
                sessions.add(testSession);
                index++;
            }
            TestDay testDay = new TestDay(i,sessions);
            cycle.getTestDays().add(testDay);
        }

        return cycle;
    }


    protected void scheduleTestsForCycle(CircadianClock clock, ParticipantState state, TestCycle cycle, DateTime now) {

        DateTime startDate = cycle.getActualStartDate();
        List<TestDay> days = cycle.getTestDays();
        int numDays = days.size();

        List<CircadianInstant> orderedPairs = clock.getRhythmInstances(startDate.toLocalDate(),numDays);

        boolean isCurrentCycle = cycle.getId()==state.currentTestCycle;
        boolean isBaseline = cycle.getId()==0;

        for(int i=0;i<numDays;i++) {

            TestDay day = days.get(i);
            DateTime wake = orderedPairs.get(i).getWakeTime();
            DateTime bed = orderedPairs.get(i).getBedTime();

            boolean isToday = day.getDayIndex()==state.currentTestDay && isCurrentCycle;
            boolean hasStarted = day.hasStarted();
            boolean isFirstDay = (i==0);

            if(!hasStarted) {
                if (isToday && isBaseline && isFirstDay) {
                    scheduleTestsForFirstBaselineDay(day, now, bed);
                } else {
                    scheduleTestsForDay(day, wake, bed);
                }
            }
        }
    }

    protected void scheduleTestsForFirstBaselineDay(TestDay day, DateTime now, DateTime bed) {
        day.setStartTime(now);
        day.setEndTime(bed);
        day.getTestSession(0).setPrescribedTime(now);
    }

    protected void scheduleTestsForDay(TestDay day, DateTime wake, DateTime bed) {

        day.setStartTime(wake);
        day.setEndTime(bed);

        int numTests = day.getNumberOfTests();
        int secondsLeft = Seconds.secondsBetween(wake,bed).getSeconds();
        int minimumTimeForAllTests = (2*60*60)*(numTests-1);

        secondsLeft -= minimumTimeForAllTests;
        if(secondsLeft<0){
            throw new UnsupportedOperationException("invalid scheduling scenario, contact technical support");
        }

        int interval = (int)((float)secondsLeft/numTests);

        DateTime begin = wake;

        // Loop through all of the tests on this day
        List<TestSession> sessions = day.getTestSessions();
        int lastIndex = sessions.size()-1;

        for (int i=0;i<sessions.size();i++) {
            if(i==lastIndex){
                interval = Seconds.secondsBetween(begin,bed).getSeconds();
            }
            DateTime temp = begin.plusSeconds(random.nextInt(interval));
            sessions.get(i).setPrescribedTime(temp);
            begin = temp.plusHours(2);
        }
    }

    public ParticipantState getExistingParticipantState(ExistingData existingData) {

        Participant participant = Study.getParticipant();
        ParticipantState state = participant.getState();
        state.circadianClock = CircadianClock.fromWakeSleepSchedule(existingData.wake_sleep_schedule);

        DateTime startDate = TimeUtil.fromUtcDouble(existingData.first_test.session_date);
        state.studyStartDate = startDate;
        Study.getScheduler().initializeCycles(startDate,Study.getParticipant());
        Study.getScheduler().scheduleTests(startDate,Study.getParticipant());

        List<TestScheduleSession> scheduleSessions = existingData.test_schedule.sessions;

        int index = 0;
        int last = scheduleSessions.size()-1;

        for(TestCycle cycle : state.testCycles ) {

            DateTime sessionDate = TimeUtil.fromUtcDouble(scheduleSessions.get(index).session_date);
            int daysShifted = Days.daysBetween(sessionDate,cycle.getScheduledStartDate()).getDays();
            int cycleId = cycle.getId();

            for(TestDay day : cycle.getTestDays()) {
                for(TestSession session : day.getTestSessions()) {

                    TestScheduleSession scheduleSession  = scheduleSessions.get(index);
                    int sessionId = Integer.valueOf(scheduleSession.session_id);
                    DateTime scheduledDateTime = TimeUtil.fromUtcDouble(scheduleSession.session_date);

                    DateTime prescribedDateTime = scheduledDateTime.plusDays(daysShifted);
                    session.setPrescribedTime(prescribedDateTime);
                    session.setScheduledDate(scheduledDateTime.toLocalDate());



                    index++;
                    if(index>last) {
                        break;
                    }
                }
            }
        }

        for (TestCycle cycle : state.testCycles) {
            int lastSession = cycle.getNumberOfTests()-1;
            cycle.setActualStartDate(cycle.getTestSession(0).getScheduledTime());
            cycle.setActualEndDate(cycle.getTestSession(lastSession).getScheduledTime().plusDays(1));
        }

        int sessionId = existingData.latest_test.session_id;
        TestCycle currentCycle = participant.getCycleBySessionId(sessionId);
        TestDay currentDay = participant.getDayBySessionId(sessionId);
        TestSession currentSession = participant.getSessionById(sessionId);

        state.currentTestCycle = currentCycle.getId();
        state.currentTestDay = currentDay.getDayIndex();
        state.currentTestSession = currentSession.getIndex();
        state.currentTestSession++;

        if(currentDay.getNumberOfTests()<=state.currentTestSession){
            state.currentTestSession = 0;
            state.currentTestDay++;
        }

        if(currentCycle.getNumberOfTestDays()<=state.currentTestDay){
            state.currentTestDay = 0;
            state.currentTestCycle++;
        }

        state.hasValidSchedule = true;
        state.isStudyRunning = true;
        state.hasCommittedToStudy = 1;
        state.hasBeenShownNotificationOverview = true;

        return state;
    }

    void logCycle(TestCycle cycle) {
        String string = "printing cycle\n";
        string += "--------------------\n";
        string += "id = "+cycle.getId()+"\n";
        string += "scheduledStartDate\t= "+cycle.getScheduledStartDate().toString()+"\n";
        string += "scheduledEndDate  \t= "+cycle.getScheduledEndDate().toString()+"\n";
        string += "actualStartDate   \t= "+cycle.getActualStartDate().toString()+"\n";
        string += "actualEndDate     \t= "+cycle.getActualEndDate().toString()+"\n";
        string += "numDays           \t= "+cycle.getNumberOfTestDays()+"\n";
        string += "numTests          \t= "+cycle.getNumberOfTests()+"\n\n";

        for(TestDay day : cycle.getTestDays()) {
            string += "dayIndex = "+day.getDayIndex()+", numTests = "+day.getNumberOfTests()+"\n";
            for(TestSession session : day.sessions) {
                string += "\ttestIndex = "+session.getIndex()+", id = "+session.getId()+", scheduledTime = "+session.getScheduledTime().toString()+"\n";
            }
            string += "\n";
        }

    }

}
