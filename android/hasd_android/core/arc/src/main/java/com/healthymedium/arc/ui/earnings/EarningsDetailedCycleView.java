//
// EarningsDetailedCycleView.java
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
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.time.TimeUtil;
import com.healthymedium.arc.ui.base.RoundedFrameLayout;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class EarningsDetailedCycleView extends LinearLayout {

    TextView textViewTitle;
    TextView textViewDates;

    LinearLayout goalLayout;
    RoundedFrameLayout ongoingLayout;

    TextView cycleTotal;


    public EarningsDetailedCycleView(Context context, EarningDetails.Cycle cycle) {
        super(context);
        View view = inflate(context, R.layout.custom_earnings_cycle_details,this);

        DateTime start = new DateTime(cycle.start_date*1000L);
        DateTime end = new DateTime(cycle.end_date*1000L);

        boolean isCurrent = (end.isAfterNow()&&start.isBeforeNow());

        textViewTitle = view.findViewById(R.id.textViewTitle);
        if(isCurrent){
            textViewTitle.setText(ViewUtil.getString(R.string.earnings_details_subheader1));
            ongoingLayout = view.findViewById(R.id.ongoingLayout);
            ongoingLayout.setVisibility(VISIBLE);
        }

        String startString = TimeUtil.format(start,R.string.format_date_lo);
        String endString = TimeUtil.format(end,R.string.format_date_lo);
        String dates = ViewUtil.getString(R.string.earnings_details_dates);
        dates = ViewUtil.replaceToken(dates,R.string.token_date1,startString);
        dates = ViewUtil.replaceToken(dates,R.string.token_date2,endString);

        textViewDates = view.findViewById(R.id.textViewDates);
        textViewDates.setText(dates);

        goalLayout = view.findViewById(R.id.goalLayout);
        boolean highlight = false;

        for(EarningDetails.Goal goal : cycle.details) {
            EarningsDetailedGoalView goalView = new EarningsDetailedGoalView(context,goal);
            highlight = !highlight;
            if(highlight){
                goalView.highlight();
            }
            goalLayout.addView(goalView);
        }

        cycleTotal = view.findViewById(R.id.cycleTotal);
        cycleTotal.setText(cycle.total);

    }

}
