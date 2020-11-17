//
// EarningsPostTestScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Earnings;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.TotalEarningsView;
import com.healthymedium.arc.ui.earnings.EarningsAchievementView;
import com.healthymedium.arc.ui.earnings.EarningsGoalView;
import com.healthymedium.arc.utilities.ViewUtil;

public class EarningsPostTestScreen extends BaseFragment {

    EarningOverview overview;

    TotalEarningsView weeklyTotal;
    TotalEarningsView studyTotal;
    LinearLayout goalLayout;
    LinearLayout achievementLayout;
    ImageView imageViewConfetti;

    public EarningsPostTestScreen() {
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings_post_test, container, false);

        imageViewConfetti = view.findViewById(R.id.imageViewConfetti);
        imageViewConfetti.setAlpha(0f);
        imageViewConfetti.animate().translationYBy(-200);

        goalLayout = view.findViewById(R.id.goalLayout);
        goalLayout.setAlpha(0f);

        TextView textViewHeader3 = view.findViewById(R.id.textViewHeader3);
        textViewHeader3.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_bonus_body)));

        Earnings earnings = Study.getParticipant().getEarnings();
        overview = earnings.getOverview();
        if(overview==null){
            overview = EarningOverview.getTestObject();
        }

        weeklyTotal = view.findViewById(R.id.weeklyTotal);
        weeklyTotal.setText(earnings.getPrevWeeklyTotal(),false);

        studyTotal = view.findViewById(R.id.studyTotal);
        studyTotal.setText(earnings.getPrevStudyTotal(),false);

        achievementLayout = view.findViewById(R.id.achievementLayout);
        for(EarningOverview.Achievement achievement : overview.new_achievements) {
            achievementLayout.addView(new EarningsAchievementView(getContext(),achievement));
        }

        for(EarningOverview.Goal goal : overview.goals) {
            boolean showCompletionCollapsed = (goal.completed && !overview.hasAchievementFor(goal));
            goalLayout.addView(new EarningsGoalView(getContext(), goal, overview.cycle, showCompletionCollapsed));
        }

        Button button = view.findViewById(R.id.buttonNext);
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

        weeklyTotal.setText(overview.cycle_earnings,true);
        studyTotal.setText(overview.total_earnings, true, new TotalEarningsView.Listener() {
            @Override
            public void onFinished() {
                goalLayout.animate().alpha(1.0f);
            }
        });
        imageViewConfetti.animate().translationYBy(200).setDuration(1000);
        imageViewConfetti.animate().alpha(1.0f).setDuration(1000);


    }
}
