//
// SplashScreen.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.healthymedium.arc.font.FontFactory;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.Proctor;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.utilities.PreferencesManager;

@SuppressLint("ValidFragment")
public class SplashScreen extends BaseFragment {

    boolean paused = false;
    boolean ready = false;
    boolean skipSegment = false;
    boolean visible = true;

    public SplashScreen() {
    }

    public SplashScreen(boolean visible) {
        this.visible = visible;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.core_fragment_splash, container, false);
        if(!visible) {
            view.setVisibility(View.INVISIBLE);
        }
        AsyncLoader loader = new AsyncLoader();
        loader.execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused && ready) {
            if(Study.isValid()){
                exit();
            }
        }
        paused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }


    private class AsyncLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Context context = getApplication();
            getMainActivity().setupKeyboardWatcher();

            Application.getInstance().updateLocale(getContext());

            if(FontFactory.getInstance()==null) {
                FontFactory.initialize(context);
            }

            if(!Fonts.areLoaded()){
                Fonts.load();
                FontFactory.getInstance().setDefaultFont(Fonts.roboto);
                FontFactory.getInstance().setDefaultBoldFont(Fonts.robotoBold);
            }

            Hints.load();

            TestCycle cycle = Study.getCurrentTestCycle();
            if(cycle !=null){
                if(cycle.getActualStartDate().isBeforeNow() && cycle.getActualEndDate().isAfterNow()){
                    Proctor.startService(getContext());
                }
            }

            // We need to check to see if we're currently in the middle of a test session.
            // If we are, and if the state machine has valid fragments, we should let it continue
            // displaying those.
            // Otherwise, just run the Study instance, and let it figure out where it needs to be.

            if(Study.getParticipant().isCurrentlyInTestSession()
                && Study.getParticipant().checkForTestAbandonment() == false
                && Study.getStateMachine().hasValidFragments()
            ) {
                skipSegment = true;
            } else {
                skipSegment = false;
                Study.getInstance().run();
            }

            ready = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void etc) {
            if(getMainActivity()==null) {
                return;
            }

            getMainActivity().getWindow().setBackgroundDrawableResource(R.drawable.core_background);

            if(!paused){
                exit();
            }
        }
    }

    private void exit(){
        if(getFragmentManager() != null) {
            getFragmentManager().popBackStack();
            if(skipSegment) {
                Study.getInstance().skipToNextSegment();
            } else {
                Study.getInstance().openNextFragment();
            }
        }
    }

}
