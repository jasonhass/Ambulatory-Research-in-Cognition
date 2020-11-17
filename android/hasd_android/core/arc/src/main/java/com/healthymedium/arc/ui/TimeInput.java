//
// TimeInput.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TimeInput extends FrameLayout {

    boolean restrictTime = false;
    boolean valid = true;

    List<Window> validWindows;
    LocalTime blockoutBegin;
    LocalTime time;

    int minWakeTime = 4;
    int maxWakeTime = 24;

    Listener listener;
    TimePicker timePicker;
    TextView errorText;

    public TimeInput(Context context) {
        super(context);
        init(context);
    }

    public TimeInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimeInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_time_input,this);
        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        timePicker.setHour(12);
        timePicker.setMinute(0);

        errorText = view.findViewById(R.id.errorText);

        time = new LocalTime(12,0);

        try{
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field minute = classForid.getField("minute");
            NumberPicker minuteSpinner = timePicker.findViewById(minute.getInt(null));
            minuteSpinner.setMinValue(0);
            minuteSpinner.setMaxValue((60 / 15) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += 15) {
                displayedValues.add(String.format("%02d", i));
            }
            minuteSpinner.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));

        } catch (Exception e) {
            e.printStackTrace();
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(listener!=null){
                    listener.onTimeChanged();
                }
                if(restrictTime){
                    time = new LocalTime(hourOfDay,minute*15);

                    for(Window window : validWindows){
                        if(window.contains(time)){
                            setValidity(true);
                            return;
                        }
                    }

                    int minutesBetween = Minutes.minutesBetween(blockoutBegin,time).getMinutes();
                    if (minutesBetween <= 0) {
                        minutesBetween += maxWakeTime * 60;
                    }
                    if (minutesBetween < minWakeTime * 60 && minutesBetween >= 0) {
                        String error = ViewUtil.getString(R.string.availability_minimum_error).replace("{HOURS}", Integer.toString(minWakeTime));
                        errorText.setText(error);
                    } else {
                        String error = ViewUtil.getString(R.string.availability_maximum_error).replace("{HOURS}", Integer.toString(maxWakeTime));
                        errorText.setText(error);
                    }

                    setValidity(false);
                    return;
                }
                setValidity(true);
            }
        });

    }

    private void setValidity(boolean valid){
        if(this.valid!=valid){
            this.valid = valid;

            int visibility = (valid) ? View.GONE:View.VISIBLE;
            errorText.setVisibility(visibility);

            if(listener!=null){
                listener.onValidityChanged(valid);
            }
        }
    }

    public void setTime(LocalTime localTime) {
        this.time = localTime;
        timePicker.setHour(localTime.getHourOfDay());
        timePicker.setMinute(localTime.getMinuteOfHour()/15);
    }

    public LocalTime getTime(){
        time = new LocalTime(timePicker.getHour(),timePicker.getMinute()*15);
        return time;
    }

    public TimePicker getTimePicker(){
        return timePicker;
    }

    public boolean isTimeValid() {
        return valid;
    }

    public void placeRestrictions(List<Window> validWindows){
        restrictTime = true;
        this.validWindows = validWindows;

        boolean valid = false;
        for(Window window : validWindows){
            if(window.contains(time)){
               valid = true;
            }
        }
        setValidity(valid);
    }

    public void placeRestrictions(LocalTime blockoutBegin, int minDuration, int maxDuration){

        this.blockoutBegin = blockoutBegin;
        minWakeTime = minDuration;
        maxWakeTime = maxDuration;

        List<Window> windows = new ArrayList<>();

        LocalTime minLocalTime = blockoutBegin.plusHours(minWakeTime);
        LocalTime maxLocalTime = blockoutBegin.plusHours(maxWakeTime);

        if(maxLocalTime.isAfter(minLocalTime)){
            windows.add(new Window(minLocalTime,maxLocalTime));
        } else {
            windows.add(new Window(minLocalTime, LocalTime.MIDNIGHT.minusSeconds(1)));
            windows.add(new Window(LocalTime.MIDNIGHT, maxLocalTime));
        }

        placeRestrictions(windows);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public interface Listener{
        void onValidityChanged(boolean valid);
        void onTimeChanged();
    }

    public class Window {
        LocalTime begin;
        LocalTime end;

        public Window(LocalTime begin, LocalTime end){
            this.begin = begin;
            this.end = end;
        }

        public boolean contains(LocalTime time){
            return (time.equals(begin) || time.isAfter(begin)) && (time.isBefore(end) || time.equals(end));
        }

    }

}
