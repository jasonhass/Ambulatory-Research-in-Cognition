//
// ProgressHomeBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.nav;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.data.TestData;
import com.healthymedium.test_suite.utilities.UI;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ProgressHomeBehavior extends Behavior {

    private int index = 0;

    private DateTime DATE_TIME;
    private long MS_PER_DAY = 86400000L;
    private long MS_PER_CYCLE = MS_PER_DAY * 182;

    private List<ProgressSet> progressList;
    private List<TestCycle> testCycles;

    class ProgressSet{
        ArrayList<Integer> sessionState;
        int currentTestDay;
        int currentTestCycle;
        long ms;
    }

    public ProgressHomeBehavior(DateTime date){
        testCycles = Study.getParticipant().getState().testCycles;
        DATE_TIME = date;
        progressList = createProgressSet();
        UI.sleep(5000);

    }


    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);



        if(index >= progressList.size()) {
            return;
        }

        int currTestCycle = progressList.get(index).currentTestCycle;
        int currTestDay = progressList.get(index).currentTestDay;
        ArrayList<Integer> currSession = progressList.get(index).sessionState;

        DateTimeUtils.setCurrentMillisFixed(progressList.get(index).ms);

        Study.getParticipant().getState().currentTestCycle = currTestCycle;
        Study.getParticipant().getState().currentTestDay = currTestDay;
        Study.getParticipant().getState().currentTestSession = 1;

        List<TestSession> testSessions = testCycles.get(currTestCycle).getTestDay(currTestDay).getTestSessions();
        for(int i = 0; i <testSessions.size(); i++ ){
            TestSession ts = testSessions.get(i);
            int score = currSession.get(i);
            ts.purgeData();
            ts.addTestData(new TestData(score));
            ts.setProgress(score);
            if(score == 100){
                ts.markCompleted();
            }
            else {
                ts.markAbandoned();
            }
        }



        index++;

        UI.click(ViewUtil.getString(R.string.resources_nav_progress));
    }


    private List<ProgressSet> createProgressSet(){
        ArrayList<ProgressSet> list = new ArrayList<>();

        ArrayList<ArrayList<Integer>> sessions = new ArrayList<ArrayList<Integer>>(){{
            add(new ArrayList<>(Arrays.asList(0,0,0,0)));
            add(new ArrayList<>(Arrays.asList(100,0,0,0)));
            add(new ArrayList<>(Arrays.asList(100,100,0,0)));
            add(new ArrayList<>(Arrays.asList(100,100,100,0)));
            add(new ArrayList<>(Arrays.asList(100,100,100,100)));
            add(new ArrayList<>(Arrays.asList(0,33,66,100)));
            add(new ArrayList<>(Arrays.asList(33,33,33,33)));
            add(new ArrayList<>(Arrays.asList(66,66,66,66)));
            add(new ArrayList<>(Arrays.asList(0,100,0,100)));
            add(new ArrayList<>(Arrays.asList(66,0,100,33)));
        }};

        //add the first 2 states of the practice baseline
        //notFinished and Finished
        for(int i =0; i < 5; i = i +4){
            ProgressSet ps = new ProgressSet();
            ps.currentTestCycle = 0;
            ps.currentTestDay = 0;
            ps.sessionState = sessions.get(i);
            ps.ms = DATE_TIME.getMillis() + 5000;
            list.add(ps);
        }

        for(int i =0; i < sessions.size(); i++){
            ProgressSet ps = new ProgressSet();
            int cycle = i < 10 ? i : 9;
            int day = i < 7 ? i : 6;
            //accounting for first cycle, where day 0 is the baseline
            ps.currentTestDay = day;
            if( i==0){
                ps.currentTestDay = day + 1;
            }
            ps.currentTestCycle = cycle;
            ps.sessionState = sessions.get(i);
            ps.ms =  DATE_TIME.getMillis() + day * MS_PER_DAY + cycle * MS_PER_CYCLE + 5000;
            list.add(ps);
        }

        return list;
    }
}
