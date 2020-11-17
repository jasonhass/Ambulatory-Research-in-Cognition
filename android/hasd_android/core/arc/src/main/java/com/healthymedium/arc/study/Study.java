//
// Study.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import android.content.Context;


import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.Device;
import com.healthymedium.arc.heartbeat.HeartbeatManager;
import com.healthymedium.arc.utilities.MigrationUtil;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.VersionUtil;

import java.lang.reflect.InvocationTargetException;

public class Study{

    public static final String TAG_INITIALIZED = "initialized";
    public static final String TAG_CONTACT_INFO = "ContactInfo";
    public static final String TAG_CONTACT_EMAIL = "ContactInfoEmail";


    protected static Study instance;
    protected static boolean valid;
    protected Context context;

    static Scheduler scheduler;
    static StateMachine stateMachine;
    static Participant participant;
    static RestClient restClient;
    static MigrationUtil migrationUtil;
    static PrivacyPolicy privacyPolicy;

    public static synchronized void initialize(Context context) {
        if(instance==null) {
            instance = new Study();
        }
        instance.context = context;
    }

    public static synchronized Study getInstance() {
        return instance;
    }

    protected Study() {

    }

    public static boolean isValid(){
        if(instance==null){
            return false;
        }
        return valid;
    }

    // class registrations -------------------------------------------------------------------------

    public boolean registerParticipantType(Class tClass) {
        return registerParticipantType(tClass,false);
    }

    public boolean registerParticipantType(Class tClass, boolean overwrite){
        if(participant!=null && !overwrite){
            return false;
        }
        if(tClass==null){
            return false;
        }
        if(!Participant.class.isAssignableFrom(tClass)){
            return false;
        }
        try {
            participant = (Participant) tClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean registerRestApi(Class clientClass) {
        return registerRestApi(clientClass,false);
    }

    public boolean registerRestApi(Class clientClass, boolean overwrite){
        if(restClient!=null && !overwrite){
            return false;
        }
        if(clientClass==null){
            return false;
        }
        if(!RestClient.class.isAssignableFrom(clientClass)){
            return false;
        }
        try {
            restClient = (RestClient) clientClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean registerRestApi(Class clientClass, Class apiClass){
        return registerRestApi(clientClass, apiClass,false);
    }

    public boolean registerRestApi(Class clientClass, Class apiClass, boolean overwrite){
        if(restClient!=null && !overwrite){
            return false;
        }
        if(clientClass==null){
            return false;
        }
        if(!RestClient.class.isAssignableFrom(clientClass)){
            return false;
        }
        try {
            restClient = (RestClient) clientClass.getDeclaredConstructor(Class.class).newInstance(apiClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return true;
    }

    public  boolean registerStateMachine(Class tClass) {
        return registerStateMachine(tClass,false);
    }

    public  boolean registerStateMachine(Class tClass, boolean overwrite){
        if(stateMachine!=null && !overwrite){
            return false;
        }
        if(tClass==null){
            return false;
        }
        if(!StateMachine.class.isAssignableFrom(tClass)){
            return false;
        }
        try {
            stateMachine = (StateMachine) tClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean registerScheduler(Class tClass){
        return registerScheduler(tClass,false);
    }

    public boolean registerScheduler(Class tClass, boolean overwrite){
        if(scheduler!=null && !overwrite){
            return false;
        }
        if(tClass==null){
            return false;
        }
        if(!Scheduler.class.isAssignableFrom(tClass)){
            return false;
        }
        try {
            scheduler = (Scheduler) tClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean registerMigrationUtil(Class tClass){
        return registerMigrationUtil(tClass,false);
    }

    public boolean registerMigrationUtil(Class tClass, boolean overwrite){
        if(migrationUtil!=null && !overwrite){
            return false;
        }
        if(tClass==null){
            return false;
        }
        if(!MigrationUtil.class.isAssignableFrom(tClass)){
            return false;
        }
        try {
            migrationUtil = (MigrationUtil) tClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean registerPrivacyPolicy(Class tClass){
        return registerPrivacyPolicy(tClass,false);
    }

    public boolean registerPrivacyPolicy(Class tClass, boolean overwrite){
        if(privacyPolicy!=null && !overwrite){
            return false;
        }
        if(tClass==null){
            return false;
        }
        if(!PrivacyPolicy.class.isAssignableFrom(tClass)){
            return false;
        }
        try {
            privacyPolicy = (PrivacyPolicy) tClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void checkRegistrations(){
        if(participant==null){
            participant = new Participant();
        }
        if(scheduler==null){
            scheduler = new Scheduler();
        }
        if(stateMachine==null){
            stateMachine = new StateMachine();
        }
        if(restClient==null){
            restClient = new RestClient(null);
        }
        if(migrationUtil==null){
            migrationUtil = new MigrationUtil();
        }
        if(privacyPolicy==null){
            privacyPolicy = new PrivacyPolicy();
        }
    }

    //  ----------------------------------------------------------------------------

    public void load(){

        checkRegistrations();

        boolean initialized = PreferencesManager.getInstance().getBoolean(TAG_INITIALIZED,false);
        if(!initialized){
            participant.initialize();
            participant.save();

            stateMachine.initialize();
            stateMachine.save();

            PreferencesManager.getInstance().putLong(MigrationUtil.TAG_VERSION_LIB,VersionUtil.getLibraryVersionCode());
            PreferencesManager.getInstance().putLong(MigrationUtil.TAG_VERSION_APP,VersionUtil.getAppVersionCode());
            PreferencesManager.getInstance().putBoolean(TAG_INITIALIZED,true);
            HeartbeatManager.getInstance().scheduleHeartbeat();

        }  else {
            migrationUtil.checkForUpdate();
            migrationUtil = null; // not needed after this

            participant.load();
            stateMachine.load();
        }

        if(Config.ENABLE_EARNINGS){
            participant.getEarnings().linkAgainstRestClient();
        }



        valid = true;
    }

    public void run(){
        stateMachine.decidePath();
        stateMachine.setupPath();
        participant.save();
        stateMachine.save();
    }

    // registered class getters --------------------------------------------------------------------

    public static Participant getParticipant(){
        return participant;
    }

    public static Scheduler getScheduler(){
        return scheduler;
    }

    public static StateMachine getStateMachine(){
        return stateMachine;
    }

    public static RestClient getRestClient(){
        return restClient;
    }

    public static PrivacyPolicy getPrivacyPolicy() { return privacyPolicy; }

    // commonly used accessors ---------------------------------------------------------------------

    public static TestCycle getCurrentTestCycle() {
        return participant.getCurrentTestCycle();
    }

    public static TestSession getCurrentTestSession() {
        return participant.getCurrentTestSession();
    }

    public static TestDay getCurrentTestDay() {
        return participant.getCurrentTestDay();
    }

    public static PathSegmentData getCurrentSegmentData() {
        try {
            return stateMachine.cache.segments.get(0).dataObject;
        } catch (IndexOutOfBoundsException e) {

            Application.getInstance().restart();
            return new PathSegmentData();
        }
    }

    public static void setCurrentSegmentData(Object object) {
        try {
            stateMachine.cache.segments.get(0).dataObject = (PathSegmentData) object;
        } catch (IndexOutOfBoundsException e) {

            Application.getInstance().restart();
        }
    }

    // operations ----------------------------------------------------------------------------------

    // try to open next fragment in segment
    // if at the end of segment, start next
    // if no more segments, decide path
    public static boolean openNextFragment(){
        return stateMachine.openNext();
    }

    public static boolean skipToNextSegment(){
        return stateMachine.skipToNextSegment();
    }

    public static boolean openNextFragment(int skips){
        return stateMachine.openNext(skips);
    }

    public static boolean openPreviousFragment() {
        return stateMachine.openPrevious();
    }

    public static boolean openPreviousFragment(int skips){
        return stateMachine.openPrevious(skips);
    }


    // Mark the current test as abandoned, and setup the stateMachine so that it knows what to
    // display next.
    
    public static void abandonTest()
    {
        //TODO: move the dialog to somewhere further up this chain
//        LoadingDialog dialog = new LoadingDialog();
//        dialog.show(NavigationManager.getInstance().getFragmentManager(),"LoadingDialog");

//        dialog.dismiss();
        stateMachine.abandonTest();
        stateMachine.decidePath();
        stateMachine.setupPath();
        stateMachine.openNext();
    }

    public static void updateAvailability(int minWakeTime, int maxWakeTime)
    {
        stateMachine.setPathSetupAvailability(minWakeTime, maxWakeTime, true);
        stateMachine.openNext();
    }

    public static void updateAvailabilityOnboarding(int minWakeTime, int maxWakeTime)
    {
        stateMachine.setPathSetupAvailability(minWakeTime, maxWakeTime, false);
        stateMachine.openNext();
    }

    public static void adjustSchedule()
    {
        stateMachine.setPathAdjustSchedule();
        stateMachine.openNext();
    }
}
