//
// SchedulerTest.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.sample;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.CircadianRhythm;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Scheduler;
import com.healthymedium.arc.study.Visit;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SchedulerTest {

    @Test
    public void scheduleTests() {
        JodaTimeAndroid.init(InstrumentationRegistry.getContext());

        List<Day> test = new ArrayList<>();
        test.add(new Day("Sunday",      "07:00:00","06:00:00"));
        test.add(new Day("Monday",      "08:00:00","12:00:00"));
        test.add(new Day("Tuesday",     "08:00:00","12:00:00"));
        test.add(new Day("Wednesday",   "08:00:00","12:30:00"));
        test.add(new Day("Thursday",    "08:00:00","12:00:00"));
        test.add(new Day("Friday",      "08:00:00","12:30:00"));
        test.add(new Day("Saturday",    "18:00:00","06:00:00"));
        List<Visit> visits = runSchedulingTest(test);

    }

    List<Visit>  runSchedulingTest(List<Day> days){
        Participant participant = new Participant();
        participant.initialize();
        participant.getState().currentTestSession = 0;
        participant.getState().currentVisit = 0;

        CircadianClock clock = new CircadianClock();

        int size = days.size();
        for(int i=0;i<size;i++){
            CircadianRhythm rhythm = clock.getRhythm(days.get(i).day);
            rhythm.setWakeTime(days.get(i).wake);
            rhythm.setBedTime(days.get(i).bed);
        }

        Assert.assertTrue("Invalid Availability",clock.isValid());
        participant.setCircadianClock(clock);

        Scheduler scheduler = new Scheduler();
        scheduler.scheduleTests(DateTime.now(),participant);

        return participant.getState().visits;
    }

    class Day {
        public String day;
        public LocalTime wake;
        public LocalTime bed;

        Day(String day, String wake,String bed){
            this.day = day;
            this.wake = LocalTime.parse(wake);
            this.bed = LocalTime.parse(bed);
        }
    }

}
