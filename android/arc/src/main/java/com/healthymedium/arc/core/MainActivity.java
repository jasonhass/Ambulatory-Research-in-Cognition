//
// MainActivity.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.HomeWatcher;
import com.healthymedium.arc.utilities.KeyboardWatcher;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.study.AbandonmentJobService;

public class MainActivity extends AppCompatActivity {

    boolean paused = false;
    boolean backAllowed = false;
    boolean backInStudy = false;
    int backInStudySkips = 0;

    boolean checkAbandonment = false;

    FrameLayout contentView;
    HomeWatcher homeWatcher;
    KeyboardWatcher keyboardWatcher;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(new Bundle());

        Intent intent = getIntent();
        if(intent!=null) {
            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                Config.OPENED_FROM_NOTIFICATION = bundle.getBoolean("OPENED_FROM_NOTIFICATION",false);
            }
        }

        setContentView(R.layout.core_activity_main);
        contentView = findViewById(R.id.content_frame);

        setup();
    }

    public void setup(){

        PreferencesManager.initialize(this);
        NavigationManager.initializeInstance(getSupportFragmentManager());

        if(PreferencesManager.getInstance().contains("language") || !Config.CHOOSE_LOCALE){
            NavigationManager.getInstance().open(new SplashScreen());
        } else {
            //NavigationManager.getInstance().open(new SetupLocaleFragment());
        }
    }


    public void setupHomeWatcher(){
        homeWatcher = new HomeWatcher(this);
        homeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                paused = true;
                checkAbandonment = false;
                if(Study.isValid()){

                    if(Study.getInstance().getParticipant().isCurrentlyInTestSession()){
                        checkAbandonment = true;
                    }
                }
            }
            @Override
            public void onHomeLongPressed() {

            }
        });
        homeWatcher.startWatch();
    }

    public void setupKeyboardWatcher(){
        keyboardWatcher = new KeyboardWatcher(this);
        keyboardWatcher.startWatch();
    }

    public void setKeyboardListener(KeyboardWatcher.OnKeyboardToggleListener listener){
        keyboardWatcher.setListener(listener);
    }

    public void removeKeyboardListener(){
        keyboardWatcher.setListener(null);
    }


    public void enableBackPress(boolean enable, boolean inStudy, int skips){
        Log.i("MainActivity","enableBackPress(enable="+enable+", inStudy="+inStudy+")");
        backInStudySkips = skips;
        backAllowed = enable;
        backInStudy = inStudy;
    }

    public void enableBackPress(boolean enable, boolean inStudy){
        Log.i("MainActivity","enableBackPress(enable="+enable+", inStudy="+inStudy+")");
        backInStudySkips = 0;
        backAllowed = enable;
        backInStudy = inStudy;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            /*
            if(homeWatcher != null){
                homeWatcher.stopWatch();
                homeWatcher = null;
            }
            finish();
            */
        } else if(backAllowed){
            if(Study.isValid() && backInStudy){
                Study.openPreviousFragment(backInStudySkips);
            } else {
                NavigationManager.getInstance().popBackStack();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(homeWatcher != null){
            homeWatcher.stopWatch();
            homeWatcher = null;
        }
        if(keyboardWatcher != null){
            keyboardWatcher.stopWatch();
            keyboardWatcher = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
        if(Study.isValid()){
            AbandonmentJobService.unscheduleSelf(getApplicationContext());
            Study.getParticipant().markResumed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        if(Study.isValid()){
            Study.getParticipant().markPaused();
            Study.getStateMachine().save();
            if(Study.getParticipant().isCurrentlyInTestSession()) {
                AbandonmentJobService.scheduleSelf(getApplicationContext());
            }
        }
    }

    public boolean isVisible() {
        return !paused;
    }

    public View getContentView(){
        return contentView;
    }

}
