//
// TestsCompletedHomeBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.completed;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.paths.informative.EarningsPostTestLoadingScreen;
import com.healthymedium.arc.paths.informative.FinishedStudyTotalsScreen;
import com.healthymedium.arc.study.StateMachineAlpha;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.core.TestBehavior;
import com.healthymedium.test_suite.data.TestData;
import com.healthymedium.test_suite.utilities.UI;

import java.util.ArrayList;
import java.util.List;


public class TestsCompletedHomeBehavior extends Behavior {
    private int index = -1;
    private int specialIndex = 0;

    class ProgressSet{
        int currentTestSession;
        int currentTestDay;
        int currentTestCycle;

        int progress;
        boolean completed;
    }

    private List<TestCycle> testCycles;
    private List<ProgressSet> progressSets;


    public TestsCompletedHomeBehavior(){
        testCycles = Study.getParticipant().getState().testCycles;
        progressSets = createProgressSet();
    }

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);


        if(specialIndex >= 2){
            return;
        }

        //handle special cases
        else if(index >= progressSets.size()){

            //show failure earning screen
            if(specialIndex == 0){
                Study.getParticipant().getState().currentTestSession = 1;
                Study.getParticipant().getState().currentTestDay = 3;
                Study.getParticipant().getState().currentTestCycle = 3;
                EarningsPostTestLoadingScreen.DEBUG_OPEN_SUCCESS = false;
            }

            //show cycle finished screen
            else if(specialIndex == 1){
                EarningsPostTestLoadingScreen.DEBUG_OPEN_SUCCESS = true;
                Study.getParticipant().getState().currentTestSession =0;
                Study.getParticipant().getState().currentTestDay = 0;
                Study.getParticipant().getState().currentTestCycle = 9;
            }


            specialIndex++;
        }


        else if(index == -1){
            FinishedStudyTotalsScreen.DEBUG_SHOW_TOTAL = false;
        }
        else {
            FinishedStudyTotalsScreen.DEBUG_SHOW_TOTAL = true;
            int currCycle = progressSets.get(index).currentTestCycle;
            int currDay = progressSets.get(index).currentTestDay;
            int currSession = progressSets.get(index).currentTestSession;

            int currProgress = progressSets.get(index).progress;
            boolean currCompleted = progressSets.get(index).completed;

            Study.getParticipant().getState().currentTestSession = currSession + 1;
            Study.getParticipant().getState().currentTestDay = currDay;
            Study.getParticipant().getState().currentTestCycle = currCycle;

            TestSession testSession = testCycles.get(currCycle).getTestDay(currDay).getTestSession(currSession);
            testSession.purgeData();
            testSession.addTestData(new TestData(currProgress));
            testSession.setProgress(currProgress);
            if(currCompleted){
                testSession.markCompleted();
            }else{
                testSession.markAbandoned();
            }



        }



        index++;


        PreferencesManager preferences = PreferencesManager.getInstance();
        preferences.putBoolean(StateMachineAlpha.TAG_TEST_COMPLETE,true);
        TestBehavior.state.lifecycle = StateMachineAlpha.LIFECYCLE_BASELINE;
        TestBehavior.state.path = StateMachineAlpha.PATH_TEST_NONE;
        StateMachineAlpha stateMachine = (StateMachineAlpha) Study.getStateMachine();
        stateMachine.setPathNoTests();

        stateMachine.openNext();
    }

    List<ProgressSet> createProgressSet(){
        List<ProgressSet> set = new ArrayList<>();
        for(int x = 7; x < 9; x++){
            for(int y = 0; y < 4; y++ ){
                ProgressSet ps = new ProgressSet();
                ps.currentTestCycle = x;
                ps.currentTestDay = 6;
                ps.currentTestSession = y;

                //going from 0 progress to 100% completed
                if(x == 7){
                    ps.progress = y * 33;
                    ps.completed = false;

                    //last day is 100% compleyed
                    if(y == 3){
                        ps.progress = 100;
                        ps.completed = true;
                    }
                }
                //100% completed every session
                else {
                    ps.progress = 100;
                    ps.completed = true;

                }
                set.add(ps);

            }
        }

        return set;
    }

}
