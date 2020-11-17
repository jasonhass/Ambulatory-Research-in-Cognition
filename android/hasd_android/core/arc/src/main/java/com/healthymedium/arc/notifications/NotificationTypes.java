//
// NotificationTypes.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications;

import com.healthymedium.arc.notifications.types.NotificationType;
import com.healthymedium.arc.notifications.types.TestProctor;
import com.healthymedium.arc.notifications.types.TestConfirmed;
import com.healthymedium.arc.notifications.types.TestMissed;
import com.healthymedium.arc.notifications.types.TestNext;
import com.healthymedium.arc.notifications.types.TestTake;
import com.healthymedium.arc.notifications.types.VisitNextDay;
import com.healthymedium.arc.notifications.types.VisitNextMonth;
import com.healthymedium.arc.notifications.types.VisitNextWeek;

public class NotificationTypes {

    public static TestProctor TestProctor = new TestProctor();
    public static TestMissed TestMissed = new TestMissed();
    public static TestTake TestTake = new TestTake();

    public static VisitNextDay VisitNextDay = new VisitNextDay();
    public static VisitNextWeek VisitNextWeek = new VisitNextWeek();
    public static VisitNextMonth VisitNextMonth = new VisitNextMonth();

    // deprecated at this point
    public static TestConfirmed TestConfirmed = new TestConfirmed();
    public static TestNext TestNext = new TestNext();

    public static NotificationType fromId(int id) {

        if(id==TestProctor.getId()){
            return TestProctor;
        }
        if(id==TestMissed.getId()){
            return TestMissed;
        }
        if(id==TestTake.getId()){
            return TestTake;
        }
        if(id==VisitNextDay.getId()){
            return VisitNextDay;
        }
        if(id==VisitNextWeek.getId()){
            return VisitNextWeek;
        }
        if(id==VisitNextMonth.getId()){
            return VisitNextMonth;
        }
        if(id==TestConfirmed.getId()){ // dep
            return TestConfirmed;
        }
        if(id==TestNext.getId()){ // dep
            return TestNext;
        }

        return null;
    }

    public static String getName(int id) {
        if(id==TestProctor.getId()){
            return "proctor";
        }
        if(id==TestMissed.getId()){
            return "test missed";
        }
        if(id==TestTake.getId()){
            return "test take";
        }
        if(id==VisitNextDay.getId()){
            return "visit next day";
        }
        if(id==VisitNextWeek.getId()){
            return "visit next week";
        }
        if(id==VisitNextMonth.getId()){
            return "visit next month";
        }
        if(id==TestConfirmed.getId()){ // dep
            return "test confirmed";
        }
        if(id==TestNext.getId()){ // dep
            return "test next";
        }

        return "?";
    }

}
