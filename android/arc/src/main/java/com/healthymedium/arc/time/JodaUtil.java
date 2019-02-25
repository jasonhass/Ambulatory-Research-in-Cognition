//
// JodaUtil.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.time;


import org.joda.time.DateTime;


public class JodaUtil {

    public static double toUtcDouble(DateTime dateTime){
        double utc = dateTime.getMillis();
        return utc/1000;
    }

    public static DateTime setTime(DateTime date, String time){
        String[] split = time.split("[: ]");
        if(split.length != 3){
            return date;
        }
        split[2]  = split[2].toLowerCase();
        int hours = Integer.valueOf(split[0]);
        int minutes = Integer.valueOf(split[1]);
        if(split[2].toLowerCase().equals("pm") && hours != 12){
            hours +=12;
        } else if(split[2].toLowerCase().equals("am") && hours == 12){
            hours = 0;
        } else {
            //hours -= 1;
        }
        DateTime newDate = date
                .withHourOfDay(hours)
                .withMinuteOfHour(minutes)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        return newDate;
    }

    public static DateTime setMidnight(DateTime date){
        DateTime newDate = date
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        return newDate;
    }


}
