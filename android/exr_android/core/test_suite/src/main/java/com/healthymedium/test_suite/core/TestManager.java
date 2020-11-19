//
// TestManager.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.healthymedium.test_suite.events.EventBase;
import com.healthymedium.test_suite.events.LogEvent;
import com.healthymedium.test_suite.events.SuiteEvent;
import com.healthymedium.test_suite.reporting.ReportingClient;
import com.healthymedium.arc.core.Device;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;

import java.util.HashMap;
import java.util.Map;

// TODO implement methods for grabbing screenshots
// TODO implement methods for using rest client

public class TestManager extends org.junit.rules.TestWatcher {

    private static final String tag = "TestManager";
    private static TestManager instance;

    private TestReport currentReport;
    private Map<Description, TestReport> map = new HashMap<>();
    private ReportingClient client;
    private int screenshotIndex;

    public static TestManager getInstance(){
        if(instance==null){
            initailize();
        }
        return instance;
    }

    public static void initailize(){
        instance = new TestManager();
        if(!Device.isInitialized()) {
            Context context = InstrumentationRegistry.getInstrumentation().getContext();
            Device.initialize(context);
        }
    }

    private TestManager(){
        super();
    }

    // logging methods -----------------------------------------------------------------------------

    public void logVerbose(String tag, String msg){
        if(currentReport!=null) {
            LogEvent event = new LogEvent(LogEvent.VERBOSE, tag, msg);
            currentReport.addEvent(event);
        }
    }

    public void logDebug(String tag, String msg){
        if(currentReport!=null) {
            LogEvent event = new LogEvent(LogEvent.DEBUG, tag, msg);
            currentReport.addEvent(event);
        }
    }

    public void logInfo(String tag, String msg){
        if(currentReport!=null) {
            LogEvent event = new LogEvent(LogEvent.INFO, tag, msg);
            currentReport.addEvent(event);
        }
    }

    public void logWarn(String tag, String msg){
        if(currentReport!=null) {
            LogEvent event = new LogEvent(LogEvent.WARN, tag, msg);
            currentReport.addEvent(event);
        }
    }

    public void logError(String tag, String msg){
        if(currentReport!=null) {
            LogEvent event = new LogEvent(LogEvent.ERROR, tag, msg);
            currentReport.addEvent(event);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void reportEvent(EventBase event){
        if(currentReport!=null){
            currentReport.addEvent(event);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // updates current report to one matching the description
    // creates new report if not found

    private void updateReport(Description description){
        if(!map.containsKey(description)){
            TestReport report = new TestReport(description);
            map.put(description,report);
        }
        currentReport = map.get(description);
    }

    // methods from junit.TestWatcher --------------------------------------------------------------

    @Override
    protected void succeeded(Description description) {
        updateReport(description);
        SuiteEvent event = new SuiteEvent("test_succeeded",description);
        currentReport.addEvent(event);
        super.succeeded(description);
    }

    @Override
    protected void failed(Throwable e, Description description) {
        updateReport(description);
        SuiteEvent event = new SuiteEvent("test_failed",description,e.getMessage());
        currentReport.addEvent(event);
        super.failed(e, description);
    }

    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
        updateReport(description);
        SuiteEvent event = new SuiteEvent("test_skipped",description, e.getMessage());
        currentReport.addEvent(event);
        super.skipped(e, description);
    }

    @Override
    protected void starting(Description description) {
        updateReport(description);
        SuiteEvent event = new SuiteEvent("test_starting",description);
        currentReport.addEvent(event);
        super.starting(description);
    }

    @Override
    protected void finished(Description description) {
        updateReport(description);
        SuiteEvent event = new SuiteEvent("test_finished",description);
        currentReport.addEvent(event);
        super.finished(description);
    }

}
