//
// DurationInput.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.healthymedium.arc.library.R;

import java.util.ArrayList;
import java.util.List;

public class DurationInput extends FrameLayout {

    NumberPicker minutePicker;
    NumberPicker hourPicker;
    TextView textViewMinute;
    TextView textViewHour;

    Listener listener;
    int minutes = 5;

    public DurationInput(Context context) {
        super(context);
        init(context);
    }

    public DurationInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DurationInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_duration_input,this);

        textViewHour = view.findViewById(R.id.textViewHour);
        hourPicker = view.findViewById(R.id.hourPicker);
        hourPicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        hourPicker.setWrapSelectorWheel(false);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(0);
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minutes = (minutePicker.getValue()*5);
                minutes += newVal*60;

                if(listener!=null){
                    listener.onDurationChanged(minutes);
                }

            }
        });

        textViewMinute = view.findViewById(R.id.textViewMinute);
        minutePicker = view.findViewById(R.id.minutePicker);
        minutePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        minutePicker.setWrapSelectorWheel(false);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue((60 / 5) - 1);
        List<String> displayedValues = new ArrayList<>();
        for (int i = 0; i < 60; i += 5) {
            displayedValues.add(String.format("%01d", i));
        }
        minutePicker.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minutes = (newVal*5);
                minutes += hourPicker.getValue()*60;

                if(listener!=null){
                    listener.onDurationChanged(minutes);
                }
            }
        });
    }

    public void setDuration(int minutes){
        this.minutes = minutes;
        if(minutePicker!=null && hourPicker!=null){
            minutePicker.setValue((((minutes)%60)/5));
            hourPicker.setValue(minutes/60);
        }
    }

    public int getDuration(){
        return minutes;
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public interface Listener{
        void onDurationChanged(int minutes);
    }

}
