//
// GenericTest.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.core;


import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.os.ConfigurationCompat;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.core.MainActivity;

import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.behaviors.Behaviors;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.navigation.NavigationManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@MainThread
@LargeTest
@RunWith(AndroidJUnit4.class)
public class GenericTest extends BaseTest {

    String tag = getClass().getSimpleName();

    @Rule
    public TestManager testManager = TestManager.getInstance();

    public static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private static boolean running = false;
    Handler handler;

    // override this
    public void setup(){

    }

    public void setupLocale(){
        PreferencesManager preferences = PreferencesManager.getInstance();
        java.util.Locale javaLocale = ConfigurationCompat.getLocales( InstrumentationRegistry.getTargetContext().getResources().getConfiguration()).get(0);

        boolean countryExist = false;
        boolean languageExist = false;
        for(Locale locale: TestBehavior.locales){
            if(locale.getCountry() == javaLocale.getCountry()){
                countryExist = true;
            }
            if(locale.getLanguage() == javaLocale.getLanguage()){
                languageExist = true;
            }
        }

        if(countryExist && languageExist){
            preferences.putString(Locale.TAG_LANGUAGE,javaLocale.getLanguage());
            preferences.putString(Locale.TAG_COUNTRY,javaLocale.getCountry());
        }else{
            preferences.putString(Locale.TAG_LANGUAGE,Locale.LANGUAGE_ENGLISH);
            preferences.putString(Locale.TAG_COUNTRY,Locale.COUNTRY_UNITED_STATES);
        }

        Application.getInstance().updateLocale(InstrumentationRegistry.getContext());

    }

    public void checkSetup(){

        if(TestBehavior.screenMap ==null){
            TestBehavior.screenMap = Behaviors.getDefault();
        }

        if(TestBehavior.classes.stateMachine !=null){
            Study.getInstance().registerStateMachine(TestBehavior.classes.stateMachine,true);
            Study.getStateMachine().load();
        }
        if(TestBehavior.classes.participant!=null){
            Study.getInstance().registerParticipantType(TestBehavior.classes.participant,true);
            Study.getParticipant().load();
        }
        if(TestBehavior.classes.scheduler!=null){
            Study.getInstance().registerScheduler(TestBehavior.classes.scheduler,true);
        }
        if(TestBehavior.classes.restClient!=null){
            Study.getInstance().registerRestApi(TestBehavior.classes.restClient, TestBehavior.classes.restApi,true);
        }

        if(TestBehavior.classes.activity==null){
            TestBehavior.classes.activity = MainActivity.class;
        }

        Study.getStateMachine().getState().currentPath = TestBehavior.state.path;
        Study.getStateMachine().getState().lifecycle = TestBehavior.state.lifecycle;
    }

    @Before
    @Override
    public void before() throws Exception {
        super.before();
        setup();
        setupLocale();
        checkSetup();
    }

    @Test
    public void test(){
        launchActivity();


        while (TestBehavior.STEPS > 0 ) {
            BaseFragment fragment = NavigationManager.getInstance().getCurrentFragment();

            String fragmentName = fragment.getClass().getSimpleName();

            while ( fragmentName.equals("SplashScreen") || fragmentName.equals("LandingScreen")) {
                UI.sleep(1000);
                fragment = NavigationManager.getInstance().getCurrentFragment();
                fragmentName = fragment.getClass().getSimpleName();
            }

            performBehavior(fragment);

            TestBehavior.STEPS--;
        }
    }

    public void performBehavior(BaseFragment fragment) {

        Class tClass = fragment.getClass();
        Behavior behavior = null;

        // try to find behavior that matches to fragment
        // if not an exact match, check if assignable from super class
        if(TestBehavior.screenMap.containsKey(tClass)){
            behavior = TestBehavior.screenMap.get(tClass);
        } else {
            for(Map.Entry<Class, Behavior> entry: TestBehavior.screenMap.entrySet()){
                Behavior entryBehavior = entry.getValue();
                if(entryBehavior.allowExtensions()) {
                    if (entry.getKey().isAssignableFrom(tClass)) {
                        behavior = entry.getValue();
                        break;
                    }
                }
            }
        }

        if(behavior==null) {
            Assert.fail("No behavior registered for "+tClass.getSimpleName());
        }

        TestBehavior.screenMap.get(tClass).onOpened(fragment);
    }



}
