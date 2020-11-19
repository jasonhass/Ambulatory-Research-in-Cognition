//
// Visit.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration.models;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class Visit {

    public int visitId;
    public double visitStartDate;            // official start date of the Visit,
    public double visitEndDate;              // official end date of the Visit,
    public double userStartDate;           // user-modified start date of the Visit,
    public double userEndDate;             // user-modified end date of the Visit,
    public int hasConfirmedDate;           // a flag indicating whether the user has confirmed the start date,
    public int hasScheduledNotifications;  // a flag indicating whether notifications have been scheduled for the Test Sessions
    public boolean valid;
    public ChronotypeSurvey chronotype;
    public List<TestSession> testSessions;

    public Visit(int visitId){
        this.visitId = visitId;
        testSessions = new ArrayList<>();
        valid = false;
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

    public DateTime getUserEndDate() {
        if(userEndDate != 0){
            return new DateTime((long)userEndDate*1000);
        } else {
            return new DateTime((long) visitEndDate *1000);
        }
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

    public List<TestSession> getTestSessions() {
        if(testSessions==null){
            testSessions = new ArrayList<>();
        }
        return testSessions;
    }

    public void acknoweledgeNotificationsHaveBeenScheduled() {
        this.hasScheduledNotifications = 1;
    }

}
