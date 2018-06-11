package com.dian.arc.libs;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;

import java.util.ArrayList;
import java.util.List;

public class QuestionDialogDuration extends DialogFragment {

    public String duration;

    TextView textHours;
    TextView textMinutes;
    NumberPicker pickerHours;
    NumberPicker pickerMinutes;
    FancyButton button;
    OnDialogDismiss listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_question_duration, container, false);
        int valHour = 0;
        int valMinute = 0;
        if(duration.isEmpty()) {
            duration = "0 "+getString(R.string.time_min);
        } else {
            String[] split = duration.split(" ");
            if(split.length==2){
                valMinute = Integer.valueOf(split[0])/5;
            } else {
                valHour = Integer.valueOf(split[0]);
                valMinute = Integer.valueOf(split[2])/5;
            }
        }


        textHours = (TextView) v.findViewById(R.id.textviewHours);
        textMinutes = (TextView) v.findViewById(R.id.textviewMinutes);


        pickerHours = (NumberPicker) v.findViewById(R.id.pickerHours);
        pickerHours.setWrapSelectorWheel(false);
        pickerHours.setMinValue(0);
        pickerHours.setMaxValue(23);
        pickerHours.setValue(valHour);
        pickerHours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if(newVal == 1){
                    textHours.setText(getString(R.string.time_hour));
                } else if(oldVal==1){
                    textHours.setText(getString(R.string.time_hours));
                }
            }
        });

        pickerMinutes = (NumberPicker) v.findViewById(R.id.pickerMinutes);
        pickerMinutes.setWrapSelectorWheel(false);
        pickerMinutes.setMinValue(0);
        pickerMinutes.setMaxValue(55);
        pickerMinutes.setValue(valMinute);
        pickerMinutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //picker.setValue((newVal < oldVal)?oldVal-5:oldVal+5);
            }
        });

        button = (FancyButton) v.findViewById(R.id.buttonQuestionDialogTime);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    pickerHours.clearFocus();
                    pickerMinutes.clearFocus();
                    listener.dismiss(getDuration());
                }
                dismiss();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textHours.setMinWidth(textHours.getWidth());
                textHours.setMaxWidth(textHours.getWidth());
                textMinutes.setMinWidth(textMinutes.getWidth());
                textMinutes.setMaxWidth(textMinutes.getWidth());
            }
        },100);

        try{
            pickerMinutes.setMinValue(0);
            pickerMinutes.setMaxValue((60 / 5) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += 5) {
                displayedValues.add(String.format("%02d", i));
            }
            pickerMinutes.setDisplayedValues(displayedValues
                    .toArray(new String[displayedValues.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    private String getDuration(){
        int hour = pickerHours.getValue();
        int minute = pickerMinutes.getValue()*5;
        String string = new String();
        string += String.format("%01d "+getString(R.string.time_hr)+", ",hour);
        /*
        if(hour > 1){
            string += String.format("%01d hr ",hour);
        } else if(hour > 0){
            string += String.format("%01d hr ",hour);
        }
        */
        string += String.format("%01d ",minute)+getString(R.string.time_min);
        return string;
    }

    public void setOnDialogDismissListener(OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss(String time);
    }
}
