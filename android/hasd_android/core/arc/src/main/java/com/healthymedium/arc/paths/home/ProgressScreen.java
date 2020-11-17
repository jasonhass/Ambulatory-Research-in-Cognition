//
// ProgressScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.paths.home;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.FAQScreen;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.TestDay;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.CircleProgressView;
import com.healthymedium.arc.ui.StudyProgressView;
import com.healthymedium.arc.ui.WeekProgressView;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

public class ProgressScreen extends BaseFragment {

    TestCycle testCycle;
    TestDay testDay;
    TestSession testSession;

    boolean isPractice = false;
    boolean isBaseline = false;

    TextView sessionHeader;
    TextView weekHeader;
    TextView studyHeader;

    TextView studyStatus;
    TextView joinedDate_label;
    TextView joinedDate_date;
    TextView finishDate_label;
    TextView finishDate_date;
    TextView timeBetween;
    TextView timeBetween_units;

    TextView endDependsDisclaimer;

    Button viewFaqButton;

    public ProgressScreen() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        Participant participant = Study.getParticipant();
        testCycle = participant.getCurrentTestCycle();
        testDay = participant.getCurrentTestDay();
        testSession = participant.getCurrentTestSession();

        ParticipantState state = participant.getState();
        int sessionIndex = state.currentTestSession-1;
        int dayIndex = state.currentTestDay;
        int cycleIndex = state.currentTestCycle;

        boolean isInCycle;

        if(testCycle==null) {
            cycleIndex--;
            testCycle = state.testCycles.get(cycleIndex);
            isInCycle = false;
            sessionIndex = 0;
        } else {
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
            isInCycle = testCycle.getActualStartDate().isBeforeNow() && testCycle.getActualEndDate().isAfterNow();
        }

        isPractice = (dayIndex==0 && sessionIndex==0 && cycleIndex==0);
        isBaseline = (cycleIndex==0);

        if(isInCycle) {
            setupTodaysSessions(view, testDay);
            setupWeekView(view, testCycle, testDay);
        }

        // study view partition
        studyStatus = view.findViewById(R.id.studyStatus);

        if(isPractice){
            StudyProgressView studyProgressView = view.findViewById(R.id.studyProgressView);
            studyProgressView.setVisibility(View.GONE);
            view.findViewById(R.id.studyStatusLayout).setVisibility(View.GONE);
        } else {
            int currCycle = testCycle.getId()+1; // Cycles are 0-indexed
            Phrase phrase = new Phrase(R.string.progress_studystatus);
            phrase.replaceNumber(currCycle);
            studyStatus.setText(phrase.toHtmlString());
        }


        sessionHeader = view.findViewById(R.id.textViewSessionsHeader);
        sessionHeader.setTypeface(Fonts.robotoMedium);
        sessionHeader.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_daily_header)));

        weekHeader = view.findViewById(R.id.textViewWeekHeader);
        weekHeader.setTypeface(Fonts.robotoMedium);
        weekHeader.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_weekly_header)));

        studyHeader = view.findViewById(R.id.textViewStudyHeader);
        studyHeader.setTypeface(Fonts.robotoMedium);
        studyHeader.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_study_header)));

        joinedDate_label = view.findViewById(R.id.joinedDate);
        joinedDate_label.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_joindate)));

        finishDate_label = view.findViewById(R.id.finishDate);
        finishDate_label.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_finishdate)));

        endDependsDisclaimer = view.findViewById(R.id.endDepends);
        endDependsDisclaimer.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_studydisclaimer)));
        ViewUtil.setLineHeight(endDependsDisclaimer,20);

        // The join date should be the start date of test cycle 0
        DateTime joinedDate = Study.getParticipant().getStartDate();
        Phrase joinedPhrase = new Phrase(R.string.progress_joindate_date);
        joinedPhrase.replaceDate(R.string.format_date_longer, joinedDate);

        joinedDate_date = view.findViewById(R.id.joinedDate_date);
        joinedDate_date.setText(joinedPhrase.toString());

        DateTime finishDate = Study.getParticipant().getFinishDate();
        Phrase finishPhrase = new Phrase(R.string.progress_finishdate_date);
        finishPhrase.replaceDate(R.string.format_date_longer, finishDate);

        finishDate_date = view.findViewById(R.id.finishDate_date);
        finishDate_date.setText(finishPhrase.toString());

        timeBetween = view.findViewById(R.id.timeBetween);
        timeBetween.setText(new Phrase(R.string.progress_timebtwtesting).toHtmlString());

        DateTime startOfFirstCycle = state.testCycles.get(0).getActualStartDate();
        DateTime startOfSecondCycle = state.testCycles.get(1).getActualStartDate();

        int weeksBetween = Weeks.weeksBetween(startOfFirstCycle,startOfSecondCycle).getWeeks();

        timeBetween_units = view.findViewById(R.id.timeBetween_units);
        Phrase units = new Phrase(R.string.progress_timebtwtesting_unit);
        units.replaceNumber(weeksBetween/4);
        units.replaceUnit(getString(R.string.unit_months));
        timeBetween_units.setText(units.toString());

        if(!isInCycle){
            int weeksCompleted = 0;
            int weeksRemaining = 0;

            for(TestCycle cycle : state.testCycles){
                if(cycle.isOver()){
                    weeksCompleted++;
                } else {
                    weeksRemaining++;
                }
            }

            Phrase completedPhrase = new Phrase(R.string.progress_weekscompleted);
            completedPhrase.replaceNumber(weeksCompleted);

            TextView studyStatus2 = view.findViewById(R.id.studyStatus2);
            studyStatus2.setText(completedPhrase.toHtmlString());
            studyStatus2.setVisibility(View.VISIBLE);

            Phrase remainingPhrase = new Phrase(R.string.progress_weeksremaining);
            remainingPhrase.replaceNumber(weeksRemaining);
            studyStatus.setText(remainingPhrase.toHtmlString());

            if(weeksRemaining==0){
                TextView studyStatusBadgeTextView = view.findViewById(R.id.studyStatusBadgeTextView);
                studyStatusBadgeTextView.setText(R.string.status_done);
                view.findViewById(R.id.asterisk).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.studyStatusBadge).setVisibility(View.GONE);
            }

            view.findViewById(R.id.textViewNextCycle).setVisibility(View.VISIBLE);
            if (weeksRemaining>0) {
                TextView textViewNextCycleDates= view.findViewById(R.id.textViewNextCycleDates);
                textViewNextCycleDates.setVisibility(View.VISIBLE);

                TestCycle nextCycle = state.testCycles.get(cycleIndex);
                Phrase dates = new Phrase(R.string.earnings_details_dates);

                DateTime start = nextCycle.getActualStartDate();
                DateTime end = nextCycle.getActualEndDate().minusDays(1);
                dates.replaceDates(R.string.format_date_lo, start, end);
                textViewNextCycleDates.setText(dates.toString());
            } else {
                view.findViewById(R.id.nextCycleBadge).setVisibility(View.VISIBLE);
            }

        }

        viewFaqButton = view.findViewById(R.id.viewFaqButton);
        viewFaqButton.setText(ViewUtil.getString(R.string.button_viewfaq));
        viewFaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQScreen faqScreen = new FAQScreen();
                NavigationManager.getInstance().open(faqScreen);
            }
        });

        return view;
    }

    private void setupTodaysSessions(View view, TestDay testDay) {

        LinearLayout layout = view.findViewById(R.id.sessions);
        layout.setVisibility(View.VISIBLE);

        LinearLayout sessionProgressLayout = view.findViewById(R.id.sessionProgressLayout);
        int margin = ViewUtil.dpToPx(4);

        for(TestSession session : testDay.getTestSessions()){
            CircleProgressView progressView = new CircleProgressView(getContext());
            sessionProgressLayout.addView(progressView);

            progressView.setBaseColor(R.color.secondaryAccent);
            progressView.setCheckmarkColor(R.color.secondaryAccent);
            progressView.setSweepColor(R.color.secondary);
            progressView.setStrokeWidth(6);
            progressView.setMargin(margin,0,margin,0);
            progressView.setProgress(session.getProgress(), false);
        }

        int testsCompleted = testDay.getNumberOfTestsFinished();
        Phrase complete = new Phrase(R.string.progress_dailystatus_complete);
        complete.replaceNumber(testsCompleted);

        TextView textViewComplete = view.findViewById(R.id.complete);
        textViewComplete.setText(complete.toHtmlString());

        TextView textViewRemaining = view.findViewById(R.id.remaining);

        int testsRemaining = testDay.getNumberOfTestsLeft();
        if(testsRemaining==0 && !isPractice){
            textViewRemaining.setVisibility(View.GONE);
            view.findViewById(R.id.bar).setVisibility(View.GONE);
            view.findViewById(R.id.dayStatusBadge).setVisibility(View.VISIBLE);
        } else {
            Phrase remaining = new Phrase(R.string.progress_dailystatus_remaining);
            remaining.replaceNumber(testsRemaining);
            textViewRemaining.setText(remaining.toHtmlString());
        }

    }

    private void setupWeekView(View view, TestCycle testCycle, TestDay testDay) {

        LinearLayout layout = view.findViewById(R.id.week);
        layout.setVisibility(View.VISIBLE);

        TextView weeklyStatus = view.findViewById(R.id.weeklyStatus);
        WeekProgressView weekProgressView = view.findViewById(R.id.weekProgressView);
        Phrase dayOf;

        int dayIndex = testDay.getDayIndex();
        if(isBaseline){
            dayIndex--;
        }

        if(isPractice){
            int sessionsFinished = testDay.getNumberOfTestsFinished();
            if(sessionsFinished>0) {
                dayOf = new Phrase(R.string.progress_baseline_notice);
            } else {
                dayOf = new Phrase(R.string.progress_practice_body2);
            }
            weeklyStatus.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17);
            ViewUtil.setLineHeight(weeklyStatus,24);
            weekProgressView.setVisibility(View.GONE);
        } else {
            dayOf = new Phrase(R.string.progess_weeklystatus);
            dayOf.replaceNumber(dayIndex+1);

            DateTime startDate = testCycle.getActualStartDate();
            if(isBaseline){
                startDate = startDate.plusDays(1);
            }
            weekProgressView.setupView(startDate,dayIndex);
        }

        weeklyStatus.setText(dayOf.toHtmlString());

        TextView startDate = view.findViewById(R.id.startDate);
        startDate.setText(new Phrase(R.string.progress_startdate).toHtmlString());

        DateTime start = testCycle.getActualStartDate();
        DateTime end = testCycle.getActualEndDate().minusDays(1);

        TextView startDate_date = view.findViewById(R.id.startDate_date);

        Phrase startPhrase = new Phrase(R.string.progress_startdate_date);
        startPhrase.replaceDate(R.string.format_date_long, start);
        startDate_date.setText(startPhrase.toString());

        TextView endDate = view.findViewById(R.id.endDate);
        endDate.setText(new Phrase(R.string.progress_enddate).toHtmlString());

        Phrase endPhrase = new Phrase(R.string.progress_enddate_date);
        endPhrase.replaceDate(R.string.format_date_long, end);

        TextView endDate_date = view.findViewById(R.id.endDate_date);
        endDate_date.setText(endPhrase.toString());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
    }

}
