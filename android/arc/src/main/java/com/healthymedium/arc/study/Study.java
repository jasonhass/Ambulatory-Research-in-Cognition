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

import com.healthymedium.arc.api.RestAPI;
import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.models.Heartbeat;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.Device;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.heartbeat.HeartbeatManager;
import com.healthymedium.arc.utilities.MigrationUtil;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.VersionUtil;

import java.lang.reflect.InvocationTargetException;

public class Study{

    protected static Study instance;
    protected static boolean valid;
    protected Context context;

    static Scheduler scheduler;
    static StudyStateMachine stateMachine;
    static Participant participant;
    static RestClient restClient;
    static MigrationUtil migrationUtil;

    public static synchronized void initialize(Context context) {
        instance = new Study(context);
    }

    public static synchronized Study getInstance() {
        return instance;
    }

    protected Study(Context context) {
        this.context = context;
    }

    public static boolean isValid(){
        if(instance==null){
            return false;
        }
        return valid;
    }

    // class registrations -------------------------------------------------------------------------

    public boolean registerParticipantType(Class tClass){
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

    public boolean registerRestApi(Class clientClass){
        return registerRestApi(clientClass, null);
    }

    public boolean registerRestApi(Class clientClass, Class apiClass){
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

    public  boolean registerStateMachine(Class tClass){
        if(tClass==null){
            return false;
        }
        if(!StudyStateMachine.class.isAssignableFrom(tClass)){
            return false;
        }
        try {
            stateMachine = (StudyStateMachine) tClass.newInstance();
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

    public void checkRegistrations(){
        if(participant==null){
            participant = new Participant();
        }
        if(scheduler==null){
            scheduler = new Scheduler();
        }
        if(stateMachine==null){
            stateMachine = new StudyStateMachine();
        }
        if(restClient==null){
            restClient = new RestClient(null);
        }
        if(migrationUtil==null){
            migrationUtil = new MigrationUtil();
        }
    }

    //  ----------------------------------------------------------------------------

    public void load(){

        checkRegistrations();

        boolean initialized = PreferencesManager.getInstance().getBoolean("initialized",false);
        if(!initialized){
            participant.initialize();
            participant.save();

            stateMachine.initialize();
            stateMachine.save();

            PreferencesManager.getInstance().putLong("libVersion",VersionUtil.getLibraryVersionCode());
            PreferencesManager.getInstance().putLong("appVersion",VersionUtil.getAppVersionCode());
            PreferencesManager.getInstance().putBoolean("initialized",true);

            HeartbeatManager.initialize(context);
            HeartbeatManager.getInstance().scheduleHeartbeat();

        }  else {
            migrationUtil.checkForUpdate();
            migrationUtil = null; // not needed after this

            stateMachine.load();
            participant.load();
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

    public static StudyStateMachine getStateMachine(){
        return stateMachine;
    }

    public static RestClient getRestClient(){
        return restClient;
    }

    // commonly used accessors ---------------------------------------------------------------------

    public static Visit getCurrentVisit(){
        return participant.getCurrentVisit();
    }

    public static TestSession getCurrentTestSession(){
        return participant.getCurrentTestSession();
    }

    public static PathSegmentData getCurrentSegmentData() {
        return stateMachine.state.segments.get(0).dataObject;
    }

    public static void setCurrentSegmentData(Object object) {
        stateMachine.state.segments.get(0).dataObject = (PathSegmentData) object;
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
}
