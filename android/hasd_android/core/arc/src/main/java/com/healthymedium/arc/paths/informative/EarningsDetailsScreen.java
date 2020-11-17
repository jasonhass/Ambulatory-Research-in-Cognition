//
// EarningsDetailsScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningDetails;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Earnings;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.earnings.EarningsDetailedCycleView;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class EarningsDetailsScreen extends BaseFragment {

    SwipeRefreshLayout refreshLayout;

    Button viewFaqButton;
    TextView studyTotal;
    TextView lastSync;
    LinearLayout cycleLayout;
    TextView textViewBack;

    public EarningsDetailsScreen() {
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings_details, container, false);

        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnabled(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        TextView textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().popBackStack();
            }
        });

        viewFaqButton = view.findViewById(R.id.viewFaqButton);
        viewFaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQScreen faqScreen = new FAQScreen();
                NavigationManager.getInstance().open(faqScreen);
            }
        });

        studyTotal = view.findViewById(R.id.studyTotal);
        lastSync = view.findViewById(R.id.textViewLastSync);
        cycleLayout = view.findViewById(R.id.cycleLayout);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setText(ViewUtil.getHtmlString(R.string.button_back));

        if(Study.getParticipant().getEarnings().hasCurrentDetails()){
            populateViews();
        } else {
            refresh();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
        refreshLayout.setProgressViewOffset(true, 0,top+ViewUtil.dpToPx(16));
    }

    private void refresh() {
        if (!Study.getRestClient().isUploadQueueEmpty()){
            refreshLayout.setRefreshing(false);
            if(getContext()!=null) {
                populateViews();
            }
            return;
        }

        refreshLayout.setRefreshing(true);

        Study.getParticipant().getEarnings().refreshDetails(new Earnings.Listener() {
            @Override
            public void onSuccess() {
                if(refreshLayout!=null) {
                    refreshLayout.setRefreshing(false);
                    Study.getParticipant().save();
                    if(getContext()!=null) {
                        populateViews();
                    }
                }
            }

            @Override
            public void onFailure() {
                if(refreshLayout!=null) {
                    refreshLayout.setRefreshing(false);
                    if(getContext()!=null) {
                        populateViews();
                    }
                }
            }
        });
    }

    private void populateViews() {

        Earnings earnings = Study.getParticipant().getEarnings();
        EarningDetails details = earnings.getDetails();

        if(details==null){
            return;
        }

        studyTotal.setText(details.total_earnings);

        String syncString = getString(R.string.earnings_sync) + " ";
        DateTime lastSyncTime = earnings.getDetailsRefreshTime();
        if(lastSyncTime != null) {
            if(lastSyncTime.plusMinutes(1).isBeforeNow()) {

                Phrase phrase = new Phrase(R.string.earnings_sync_datetime);
                phrase.replaceDate(R.string.format_date, lastSyncTime);
                phrase.replaceTime(R.string.format_time, lastSyncTime);

                syncString += phrase.toString();
            } else {
                syncString += getString(R.string.earnings_sync_justnow);
            }
        }
        lastSync.setText(syncString);

        cycleLayout.removeAllViews();
        for(EarningDetails.Cycle cycle : details.cycles){
            EarningsDetailedCycleView cycleView = new EarningsDetailedCycleView(getContext(),cycle);
            cycleLayout.addView(cycleView);
        }
    }

}
