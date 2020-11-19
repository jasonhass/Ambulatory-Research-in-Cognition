//
// SchedulerTest.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import com.healthymedium.arc.time.TimeUtil;
import com.healthymedium.arc.utilities.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class SchedulerTest {


    @Test
    public void testDefault() {

        Log.pointToSystemOut();
        Log.v("testDefault","\n--------------------------------------------------\n"+
                "one schedule");

        Participant participant = Participants.getDefault();

        DateTime now = DateTime.parse("2019-07-29 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss"));
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        TestScheduler scheduler = new TestScheduler();
        scheduler.scheduleTests(now,participant);
    }

    @Test
    public void test() {

        Log.pointToSystemOut();


        DateTime now = DateTime.parse("2019-07-29 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss"));
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        TestScheduler scheduler = new TestScheduler();
        CircadianClock clock = new CircadianClock();

        Participant participant = Participants.getDefault();
        participant.getState().circadianClock = clock;

        List<CircadianRhythm> rhythms = new ArrayList<>();

        //  2 - span two days to make sure overnight permutations are generated
        // 24 - hours in  a day
        //  4 - fifteen minute increments
        int increments = 2*24*4;

        DateTime start = TimeUtil.setMidnight(DateTime.now());
        DateTime wake = start.minusMinutes(15);
        DateTime bed;

        // generate a list of unique
        for(int i=0; i<increments; i++){
            wake = wake.plusMinutes(15);
            bed = start;

            for(int j=0; j<increments; j++){
                bed = bed.plusMinutes(15);

                if(wake.plusHours(8).isAfter(bed) || bed.minusHours(18).isAfter(wake)) {
                    // in violoation of the 8 hour min or 18 hour max span
                    continue;
                }

                boolean found = false;
                for(CircadianRhythm oldRhythm : rhythms){
                    if(oldRhythm.getWakeTime().equals(wake.toLocalTime())){
                        found= true;
                        break;
                    }
                    if(oldRhythm.getBedTime().equals(bed.toLocalTime())){
                        found= true;
                        break;
                    }
                }

                if(!found){
                    CircadianRhythm rhythm = new CircadianRhythm("");
                    rhythm.setTimes(wake.toLocalTime(),bed.toLocalTime());
                    rhythms.add(rhythm);
                }
            }
        }

        for(CircadianRhythm rhythm : rhythms){
            clock.setRhythms(rhythm.getWakeTime(),rhythm.getBedTime());

            if(!clock.isValid()) {

                continue;
            }

            scheduler.scheduleTests(now,participant);

            List<TestCycle> cycles = participant.getState().testCycles;
            for(TestCycle cycle : cycles) {
                checkConformance(rhythm,cycle);
            }
        }

    }

    private void checkConformance(CircadianRhythm rhythm, TestCycle cycle) {

        List<TestDay> days = cycle.getTestDays();

        if(cycle.getId()==0) {
            Assert.assertTrue(days.size()==8);
        } else {
            Assert.assertTrue(days.size()==7);
        }

        for(TestDay day : days){

            if(day.getDayIndex()==0 && cycle.getId()==0) {
               continue;
            }

            DateTime start = day.getStartTime();
            DateTime end = day.getEndTime();

            // make sure start and end reflect the rhythm
            Assert.assertTrue(start.toLocalTime().isEqual(rhythm.getWakeTime()));
            Assert.assertTrue(end.toLocalTime().isEqual(rhythm.getBedTime()));

            List<TestSession> sessions = day.getTestSessions();
            int size = sessions.size();
            Assert.assertTrue(size==4);

            // Testing schedule within availability window set by user (e.g., 9 a.m. to 9 p.m.)
            for(TestSession session : sessions){
                boolean afterStart = session.getScheduledTime().isAfter(start);
                boolean equalStart = session.getScheduledTime().isEqual(start);
                boolean beforeEnd = session.getScheduledTime().isBefore(end);
                boolean equalEnd = session.getScheduledTime().isEqual(end);

                // logic structured to allow easy debugging
                if(!(afterStart || equalStart)){
                    Assert.assertTrue(false);
                }

                if(!(beforeEnd || equalEnd)) {
                    Assert.assertTrue(false);
                }

            }

            // 2 hours minimum between tests
            // Test times are sequential
            for(int i=1; i<size; i++){
                DateTime missedTime = sessions.get(i-1).getScheduledTime().plusHours(2);
                DateTime scheduledTime = sessions.get(i).getScheduledTime();

                boolean before = missedTime.isBefore(scheduledTime);
                boolean equal = missedTime.isEqual(scheduledTime);

                if(!(before || equal)){
                    Assert.assertTrue(false);
                }
            }
        }
    }


    public class TestScheduler extends Scheduler {

        public TestScheduler() {
            super();
        }

        @Override
        public void initializeCycles(DateTime now, Participant participant) {

            List<TestCycle> cycles = participant.getState().testCycles;
            DateTime midnight = TimeUtil.setMidnight(now);

            TestCycle baseline = initializeBaselineCycle(midnight,8);
            cycles.add(baseline);

            TestCycle visit1 = initializeCycle(1,4,midnight.plusDays(90),7);
            cycles.add(visit1);

            int id = 0;
            for(TestCycle cycle : cycles) {
                for(TestDay day : cycle.getTestDays()) {
                    for(TestSession session : day.getTestSessions()) {
                        session.setId(id);
                        id++;
                    }
                }
            }

        }
    }

}
