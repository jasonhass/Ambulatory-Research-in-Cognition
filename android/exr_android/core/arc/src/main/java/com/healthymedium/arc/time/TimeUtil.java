//
// TimeUtil.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.time;


import android.content.ContentResolver;
import android.provider.Settings;
import android.support.annotation.StringRes;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Locale;


public class TimeUtil {

    public static String getWeekday(){
        return getWeekday(DateTime.now());
    }

    public static String getWeekday(DateTime dateTime){

        switch(dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                return "Sunday";
            case DateTimeConstants.MONDAY:
                return "Monday";
            case DateTimeConstants.TUESDAY:
                return "Tuesday";
            case DateTimeConstants.WEDNESDAY:
                return "Wednesday";
            case DateTimeConstants.THURSDAY:
                return "Thursday";
            case DateTimeConstants.FRIDAY:
                return "Friday";
            case DateTimeConstants.SATURDAY:
                return "Saturday";
            default:
                return "";
        }
    }

    public static double toUtcDouble(DateTime dateTime){
        double utc = dateTime.getMillis();
        return utc/1000;
    }

    public static DateTime fromUtcDouble(double dateTime){
        long longTime = (long)(dateTime*1000L);
        return new DateTime(longTime);
    }

    public static String format(DateTime dateTime, @StringRes int format){
        return dateTime.toString(ViewUtil.getString(format));
    }

    public static String format(DateTime dateTime, @StringRes int format, Locale locale){
        return dateTime.toString(ViewUtil.getString(format),locale);
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

    public static boolean isAutoTimeEnabled(){
        ContentResolver resolver = Application.getInstance().getContentResolver();
        boolean autoTimeEnabled = Settings.Global.getInt(resolver, Settings.Global.AUTO_TIME, 0)==1;
        boolean autoTimeZoneEnabled = Settings.Global.getInt(resolver, Settings.Global.AUTO_TIME_ZONE, 0)==1;
        return autoTimeEnabled && autoTimeZoneEnabled;
    }

}
