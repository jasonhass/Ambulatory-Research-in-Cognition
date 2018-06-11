package com.dian.arc.libs;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.dian.arc.libs.custom.FancyButton;

import org.joda.time.DateTime;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QuestionDialogTime extends DialogFragment {

    public String time;
    public String otherTime;
    public boolean restrictTime = false;
    public boolean restrictWake = false;
    int otherHour = 0;
    int otherMin = 0;

    int lastMinute;
    TimePicker timePicker;
    FancyButton button;
    OnDialogDismiss listener;
    SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_question_time, container, false);
        if(restrictTime && !otherTime.isEmpty()){
            Date otherDate = null;
            try {
                otherDate = dateFormat.parse(otherTime);
            } catch (ParseException e) {
            }

            if(otherDate != null){
                Calendar otherCalendar = Calendar.getInstance();
                otherCalendar.setTime(otherDate);
                otherHour = otherCalendar.get(Calendar.HOUR_OF_DAY);
                otherMin = otherCalendar.get(Calendar.MINUTE);
            } else {restrictTime = false;}
        }else {restrictTime = false;}

        Date date = null;

        if(time.isEmpty()){
            time = "12:00 PM";
        }
        timePicker = (TimePicker) v.findViewById(R.id.pickerQuestionDialogTime);

        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
        }
        Calendar calendar = Calendar.getInstance();
        if(date != null) {
            calendar.setTime(date);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE)/5);
        } else {
            //noinspection deprecation
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            //noinspection deprecation
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE)/5);
        }
        button = (FancyButton) v.findViewById(R.id.buttonQuestionDialogTime);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    timePicker.clearFocus();
                    listener.dismiss(getTime());
                }
                dismiss();
            }
        });

        try{
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            Field minute = classForid.getField("minute");

            NumberPicker minuteSpinner = (NumberPicker) timePicker.findViewById(minute.getInt(null));
            minuteSpinner.setMinValue(0);
            minuteSpinner.setMaxValue((60 / 5) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += 5) {
                displayedValues.add(String.format("%02d", i));
            }
            minuteSpinner.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));

        } catch (Exception e) {
            e.printStackTrace();
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(restrictTime){
                    boolean enable;
                    if(restrictWake) {
                        int otherTime = otherMin+(otherHour*60);
                        enable = (otherTime - ((60*hourOfDay)+minute)) >= 240;
                    } else {
                        int otherTime = otherMin+(otherHour*60);
                        enable = (((60*hourOfDay)+minute) - otherTime) >= 240;
                    }
                    button.setEnabled(enable);
                }
            }
        });

        if(restrictTime) {
            boolean enable;
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            if(restrictWake) {
                int otherTime = otherMin+(otherHour*60);
                enable = (otherTime - ((60*hourOfDay)+minute)) >= 240;
            } else {
                int otherTime = otherMin+(otherHour*60);
                enable = (((60*hourOfDay)+minute) - otherTime) >= 240;
            }
            button.setEnabled(enable);
        }

        return v;
    }


    private String getTime(){
        int hour;
        int minute;
        String period;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute()*5;
        } else {
            //noinspection deprecation
            hour = timePicker.getCurrentHour();
            //noinspection deprecation
            minute = timePicker.getCurrentMinute()*5;
        }
        if(hour>=12){
            if(hour>12){
                hour-=12;
            }
            period = "PM";
        } else {
            if(hour==0){
                hour=12;
            }
            period = "AM";
        }
        return String.format("%01d:%02d "+period,hour,minute);
    }

    public void setOnDialogDismissListener(OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss(String time);
    }
}
