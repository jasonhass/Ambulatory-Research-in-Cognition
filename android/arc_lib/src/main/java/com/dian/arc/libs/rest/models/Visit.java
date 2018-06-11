package com.dian.arc.libs.rest.models;

import android.os.SystemClock;

import com.dian.arc.libs.rest.RestClient;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.JodaUtil;
import com.dian.arc.libs.utilities.NotificationManager;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Visit {

    int visitId;
    double visitStartDate;            // official start date of the Visit,
    double visitEndDate;              // official end date of the Visit,
    double userStartDate;           // user-modified start date of the Visit,
    double userEndDate;             // user-modified end date of the Visit,
    int hasConfirmedDate;           // a flag indicating whether the user has confirmed the start date,
    int hasScheduledNotifications;  // a flag indicating whether notifications have been scheduled for the Test Sessions
    boolean valid;
    ChronotypeSurvey chronotype;
    List<TestSession> testSessions;

    public Visit(int visitId){
        this.visitId = visitId;
        testSessions = new ArrayList<>();
        valid = false;

    }

    public Visit(int visitId, DateTime start){
        this.visitId = visitId;
        visitStartDate = JodaUtil.toUtcDouble(start);
        visitEndDate = JodaUtil.toUtcDouble(start.plusWeeks(1));
        testSessions = new ArrayList<>();
        valid = true;
    }

    public void confirmDate(){
        hasConfirmedDate = 1;
    }

    public boolean hasConfirmedDate() {
        return hasConfirmedDate==1;
    }

    public int getVisitId() {
        return visitId;
    }

    public DateTime getVisitStartDate() {
        return new DateTime((long) visitStartDate *1000);
    }

    public DateTime getVisitEndDate() {
        return new DateTime((long) visitEndDate *1000);
    }

    public DateTime getUserStartDate() {
        if(userStartDate != 0){
            return new DateTime((long)userStartDate*1000);
        } else {
            return new DateTime((long) visitStartDate *1000);
        }
    }

    public void setUserStartDate(DateTime userStartDate) {
        this.userStartDate = JodaUtil.toUtcDouble(userStartDate);;
    }

    public DateTime getUserEndDate() {
        if(userEndDate != 0){
            return new DateTime((long)userEndDate*1000);
        } else {
            return new DateTime((long) visitEndDate *1000);
        }
    }

    public void setUserEndDate(DateTime userEndDate) {
        this.userEndDate = JodaUtil.toUtcDouble(userEndDate);
    }

    public boolean hasScheduledNotifications() {
        return hasScheduledNotifications==1;
    }

    public ChronotypeSurvey getChronotype() {
        if(chronotype==null){
            chronotype = new ChronotypeSurvey();
        }
        return chronotype;
    }

    public void setChronotype(ChronotypeSurvey chronotype) {
        this.chronotype = chronotype;
    }

    public List<TestSession> getTestSessions() {
        if(testSessions==null){
            testSessions = new ArrayList<>();
        }
        return testSessions;
    }

    private int getDayOfWeekIndex(DateTime dateTime) {
        int dayOfWeek = dateTime.getDayOfWeek();
        // sun = 7
        // mon = 1
        // tue = 2
        // wed = 3
        // thu = 4
        // fri = 5
        // sat = 6
        if(dayOfWeek==7){
            dayOfWeek = 0;
        }
        // sun = 0
        // mon = 1
        // tue = 2
        // wed = 3
        // thu = 4
        // fri = 5
        // sat = 6
        return dayOfWeek;
    }

    private void unscheduleNotifications(boolean reschedule) {
        if(reschedule){
            for(int i=0;i<28;i++){
                NotificationManager.getInstance().removeNotification(i, NotificationManager.TEST_TAKE);
                NotificationManager.getInstance().removeNotification(i, NotificationManager.TEST_MISSED);
            }
        }
    }

    private void scheduleNotifications(boolean reschedule) {
        for(TestSession session: testSessions) {
            if(session.getSessionDate().isAfterNow()) {
                NotificationManager.getInstance().scheduleNotification(session.getSessionId(), NotificationManager.TEST_TAKE, session.getSessionDate());
                NotificationManager.getInstance().scheduleNotification(session.getSessionId(), NotificationManager.TEST_MISSED, session.getSessionDate().plusHours(2));
            }
        }
        if(!reschedule) {
            DateTime userStartDate = getUserStartDate();
            DateTime week4Prior = userStartDate.minusWeeks(4);
            DateTime week3Prior = userStartDate.minusWeeks(3);
            DateTime week2Prior = userStartDate.minusWeeks(2);
            DateTime week1Prior = userStartDate.minusWeeks(1);
            if (week4Prior.isAfterNow()) {
                NotificationManager.getInstance().scheduleNotification(1, NotificationManager.TEST_CONFIRM, week4Prior);
            }
            if (week3Prior.isAfterNow()) {
                NotificationManager.getInstance().scheduleNotification(1, NotificationManager.TEST_NEXT, week3Prior);
            }
            if (week2Prior.isAfterNow()) {
                NotificationManager.getInstance().scheduleNotification(2, NotificationManager.TEST_NEXT, week2Prior);
            }
            if (week1Prior.isAfterNow()) {
                NotificationManager.getInstance().scheduleNotification(3, NotificationManager.TEST_NEXT, week1Prior);
            }
        }
        hasScheduledNotifications = 1;
    }






















    // only used once the arc has started
    public void rescheduleTests(ArcManager.State state){

        Random random = new Random(SystemClock.currentThreadTimeMillis());

        int sessionId = state.currentTestSession;
        int dayIndex = (int)Math.floor((sessionId)/4);
        int testsToday = (sessionId)-(dayIndex*4);

        DateTime wake = DateTime.now();
        DateTime bed = DateTime.now();

        int dayOfWeek = getDayOfWeekIndex(wake);

        wake = JodaUtil.setTime(wake,state.wakeSleepSchedule.get(dayOfWeek).getWakeTime());
        bed = JodaUtil.setTime(bed,state.wakeSleepSchedule.get(dayOfWeek).getBedTime());
        if (wake.isBeforeNow()) {
            wake = DateTime.now();
        }

        unscheduleNotifications(true);

        double gap = JodaUtil.toUtcDouble(bed) - JodaUtil.toUtcDouble(wake);
        double period;
        int offset = 0;
        //assume

        if(gap>=(testsToday*140*60)) {
            gap -= 6*60*60;
            period = gap / (3 - testsToday);
            if (period <= 0) {
                period = 10;
            }

            DateTime lastTest = wake;

            switch (testsToday) {
                case 0:
                    TestSession t0 = testSessions.get(sessionId + offset);
                    wake = t0.getSessionDate();
                    offset++;
                    break;
                case 1:
                    TestSession t1 = testSessions.get(sessionId + offset);
                    lastTest = wake.plusSeconds(random.nextInt((int) period));
                    t1.setSessionDate(lastTest);
                    testSessions.set(sessionId + offset, t1);
                    offset++;
                case 2:
                    TestSession t2 = testSessions.get(sessionId + offset);
                    lastTest = lastTest.plusHours(2).plusSeconds((random.nextInt((int) period)));
                    t2.setSessionDate(lastTest);
                    testSessions.set(sessionId + offset, t2);
                    offset++;
                case 3:
                    TestSession t3 = testSessions.get(sessionId + offset);
                    lastTest = lastTest.plusHours(2).plusSeconds((random.nextInt((int) period)));
                    t3.setSessionDate(lastTest);
                    testSessions.set(sessionId + offset, t3);
                    break;
            }
        } else {
            period = gap / (5 - testsToday);
            if (period <= 0) {
                 period = 10;
            }
            switch (testsToday) {
                case 0:
                    TestSession t0 = testSessions.get(sessionId + offset);
                    wake = t0.getSessionDate();
                    offset++;
                    break;
                case 1:
                    TestSession t1 = testSessions.get(sessionId + offset);
                    t1.setSessionDate(wake.plusSeconds((int) (((offset+1) * period))));
                    testSessions.set(sessionId + offset, t1);
                    offset++;
                case 2:
                    TestSession t2 = testSessions.get(sessionId + offset);
                    t2.setSessionDate(wake.plusSeconds((int) ((offset+1)* period)));
                    testSessions.set(sessionId + offset, t2);
                    offset++;
                case 3:
                    TestSession t3 = testSessions.get(sessionId + offset);
                    t3.setSessionDate(wake.plusSeconds((int) ((offset+1)* period)));
                    testSessions.set(sessionId + offset, t3);
                    offset++;
                    break;
            }
        }
        
        dayOfWeek = getDayOfWeekIndex(bed);
        int daysLeft = 7-dayIndex;

        for(int i=dayIndex;i<daysLeft;i++){
            wake = JodaUtil.setTime(wake,state.wakeSleepSchedule.get(dayOfWeek).getWakeTime());
            bed = JodaUtil.setTime(bed,state.wakeSleepSchedule.get(dayOfWeek).getBedTime());
            boolean nightshift = bed.isBefore(wake);
            if(nightshift){
                bed = bed.plusDays(1);
            }

            gap = JodaUtil.toUtcDouble(bed) - JodaUtil.toUtcDouble(wake);

            if(gap<(7*60*60)) {
                period = gap / 5;
                if(period<=0){
                    period=10;
                }

                TestSession t0 = testSessions.get((i*4)+0);
                t0.setSessionDate(wake.plusSeconds((int) ((period))));
                testSessions.set((i*4)+0,t0);

                TestSession t1 = testSessions.get((i*4)+1);
                t1.setSessionDate(wake.plusSeconds((int) ((2 * period))));
                testSessions.set((i*4)+1,t1);

                TestSession t2 = testSessions.get((i*4)+2);
                t2.setSessionDate(wake.plusSeconds((int) ((3 * period))));
                testSessions.set((i*4)+2,t2);

                TestSession t3 = testSessions.get((i*4)+3);
                t3.setSessionDate(wake.plusSeconds((int) ((4 * period))));
                testSessions.set((i*4)+3,t3);

            } else {
                gap -= 6 * 60 * 60;
                period = gap / 4;
                if (period <= 0) {
                    period = 10;
                }

                DateTime lastTest = wake.plusSeconds(random.nextInt((int)period));
                TestSession t0 = testSessions.get((i*4)+0);
                t0.setSessionDate(lastTest);
                testSessions.set((i*4)+0,t0);

                TestSession t1 = testSessions.get((i*4)+1);
                lastTest = lastTest.plusHours(2).plusSeconds(random.nextInt((int)period));
                t1.setSessionDate(lastTest);
                testSessions.set((i*4)+1,t1);

                TestSession t2 = testSessions.get((i*4)+2);
                lastTest = lastTest.plusHours(2).plusSeconds(random.nextInt((int)period));
                t2.setSessionDate(lastTest);
                testSessions.set((i*4)+2,t2);

                TestSession t3 = testSessions.get((i*4)+3);
                lastTest = lastTest.plusHours(2).plusSeconds(random.nextInt((int)period));
                t3.setSessionDate(lastTest);
                testSessions.set((i*4)+3,t3);
            }

            wake = wake.plusDays(1);
            if(!nightshift) {
                bed = bed.plusDays(1);
            }
            dayOfWeek = getDayOfWeekIndex(bed);
        }
        scheduleNotifications(true);
    }

    public void updateStartDate(ArcManager.State state){
        int sessionId = state.currentTestSession;
        for(int i=sessionId;i<28;i++){
            NotificationManager.getInstance().removeNotification(i, NotificationManager.TEST_TAKE);
            NotificationManager.getInstance().removeNotification(i, NotificationManager.TEST_MISSED);
        }
        NotificationManager.getInstance().removeNotification(1, NotificationManager.TEST_CONFIRM);
        NotificationManager.getInstance().removeNotification(1, NotificationManager.TEST_NEXT);
        NotificationManager.getInstance().removeNotification(2, NotificationManager.TEST_NEXT);
        NotificationManager.getInstance().removeNotification(3, NotificationManager.TEST_NEXT);
        testSessions.clear();
        scheduleTests(state);

        VisitStartDateChange dateChange = new VisitStartDateChange();
        dateChange.setArcId(ArcManager.getInstance().getArcId());
        dateChange.setDeviceId(ArcManager.getInstance().getDeviceId());
        dateChange.setVisitId(visitId);
        dateChange.setArcStartDate(visitStartDate);
        dateChange.setArcEndDate(visitEndDate);
        dateChange.setUserStartDate(userStartDate);
        dateChange.setUserEndDate(userEndDate);
        RestClient.updateArcDate(dateChange);
    }

    public void scheduleTests(ArcManager.State state){

        Random random = new Random(SystemClock.currentThreadTimeMillis());

        DateTime wake = getUserStartDate();
        DateTime bed =  getUserStartDate();

        int dayOfWeek = getDayOfWeekIndex(wake);


        int sessionId = 0;
        double gap;
        double period;
        boolean startNow = getUserStartDate().isEqual(JodaUtil.setMidnight(DateTime.now()));

        for(int i=0;i<7;i++) {

            TestSession t0 = null;
            TestSession t1 = null;
            TestSession t2 = null;
            TestSession t3 = null;

            wake = JodaUtil.setTime(wake,state.wakeSleepSchedule.get(dayOfWeek).getWakeTime());
            bed = JodaUtil.setTime(bed,state.wakeSleepSchedule.get(dayOfWeek).getBedTime());
            boolean nightshift = bed.isBefore(wake);
            if(nightshift){
                bed = bed.plusDays(1);
            }

            if(startNow && i == 0){
                wake = DateTime.now();
            }

            gap = JodaUtil.toUtcDouble(bed) - JodaUtil.toUtcDouble(wake);

            if(gap<(7*60*60)) {
                period = gap / 5;
                if(period<=0){
                    period=10;
                }

                t0 = new TestSession(state.currentVisit,sessionId);
                if(startNow && i == 0){
                    t0.setSessionDate(wake);
                } else {
                    t0.setSessionDate(wake.plusSeconds((int) ((period))));
                }

                t1 = new TestSession(state.currentVisit,sessionId+1);
                t1.setSessionDate(wake.plusSeconds((int) ((2 * period))));

                t2 = new TestSession(state.currentVisit,sessionId+2);
                t2.setSessionDate(wake.plusSeconds((int) ((3 * period))));

                t3 = new TestSession(state.currentVisit,sessionId+3);
                t3.setSessionDate(wake.plusSeconds((int) ((4 * period))));

            } else {
                gap -= 6 * 60 * 60;
                period = gap / 4;
                if (period <= 0) {
                    period = 10;
                }

                DateTime lastTest = null;
                if(startNow && i == 0){
                    lastTest = wake;
                } else {
                    lastTest = wake.plusSeconds(random.nextInt((int)period));
                }
                t0 = new TestSession(state.currentVisit,sessionId);
                t0.setSessionDate(lastTest);

                t1 = new TestSession(state.currentVisit,sessionId+1);
                lastTest = lastTest.plusHours(2).plusSeconds(random.nextInt((int)period));
                t1.setSessionDate(lastTest);

                t2 = new TestSession(state.currentVisit,sessionId+2);
                lastTest = lastTest.plusHours(2).plusSeconds(random.nextInt((int)period));
                t2.setSessionDate(lastTest);

                t3 = new TestSession(state.currentVisit,sessionId+3);
                lastTest = lastTest.plusHours(2).plusSeconds(random.nextInt((int)period));
                t3.setSessionDate(lastTest);
            }

            sessionId+=4;

            testSessions.add(t0);
            testSessions.add(t1);
            testSessions.add(t2);
            testSessions.add(t3);

            wake = wake.plusDays(1);
            if(!nightshift) {
                bed = bed.plusDays(1);
            }
            dayOfWeek = getDayOfWeekIndex(bed);
        }
        scheduleNotifications(false);
    }

}
