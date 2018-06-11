package com.dian.arc.libs.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.dian.arc.libs.BaselineFragment;
import com.dian.arc.libs.BetweenFragment;
import com.dian.arc.libs.Chronotype0Fragment;
import com.dian.arc.libs.Chronotype1Fragment;
import com.dian.arc.libs.Chronotype2Fragment;
import com.dian.arc.libs.Chronotype3Fragment;
import com.dian.arc.libs.Chronotype4Fragment;
import com.dian.arc.libs.ConfirmTestFragment;
import com.dian.arc.libs.ContextFragment;
import com.dian.arc.libs.GridInstruction1Fragment;
import com.dian.arc.libs.GridInstruction2Fragment;
import com.dian.arc.libs.GridLettersFragment;
import com.dian.arc.libs.GridStudyFragment;
import com.dian.arc.libs.GridTestFragment;
import com.dian.arc.libs.HeartbeatReceiver;
import com.dian.arc.libs.IdVerificationFragment;
import com.dian.arc.libs.InterruptedFragment;
import com.dian.arc.libs.NoTestFragment;
import com.dian.arc.libs.PriceInstructionFragment;
import com.dian.arc.libs.PriceTestCompareFragment;
import com.dian.arc.libs.PriceTestMatchFragment;
import com.dian.arc.libs.SetupAvailabilityFragment;
import com.dian.arc.libs.SetupDateFragment;
import com.dian.arc.libs.SetupParticipantFragment;
import com.dian.arc.libs.SymbolInstructionFragment;
import com.dian.arc.libs.SymbolTestFragment;
import com.dian.arc.libs.TestInstructionFragment;
import com.dian.arc.libs.TestPopup1Fragment;
import com.dian.arc.libs.UpgradeFragment;
import com.dian.arc.libs.Wakeup0Fragment;
import com.dian.arc.libs.Wakeup1Fragment;
import com.dian.arc.libs.rest.models.Visit;
import com.dian.arc.libs.rest.RestClient;
import com.dian.arc.libs.rest.models.MissedTestSession;
import com.dian.arc.libs.rest.models.Session;
import com.dian.arc.libs.rest.models.TestSession;
import com.dian.arc.libs.rest.models.TestSessionSchedule;
import com.dian.arc.libs.rest.models.WakeSleepSchedule;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ArcManager {

    public static final int SETUP_INIT = 0;
    public static final int SETUP_NEW = 1;
    public static final int SETUP_EXISTING = 2;
    public static final int PATH_FIRST_DAY = 3;
    public static final int PATH_FIRST_BURST = 4;
    public static final int PATH_LAST_BURST = 5;
    public static final int PATH_OTHER = 6;
    public static final int PATH_BETWEEN = 7;
    public static final int PATH_NONE = 8;
    public static final int PATH_BASELINE = 9;
    public static final int PATH_AVAILABILITY = 10;
    public static final int PATH_OVER = 11;
    public static final int PATH_CONFIRM = 12;

    public static final int LIFECYCLE_INIT = 0;
    public static final int LIFECYCLE_TRIAL = 1;
    public static final int LIFECYCLE_BASELINE= 2;
    public static final int LIFECYCLE_IDLE = 3;
    public static final int LIFECYCLE_ARC = 4;
    public static final int LIFECYCLE_OVER = 5;

    private static ArcManager instance;
    private List<Fragment> fragments = new ArrayList<>();
    private State state;
    private Context context;

    // singleton methods ---------------------------------------------------------------------------

    public static synchronized void initialize(Context context) {
        instance = new ArcManager(context);
        instance.init();
    }

    public static synchronized ArcManager getInstance() {
        if(instance==null){
            instance = new ArcManager(ContextSingleton.getContext());
            instance.init();
        }
        return instance;
    }

    // constructor ---------------------------------------------------------------------------------

    private ArcManager(Context context) {
        this.context = context;
        state = PreferencesManager.getInstance().getObject("state",State.class);
        if(!state.initialized){
            state = new State();
            state.initialized = true;
            state.visits = new ArrayList<>();
            state.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            state.deviceInfo = "Android|"+getDeviceName()+"|"+ Build.VERSION.RELEASE+"|"+Build.VERSION.SDK_INT+"|"+Build.FINGERPRINT;
            if(Config.REST_HEARTBEAT){
                scheduleHeartbeatAlarm();
            }
            //scheduleUploadCheck(context);
        }
    }

    private void init(){
        if (isCurrentlyInTestSession()) {
            markTestAbandoned();
        } else {
            decidePath();
            setupPath();
        }
    }

    // state machine decisions ---------------------------------------------------------------------

    public void decidePath(){
        if(!currentArcIsValid()){
            return;
        }
            switch (state.lifecycle) {
                case LIFECYCLE_BASELINE:
                    if(state.currentPath != SETUP_EXISTING) {
                        state.currentPath = PATH_BASELINE;
                    }
                    break;
                case LIFECYCLE_TRIAL:
                case LIFECYCLE_ARC:
                    if (getCurrentVisit().getUserStartDate().isAfterNow()) {
                        state.currentPath = PATH_NONE;
                    } else if (getCurrentTestSession().getSessionDate().minusMinutes(5).isAfterNow()) {
                        state.currentPath = PATH_BETWEEN;
                    } else if (getCurrentTestSession().getExpirationDate().isBeforeNow()) {
                        markTestMissed();
                        decidePath();
                    } else if (state.currentTestSession == 0) {
                        state.currentPath = PATH_FIRST_BURST;
                        state.nextPath = PATH_BETWEEN;
                    } else if (state.currentTestSession == 27) {
                        state.currentPath = PATH_LAST_BURST;
                        state.nextPath = PATH_BASELINE;
                    } else if ((state.currentTestSession) % 4 == 0) {
                        state.currentPath = PATH_FIRST_DAY;
                    } else {
                        state.currentPath = PATH_OTHER;
                    }
                    break;
                case LIFECYCLE_IDLE:
                    if (getCurrentVisit().getUserStartDate().isBeforeNow()) {
                        state.lifecycle = LIFECYCLE_ARC;
                        decidePath();
                    } else if(getCurrentVisit().getUserStartDate().minusWeeks(4).isBeforeNow() && !getCurrentVisit().hasConfirmedDate()){
                        state.currentPath = PATH_CONFIRM;
                    }
                    break;
                case LIFECYCLE_OVER:
                    state.currentPath = PATH_OVER;
                    // send files if any remain
                    break;
            }
    }

    public void start(){
        if(fragments.size() > 0) {
            NavigationManager.getInstance().open(fragments.remove(0));
        }
    }

    public boolean nextFragment(boolean resetStepCount){
        if(fragments.size() > 0) {
            NavigationManager.getInstance().getFragmentManager().popBackStack();
            NavigationManager.getInstance().open(fragments.remove(0));
            if(resetStepCount){
                state.currentPathStepsTaken=0;
            } else {
                state.currentPathStepsTaken++;
            }
            saveState();
            return true;
        } else {
            state.currentPath = state.nextPath;
            state.currentPathStepsTaken = 0;
            state.nextPath = PATH_BETWEEN;
            setupPath();
            nextFragment(true);
        }
        return false;
    }

    public boolean nextFragment(){
        return nextFragment(false);
    }

    public void setupArcs(int currentVisit, DateTime studyStart){
        for(int i = state.currentVisit; i<currentVisit; i++){
            state.visits.add(new Visit(i-1));
        }
        state.currentVisit = currentVisit;

        for(int i=currentVisit;i<Config.ARC_INTERVALS.length;i++){
            state.visits.add(new Visit(i, studyStart));
            studyStart = studyStart.plusWeeks(Config.ARC_INTERVALS[i]);
        }
        saveState();
    }

    public void startTrial(){
        state.lifecycle = LIFECYCLE_TRIAL;
        state.visits = new ArrayList<>();
        state.visits.add(new Visit(0, JodaUtil.setMidnight(DateTime.now())));
        state.currentVisit = 0;
        state.currentTestSession = 0;
        state.currentPathStepsTaken = 0;
        saveState();
    }

    // state machine helpers ---------------------------------------------------------------------

    public void setPathEditAvailability(){
        state.currentPath = PATH_AVAILABILITY;
        state.nextPath = PATH_BETWEEN;
        fragments.clear();
        fragments.add(new SetupAvailabilityFragment());
    }

    public void setPathBaseline(){
        state.currentPath = PATH_BASELINE;
        state.nextPath = PATH_BASELINE;
        fragments.clear();
        fragments.add(new BaselineFragment());
        fragments.add(new SetupParticipantFragment());
    }

    public void setPathSetupInit(){
        state.currentPath = SETUP_INIT;
        fragments.clear();
        fragments.add(new SetupParticipantFragment());
    }

    public void setPathSetupNew(){
        state.currentPath = SETUP_NEW;
        state.nextPath = PATH_NONE;
        fragments.clear();
        fragments.add(new SetupAvailabilityFragment());
    }

    public void setPathConfirm(){
        state.currentPath = PATH_CONFIRM;
        fragments.clear();
        fragments.add(new ConfirmTestFragment());
    }

    public void setPathOver(){
        state.currentPath = PATH_OVER;
        state.nextPath = PATH_OVER;
        fragments.clear();
    }

    public void setPathSetupExisting(int visitId){
        Log.e("setPathSetupExisting","visitId="+visitId);
        state.currentPath = SETUP_EXISTING;
        //state.lifecycle = LIFECYCLE_ARC;
        fragments.clear();
        SetupDateFragment setup = new SetupDateFragment();
        setup.setVisitId(visitId);
        fragments.add(setup);
        fragments.add(new SetupAvailabilityFragment());

        for(int i = 0; i<state.currentPathStepsTaken; i++){
            fragments.remove(0);
        }
    }

    public void setPathTestFirstOfDay(){
        state.currentPath = PATH_FIRST_DAY;
        fragments.clear();
        fragments.add(IdVerificationFragment.create(true));
        fragments.add(new Wakeup0Fragment());
        fragments.add(new Wakeup1Fragment());
        fragments.add(new ContextFragment());
        fragments.add(new TestInstructionFragment());
        setupTestOrder();
        fragments.add(new InterruptedFragment());
        fragments.add(IdVerificationFragment.create(false));
    }

    public void setPathTestFirstOfBurst(){
        state.currentPath = PATH_FIRST_BURST;
        fragments.clear();
        fragments.add(IdVerificationFragment.create(true));
        fragments.add(new Chronotype0Fragment());
        fragments.add(new Chronotype1Fragment());
        fragments.add(new Chronotype2Fragment());
        fragments.add(new Chronotype3Fragment());
        fragments.add(new Chronotype4Fragment());
        fragments.add(new Wakeup0Fragment());
        fragments.add(new Wakeup1Fragment());
        fragments.add(new ContextFragment());
        fragments.add(new TestInstructionFragment());
        setupTestOrder();
        fragments.add(new InterruptedFragment());
        fragments.add(IdVerificationFragment.create(false));
    }

    public void setPathTestLastOfBurst(){
        state.currentPath = PATH_LAST_BURST;
        if(state.currentVisit ==1) {
            state.nextPath = PATH_BASELINE;
        } else {
            state.nextPath = PATH_NONE;
        }
        fragments.clear();
        fragments.add(IdVerificationFragment.create(true));
        fragments.add(new ContextFragment());
        fragments.add(new TestInstructionFragment());
        setupTestOrder();
        fragments.add(new InterruptedFragment());
        fragments.add(new UpgradeFragment());
        fragments.add(IdVerificationFragment.create(false));
    }

    public void setPathTestOther(){
        state.currentPath = PATH_OTHER;
        fragments.clear();
        fragments.add(IdVerificationFragment.create(true));
        fragments.add(new ContextFragment());
        fragments.add(new TestInstructionFragment());
        setupTestOrder();
        fragments.add(new InterruptedFragment());
        fragments.add(IdVerificationFragment.create(false));
    }

    public void setPathNoTests(){
        state.currentPath = PATH_NONE;
        state.currentPathStepsTaken = 0;
        fragments.clear();
        fragments.add(new NoTestFragment());
    }

    public void setPathBetweenTests(){
        state.currentPath = PATH_BETWEEN;
        state.currentPathStepsTaken = 0;
        fragments.clear();
        fragments.add(new BetweenFragment());
    }

    public void setupPath(){
        switch (state.currentPath){
            case SETUP_INIT:
                setPathSetupInit();
                break;
            case SETUP_NEW:
                setPathSetupNew();
                break;
            case SETUP_EXISTING:
                setPathSetupExisting(state.currentVisit);
                break;
            case PATH_FIRST_DAY:
                setPathTestFirstOfDay();
                break;
            case PATH_FIRST_BURST:
                setPathTestFirstOfBurst();
                break;
            case PATH_LAST_BURST:
                setPathTestLastOfBurst();
                break;
            case PATH_OTHER:
                setPathTestOther();
                break;
            case PATH_BETWEEN:
                setPathBetweenTests();
                break;
            case PATH_NONE:
                setPathNoTests();
                break;
            case PATH_AVAILABILITY:
                setPathEditAvailability();
                break;
            case PATH_BASELINE:
                setPathBaseline();
                break;
            case PATH_OVER:
                setPathOver();
                break;
            case PATH_CONFIRM:
                setPathConfirm();
                break;
        }
    }

    // state ---------------------------------------------------------------------------------------

    public class State{
        public boolean initialized;

        public int currentPathStepsTaken;
        public int currentPath;
        public int nextPath;

        public int currentTestSession;
        public int currentVisit;

        public int lifecycle;
        public String deviceInfo;

        String arcId;
        public String deviceId;
        String raterId;
        public List<Visit> visits;
        public WakeSleepSchedule wakeSleepSchedule;

        long studyStartDate;
        public double lastHeartbeat;

        public State(){
            currentPath = SETUP_INIT;
            lifecycle = LIFECYCLE_INIT;
            nextPath = PATH_BETWEEN;
            arcId = new String();
            raterId = new String();
            visits = new ArrayList<>();

        }
    }

    public void saveState(){
        PreferencesManager.getInstance().putObject("state",state);
    }

    // test methods --------------------------------------------------------------------------------

    public void setupTestOrder(){
        Integer[] orderArray = new Integer[]{1,2,3};
        List<Integer> order = Arrays.asList(orderArray);
        Collections.shuffle(order);
        for(int i =0;i<3;i++){
            switch(order.get(i)){
                case 1:
                    SymbolInstructionFragment fragmentSymbol = new SymbolInstructionFragment();
                    fragmentSymbol.order = i+1;
                    fragments.add(fragmentSymbol);
                    fragments.add(new SymbolTestFragment());
                    break;
                case 2:
                    PriceInstructionFragment fragmentPrice = new PriceInstructionFragment();
                    fragmentPrice.order = i+1;
                    fragments.add(fragmentPrice);
                    fragments.add(new PriceTestCompareFragment());
                    fragments.add(new PriceTestMatchFragment());
                    break;
                case 3:
                    GridInstruction1Fragment fragmentGrid1 = new GridInstruction1Fragment();
                    fragmentGrid1.order = i+1;
                    fragments.add(fragmentGrid1);
                    GridInstruction2Fragment fragmentGrid2 = new GridInstruction2Fragment();
                    fragmentGrid2.order = i+1;
                    fragments.add(fragmentGrid2);

                    fragments.add(new GridStudyFragment());
                    fragments.add(new GridLettersFragment());
                    fragments.add(new GridTestFragment());
                    fragments.add(new GridStudyFragment());
                    fragments.add(new GridLettersFragment());
                    GridTestFragment gridTestFragment = new GridTestFragment();
                    gridTestFragment.second = true;
                    fragments.add(gridTestFragment);
                    break;
            }
            if(i!=2){
                fragments.add(new TestPopup1Fragment());
            }
        }
    }

    public boolean isCurrentlyInTestSession(){
        if(state.lifecycle==LIFECYCLE_BASELINE){
            return false;
        }
        if(state.visits.size()<=state.currentVisit){
            return false;
        }
        if(state.lifecycle==LIFECYCLE_INIT || getCurrentVisit().getTestSessions().size()==0){
            return false;
        }
        return getCurrentTestSession().isCurrentlyInTestSession();
    }

    public boolean currentArcIsValid(){
        return state.visits.size()>state.currentVisit;
    }

    public void markTestStarted(){
        PreferencesManager.getInstance().putInt("test_missed_count", 0);
        getCurrentTestSession().setStartTime(DateTime.now());
        int sessionId = getCurrentTestSession().getSessionId();
        NotificationManager.getInstance().removeNotification(NotificationManager.TEST_MISSED,sessionId);
        saveState();
    }

    public void markTestMissed(){
        getCurrentTestSession().setMissedSession();
        TestSession session = getCurrentTestSession();
        MissedTestSession missed = new MissedTestSession();
        missed.setVisitId(session.getVisitId());
        missed.setDeviceId(getDeviceId());
        missed.setDeviceInfo(getDeviceInfo());
        missed.setSessionId(session.getSessionId());
        missed.setArcId(getArcId());
        missed.setSessionDate(session.getSessionDate());
        RestClient.sendMissedTestSession(missed);
        getCurrentTestSession().purgeData();
        incrementTestIndex();
    }

    public void markTestFinished(){
        getCurrentTestSession().setCompleted(true);
        RestClient.sendTestSession(getDeviceId(),getCurrentTestSession());
        getCurrentTestSession().purgeData();
        incrementTestIndex();
        decidePath();
        setupPath();
    }

    public void markTestAbandoned(){
        getCurrentTestSession().setCompleted(false);
        RestClient.sendAbandonedTestSession(getDeviceId(),getCurrentTestSession());
        getCurrentTestSession().purgeData();
        incrementTestIndex();
        decidePath();
        setupPath();
    }

    public void scheduleTests(){
        if(state.currentPath==PATH_AVAILABILITY) {
            getCurrentVisit().rescheduleTests(state);
            state.currentPath=PATH_BETWEEN;
            state.currentPathStepsTaken = 0;
            saveState();
        } else {
            getCurrentVisit().scheduleTests(state);
        }
        TestSessionSchedule schedule = new TestSessionSchedule();
        schedule.setDeviceId(getDeviceId());
        schedule.setVisitId(getCurrentVisit().getVisitId());
        schedule.setArcId(getArcId());
        List<Session> sessions = new ArrayList<>();
        for(TestSession session : getCurrentVisit().getTestSessions()){
            sessions.add(new Session(session.getSessionId(),JodaUtil.toUtcDouble(session.getSessionDate())));
        }
        schedule.setSessions(sessions);
        RestClient.sendTestSessionSchedule(schedule);
        saveState();
    }

    private void incrementTestIndex(){
        state.currentTestSession++;
        state.currentPathStepsTaken = 0;
        int max = getCurrentVisit().getTestSessions().size()-1;
        if(state.currentTestSession > max){
            SignatureManager.getInstance().purge();
            state.currentTestSession = 0;
            state.currentVisit++;
            if(state.currentVisit ==1){
                state.currentPath = PATH_BASELINE;
                state.lifecycle = LIFECYCLE_BASELINE;
            } else {
                scheduleTests();
                state.lifecycle = LIFECYCLE_IDLE;
                state.currentPath = PATH_NONE;
            }
            if(state.currentVisit >= Config.ARC_INTERVALS.length){
                state.lifecycle = LIFECYCLE_OVER;
                state.currentPath = PATH_OVER;
                state.currentVisit--;
                state.currentTestSession = 27;
            }
        }
        saveState();
    }

    // global accessors -----------------------------------------------------------------------------------

    public String getDeviceInfo(){
        return state.deviceInfo;
    }

    public String getDeviceId(){
        return state.deviceId;
    }

    public String getArcId(){
        return state.arcId;
    }

    public void setArcId(String id){
        state.arcId = id;
    }

    public String getRaterId(){
        return state.raterId;
    }

    public void setRaterId(String id){
        state.raterId = id;
    }

    public void setWakeSleepSchedule(WakeSleepSchedule schedule){
        state.wakeSleepSchedule = schedule;
        RestClient.sendWakeSleepData(schedule);
    }

    public WakeSleepSchedule getWakeSleepSchedule(){
        return state.wakeSleepSchedule;
    }

    public State getState(){
        return state;
    }

    public Visit getCurrentVisit(){
        return state.visits.get(state.currentVisit);
    }

    public TestSession getCurrentTestSession(){
        return getCurrentVisit().getTestSessions().get(state.currentTestSession);
    }

    public int getLifecycleStatus(){
        return state.lifecycle;
    }

    public void tryHeartbeat(){
        if(new DateTime((long)(state.lastHeartbeat*1000)).plusDays(1).isBeforeNow()) {
            Call<ResponseBody> call = RestClient.getService().sendHeartbeat(getDeviceId());
            call.enqueue(heartbeatCallback);
        }
    }

    private Callback heartbeatCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if(response != null){
                if(response.raw().code()==200){
                    state.lastHeartbeat = JodaUtil.toUtcDouble(DateTime.now());
                    saveState();
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    };

    public void scheduleHeartbeatAlarm(){
        Intent notificationIntent = new Intent(context, HeartbeatReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,AlarmManager.INTERVAL_HALF_DAY, AlarmManager.INTERVAL_HALF_DAY,pendingIntent);
    }

    public void cancelHeartbeatAlarm(){
        Intent notificationIntent = new Intent(context, HeartbeatReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

}
