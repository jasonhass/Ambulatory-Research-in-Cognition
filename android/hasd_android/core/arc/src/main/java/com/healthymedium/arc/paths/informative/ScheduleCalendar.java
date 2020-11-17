//
// ScheduleCalendar.java
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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.ui.Button;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ScheduleCalendar extends BaseFragment {

    View view;

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewSubHeader;
    Button buttonNext;

    public ScheduleCalendar() {
        allowBackPress(true);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule_calendar, container, false);


        Participant participant = Study.getParticipant();
        TestCycle cycle = participant.getCurrentTestCycle();

        DateTime visitStart = cycle.getActualStartDate();
        DateTime visitEnd = cycle.getActualEndDate();

        java.util.Locale locale = Application.getInstance().getLocale();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM d").withLocale(locale);

        String start = fmt.print(visitStart);
        String end = fmt.print(visitEnd.minusDays(1));

        textViewHeader = view.findViewById(R.id.textViewHeader);

        String header = ViewUtil.getString(R.string.dateshift_confirmation);
        header = header.replace("{DATE1}", start);
        header = header.replace("{DATE2}", end);

        textViewHeader.setText(Html.fromHtml(header));

        updateCalendar(visitStart, visitEnd);

        textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        //textViewSubHeader.setLineSpacing(ViewUtil.dpToPx(3),1.0f);

        textViewSubHeader.setVisibility(View.GONE);

        textViewBack = view.findViewById(R.id.textViewBack);
        //textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });

        textViewBack.setVisibility(View.VISIBLE);


        buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getInstance().openNextFragment();
            }
        });

        return view;
    }

    public void updateCalendar(DateTime startTime, DateTime endTime) {

        // Get calendar components
        TextView sunday1 = view.findViewById(R.id.sunday1);
        FrameLayout sunday1_left = view.findViewById(R.id.sunday1_left);
        TextView sunday2 = view.findViewById(R.id.sunday2);
        FrameLayout sunday2_right = view.findViewById(R.id.sunday2_right);

        TextView monday1 = view.findViewById(R.id.monday1);
        FrameLayout monday1_left = view.findViewById(R.id.monday1_left);
        TextView monday2 = view.findViewById(R.id.monday2);
        FrameLayout monday2_right = view.findViewById(R.id.monday2_right);

        TextView tuesday1 = view.findViewById(R.id.tuesday1);
        FrameLayout tuesday1_left = view.findViewById(R.id.tuesday1_left);
        TextView tuesday2 = view.findViewById(R.id.tuesday2);
        FrameLayout tuesday2_right = view.findViewById(R.id.tuesday2_right);

        TextView wednesday1 = view.findViewById(R.id.wednesday1);
        FrameLayout wednesday1_left = view.findViewById(R.id.wednesday1_left);
        TextView wednesday2 = view.findViewById(R.id.wednesday2);
        FrameLayout wednesday2_right = view.findViewById(R.id.wednesday2_right);

        TextView thursday1 = view.findViewById(R.id.thursday1);
        FrameLayout thursday1_left = view.findViewById(R.id.thursday1_left);
        TextView thursday2 = view.findViewById(R.id.thursday2);
        FrameLayout thursday2_right = view.findViewById(R.id.thursday2_right);

        TextView friday1 = view.findViewById(R.id.friday1);
        FrameLayout friday1_left = view.findViewById(R.id.friday1_left);
        TextView friday2 = view.findViewById(R.id.friday2);
        FrameLayout friday2_right = view.findViewById(R.id.friday2_right);

        TextView saturday1 = view.findViewById(R.id.saturday1);
        FrameLayout saturday1_left = view.findViewById(R.id.saturday1_left);
        FrameLayout saturday1_right = view.findViewById(R.id.saturday1_right);
        TextView saturday2 = view.findViewById(R.id.saturday2);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("E");
        String startDay = fmt.print(startTime);

        fmt = DateTimeFormat.forPattern("d");

        int dayOfWeek = startTime.getDayOfWeek();

        if (dayOfWeek == DateTimeConstants.MONDAY) {
            // start
            monday1.setText(fmt.print(startTime));
            monday1_left.setVisibility(View.VISIBLE);

            // end
            sunday2.setText(fmt.print(startTime.plusDays(6)));
            sunday2_right.setVisibility(View.VISIBLE);

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(1)));
            tuesday1.setText(fmt.print(startTime.plusDays(1)));
            wednesday1.setText(fmt.print(startTime.plusDays(2)));
            thursday1.setText(fmt.print(startTime.plusDays(3)));
            friday1.setText(fmt.print(startTime.plusDays(4)));
            saturday1.setText(fmt.print(startTime.plusDays(5)));
            monday2.setText(fmt.print(startTime.plusDays(7)));
            tuesday2.setText(fmt.print(startTime.plusDays(8)));
            wednesday2.setText(fmt.print(startTime.plusDays(9)));
            thursday2.setText(fmt.print(startTime.plusDays(10)));
            friday2.setText(fmt.print(startTime.plusDays(11)));
            saturday2.setText(fmt.print(startTime.plusDays(12)));

            // update backgrounds
            monday1.setBackgroundResource(R.color.light);
            monday1.setTextColor(getResources().getColor(R.color.black));

            tuesday1.setBackgroundResource(R.color.light);
            tuesday1.setTextColor(getResources().getColor(R.color.black));

            wednesday1.setBackgroundResource(R.color.light);
            wednesday1.setTextColor(getResources().getColor(R.color.black));

            thursday1.setBackgroundResource(R.color.light);
            thursday1.setTextColor(getResources().getColor(R.color.black));

            friday1.setBackgroundResource(R.color.light);
            friday1.setTextColor(getResources().getColor(R.color.black));

            saturday1.setBackgroundResource(R.color.light);
            saturday1.setTextColor(getResources().getColor(R.color.black));

            sunday2.setBackgroundResource(R.color.light);
            sunday2.setTextColor(getResources().getColor(R.color.black));
        } else if (dayOfWeek == DateTimeConstants.TUESDAY) {
            // start
            tuesday1.setText(fmt.print(startTime));
            tuesday1_left.setVisibility(View.VISIBLE);

            // end
            monday2.setText(fmt.print(startTime.plusDays(6)));
            monday2_right.setVisibility(View.VISIBLE);

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(2)));
            monday1.setText(fmt.print(startTime.minusDays(1)));
            wednesday1.setText(fmt.print(startTime.plusDays(1)));
            thursday1.setText(fmt.print(startTime.plusDays(2)));
            friday1.setText(fmt.print(startTime.plusDays(3)));
            saturday1.setText(fmt.print(startTime.plusDays(4)));
            sunday2.setText(fmt.print(startTime.plusDays(5)));
            tuesday2.setText(fmt.print(startTime.plusDays(7)));
            wednesday2.setText(fmt.print(startTime.plusDays(8)));
            thursday2.setText(fmt.print(startTime.plusDays(9)));
            friday2.setText(fmt.print(startTime.plusDays(10)));
            saturday2.setText(fmt.print(startTime.plusDays(11)));

            // update backgrounds
            tuesday1.setBackgroundResource(R.color.light);
            tuesday1.setTextColor(getResources().getColor(R.color.black));

            wednesday1.setBackgroundResource(R.color.light);
            wednesday1.setTextColor(getResources().getColor(R.color.black));

            thursday1.setBackgroundResource(R.color.light);
            thursday1.setTextColor(getResources().getColor(R.color.black));

            friday1.setBackgroundResource(R.color.light);
            friday1.setTextColor(getResources().getColor(R.color.black));

            saturday1.setBackgroundResource(R.color.light);
            saturday1.setTextColor(getResources().getColor(R.color.black));

            sunday2.setBackgroundResource(R.color.light);
            sunday2.setTextColor(getResources().getColor(R.color.black));

            monday2.setBackgroundResource(R.color.light);
            monday2.setTextColor(getResources().getColor(R.color.black));
        } else if (dayOfWeek == DateTimeConstants.WEDNESDAY) {
            // start
            wednesday1.setText(fmt.print(startTime));
            wednesday1_left.setVisibility(View.VISIBLE);

            // end
            tuesday2.setText(fmt.print(startTime.plusDays(6)));
            tuesday2_right.setVisibility(View.VISIBLE);

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(3)));
            monday1.setText(fmt.print(startTime.minusDays(2)));
            tuesday1.setText(fmt.print(startTime.minusDays(1)));
            thursday1.setText(fmt.print(startTime.plusDays(1)));
            friday1.setText(fmt.print(startTime.plusDays(2)));
            saturday1.setText(fmt.print(startTime.plusDays(3)));
            sunday2.setText(fmt.print(startTime.plusDays(4)));
            monday2.setText(fmt.print(startTime.plusDays(5)));
            wednesday2.setText(fmt.print(startTime.plusDays(7)));
            thursday2.setText(fmt.print(startTime.plusDays(8)));
            friday2.setText(fmt.print(startTime.plusDays(9)));
            saturday2.setText(fmt.print(startTime.plusDays(10)));

            // update backgrounds
            wednesday1.setBackgroundResource(R.color.light);
            wednesday1.setTextColor(getResources().getColor(R.color.black));

            thursday1.setBackgroundResource(R.color.light);
            thursday1.setTextColor(getResources().getColor(R.color.black));

            friday1.setBackgroundResource(R.color.light);
            friday1.setTextColor(getResources().getColor(R.color.black));

            saturday1.setBackgroundResource(R.color.light);
            saturday1.setTextColor(getResources().getColor(R.color.black));

            sunday2.setBackgroundResource(R.color.light);
            sunday2.setTextColor(getResources().getColor(R.color.black));

            monday2.setBackgroundResource(R.color.light);
            monday2.setTextColor(getResources().getColor(R.color.black));

            tuesday2.setBackgroundResource(R.color.light);
            tuesday2.setTextColor(getResources().getColor(R.color.black));
        } else if (dayOfWeek == DateTimeConstants.THURSDAY) {
            // start
            thursday1.setText(fmt.print(startTime));
            thursday1_left.setVisibility(View.VISIBLE);

            // end
            wednesday2.setText(fmt.print(startTime.plusDays(6)));
            wednesday2_right.setVisibility(View.VISIBLE);

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(4)));
            monday1.setText(fmt.print(startTime.minusDays(3)));
            tuesday1.setText(fmt.print(startTime.minusDays(2)));
            wednesday1.setText(fmt.print(startTime.minusDays(1)));
            friday1.setText(fmt.print(startTime.plusDays(1)));
            saturday1.setText(fmt.print(startTime.plusDays(2)));
            sunday2.setText(fmt.print(startTime.plusDays(3)));
            monday2.setText(fmt.print(startTime.plusDays(4)));
            tuesday2.setText(fmt.print(startTime.plusDays(5)));
            thursday2.setText(fmt.print(startTime.plusDays(7)));
            friday2.setText(fmt.print(startTime.plusDays(8)));
            saturday2.setText(fmt.print(startTime.plusDays(9)));

            // update backgrounds
            thursday1.setBackgroundResource(R.color.light);
            thursday1.setTextColor(getResources().getColor(R.color.black));

            friday1.setBackgroundResource(R.color.light);
            friday1.setTextColor(getResources().getColor(R.color.black));

            saturday1.setBackgroundResource(R.color.light);
            saturday1.setTextColor(getResources().getColor(R.color.black));

            sunday2.setBackgroundResource(R.color.light);
            sunday2.setTextColor(getResources().getColor(R.color.black));

            monday2.setBackgroundResource(R.color.light);
            monday2.setTextColor(getResources().getColor(R.color.black));

            tuesday2.setBackgroundResource(R.color.light);
            tuesday2.setTextColor(getResources().getColor(R.color.black));

            wednesday2.setBackgroundResource(R.color.light);
            wednesday2.setTextColor(getResources().getColor(R.color.black));
        } else if (dayOfWeek == DateTimeConstants.FRIDAY) {
            // start
            friday1.setText(fmt.print(startTime));
            friday1_left.setVisibility(View.VISIBLE);

            // end
            thursday2.setText(fmt.print(startTime.plusDays(6)));
            thursday2_right.setVisibility(View.VISIBLE);

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(5)));
            monday1.setText(fmt.print(startTime.minusDays(4)));
            tuesday1.setText(fmt.print(startTime.minusDays(3)));
            wednesday1.setText(fmt.print(startTime.minusDays(2)));
            thursday1.setText(fmt.print(startTime.minusDays(1)));
            saturday1.setText(fmt.print(startTime.plusDays(1)));
            sunday2.setText(fmt.print(startTime.plusDays(2)));
            monday2.setText(fmt.print(startTime.plusDays(3)));
            tuesday2.setText(fmt.print(startTime.plusDays(4)));
            wednesday2.setText(fmt.print(startTime.plusDays(5)));
            friday2.setText(fmt.print(startTime.plusDays(7)));
            saturday2.setText(fmt.print(startTime.plusDays(8)));

            // update backgrounds
            friday1.setBackgroundResource(R.color.light);
            friday1.setTextColor(getResources().getColor(R.color.black));

            saturday1.setBackgroundResource(R.color.light);
            saturday1.setTextColor(getResources().getColor(R.color.black));

            sunday2.setBackgroundResource(R.color.light);
            sunday2.setTextColor(getResources().getColor(R.color.black));

            monday2.setBackgroundResource(R.color.light);
            monday2.setTextColor(getResources().getColor(R.color.black));

            tuesday2.setBackgroundResource(R.color.light);
            tuesday2.setTextColor(getResources().getColor(R.color.black));

            wednesday2.setBackgroundResource(R.color.light);
            wednesday2.setTextColor(getResources().getColor(R.color.black));

            thursday2.setBackgroundResource(R.color.light);
            thursday2.setTextColor(getResources().getColor(R.color.black));
        } else if (dayOfWeek == DateTimeConstants.SATURDAY) {
            // start
            saturday1.setText(fmt.print(startTime));
            saturday1_left.setVisibility(View.VISIBLE);

            // end
            friday2.setText(fmt.print(startTime.plusDays(6)));
            friday2_right.setVisibility(View.VISIBLE);

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(6)));
            monday1.setText(fmt.print(startTime.minusDays(5)));
            tuesday1.setText(fmt.print(startTime.minusDays(4)));
            wednesday1.setText(fmt.print(startTime.minusDays(3)));
            thursday1.setText(fmt.print(startTime.minusDays(2)));
            friday1.setText(fmt.print(startTime.minusDays(1)));
            sunday2.setText(fmt.print(startTime.plusDays(1)));
            monday2.setText(fmt.print(startTime.plusDays(2)));
            tuesday2.setText(fmt.print(startTime.plusDays(3)));
            wednesday2.setText(fmt.print(startTime.plusDays(4)));
            thursday2.setText(fmt.print(startTime.plusDays(5)));
            saturday2.setText(fmt.print(startTime.plusDays(7)));

            // update backgrounds
            saturday1.setBackgroundResource(R.color.light);
            saturday1.setTextColor(getResources().getColor(R.color.black));

            sunday2.setBackgroundResource(R.color.light);
            sunday2.setTextColor(getResources().getColor(R.color.black));

            monday2.setBackgroundResource(R.color.light);
            monday2.setTextColor(getResources().getColor(R.color.black));

            tuesday2.setBackgroundResource(R.color.light);
            tuesday2.setTextColor(getResources().getColor(R.color.black));

            wednesday2.setBackgroundResource(R.color.light);
            wednesday2.setTextColor(getResources().getColor(R.color.black));

            thursday2.setBackgroundResource(R.color.light);
            thursday2.setTextColor(getResources().getColor(R.color.black));

            friday2.setBackgroundResource(R.color.light);
            friday2.setTextColor(getResources().getColor(R.color.black));
        } else if (dayOfWeek == DateTimeConstants.SUNDAY) {
            // start
            sunday1.setText(fmt.print(startTime));
            sunday1_left.setVisibility(View.VISIBLE);

            // end
            saturday1.setText(fmt.print(startTime.plusDays(6)));
            saturday1_right.setVisibility(View.VISIBLE);

            // everything else
            monday1.setText(fmt.print(startTime.plusDays(1)));
            tuesday1.setText(fmt.print(startTime.plusDays(2)));
            wednesday1.setText(fmt.print(startTime.plusDays(3)));
            thursday1.setText(fmt.print(startTime.plusDays(4)));
            friday1.setText(fmt.print(startTime.plusDays(5)));
            sunday2.setText(fmt.print(startTime.plusDays(7)));
            monday2.setText(fmt.print(startTime.plusDays(8)));
            tuesday2.setText(fmt.print(startTime.plusDays(9)));
            wednesday2.setText(fmt.print(startTime.plusDays(10)));
            thursday2.setText(fmt.print(startTime.plusDays(11)));
            friday2.setText(fmt.print(startTime.plusDays(12)));
            saturday2.setText(fmt.print(startTime.plusDays(13)));

            // update backgrounds
            sunday1.setBackgroundResource(R.color.light);
            sunday1.setTextColor(getResources().getColor(R.color.black));

            monday1.setBackgroundResource(R.color.light);
            monday1.setTextColor(getResources().getColor(R.color.black));

            tuesday1.setBackgroundResource(R.color.light);
            tuesday1.setTextColor(getResources().getColor(R.color.black));

            wednesday1.setBackgroundResource(R.color.light);
            wednesday1.setTextColor(getResources().getColor(R.color.black));

            thursday1.setBackgroundResource(R.color.light);
            thursday1.setTextColor(getResources().getColor(R.color.black));

            friday1.setBackgroundResource(R.color.light);
            friday1.setTextColor(getResources().getColor(R.color.black));

            saturday1.setBackgroundResource(R.color.light);
            saturday1.setTextColor(getResources().getColor(R.color.black));
        }
    }
}
