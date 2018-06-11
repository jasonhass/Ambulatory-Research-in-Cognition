package com.dian.arc.libs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.custom.WeekdayButton;
import com.dian.arc.libs.rest.models.WakeSleepSchedule;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.FontFactory;
import com.dian.arc.libs.utilities.JodaUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class SetupAvailabilityFragment extends BaseFragment {

    FancyButton button;
    TextView wakeTime;
    TextView bedTime;
    TextView copyTime;
    List<WeekdayButton> weekDays;
    WakeSleepSchedule schedule;
    int lastIndex;
    boolean sleepTrigger[] = new boolean[7];
    boolean wakeTrigger[] = new boolean[7];

    public SetupAvailabilityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup_availability, container, false);

        setupDebug(view);

        ((TextView)view.findViewById(R.id.textView)).setTypeface(FontFactory.tahomaBold);

        button = (FancyButton)view.findViewById(R.id.fancybuttonSetupAvailabilityNext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                wakeTime.setEnabled(false);
                bedTime.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SchedulingTask task = new SchedulingTask();
                        task.execute();
                    }
                },100);
            }
        });

        wakeTime = (TextView) view.findViewById(R.id.textviewWaketime);
        wakeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionDialogTime dialog = new QuestionDialogTime();
                dialog.time = wakeTime.getText().toString();
                dialog.otherTime = bedTime.getText().toString();;
                dialog.restrictTime = true;
                dialog.restrictWake = true;
                dialog.setOnDialogDismissListener(new QuestionDialogTime.OnDialogDismiss() {
                    @Override
                    public void dismiss(String time) {
                        wakeTime.setText(time);
                        schedule.get(lastIndex).setWakeTime(wakeTime.getText().toString());
                        new Handler().postDelayed(updateWake,100);
                    }
                });
                dialog.show(getFragmentManager(),QuestionDialogTime.class.getSimpleName());
            }
        });

        bedTime = (TextView) view.findViewById(R.id.textviewBedtime);
        bedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionDialogTime dialog = new QuestionDialogTime();
                dialog.time = bedTime.getText().toString();
                dialog.otherTime = wakeTime.getText().toString();
                dialog.restrictTime = true;
                dialog.restrictWake = false;
                dialog.setOnDialogDismissListener(new QuestionDialogTime.OnDialogDismiss() {
                    @Override
                    public void dismiss(String time) {
                        bedTime.setText(time);
                        schedule.get(lastIndex).setBedTime(bedTime.getText().toString());
                        new Handler().postDelayed(updateSleep,100);
                    }
                });
                dialog.show(getFragmentManager(),QuestionDialogTime.class.getSimpleName());
            }
        });

        copyTime = (TextView)view.findViewById(R.id.textviewSetupCopyTime);
        copyTime.setTypeface(FontFactory.tahomaBold);
        copyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schedule.get(lastIndex).setBedTime(bedTime.getText().toString());
                schedule.get(lastIndex).setWakeTime(wakeTime.getText().toString());
                ListDialog dialog = new ListDialog();
                Context c=getContext();
                String[] headers = new String[7];
                headers[0] = c.getString(R.string.setup_b_popup_header_su);
                headers[1] = c.getString(R.string.setup_b_popup_header_mo);
                headers[2] = c.getString(R.string.setup_b_popup_header_tu);
                headers[3] = c.getString(R.string.setup_b_popup_header_we);
                headers[4] = c.getString(R.string.setup_b_popup_header_th);
                headers[5] = c.getString(R.string.setup_b_popup_header_fr);
                headers[6] = c.getString(R.string.setup_b_popup_header_sa);

                dialog.title = headers[lastIndex];
                dialog.options = getListDialogOptions();
                dialog.selected = "";
                dialog.setOnDialogDismissListener(new ListDialog.OnDialogDismiss() {
                    @Override
                    public void dismiss(String selected) {
                        updateTimes(selected);
                        button.setEnabled(isFormValid());
                    }
                });
                dialog.show(getFragmentManager(),ListDialog.class.getSimpleName());
            }
        });

        weekDays = new ArrayList<>();
        weekDays.add((WeekdayButton)view.findViewById(R.id.weekdaySun));
        weekDays.add((WeekdayButton)view.findViewById(R.id.weekdayMon));
        weekDays.add((WeekdayButton)view.findViewById(R.id.weekdayTue));
        weekDays.add((WeekdayButton)view.findViewById(R.id.weekdayWed));
        weekDays.add((WeekdayButton)view.findViewById(R.id.weekdayThu));
        weekDays.add((WeekdayButton)view.findViewById(R.id.weekdayFri));
        weekDays.add((WeekdayButton)view.findViewById(R.id.weekdaySat));

        schedule = ArcManager.getInstance().getWakeSleepSchedule();
        if(schedule==null){
            schedule = new WakeSleepSchedule(getContext());
        }
        wakeTime.setText(schedule.get(lastIndex).getWakeTime());
        bedTime.setText(schedule.get(lastIndex).getBedTime());

        boolean ready = true;
        for(int j=0;j<7;j++){
            wakeTrigger[j] = schedule.get(j).getWakeTime()!=null;
            sleepTrigger[j] = schedule.get(j).getBedTime()!=null;
            if(wakeTrigger[j] && sleepTrigger[j]){
                weekDays.get(j).setDone();
            } else {
                ready = false;
            }
        }
        weekDays.get(lastIndex).toggle(true);
        button.setEnabled(ready);

        int i=0;
        for(WeekdayButton button: weekDays){
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!((WeekdayButton)view).isOn()) {
                        schedule.get(lastIndex).setBedTime(bedTime.getText().toString());
                        schedule.get(lastIndex).setWakeTime(wakeTime.getText().toString());
                        bedTime.setText(schedule.get(finalI).getBedTime());
                        wakeTime.setText(schedule.get(finalI).getWakeTime());
                        lastIndex = finalI;
                        for(WeekdayButton button: weekDays){
                            button.toggle(false);
                        }
                        ((WeekdayButton)view).toggle(true);
                        if (!bedTime.getText().toString().isEmpty() &&  !wakeTime.getText().toString().isEmpty()){
                            copyTime.setVisibility(View.VISIBLE);
                        } else {
                            copyTime.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });
            i++;
        }

        if(!bedTime.getText().toString().isEmpty() &&  !wakeTime.getText().toString().isEmpty()){
            copyTime.setVisibility(View.VISIBLE);
        } else {
            copyTime.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private Runnable updateSleep = new Runnable() {
        @Override
        public void run() {
            sleepTrigger[lastIndex] = true;
            if(sleepTrigger[lastIndex] && wakeTrigger[lastIndex]){
                schedule.setCreatedOn(JodaUtil.toUtcDouble(DateTime.now()));
                weekDays.get(lastIndex).setDone();
                weekDays.get(lastIndex).toggle(true);
                copyTime.setVisibility(View.VISIBLE);
                button.setEnabled(isFormValid());
            }
        }
    };

    private Runnable updateWake = new Runnable() {
        @Override
        public void run() {
            wakeTrigger[lastIndex] = true;
            if(sleepTrigger[lastIndex] && wakeTrigger[lastIndex]){
                schedule.setCreatedOn(JodaUtil.toUtcDouble(DateTime.now()));
                weekDays.get(lastIndex).setDone();
                weekDays.get(lastIndex).toggle(true);
                copyTime.setVisibility(View.VISIBLE);
                button.setEnabled(isFormValid());
            }
        }
    };

    private String getListDialogOptions(){
        String split[] = new String[7];
        split[0] = getString(R.string.weekday_sunday);
        split[1] = getString(R.string.weekday_monday);
        split[2] = getString(R.string.weekday_tuesday);
        split[3] = getString(R.string.weekday_wednesday);
        split[4] = getString(R.string.weekday_thursday);
        split[5] = getString(R.string.weekday_friday);
        split[6] = getString(R.string.weekday_saturday);
        String days = new String();
        for(int i=0;i<split.length;i++){
            if(i != lastIndex){
                days += split[i]+",";
            }
        }
        return days.substring(0,days.length()-1);
    }

    private String getDayString(){
        String days = getString(R.string.weekdays_su)+","+getString(R.string.weekdays_mo)+","+getString(R.string.weekdays_tu)+","+getString(R.string.weekdays_we)+","+getString(R.string.weekdays_th)+","+getString(R.string.weekdays_fr)+","+getString(R.string.weekdays_sa);
        String[] split = days.split(",");
        return split[lastIndex];
    }

    private void updateTimes(String string){
        String days = getString(R.string.weekdays_su)+","+getString(R.string.weekdays_mo)+","+getString(R.string.weekdays_tu)+","+getString(R.string.weekdays_we)+","+getString(R.string.weekdays_th)+","+getString(R.string.weekdays_fr)+","+getString(R.string.weekdays_sa);
        String[] options = new String[7];
        options[0] = getString(R.string.weekday_sunday);
        options[1] = getString(R.string.weekday_monday);
        options[2] = getString(R.string.weekday_tuesday);
        options[3] = getString(R.string.weekday_wednesday);
        options[4] = getString(R.string.weekday_thursday);
        options[5] = getString(R.string.weekday_friday);
        options[6] = getString(R.string.weekday_saturday);
        String[] selected = string.split(",");

        String wake = schedule.get(lastIndex).getWakeTime();
        String sleep = schedule.get(lastIndex).getBedTime();

        for(int i=0;i<selected.length;i++){
            for(int j=0;j<options.length;j++){
                if(selected[i].equals(options[j])){
                    schedule.get(j).setWakeTime(wake);
                    schedule.get(j).setBedTime(sleep);
                    schedule.setCreatedOn(JodaUtil.toUtcDouble(DateTime.now()));
                    weekDays.get(j).setDone();
                }
            }
        }
    }

    private boolean isFormValid(){
        boolean valid = true;
        for(WeekdayButton button : weekDays){
            if(!button.isDone()){
                valid = false;
            }
        }
        return valid;
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
            ArcManager.getInstance().setWakeSleepSchedule(schedule);
            if(ArcManager.getInstance().getState().currentPath==ArcManager.SETUP_NEW) {
                ArcManager.getInstance().startTrial();
            }
            ArcManager.getInstance().scheduleTests();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //hide the dialog
            loadingDialog.dismiss();
            if(ArcManager.getInstance().getLifecycleStatus()==ArcManager.LIFECYCLE_BASELINE || ArcManager.getInstance().getState().currentPath==ArcManager.SETUP_EXISTING){
                ArcManager.getInstance().getState().lifecycle = ArcManager.LIFECYCLE_ARC;
                ArcManager.getInstance().getState().nextPath = ArcManager.PATH_NONE;
            }
            ArcManager.getInstance().nextFragment();
            super.onPostExecute(result);
        }

    }
}
