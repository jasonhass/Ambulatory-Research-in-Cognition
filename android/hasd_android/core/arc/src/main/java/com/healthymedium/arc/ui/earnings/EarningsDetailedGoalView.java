//
// EarningsDetailedGoalView.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui.earnings;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningDetails;
import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.RoundedRelativeLayout;
import com.healthymedium.arc.utilities.ViewUtil;

import static com.healthymedium.arc.study.Earnings.FOUR_OUT_OF_FOUR;
import static com.healthymedium.arc.study.Earnings.TEST_SESSION;
import static com.healthymedium.arc.study.Earnings.TWENTY_ONE_SESSIONS;
import static com.healthymedium.arc.study.Earnings.TWO_A_DAY;

public class EarningsDetailedGoalView extends LinearLayout {

    TextView textViewName;
    TextView textViewDesc;
    TextView textViewValue;
    LinearLayout linearLayout;

    public EarningsDetailedGoalView(Context context, EarningDetails.Goal goal) {
        super(context);
        View view = inflate(context, R.layout.custom_earnings_cycle_details_goal,this);
        linearLayout = view.findViewById(R.id.linearLayout);

        textViewName = view.findViewById(R.id.textViewName);
        textViewDesc = view.findViewById(R.id.textViewDesc);
        textViewValue = view.findViewById(R.id.textViewValue);

        String name = new String();
        switch (goal.name) {
            case TEST_SESSION:
                name = ViewUtil.getString(R.string.earnings_details_complete_test_session);
                break;
            case TWENTY_ONE_SESSIONS:
                name = ViewUtil.getString(R.string.earnings_details_21sessions);
                break;
            case TWO_A_DAY:
                name = ViewUtil.getString(R.string.earnings_details_2aday);
                break;
            case FOUR_OUT_OF_FOUR:
                name = ViewUtil.getString(R.string.earnings_details_4of4);
                break;
        }
        textViewName.setText(name);
        textViewValue.setText(goal.amount_earned);
        textViewDesc.setText(goal.value);
        if(goal.count_completed>1){
            String times = ViewUtil.getString(R.string.earnings_details_number_sessions);
            times = ViewUtil.replaceToken(times,R.string.token_number,String.valueOf(goal.count_completed));
            textViewDesc.append(" " + times);
        }

    }

    public void highlight() {
        linearLayout.setBackgroundColor(ViewUtil.getColor(R.color.earningsDetailsBlue));
    }

}
