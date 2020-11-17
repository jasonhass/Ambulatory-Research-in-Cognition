//
// EarningsHomeBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.nav;

import com.healthymedium.arc.api.models.EarningDetails;
import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.UI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EarningsHomeBehavior extends Behavior {

    class EarningSet {
        EarningOverview overview;
        EarningDetails details;
    }
    private List<EarningSet> earnings;

    private int index = -1;

    public EarningsHomeBehavior() {
        earnings = createEarningsList();
    }

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);

        if(index >= earnings.size()) {
            return;
        }

        if(index==0){
            Study.getParticipant().getState().currentTestSession = 1;
            Study.getParticipant().getState().currentTestDay = 1;
            Study.getParticipant().getState().currentTestCycle = 0;
        }

        if(index >= 0) {
            Study.getParticipant().getState().earnings.setOverview(earnings.get(index).overview);
            Study.getParticipant().getState().earnings.setDetails( earnings.get(index).details);
        }

        index++;

        UI.click(ViewUtil.getString(R.string.resources_nav_earnings));
    }

    public  List<EarningSet> createEarningsList(){
        List<EarningSet> earningsList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> fourOutGoals = new ArrayList<ArrayList<Integer>>(){{
            add(new ArrayList<>(Arrays.asList(100,0,0,0)));
            add(new ArrayList<>(Arrays.asList(100,100,0,0)));
            add(new ArrayList<>(Arrays.asList(100,100,100,0)));
            add(new ArrayList<>(Arrays.asList(100,100,100,100)));
        }};
        ArrayList<ArrayList<Integer>> twoADayGoals = new ArrayList<ArrayList<Integer>>(){{
            add(new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0)));
            add(new ArrayList<>(Arrays.asList(100,0,0,0,0,0,0)));
            add(new ArrayList<>(Arrays.asList(100,100,0,0,0,0,0)));
            add(new ArrayList<>(Arrays.asList(100,100,100,0,0,0,0)));
            add(new ArrayList<>(Arrays.asList(100,100,100,100,0,0,0)));
            add(new ArrayList<>(Arrays.asList(100,100,100,100,100,0,0)));
            add(new ArrayList<>(Arrays.asList(100,100,100,100,100,100,0)));
            add(new ArrayList<>(Arrays.asList(100,100,100,100,100,100,100)));
        }};
        int twentyProgress = 0;
        for(int x = 0; x < 7; x++){
            for(int y = 0; y < 4; y++){
                twentyProgress++;
                int actualProgress = (int) Math.ceil((100f/21) * twentyProgress);
                boolean twentyComplete = false;
                if(twentyProgress >= 21){
                    twentyComplete = true;
                }
                boolean twoComplete = false;
                int twoIndex = x;
                if(y > 0){
                    twoIndex++;
                    if(x==6){
                        twoComplete = true;
                    }
                }
                boolean fourComplete = false;
                if(y == 3){
                    fourComplete = true;
                }
                EarningSet set = new EarningSet();
                set.overview = EarningOverview.createEarningOverview(
                        0, "$0.50", "$13.50",
                        fourComplete, "$0.00", 1, "$1.00", fourOutGoals.get(y),
                        twoComplete, "$0.00", 1, "$6.00", twoADayGoals.get(twoIndex),
                        twentyComplete, "$5.00", actualProgress, "$5.00"
                );
                earningsList.add(set);
            }
        }
        return earningsList;
    }
}
