//
// GuidedTest.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.core;

import android.support.annotation.MainThread;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.healthymedium.arc.study.Study;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@MainThread
@LargeTest
@RunWith(AndroidJUnit4.class)
public class GuidedTest extends BaseTest {

    public static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private static boolean running = false;

    // override this
    public void setup(){

    }

    public void checkSetup(){

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

        while (running) {
            try {
                queue.take().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
