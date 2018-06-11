package com.dian.arc.libs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.dian.arc.libs.custom.FancyButton;

public class QuestionDialogNumber extends DialogFragment {

    public int value;
    public int min;
    public int max;
    NumberPicker picker;
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
        View v = inflater.inflate(R.layout.dialog_question_number, container, false);
        picker = (NumberPicker) v.findViewById(R.id.pickerQuestionDialogNumber);
        picker.setMinValue(min);
        picker.setMaxValue(max);
        picker.setValue(value);
        picker.setWrapSelectorWheel(false);

        button = (FancyButton) v.findViewById(R.id.buttonQuestionDialogTime);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.dismiss(String.valueOf(picker.getValue()));
                }
                dismiss();
            }
        });
        return v;
    }

    public void setOnDialogDismissListener(OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss(String time);
    }
}
