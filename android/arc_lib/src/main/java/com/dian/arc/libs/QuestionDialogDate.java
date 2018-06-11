package com.dian.arc.libs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.utilities.ArcManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class QuestionDialogDate extends DialogFragment {

    NumberPicker pickerDate;
    FancyButton button;
    OnDialogDismiss listener;
    int count = 5;

    private DateTime finalDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_question_date, container, false);
        pickerDate = (NumberPicker)v.findViewById(R.id.pickerDate);
        pickerDate.setWrapSelectorWheel(false);
        final DateTime startDate = ArcManager.getInstance().getCurrentVisit().getVisitStartDate();
        try{
            List<String> displayedValues = new ArrayList<>();
            int value = 2;
            String format = getString(R.string.format_date);
            if(startDate.minusWeeks(2).isBeforeNow()){
                count = 4;
                value = 1;
                displayedValues.add(startDate.minusWeeks(1).toString(format));
                displayedValues.add(startDate.toString(format));
                displayedValues.add(startDate.plusWeeks(1).toString(format));
                displayedValues.add(startDate.plusWeeks(2).toString(format));
            } else if (startDate.minusWeeks(1).isBeforeNow()){
                count = 3;
                value = 0;
                displayedValues.add(startDate.toString(format));
                displayedValues.add(startDate.plusWeeks(1).toString(format));
                displayedValues.add(startDate.plusWeeks(2).toString(format));
            } else if (startDate.isBeforeNow()){
                count = 2;
                value = 0;
                displayedValues.add(startDate.plusWeeks(1).toString(format));
                displayedValues.add(startDate.plusWeeks(2).toString(format));
            } else if (startDate.plusWeeks(1).isBeforeNow()){
                count = 1;
                value = 0;
                displayedValues.add(startDate.plusWeeks(2).toString(format));
            } else {
                count = 5;
                value = 2;
                displayedValues.add(startDate.minusWeeks(2).toString(format));
                displayedValues.add(startDate.minusWeeks(1).toString(format));
                displayedValues.add(startDate.toString(format));
                displayedValues.add(startDate.plusWeeks(1).toString(format));
                displayedValues.add(startDate.plusWeeks(2).toString(format));
            }

            pickerDate.setMinValue(0);
            pickerDate.setMaxValue(count-1);
            pickerDate.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));
            pickerDate.setValue(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        button = (FancyButton) v.findViewById(R.id.buttonQuestionDialogDate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    int value = pickerDate.getValue();
                    switch (count){
                        case 5:
                            if(value >=2) {
                                finalDate = startDate.plusWeeks(value-2);
                            } else {
                                finalDate = startDate.minusWeeks(2-value);
                            }
                            break;
                        case 4:
                            if(value >=1) {
                                finalDate = startDate.plusWeeks(value-1);
                            } else {
                                finalDate = startDate.minusWeeks(1);
                            }
                            break;
                        case 3:
                            finalDate = startDate.plusWeeks(value);
                            break;
                        case 2:
                            finalDate = startDate.plusWeeks(value+1);
                            break;
                        case 1:
                            finalDate = startDate.plusWeeks(2);
                            break;
                    }
                    ArcManager.getInstance().getCurrentVisit().setUserStartDate(finalDate);
                    ArcManager.getInstance().getCurrentVisit().setUserEndDate(finalDate.plusWeeks(1));
                    new SchedulingTask().execute();
                }
            }
        });

        return v;
    }

    private void dismissDialog(){
        dismiss();
    }

    public void setOnDialogDismissListener(OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss(String time);
    }


    private class SchedulingTask extends AsyncTask<Void, Void, Void> {
        LoadingDialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = new LoadingDialog();
            loadingDialog.show(getFragmentManager(),"LoadingDialog");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ArcManager.getInstance().getCurrentVisit().updateStartDate(ArcManager.getInstance().getState());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //hide the dialog
            loadingDialog.dismiss();
            listener.dismiss(finalDate.toString(getString(R.string.format_date)));
            dismissDialog();
            super.onPostExecute(result);
        }

    }
}
