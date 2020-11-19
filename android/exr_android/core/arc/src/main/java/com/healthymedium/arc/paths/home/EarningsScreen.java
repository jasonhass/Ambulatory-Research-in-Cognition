//
// EarningsScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.EarningsDetailsScreen;
import com.healthymedium.arc.paths.informative.FAQScreen;
import com.healthymedium.arc.study.Earnings;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestDay;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.earnings.EarningsGoalView;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class EarningsScreen extends BaseFragment {

    SwipeRefreshLayout refreshLayout;

    TextView headerText;
    TextView weeklyTotal;
    TextView studyTotal;
    TextView lastSync;
    LinearLayout goalLayout;

    TextView weeklyTotalLabel;
    TextView studyTotalLabel;

    TextView earningsBody1;
    Button viewDetailsButton;
    TextView bonusHeader;
    TextView bonusBody;
    Button viewFaqButton;

    public EarningsScreen() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);

        Participant participant = Study.getParticipant();
        TestCycle testCycle = participant.getCurrentTestCycle();
        TestDay testDay = participant.getCurrentTestDay();
        TestSession testSession = participant.getCurrentTestSession();

        ParticipantState state = participant.getState();
        int sessionIndex = state.currentTestSession-1;
        int dayIndex = state.currentTestDay;
        int cycleIndex = state.currentTestCycle;

        if(testDay.getStartTime().isAfterNow()) {
            if(sessionIndex<0) {
                dayIndex--;
                if (dayIndex < 0) {
                    cycleIndex--;
                    testCycle = state.testCycles.get(cycleIndex);
                    dayIndex = testCycle.getNumberOfTestDays() - 1;
                }
                testDay = testCycle.getTestDay(dayIndex);
                sessionIndex = testDay.getNumberOfTests() - 1;
                testSession = testDay.getTestSession(sessionIndex);
            }
        }

        boolean isPractice = (dayIndex==0 && sessionIndex==0 && cycleIndex==0);

        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnabled(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!Study.getRestClient().isUploadQueueEmpty()){
                    refreshLayout.setRefreshing(false);
                    return;
                }

                Study.getParticipant().getEarnings().refreshOverview(new Earnings.Listener() {
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
                        }
                    }
                });
            }
        });

        String body = new String();
        if(isPractice){
            body += ViewUtil.getString(R.string.earnings_body0);
        }else {
            body += ViewUtil.getString(R.string.earnings_body1);
        }

        headerText = view.findViewById(R.id.textViewHeader);
        headerText.setTypeface(Fonts.robotoMedium);
        headerText.setText(ViewUtil.getString(R.string.resources_nav_earnings));

        earningsBody1 = view.findViewById(R.id.earningsBody1);
        earningsBody1.setText(Html.fromHtml(body));

        viewDetailsButton = view.findViewById(R.id.viewDetailsButton);
        viewDetailsButton.setText(ViewUtil.getString(R.string.button_viewdetails));
        viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EarningsDetailsScreen earningsDetailsScreen = new EarningsDetailsScreen();
                NavigationManager.getInstance().open(earningsDetailsScreen);
            }
        });

        bonusHeader = view.findViewById(R.id.bonusGoalsHeader);
        bonusHeader.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_bonus_header)));
        bonusHeader.setTypeface(Fonts.robotoMedium);

        bonusBody = view.findViewById(R.id.bonusBody);
        bonusBody.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_bonus_body)));
        ViewUtil.setLineHeight(bonusBody,26);

        viewFaqButton = view.findViewById(R.id.viewFaqButton);
        viewFaqButton.setText(ViewUtil.getString(R.string.button_viewfaq));
        viewFaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQScreen faqScreen = new FAQScreen();
                NavigationManager.getInstance().open(faqScreen);
            }
        });

        goalLayout = view.findViewById(R.id.goalLayout);
        weeklyTotal = view.findViewById(R.id.weeklyTotal);
        weeklyTotal.setTypeface(Fonts.robotoMedium);
        studyTotal = view.findViewById(R.id.studyTotal);
        studyTotal.setTypeface(Fonts.robotoMedium);
        lastSync = view.findViewById(R.id.textViewLastSync);

        weeklyTotalLabel = view.findViewById(R.id.weeklyTotalLabel);
        studyTotalLabel = view.findViewById(R.id.studyTotalLabel);

        weeklyTotalLabel.setText(ViewUtil.getString(R.string.earnings_weektotal));
        studyTotalLabel.setText(ViewUtil.getString(R.string.earnings_studytotal));

        populateViews();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
        refreshLayout.setProgressViewOffset(true, 0,top+ViewUtil.dpToPx(16));
    }

    private void populateViews() {
        EarningOverview overview = Study.getParticipant().getEarnings().getOverview();
        if(overview==null){
            return;
        }

        weeklyTotal.setText(overview.cycle_earnings);
        studyTotal.setText(overview.total_earnings);

        String syncString = getString(R.string.earnings_sync) + " ";
        DateTime lastSyncTime = Study.getParticipant().getEarnings().getOverviewRefreshTime();
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

        goalLayout.removeAllViews();
        for(EarningOverview.Goal goal : overview.goals){
            goalLayout.addView(new EarningsGoalView(getContext(),goal, overview.cycle,false));
        }
    }

}
