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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.healthymedium.arc.notifications.ProctorDeviation;
import com.healthymedium.arc.paths.battery_optimization.BatteryOptimizationReminder;
import com.healthymedium.arc.utilities.Log;

import android.view.View;
import android.widget.FrameLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.questions.QuestionLanguagePreference;
import com.healthymedium.arc.study.AbandonmentJobService;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.HomeWatcher;
import com.healthymedium.arc.utilities.KeyboardWatcher;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean paused = false;
    boolean backAllowed = false;
    boolean backInStudy = false;
    boolean hasNewIntent = false;
    int backInStudySkips = 0;

    FrameLayout contentView;
    HomeWatcher homeWatcher;
    KeyboardWatcher keyboardWatcher;


    @Override
    protected void onStart() {
        super.onStart();
        if(hasNewIntent){
            hasNewIntent = false;
            if(!Study.getStateMachine().isCurrentlyInTestPath()){
                Study.openNextFragment();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(new Bundle());


        Intent intent = getIntent();
        parseIntent(intent);

        setContentView(R.layout.core_activity_main);
        contentView = findViewById(R.id.content_frame);

        setup();
    }

    @Override
    protected void onNewIntent(Intent intent) {

        parseIntent(intent);
        hasNewIntent = true;

    }

    private void parseIntent(Intent intent){

        if(intent!=null) {
            Config.OPENED_FROM_NOTIFICATION = intent.getBooleanExtra(Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION,false);
            Config.OPENED_FROM_VISIT_NOTIFICATION = intent.getBooleanExtra(Config.INTENT_EXTRA_OPENED_FROM_VISIT_NOTIFICATION,false);
            boolean restart = intent.getBooleanExtra(Application.TAG_RESTART,false);
            if(restart){
                PreferencesManager.getInstance().putBoolean(Application.TAG_RESTART,true);

            }
        }


    }

    public void setup(){
        NavigationManager.initialize(getSupportFragmentManager());

        if(PreferencesManager.getInstance().getBoolean(Application.TAG_RESTART,false)){
            PreferencesManager.getInstance().putBoolean(Application.TAG_RESTART,false);

            Phrase phrase = new Phrase(R.string.low_memory_restart_dialogue);
            phrase.replace(R.string.token_app_name, R.string.app_name);

            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(getString(R.string.low_memory_restart_dialogue_header))
                    .setMessage(phrase.toString())
                    .setPositiveButton(getString(R.string.button_okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }

        if(ProctorDeviation.shouldRequestBeMade()){
            NavigationManager.getInstance().open(new BatteryOptimizationReminder());
            return;
        }

        if(PreferencesManager.getInstance().contains(Locale.TAG_LANGUAGE) || !Config.CHOOSE_LOCALE) {
            NavigationManager.getInstance().open(new SplashScreen());
            return;
        }

        List<Locale> locales = Application.getInstance().getLocaleOptions();
        List<String> options = new ArrayList<>();
        for(Locale locale : locales) {
            options.add(locale.getLabel());
        }

        QuestionLanguagePreference fragment = new QuestionLanguagePreference();
        NavigationManager.getInstance().open(fragment);
    }

    public void setupHomeWatcher(){
        homeWatcher = new HomeWatcher(this);
        homeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {

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

        backInStudySkips = skips;
        backAllowed = enable;
        backInStudy = inStudy;
    }

    public void enableBackPress(boolean enable, boolean inStudy){

        backInStudySkips = 0;
        backAllowed = enable;
        backInStudy = inStudy;
    }

    @Override
    public void onBackPressed() {
        if(backAllowed){
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

        if(Study.isValid()){
            AbandonmentJobService.unscheduleSelf(getApplicationContext());
            //if we were paused, then we need to call the resume handler.
            if(paused) {
                Study.getParticipant().markResumed();
            }
        }
        paused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        paused = true;
        if(Study.isValid()){
            Study.getParticipant().markPaused();
            Study.getStateMachine().save(true);
            if(Study.getParticipant().isCurrentlyInTestSession()) {
                AbandonmentJobService.scheduleSelf(getApplicationContext());
            }
        }
    }

    @Override
    protected void attachBaseContext(Context context) {

        super.attachBaseContext(context);
    }

    public boolean isVisible() {
        return !paused;
    }

    public View getContentView(){
        return contentView;
    }


}
