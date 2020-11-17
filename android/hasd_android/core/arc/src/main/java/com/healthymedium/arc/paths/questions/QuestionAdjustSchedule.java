//
// QuestionAdjustSchedule.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.paths.informative.ScheduleCalendar;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.study.Scheduler;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class QuestionAdjustSchedule extends QuestionTemplate {

    boolean allowHelp;

    List<Option> options = new ArrayList<>();
    int shiftDays = 0;
    int index = 0;

    public QuestionAdjustSchedule(boolean allowBack, boolean allowHelp, String header, String subheader) {
        super(allowBack,header,subheader, ViewUtil.getString(R.string.button_confirm));
        this.allowHelp = allowHelp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(allowHelp);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNext.setEnabled(false);

                shiftDays = (int) onValueCollection();
                updateDates();
                Study.getRestClient().submitTestSchedule();
                NavigationManager.getInstance().open(new ScheduleCalendar());
            }
        });

        NumberPicker picker = new NumberPicker(Application.getInstance());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerTextColor(picker, ContextCompat.getColor(Application.getInstance(), R.color.text));
        content.addView(picker);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                index = newVal;
                if(!buttonNext.isEnabled()) {
                    buttonNext.setEnabled(true);
                }
            }
        });

        TestCycle cycle = Study.getCurrentTestCycle();
        DateTime scheduledStart = cycle.getScheduledStartDate();
        DateTime scheduledEnd = cycle.getScheduledEndDate();

        options.clear();

        for(int i=7; i>0; i--) {
            Option option = new Option(scheduledStart.minusDays(i),scheduledEnd.minusDays(i+1));
            options.add(option);
        }

        for(int i=0; i<=7; i++) {
            Option option = new Option(scheduledStart.plusDays(i),scheduledEnd.plusDays(i-1));
            options.add(option);
        }

        DateTime actualStart = cycle.getActualStartDate();
        String[] data = new String[options.size()];
        index = 0;

        for(int i=0; i<options.size(); i++) {
            if(options.get(i).start.equals(actualStart)) {
                index = i;
            }
            data[i] = options.get(i).label;
        }

        picker.setMinValue(0);
        picker.setMaxValue(data.length-1);
        picker.setDisplayedValues(data);
        picker.setValue(index);

        return view;
    }

    @Override
    public Object onValueCollection(){
        // if we have any doubts, don't change anything
        if(index < 0 || index >= options.size()) {
            return 0;
        }

        // calculate the number of days we need to shift the cycle by
        DateTime start = Study.getCurrentTestCycle().getScheduledStartDate();
        Option option = options.get(index);
        int days = Days.daysBetween(start.toLocalDate(),option.start.toLocalDate()).getDays();

        return days;
    }

    public void updateDates() {
        TestCycle cycle = Study.getCurrentTestCycle();
        Scheduler scheduler = Study.getScheduler();

        scheduler.unscheduleNotifications(cycle);
        cycle.shiftSchedule(shiftDays);
        scheduler.scheduleNotifications(cycle, false);
    }

    public void setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        try{
            Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
            selectorWheelPaintField.setAccessible(true);
            ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
        }
        catch(Exception e){
            // nothing
        }

        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                ((EditText) child).setTextColor(color);
            }
        }
        numberPicker.invalidate();
    }

    class Option {
        DateTime start;
        DateTime end;
        String label;

        public Option(DateTime start, DateTime end) {
            Phrase phrase = new Phrase("{DATE1}-{DATE2}");
            phrase.replaceDates(
                    R.string.format_date_schedule,
                    start,
                    end);
            label = phrase.toString();
            this.start = start;
            this.end = end;
        }
    }

}
