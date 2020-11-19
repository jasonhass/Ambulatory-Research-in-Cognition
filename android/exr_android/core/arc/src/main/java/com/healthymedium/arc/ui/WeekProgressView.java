//
// WeekProgressView.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.HashMap;

/*
    Displays days of week with indicator circle on current day.

    Usage:
        Define in XML:
            <com.healthymedium.arc.ui.WeekProgressView
                android:id="@+id/weekProgressView"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>

        Set cycle start date and current day index in Java:
            WeekProgressView weekProgressView = view.findViewById(R.id.weekProgressView);
            weekProgressView.setupView(DateTime startDate, int currentDay);

        Current day of the week will highlight itself
*/

public class WeekProgressView extends LinearProgressView {

    protected DateTime startDate;
    protected int currentDay = 0;
    private int days[] = new int[]{
            R.string.day_abbrev_sun,
            R.string.day_abbrev_mon,
            R.string.day_abbrev_tues,
            R.string.day_abbrev_weds,
            R.string.day_abbrev_thurs,
            R.string.day_abbrev_fri,
            R.string.day_abbrev_sat
    };

    public WeekProgressView(Context context) {
        super(context);
    }

    public WeekProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeekProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        backgroundBorderColor = R.color.weekProgressBorder;
        progressColor = R.color.weekProgressFill;
        indicatorColor = R.color.weekProgressFill;
        indicatorTextColor = R.color.white;
        indicatorWidth = ViewUtil.dpToPx(50);
        super.init(context);
    }

    @Override
    protected void initOnMeasure() {
        if(padding == null) {
            indicatorWidth = Math.max(indicatorWidth, unitWidth);
            padding = 0;
            if (indicatorWidth > unitWidth) {
                padding = (indicatorWidth - unitWidth) / 2;
            }
            backgroundLayout.setPadding(padding, 0, padding, 0);
        }

        DateTime day = startDate;
        for (int i = 0; i <= currentDay; i++) {
            int dayIndex = getDayIndex(day);
            day = day.plusDays(1);

            int width = backgroundLayout.getChildAt(i).getMeasuredWidth() - padding*2/7;
            LinearLayout.LayoutParams dayTextParams = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView dayTextView = new TextView(getContext());
            dayTextView.setLayoutParams(dayTextParams);
            dayTextView.setTypeface(Fonts.robotoBold);
            dayTextView.setTextSize(16);
            dayTextView.setTextColor(ViewUtil.getColor(getContext(), R.color.white));
            dayTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            dayTextView.setText(ViewUtil.getString(days[dayIndex]));
            dayTextView.setPadding(0, ViewUtil.dpToPx(5), 0, ViewUtil.dpToPx(5));
            progressLayout.addView(dayTextView);
        }
        progressLayout.setPadding(padding, 0, padding, 0);

        progressText = ViewUtil.getString(days[getDayIndex(startDate.plusDays(currentDay))]);

        super.initOnMeasure();
    }


    private void buildView() {
        backgroundLayout.removeAllViews();
        LinearLayout.LayoutParams dayTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        dayTextParams.weight = 1;
        DateTime day = startDate;
        for(int i=0;i<7;i++) {
            int dayIndex = getDayIndex(day);
            day = day.plusDays(1);

            TextView dayTextView = new TextView(getContext());
            dayTextView.setLayoutParams(dayTextParams);
            dayTextView.setTypeface(Fonts.robotoBold);
            dayTextView.setTextSize(16);
            dayTextView.setTextColor(ViewUtil.getColor(getContext(), R.color.text));
            dayTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            dayTextView.setText(ViewUtil.getString(days[dayIndex]));
            dayTextView.setPadding(0, ViewUtil.dpToPx(5), 0, ViewUtil.dpToPx(5));
            backgroundLayout.addView(dayTextView);
        }
    }

    public void setupView(DateTime startDate, int currentDay) {
        this.startDate = startDate;
        this.currentDay = currentDay;
        this.progress = currentDay;
        this.maxValue = days.length-1;
        buildView();
    }

    private int getDayIndex(DateTime dateTime) {
        int dayIndex = dateTime.getDayOfWeek();
        if(dayIndex==7){
            dayIndex = 0;
        }
        return dayIndex;
    }

}
