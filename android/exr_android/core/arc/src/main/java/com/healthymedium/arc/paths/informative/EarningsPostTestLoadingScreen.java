//
// EarningsPostTestLoadingScreen.java
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Earnings;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.navigation.NavigationManager;

public class EarningsPostTestLoadingScreen extends BaseFragment {

    ProgressBar progressBar;
    TextView textView;

    Earnings earnings;
    Handler handler;
    boolean practiceTest;

    public EarningsPostTestLoadingScreen() {
        ParticipantState state = Study.getParticipant().getState();
        practiceTest = (state.currentTestCycle==0 && state.currentTestDay==1 && state.currentTestSession==0);
        TransitionSet set = new TransitionSet();
        set.enter = R.anim.slide_in_right;
        set.popEnter =  R.anim.slide_in_left;
        if(!practiceTest) {
            set.exit = R.anim.slide_out_left;
            set.popExit = R.anim.slide_out_right;
        }
        setTransitionSet(set);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings_post_test_loading, container, false);

        if(practiceTest){
            Study.openNextFragment();
            return view;
        }

        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.textView);
        handler = new Handler();
        handler.postDelayed(runnable,10000);

        earnings = Study.getParticipant().getEarnings();
        if(!earnings.hasCurrentOverview()) {
            earnings.refreshOverview(new Earnings.Listener() {
                @Override
                public void onSuccess() {
                    checkLoading();
                }

                @Override
                public void onFailure() {
                    checkLoading();
                }
            });
        }
        if(!earnings.hasCurrentDetails()) {
            earnings.refreshDetails(new Earnings.Listener() {
                @Override
                public void onSuccess() {
                    checkLoading();
                }

                @Override
                public void onFailure() {
                    checkLoading();
                }
            });
        }
        checkLoading();

        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);
        animateAlpha(1.0f,null);
    }

    private void animateAlpha(float value, Runnable runnable) {
        if(textView!=null){
            textView.animate().alpha(value);
        }
        if(progressBar!=null) {
            progressBar.animate().alpha(value);
        }
        if(runnable!=null && handler!=null) {
            handler.postDelayed(runnable,300);
        }
    }

    private void checkLoading() {
        if(EarningsPostTestLoadingScreen.this==null){
            return;
        }
        if(earnings.isRefreshingOverview()||earnings.isRefreshingDetails()) {
            return;
        }
        handler.removeCallbacks(runnable);
        if(earnings.hasCurrentOverview() && earnings.hasCurrentDetails()) {
            openSuccess();
        } else {
            openFailure();
        }
    }

    private void openSuccess(){
        animateAlpha(0f, new Runnable() {
            @Override
            public void run() {
                NavigationManager.getInstance().open(new EarningsPostTestScreen());
            }
        });
    }

    private void openFailure(){
        animateAlpha(0f, new Runnable() {
            @Override
            public void run() {
                NavigationManager.getInstance().open(new EarningsPostTestUnavailableScreen());
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(Config.REST_BLACKHOLE){
                openSuccess();
//                openFailure();
                return;
            }
            openFailure();
        }
    };

}
