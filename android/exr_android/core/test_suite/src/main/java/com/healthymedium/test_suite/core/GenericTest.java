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

import android.content.Context;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.behaviors.Behaviors;
import com.healthymedium.test_suite.utilities.CircadianClocks;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.navigation.NavigationController;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.navigation.NavigationManager;

import org.joda.time.DateTime;
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

    //@Rule
    //public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Rule
    public TestManager testManager = TestManager.getInstance();

    public static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private static boolean running = false;
    Handler handler;

    // override this
    public void setup(){

    }

    public void checkSetup(){

        if(TestConfig.behaviorMap==null){
            TestConfig.behaviorMap = Behaviors.getDefault();
        }
        if(TestConfig.circadianClock==null){
            TestConfig.circadianClock = CircadianClocks.getDayShift();
        }

        if(TestConfig.Classes.stateMachine !=null){
            Study.getInstance().registerStateMachine(TestConfig.Classes.stateMachine,true);
            Study.getStateMachine().load();
        }
        if(TestConfig.Classes.participant!=null){
            Study.getInstance().registerParticipantType(TestConfig.Classes.participant,true);
            Study.getParticipant().load();
        }
        if(TestConfig.Classes.scheduler!=null){
            Study.getInstance().registerScheduler(TestConfig.Classes.scheduler,true);
        }
        if(TestConfig.Classes.restClient!=null){
            Study.getInstance().registerRestApi(TestConfig.Classes.restClient,TestConfig.Classes.restApi,true);
        }

        Study.getStateMachine().getState().currentPath = TestConfig.StudyState.path;
        Study.getStateMachine().getState().lifecycle = TestConfig.StudyState.lifecycle;

    }

    @Before
    @Override
    public void before() throws Exception {
        super.before();
        setup();
        checkSetup();
    }

    @Test
    public void test(){

        launchActivity();

        NavigationManager.getInstance().setListener(navigationListener);

        Context context = InstrumentationRegistry.getContext();
        handler = new Handler(context.getMainLooper());
        running = true;

        if(TestConfig.needsToSchedule){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Study.getScheduler().scheduleTests(DateTime.now(),Study.getParticipant());
                }
            };
            queue.add(runnable);
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BaseFragment fragment = NavigationManager.getInstance().getCurrentFragment();
                performBehavior(fragment,true);
            }
        };
        queue.add(runnable);

        while (running) {
            try {
                queue.take().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void performBehavior(BaseFragment fragment, boolean opened) {

        Class tClass = fragment.getClass();
        Behavior behavior = null;

        // try to find behavior that matches to fragment
        // if not an exact match, check if assignable from super class
        if(TestConfig.behaviorMap.containsKey(tClass)){
            behavior = TestConfig.behaviorMap.get(tClass);
        } else {
            for(Map.Entry<Class, Behavior> entry: TestConfig.behaviorMap.entrySet()){
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

        UI.sleep(400);
        if(opened){
            TestConfig.behaviorMap.get(tClass).onOpened(fragment);
        } else {
            TestConfig.behaviorMap.get(tClass).onPoppedBack(fragment);
        }
        handler.postDelayed(runnableTimeout,5000);

    }

    NavigationController.Listener navigationListener = new NavigationController.Listener() {
        @Override
        public void onOpen() {
            queue.add(runnableOpen);
        }

        @Override
        public void onPopBack() {
            queue.add(runnablePop);
        }
    };

    Runnable runnableOpen = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(runnableTimeout);
            BaseFragment fragment = NavigationManager.getInstance().getCurrentFragment();
            performBehavior(fragment,true);
        }
    };

    Runnable runnablePop = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(runnableTimeout);
            BaseFragment fragment = NavigationManager.getInstance().getCurrentFragment();
            performBehavior(fragment,false);
        }
    };

    Runnable runnableTimeout = new Runnable() {
        @Override
        public void run() {
            System.out.println("test timed out");
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    running = false;
                }
            };
            queue.add(runnable);
        }
    };

}
