//
// TestSession.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import android.util.Log;

import com.healthymedium.arc.api.tests.BaseTest;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class TestSession {

    private int dayIndex;
    private int index;
    private int id;
    private DateTime scheduledTime;
    private DateTime startTime;
    private DateTime completeTime;
    private DateTime expirationTime;

    private List<Object> testData = new ArrayList<>();

    private boolean finishedSession;
    private boolean missedSession;
    private boolean interrupted;

    public TestSession(int dayIndex, int index, int id){
        this.dayIndex = dayIndex;
        this.index = index;
        this.id = id;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public int getIndex() {
        return index;
    }

    public int getId() {
        return id;
    }

    public boolean isFinished(){
        return finishedSession;
    }

    public void markCompleted(boolean finished){
        completeTime = DateTime.now();
        finishedSession = finished;
    }

    public void markAbandoned() {
        completeTime = DateTime.now();
        finishedSession = false;
    }

    public void markMissed(){
        missedSession = true;
        finishedSession = false;
    }

    public DateTime getExpirationTime() {
        return expirationTime;
    }

    public DateTime getScheduledTime() {
        return scheduledTime;
    }

    public DateTime getCompletedTime() {
        return completeTime;
    }

    public void setScheduledTime(DateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
        this.expirationTime = scheduledTime.plusHours(2);
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void markStarted() {
        this.startTime = DateTime.now();
    }

    public void addTestData(BaseTest data){
        Log.i("TestSession","addTestData("+data.getClass().getName()+")");
        testData.add(data);
    }

    public boolean isOver(){
        return (wasFinished() || wasMissed());
    }

    public boolean isOngoing() {
        return startTime!=null && completeTime==null && !missedSession;
    }

    public boolean isAvailable(){
        DateTime now = DateTime.now();
        return (scheduledTime.isBefore(now) && expirationTime.isAfter(now));
    }

    public List<Object> getTestData(){
        return testData;
    }

    public boolean wasInterrupted() {
        return interrupted;
    }

    public boolean wasFinished() {
        return finishedSession;
    }

    public boolean wasMissed() {
        return missedSession;
    }

    public void markInterrupted() {
        this.interrupted = true;
    }

    public int getDayOfWeek() {
        int dayOfWeek = scheduledTime.getDayOfWeek();
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

    public void purgeData(){
        startTime = null;
        completeTime = null;
        expirationTime = null;

        testData.clear();

        finishedSession = false;
        missedSession = false;
        interrupted = false;
    }

}
