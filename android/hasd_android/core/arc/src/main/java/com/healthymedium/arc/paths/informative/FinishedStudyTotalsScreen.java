//
// FinishedStudyTotalsScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.api.models.StudySummary;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.utilities.ViewUtil;

public class FinishedStudyTotalsScreen extends BaseFragment {

    public static boolean DEBUG_SHOW_TOTAL = true;

    ProgressBar progressBar;

    LinearLayout linearLayoutResults;
    TextView textViewTotalEarned;
    TextView textViewTotalTests;
    TextView textViewTotalDays;
    TextView textViewTotalGoals;

    TextView textViewError;
    Button button;

    public FinishedStudyTotalsScreen() {
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finished_study_totals, container, false);

        // get inflated views ----------------------------------------------------------------------
        TextView header = view.findViewById(R.id.textViewHeader);
        header.setTypeface(Fonts.robotoMedium);

        TextView textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        ViewUtil.setLineHeight(textViewSubHeader,26);

        progressBar = view.findViewById(R.id.progressBar);

        linearLayoutResults = view.findViewById(R.id.linearLayoutResults);
        textViewTotalEarned = view.findViewById(R.id.textViewTotalEarned);
        textViewTotalTests = view.findViewById(R.id.textViewTotalTests);
        textViewTotalDays = view.findViewById(R.id.textViewTotalDays);
        textViewTotalGoals = view.findViewById(R.id.textViewTotalGoals);

        textViewError = view.findViewById(R.id.textViewError);
        ViewUtil.setLineHeight(textViewError,26);

        button = view.findViewById(R.id.button);

        // -----------------------------------------------------------------------------------------

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Study.openNextFragment();
            }
        });

        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);
        progressBar.animate().alpha(1.0f).setDuration(300);

        if(Config.REST_BLACKHOLE) {
            if(DEBUG_SHOW_TOTAL){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StudySummary summary = new StudySummary();
                        summary.days_tested = 168;
                        summary.goals_met = 13;
                        summary.tests_taken = 302;
                        summary.total_earnings = "$115.75";
                        showTotals(summary);
                    }
                },2000);
            }
            else{
                showError();
            }

        }

        Study.getRestClient().getStudySummary(new RestClient.Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                if(!response.successful) {
                    showError();
                }
                StudySummary summary = response.getOptionalAs("summary", StudySummary.class);
                if(summary!=null) {
                    showTotals(summary);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(RestResponse response) {
                showError();
            }
        });

    }

    private void showTotals(StudySummary summary) {
        textViewTotalEarned.setText(summary.total_earnings);
        textViewTotalTests.setText(String.valueOf(summary.tests_taken));
        textViewTotalDays.setText(String.valueOf(summary.days_tested));
        textViewTotalGoals.setText(String.valueOf(summary.goals_met));

        progressBar.setVisibility(View.GONE);
        linearLayoutResults.setVisibility(View.VISIBLE);
        linearLayoutResults.animate().alpha(1.0f).setDuration(300);
        button.setEnabled(true);
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        textViewError.setVisibility(View.VISIBLE);
        textViewError.animate().alpha(1.0f).setDuration(300);
        button.setEnabled(true);
    }

}
