//
// DebugDialog.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.core;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;



import com.healthymedium.arc.notifications.NotificationNodes;
import com.healthymedium.arc.notifications.ProctorDeviation;
import com.healthymedium.arc.paths.questions.QuestionLanguagePreference;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestDay;
import com.healthymedium.arc.utilities.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.notifications.NotificationTypes;
import com.healthymedium.arc.study.State;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.PriceManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class DebugDialog extends DialogFragment {

    TextView textViewStatus;
    boolean viewingStatus = true;

    View optionsView;
    View statusView;
    Button button;

    static public void launch(){
        DebugDialog dialog = new DebugDialog();
        dialog.show(NavigationManager.getInstance().getFragmentManager(),DebugDialog.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_debug, container, false);

        TextView textViewDateTime = v.findViewById(R.id.textViewDateTime);
        textViewDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_DATE_SETTINGS);
                startActivity(intent);
            }
        });

        TextView textViewBatteryOptimization = v.findViewById(R.id.textViewBatteryOptimization);
        textViewBatteryOptimization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
                dismiss();
            }
        });

        TextView textViewClearAppData = v.findViewById(R.id.textViewClearAppData);
        textViewClearAppData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager manager = (ActivityManager)getContext().getSystemService(ACTIVITY_SERVICE);
                boolean cleared = manager.clearApplicationUserData();
                if(!cleared) {
                    Toast.makeText(getContext(),"Failed to clear app data",Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView textViewLocale = v.findViewById(R.id.textViewLocale);
        textViewLocale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                PriceManager.getInstance().clearCache();
                QuestionLanguagePreference screen = new QuestionLanguagePreference();
                NavigationManager.getInstance().removeController();
                NavigationManager.getInstance().open(screen);
            }
        });

        TextView textViewSendLogs = v.findViewById(R.id.textViewSendLogs);
        textViewSendLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                
            }
        });

        TextView textViewDeviation = v.findViewById(R.id.textViewDeviation);
        textViewDeviation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProctorDeviation deviation = ProctorDeviation.load();
                deviation.markIncident();
                deviation.save();
                dismiss();
            }
        });

        textViewStatus = v.findViewById(R.id.textviewStatus);
        textViewStatus.setText(getStatus());

        statusView = v.findViewById(R.id.scrollViewStatus);
        optionsView = v.findViewById(R.id.scrollViewOptions);
        button = v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewingStatus){
                    switchToOptions();
                } else {
                    switchToStatus();
                }
            }
        });

        return v;
    }


    private void switchToOptions() {
        viewingStatus = false;
        button.setEnabled(false);
        optionsView.setVisibility(View.VISIBLE);
        statusView.animate().alpha(0.0f).setDuration(200);
        optionsView.animate().alpha(1.0f).setDuration(300);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                statusView.setVisibility(View.INVISIBLE);
                button.setText("View Status");
                button.setEnabled(true);
            }
        },300);
    }

    private void switchToStatus() {
        viewingStatus = true;
        button.setEnabled(false);
        statusView.setVisibility(View.VISIBLE);
        statusView.animate().alpha(1.0f).setDuration(300);
        optionsView.animate().alpha(0.0f).setDuration(200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                optionsView.setVisibility(View.INVISIBLE);
                button.setText("View Debug Options");
                button.setEnabled(true);
            }
        },300);
    }

    private String getStatus() {

        State studyState = Study.getStateMachine().getState();
        Participant participant = Study.getParticipant();

        String status = "";
        status += "locale config: " + Application.getInstance().getLocale().getDisplayName() + "\n";
        status += "locale prefs: " + PreferencesManager.getInstance().getString(Locale.TAG_LANGUAGE, "null") + "_" +
                PreferencesManager.getInstance().getString(Locale.TAG_COUNTRY, "null") + "\n\n";
        status += "lifecycle: "+Study.getStateMachine().getLifecycleName(studyState.lifecycle).toLowerCase()+"\n";
        status += "path: "+Study.getStateMachine().getPathName(studyState.currentPath).toLowerCase()+"\n\n";

        status += "cycle: "+participant.getState().currentTestCycle +"\n";
        status += "day: "+participant.getState().currentTestDay+"\n";
        status += "test: "+participant.getState().currentTestSession+"\n";

        if(!deviceHasNotificationParity()){
            status += "\ndetected a loss of parity between scheduled tests and notifications!\n";
        }

        TestCycle cycle = participant.getCurrentTestCycle();
        if(cycle==null) {
            status += "\n-- uninitialized --\n";
            return status;
        }

        status += "\n-- scheduled tests --\n";

        for(TestDay day : cycle.getTestDays()){
            status += "\nday "+day.getDayIndex()+"\n";
            for (TestSession session : day.getTestSessions()) {
                status += session.getScheduledTime().toString("MM/dd/yyyy   hh:mm:ss a\n");
            }
        }

        List<NotificationNode> nodes = NotificationManager.getInstance().getNodes().getAll();
        if(nodes.size()==0){
            return status;
        }

        status += "\n-- notifications --\n";

        List<List<NotificationNode>> structuredNodes = new ArrayList<>();

        int lastId = nodes.get(0).id;
        List<NotificationNode> set = new ArrayList<>();
        for(NotificationNode node : nodes) {
            if(node.id!=lastId){
                structuredNodes.add(set);
                set = new ArrayList<>();
            }
            set.add(node);
            lastId = node.id;
        }

        for(List<NotificationNode> list : structuredNodes) {
            if(list.size()==0){
                continue;
            }
            status += "\nid "+list.get(0).id+"\n";

            for(NotificationNode node : list) {
                String name = NotificationTypes.getName(node.type);
                status += node.time.toString("MM/dd   hh:mm:ss a (") + name + ")\n";
            }
        }

        return status;
    }

    boolean deviceHasNotificationParity() {
        TestCycle cycle = Study.getCurrentTestCycle();
        if(cycle==null) {
            return true;
        }
        NotificationNodes nodes = NotificationManager.getInstance().getNodes();
        List<TestSession> sessions = cycle.getTestSessions();

        for(TestSession session : sessions){

            DateTime scheduledTime = session.getScheduledTime();
            if(scheduledTime.isAfterNow()){
                NotificationNode take = nodes.get(NotificationTypes.TestTake.getId(),session.getId());
                if(take!=null){
                    return false;
                }
                if(!scheduledTime.equals(take.time)) {
                    return false;
                }
            }

            DateTime expirationTime = session.getExpirationTime();
            if(expirationTime.isAfterNow()) {
                NotificationNode missed = nodes.get(NotificationTypes.TestMissed.getId(), session.getId());
                if(missed==null){
                    return false;
                }
                if (!expirationTime.equals(missed.time)) {
                    return false;
                }
            }

        }

        return true;
    }

}
