package com.dian.arc.libs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dian.arc.libs.custom.CalendarView;
import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.Config;
import com.dian.arc.libs.utilities.JodaUtil;

import org.joda.time.DateTime;

public class SetupDateFragment extends BaseFragment {

    CalendarView calendarView;
    DateTime dateTime;
    FancyButton button;
    int visitId;

    public SetupDateFragment() {

    }

    public void setVisitId(int visitId){
        this.visitId = visitId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup_date, container, false);

        setupDebug(view);

        final DateTime today = JodaUtil.setMidnight(DateTime.now());
        button = (FancyButton)view.findViewById(R.id.fancybuttonSetupDateConfirm);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calendarView.hasValidDate()) {
                    dateTime = calendarView.getDate();
                    SchedulingTask task = new SchedulingTask();
                    task.execute();
                }
            }
        });

        calendarView = (CalendarView)view.findViewById(R.id.calendarView);
        calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void dateSelected(DateTime date) {
                button.setEnabled(date.isEqual(today) || date.isAfter(today));
            }

            @Override
            public void invalidated() {
                button.setEnabled(false);
            }
        });

        if(Config.SKIP_BASELINE && visitId==1) {
            view.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dateTime = today;
                    SchedulingTask task = new SchedulingTask();
                    task.execute();
                }
            },500);
        }


        return view;
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
            ArcManager.getInstance().setupArcs(visitId,dateTime);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            loadingDialog.dismiss();
            ArcManager.getInstance().nextFragment();
            super.onPostExecute(result);
        }

    }

}
