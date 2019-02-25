//
// Visit.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import android.util.Log;

import com.healthymedium.arc.time.JodaUtil;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class Visit {

    private int id;
    private DateTime scheduledStartDate;          // official start date of the Visit,
    private DateTime scheduledEndDate;            // official end date of the Visit,
    private DateTime actualStartDate;           // user-modified start date of the Visit,
    private DateTime actualEndDate;             // user-modified end date of the Visit,
    private boolean hasConfirmedDate;           // a flag indicating whether the user has confirmed the start date,
    private boolean hasScheduledNotifications;  // a flag indicating whether notifications have been scheduled for the SubmitTest Sessions
    boolean valid;

    List<TestSession> testSessions;

    public Visit(int id){
        this.id = id;
        testSessions = new ArrayList<>();
        valid = false;

    }

    public Visit(int id, DateTime start, DateTime end){
        this.id = id;
        scheduledStartDate = JodaUtil.setMidnight(start);
        scheduledEndDate = JodaUtil.setMidnight(end);
        testSessions = new ArrayList<>();
        valid = true;
    }

    public void confirmDate(){
        hasConfirmedDate = true;
    }

    public boolean hasConfirmedDate() {
        return hasConfirmedDate;
    }

    public int getId() {
        return id;
    }

    public DateTime getScheduledStartDate() {
        return scheduledStartDate;
    }

    public DateTime getScheduledEndDate() {
        return scheduledEndDate;
    }

    public DateTime getActualStartDate() {
        if(actualStartDate == null) {
            return scheduledStartDate;
        }
        return actualStartDate;
    }

    public void setActualStartDate(DateTime actualStartDate) {
        this.actualStartDate = JodaUtil.setMidnight(actualStartDate);
    }

    public DateTime getActualEndDate() {
        if(actualEndDate == null){
            return scheduledEndDate;
        }
        return actualEndDate;
    }

    public void setActualEndDate(DateTime actualEndDate) {
        this.actualEndDate = JodaUtil.setMidnight(actualEndDate);
    }

    public boolean hasScheduledNotifications() {
        return hasScheduledNotifications;
    }

    public List<TestSession> getTestSessions() {
        if(testSessions==null){
            testSessions = new ArrayList<>();
        }
        return testSessions;
    }

    public void updateTestSessions(List<TestSession> sessions){
        testSessions = sessions;
    }


    public void confirmNotificationsScheduled() {
        this.hasScheduledNotifications = true;
    }

    public int getNumberOfTestsAvailable(){
        int count = 0;
        for(TestSession test : testSessions){
            if(!test.isOver() && test.isAvailable()){
                count++;
            }
        }
        return count;
    }

    public int getNumberOfDays(){
        return Days.daysBetween(getActualStartDate(),getActualEndDate()).getDays();
    }

    public int getNumberOfTests(){
        return testSessions.size();
    }


    public int getNumberOfTests(int dayIndex){
        int count = 0;
        for(TestSession test : testSessions){
            if(test.getDayIndex()==dayIndex){
                count++;
            }
        }
        return count;
    }

    public int getNumberOfTestsToday(){
        LocalDate today = LocalDate.now();
        return getNumberOfTests(today);
    }

    public int getNumberOfTests(LocalDate date){
        int count = 0;
        for(TestSession test : testSessions){
            if(test.getScheduledTime().toLocalDate().equals(date)){
                count++;
            }
        }
        return count;
    }

    public int getNumberOfTests(DateTime before, DateTime after){
        int count = 0;
        for(TestSession test : testSessions){
            DateTime date = test.getScheduledTime();
            if((date.equals(before) || date.isAfter(before)) && (date.equals(after) ||date.isBefore(after))){
                count++;
            }
        }
        return count;
    }
    public int getNumberOfTestsLeftForToday(){
        int count = 0;
        LocalDate today = LocalDate.now();
        for(TestSession test : testSessions){
            if(!test.isOver() && test.getScheduledTime().toLocalDate().equals(today)){
                count++;
            }
        }
        return count;
    }

    public int getNumberOfTestsLeft(){
        int count = 0;
        for(TestSession test : testSessions){
            if(!test.isOver()){
                count++;
            }
        }
        return count;
    }

    public boolean hasThereBeenAFinishedTest(){
        boolean result = false;
        for(TestSession test : testSessions){
            if(test.isFinished()){
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean hasThereBeenAFinishedTest(int dayIndex){
        boolean result = false;
        for(TestSession test : testSessions){
            if(test.getDayIndex()==dayIndex && test.isFinished()){
                result = true;
                break;
            }
        }
        return result;
    }

    void logTests(){
        int size = testSessions.size();
        for(int i=0;i<size;i++){
            TestSession test = testSessions.get(i);
            Log.i("Visit","index="+test.getIndex()+", id="+test.getId()+", scheduledTime="+test.getScheduledTime().toString());
        }
    }

}
