//
// RestClient.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.healthymedium.arc.api.models.CachedObject;
import com.healthymedium.arc.api.models.DayProgress;
import com.healthymedium.arc.api.models.DeviceRegistration;
import com.healthymedium.arc.api.models.ExistingData;
import com.healthymedium.arc.api.models.Heartbeat;
import com.healthymedium.arc.api.models.CachedSignature;
import com.healthymedium.arc.api.models.SessionInfo;
import com.healthymedium.arc.api.models.SessionProgress;
import com.healthymedium.arc.api.models.TestSchedule;
import com.healthymedium.arc.api.models.TestScheduleSession;
import com.healthymedium.arc.api.models.TestSubmission;
import com.healthymedium.arc.api.models.WakeSleepData;
import com.healthymedium.arc.api.models.WakeSleepSchedule;
import com.healthymedium.arc.api.models.VerificationCodeRequest;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.Device;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.CircadianRhythm;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestDay;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.time.TimeUtil;
import com.healthymedium.arc.utilities.CacheManager;
import com.healthymedium.arc.utilities.Log;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.VersionUtil;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.healthymedium.arc.paths.home.HomeScreen.HINT_FIRST_TEST;
import static com.healthymedium.arc.paths.home.HomeScreen.HINT_POST_PAID_TEST;
import static com.healthymedium.arc.paths.home.HomeScreen.HINT_TOUR;
import static com.healthymedium.arc.paths.home.HomeScreen.HINT_POST_BASELINE;
import static com.healthymedium.arc.paths.templates.TestInfoTemplate.HINT_GRID_TUTORIAL;
import static com.healthymedium.arc.paths.templates.TestInfoTemplate.HINT_PRICES_TUTORIAL;
import static com.healthymedium.arc.paths.templates.TestInfoTemplate.HINT_REPEAT_TUTORIAL;
import static com.healthymedium.arc.paths.templates.TestInfoTemplate.HINT_SYMBOL_TUTORIAL;
import static com.healthymedium.arc.paths.tutorials.GridTutorial.HINT_PREVENT_TUTORIAL_CLOSE_GRIDS;
import static com.healthymedium.arc.paths.tutorials.PricesTutorial.HINT_PREVENT_TUTORIAL_CLOSE_PRICES;
import static com.healthymedium.arc.paths.tutorials.SymbolTutorial.HINT_PREVENT_TUTORIAL_CLOSE_SYMBOLS;
import static com.healthymedium.arc.study.Study.TAG_CONTACT_EMAIL;
import static com.healthymedium.arc.study.Study.TAG_CONTACT_INFO;

@SuppressWarnings("unchecked")
public class RestClient <Api>{

    private Class<Api> type;
    private RestAPI service;
    private Api serviceExtension;
    protected Gson gson;
    protected Retrofit retrofit;

    protected List<Object> uploadQueue = Collections.synchronizedList(new ArrayList<>());
    protected Map<Class, UploadBehavior> uploadBehaviorMap = Collections.synchronizedMap(new HashMap<Class, UploadBehavior>());
    private List<UploadListener> uploadListeners = new ArrayList<>();
    protected boolean uploading = false;

    public RestClient(Class<Api> type) {
        this.type = type;
        initialize();
    }

    protected synchronized void initialize() {


        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl httpUrl = request.url().newBuilder().build();
                request = request.newBuilder().url(httpUrl).build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = clientBuilder.build();

        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .registerTypeAdapter(Double.class,new DoubleTypeAdapter())
                .setPrettyPrinting()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Config.REST_ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = retrofit.create(RestAPI.class);
        if(type!=null){
            serviceExtension = retrofit.create(type);
        }

        if(PreferencesManager.getInstance().contains("uploadQueue")) {
            List uploadData = Arrays.asList(PreferencesManager.getInstance().getObject("uploadQueue", Object[].class));
            uploadQueue = Collections.synchronizedList(new ArrayList<>(uploadData));

        } else {
            uploadQueue = Collections.synchronizedList(new ArrayList<>());
        }

        uploadBehaviorMap.put(TestSchedule.class, new UploadBehavior() {
            @Override
            public Call callRequested(Object object, JsonObject json) {
                return getService().submitTestSchedule(Device.getId(),json);
            }
        });

        uploadBehaviorMap.put(WakeSleepSchedule.class, new UploadBehavior() {
            @Override
            public Call callRequested(Object object, JsonObject json) {
                return getService().submitWakeSleepSchedule(Device.getId(),json);
            }
        });

        uploadBehaviorMap.put(TestSubmission.class, new UploadBehavior() {
            @Override
            public Call callRequested(Object object, JsonObject json) {
                return getService().submitTest(Device.getId(),json);
            }
        });

        uploadBehaviorMap.put(CachedSignature.class, new UploadBehavior() {
            @Override
            public Call callRequested(Object object, JsonObject json) {
                RequestBody requestBody = ((CachedSignature)object).getRequestBody();
                return getService().submitSignature(requestBody,Device.getId());
            }
        });

    }

    public RestAPI getService() {
        return service;
    }

    public Api getServiceExtension() {
        return serviceExtension;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    // public calls --------------------------------------------------------------------------------

    public void registerDevice(final DeviceRegistration registration, final Listener listener){
        if(Config.REST_BLACKHOLE) {
            return;
        }

        CallbackChain chain = new CallbackChain(gson);

        Call<ResponseBody> call = getService().registerDevice(serialize(registration));
        chain.addLink(call);

        if(Config.CHECK_CONTACT_INFO){
            Call<ResponseBody> contactInfo = getService().getContactInfo(Device.getId());
            chain.addLink(contactInfo,contactListener);
        }

        if(Config.CHECK_SESSION_INFO) {
            Call<ResponseBody> sessionInfo = getService().getSessionInfo(Device.getId());
            chain.addLink(sessionInfo, sessionListener);
        }

        chain.execute(listener);
    }

    public void registerDevice(String participantId, String authorizationCode, boolean existingUser, final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        DeviceRegistration registration = new DeviceRegistration();
        registration.app_version = VersionUtil.getAppVersionName();
        registration.device_id = Device.getId();
        registration.device_info = Device.getInfo();
        registration.participant_id = participantId;
        registration.authorization_code = authorizationCode;
        if(existingUser){
            registration.override = Boolean.TRUE;
        }
        registerDevice(registration,listener);
    }

    public void requestVerificationCode(String participantId, Listener listener){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        VerificationCodeRequest request = new VerificationCodeRequest();
        request.setArcId(participantId);
        JsonObject json = serialize(request);

        Call<ResponseBody> call = getService().requestVerificationCode(json);
        call.enqueue(createCallback(listener));
    }

    public void getSessionInfo(final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }

        Call<ResponseBody> call = getService().getSessionInfo(Device.getId());
        call.enqueue(createCallback(listener));
    }

    public void sendHeartbeat(String participantId, final Listener listener ){
        if(Config.REST_BLACKHOLE || !Config.REST_HEARTBEAT) {
            return;
        }


        Heartbeat heartbeat = new Heartbeat();
        heartbeat.app_version = VersionUtil.getAppVersionName();
        heartbeat.device_id = Device.getId();
        heartbeat.device_info = Device.getInfo();
        heartbeat.participant_id = participantId;

        JsonObject json = serialize(heartbeat);
        Call<ResponseBody> call = getService().sendHeartbeat(Device.getId(), json);
        call.enqueue(createCallback(listener));
    }

    public void getStudyProgress(final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        Call<ResponseBody> call = getService().getStudyProgress(Device.getId());
        call.enqueue(createCallback(listener));
    }

    public void getCycleProgress(int index, final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        Call<ResponseBody> call = getService().getCycleProgress(Device.getId(),index);
        call.enqueue(createCallback(listener));
    }

    public void getCurrentCycleProgress(final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        Call<ResponseBody> call = getService().getCycleProgress(Device.getId(),null);
        call.enqueue(createCallback(listener));
    }

    public void getDayProgress(int cycleIndex, int dayIndex, final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        Call<ResponseBody> call = getService().getDayProgress(Device.getId(),cycleIndex,dayIndex);
        call.enqueue(createCallback(listener));
    }

    public void getCurrentDayProgress(final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        Call<ResponseBody> call = getService().getDayProgress(Device.getId(),null,null);
        call.enqueue(createCallback(listener));
    }

    public void getEarningOverview(int cycleIndex, int dayIndex, final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        Call<ResponseBody> call = getService().getEarningOverview(Device.getId(),cycleIndex,dayIndex);
        call.enqueue(createCallback(listener));
    }

    public void getEarningOverview(int cycleIndex, final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        Call<ResponseBody> call = getService().getEarningOverview(Device.getId(),cycleIndex,null);
        call.enqueue(createCallback(listener));
    }

    public void getEarningOverview(final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        Call<ResponseBody> call = getService().getEarningOverview(Device.getId(),null,null);
        call.enqueue(createCallback(listener));
    }

    public void getEarningDetails(final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        Call<ResponseBody> call = getService().getEarningDetails(Device.getId());
        call.enqueue(createCallback(listener));
    }

    public void submitWakeSleepSchedule(){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        Participant participant = Study.getParticipant();
        CircadianClock clock = participant.getCircadianClock();

        WakeSleepSchedule schedule = new WakeSleepSchedule();
        schedule.app_version = VersionUtil.getAppVersionName();
        schedule.device_id = Device.getId();
        schedule.device_info = Device.getInfo();
        schedule.participant_id = participant.getId();
        schedule.wakeSleepData = new ArrayList<>();

        for(CircadianRhythm rhythm : clock.getRhythms()) {
            WakeSleepData data = new WakeSleepData();
            data.bed = rhythm.getBedTime().toString("h:mm a");
            data.wake = rhythm.getWakeTime().toString("h:mm a");
            data.weekday = rhythm.getWeekday();
            schedule.wakeSleepData.add(data);
        }

        if(uploading){
            uploadQueue.add(schedule);
            saveUploadQueue();
        } else {
            markUploadStarted();
            JsonObject json = serialize(schedule);
            Call<ResponseBody> call = getService().submitWakeSleepSchedule(Device.getId(), json);
            call.enqueue(createDataCallback(schedule));
        }
    }

    public void submitTestSchedule(){
        if(Config.REST_BLACKHOLE) {
            return;
        }


        Participant participant = Study.getParticipant();
        ParticipantState state = participant.getState();

        TestSchedule schedule = new TestSchedule();
        schedule.app_version = VersionUtil.getAppVersionName();
        schedule.device_id = Device.getId();
        schedule.device_info = Device.getInfo();
        schedule.participant_id = participant.getId();
        schedule.sessions = new ArrayList<>();

        for(TestCycle cycle : state.testCycles){
            for(TestDay day : cycle.getTestDays()){
                for(TestSession session : day.getTestSessions()){
                    TestScheduleSession scheduleSession = new TestScheduleSession();
                    scheduleSession.session = session.getIndex();
                    scheduleSession.session_id = String.valueOf(session.getId());
                    scheduleSession.session_date = TimeUtil.toUtcDouble(session.getScheduledTime());
                    scheduleSession.week = Weeks.weeksBetween(state.testCycles.get(0).getScheduledStartDate(), session.getScheduledTime()).getWeeks();
                    scheduleSession.day = day.getDayIndex();
                    scheduleSession.types = new ArrayList<>();
                    scheduleSession.types.add("cognitive");
                    schedule.sessions.add(scheduleSession);
                }
            }
        }

        if(uploading) {
            uploadQueue.add(schedule);
            saveUploadQueue();
        } else {
            markUploadStarted();
            JsonObject json = serialize(schedule);
            Call<ResponseBody> call = getService().submitTestSchedule(Device.getId(), json);
            call.enqueue(createDataCallback(schedule));
        }
    }

    public void submitSignature(Bitmap bitmap) {

        if(Config.REST_BLACKHOLE) {
            return;
        }

        String key = "signature_" + DateTime.now().getMillis();
        CacheManager.getInstance().putBitmap(key,bitmap,100);

        CachedSignature signature = new CachedSignature();
        signature.filename = key;
        signature.participant_id = Study.getParticipant().getId();
        signature.session_id = String.valueOf(Study.getCurrentTestSession().getId());

        if(uploading) {

            uploadQueue.add(signature);
            saveUploadQueue();
        } else {

            markUploadStarted();
            Call<ResponseBody> call = getService().submitSignature(signature.getRequestBody(),Device.getId());
            call.enqueue(createCachedCallback(signature));
        }
    }

    public void submitTest(TestSession session) {
        if(Config.REST_BLACKHOLE) {
            return;
        }
        TestSubmission test = createTestSubmission(session);
        session.purgeData();
        submitTest(test);
    }

    public void submitTest(TestSubmission test) {
        if(Config.REST_BLACKHOLE) {
            return;
        }

        if(uploading){
            uploadQueue.add(test);
            saveUploadQueue();
        } else {
            markUploadStarted();
            JsonObject json = serialize(test);
            Call<ResponseBody> call = getService().submitTest(Device.getId(), json);
            call.enqueue(createDataCallback(test));
        }
    }

    // utility functions ---------------------------------------------------------------------------

    protected TestSubmission createTestSubmission(TestSession session)
    {
        TestSubmission test  = new TestSubmission();
        test.app_version = VersionUtil.getAppVersionName();
        test.device_id = Device.getId();
        test.device_info = Device.getInfo();
        test.participant_id = Study.getParticipant().getId();

        test.session_id = String.valueOf(session.getId());
        test.id = test.session_id;
        test.session_date = TimeUtil.toUtcDouble(session.getScheduledTime());
        if(session.getStartTime()!=null){
            test.start_time = TimeUtil.toUtcDouble(session.getStartTime());
        }
        test.day = session.getDayIndex();

        List<TestCycle> cycles =  Study.getParticipant().getState().testCycles;

        test.week = Weeks.weeksBetween(cycles.get(0).getScheduledStartDate(), session.getScheduledTime()).getWeeks();
        test.session = session.getIndex();

        test.missed_session = session.wasMissed() ? 1 : 0;
        test.finished_session = session.wasFinished() ? 1 : 0;
        test.interrupted = session.wasInterrupted();
        test.tests = session.getCopyOfTestData();

        return test;
    }

    // callback creation ---------------------------------------------------------------------------

    protected Callback createCallback(@Nullable final Listener listener) {

        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> retrofitResponse) {
                if(listener==null){
                    return;
                }

                RestResponse response = RestResponse.fromRetrofitResponse(retrofitResponse);

                if(response.successful){

                    listener.onSuccess(response);
                } else {

                    listener.onFailure(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                if(listener==null){
                    return;
                }

                RestResponse response = RestResponse.fromRetrofitFailure(throwable);


                listener.onFailure(response);
            }
        };
    }

    protected Callback createDataCallback(final Object object) {

        return createCallback(new Listener() {
            @Override
            public void onSuccess(RestResponse response) {

                if(uploadQueue.contains(object)) {
                    uploadQueue.remove(object);
                    saveUploadQueue();
                }
                popQueue();
            }

            @Override
            public void onFailure(RestResponse response) {

                if(!uploadQueue.contains(object)){
                    uploadQueue.add(object);
                    saveUploadQueue();
                }
                markUploadStopped();
            }
        });
    }

    protected Callback createCachedCallback(final CachedObject object) {

        return createCallback(new Listener() {
            @Override
            public void onSuccess(RestResponse response) {

                if(uploadQueue.contains(object)) {
                    uploadQueue.remove(object);
                    saveUploadQueue();
                }
                CacheManager.getInstance().remove(object.filename);
                popQueue();
            }

            @Override
            public void onFailure(RestResponse response) {

                if(!uploadQueue.contains(object)){
                    uploadQueue.add(object);
                    saveUploadQueue();
                }
                markUploadStopped();
            }
        });
    }

    protected CallbackChain.Listener contactListener = new CallbackChain.Listener() {
        @Override
        public boolean onResponse(CallbackChain chain, RestResponse response) {
            boolean success = false;
            if(response.successful) {
                if (response.optional.has("contact_info")) {
                    JsonObject contactJson = response.optional.get("contact_info").getAsJsonObject();
                    if (contactJson.has("phone")) {
                        String rawPhoneNumber = contactJson.get("phone").getAsString();
                        PreferencesManager.getInstance().putString(TAG_CONTACT_INFO, Phrase.formatPhoneNumber(rawPhoneNumber));
                        success = true;
                    }

                    if(contactJson.has("email")) {
                        PreferencesManager.getInstance().putString(TAG_CONTACT_EMAIL, contactJson.get("email").getAsString());
                        success = true;
                    }
                }
            }
            return success;
        }

        @Override
        public boolean onFailure(CallbackChain chain, RestResponse response) {
            return false;
        }
    };

    CallbackChain.Listener sessionListener = new CallbackChain.Listener() {
        @Override
        public boolean onResponse(CallbackChain chain, RestResponse response) {
            if(response.successful){

                JsonElement firstElement = response.optional.get("first_test");
                JsonElement latestElement = response.optional.get("latest_test");
                if(firstElement.isJsonNull()){
                    return true;
                }

                ExistingData existingData = new ExistingData();
                existingData.first_test = gson.fromJson(firstElement,SessionInfo.class);

                if(latestElement.isJsonNull()) {
                    existingData.latest_test = existingData.first_test;
                } else {
                    existingData.latest_test = gson.fromJson(latestElement,SessionInfo.class);
                }

                chain.setCachedObject(existingData);

                Call<ResponseBody> wakeSleepSchedule = getService().getWakeSleepSchedule(Device.getId());
                chain.addLink(wakeSleepSchedule, wakeSleepListener);

                return true;
            }
            return false;
        }

        @Override
        public boolean onFailure(CallbackChain chain, RestResponse response) {
            return false;
        }
    };

    CallbackChain.Listener wakeSleepListener = new CallbackChain.Listener() {
        @Override
        public boolean onResponse(CallbackChain chain, RestResponse response) {
            if(response.successful){

                JsonElement wakeSleepJson = response.optional.get("wake_sleep_schedule");
                if(wakeSleepJson.isJsonNull()){
                    return false;
                }

                ExistingData existingData = (ExistingData) chain.getCachedObject();
                existingData.wake_sleep_schedule = gson.fromJson(wakeSleepJson,WakeSleepSchedule.class);

                Call<ResponseBody> testScheduleCall = getService().getTestSchedule(Device.getId());
                chain.addLink(testScheduleCall,testScheduleListener);

                return true;
            }
            return false;
        }

        @Override
        public boolean onFailure(CallbackChain chain, RestResponse response) {
            return false;
        }
    };

    CallbackChain.Listener testScheduleListener = new CallbackChain.Listener() {
        @Override
        public boolean onResponse(CallbackChain chain, RestResponse response) {
            if(response.successful){

                JsonElement testSessionJson = response.optional.get("test_schedule");
                if(testSessionJson.isJsonNull()){
                    return false;
                }

                ExistingData existingData = (ExistingData) chain.getCachedObject();
                existingData.test_schedule = gson.fromJson(testSessionJson,TestSchedule.class);

                if(!existingData.isValid()){
                    return false;
                }

                ParticipantState state = Study.getScheduler().getExistingParticipantState(existingData);
                Study.getParticipant().setState(state);
                Study.getScheduler().scheduleNotifications(Study.getCurrentTestCycle(), false);

                if(Config.CHECK_PROGRESS_INFO) {
                    int cycle = state.currentTestCycle;
                    int day = state.currentTestDay;
                    if(state.currentTestSession==0) {
                        day--;
                    }
                    if(day>=0){
                        Call<ResponseBody> cycleProgress = getService().getDayProgress(Device.getId(), cycle, day);
                        chain.addLink(cycleProgress, progressListener);
                    }
                }

                Hints.markShown(HINT_PREVENT_TUTORIAL_CLOSE_GRIDS);
                Hints.markShown(HINT_PREVENT_TUTORIAL_CLOSE_PRICES);
                Hints.markShown(HINT_PREVENT_TUTORIAL_CLOSE_SYMBOLS);

                Hints.markShown(HINT_GRID_TUTORIAL);
                Hints.markShown(HINT_PRICES_TUTORIAL);
                Hints.markShown(HINT_SYMBOL_TUTORIAL);
                Hints.markShown(HINT_REPEAT_TUTORIAL);

                Hints.markShown(HINT_TOUR);
                Hints.markShown(HINT_POST_BASELINE);
                Hints.markShown(HINT_POST_PAID_TEST);
                Hints.markShown(HINT_FIRST_TEST);

                return true;
            }
            return false;
        }

        @Override
        public boolean onFailure(CallbackChain chain, RestResponse response) {
            return false;
        }
    };

    CallbackChain.Listener progressListener = new CallbackChain.Listener() {
        @Override
        public boolean onResponse(CallbackChain chain, RestResponse response) {
            if(response.successful){
                DayProgress progress = response.getOptionalAs("day_progress", DayProgress.class);
                List<TestSession> testSessions = Study.getParticipant().getState().testCycles.get(progress.cycle).getTestDay(progress.day).getTestSessions();
                for(SessionProgress sessionProgress : progress.sessions) {
                    TestSession session = testSessions.get(sessionProgress.session_index);
                    session.setProgress(sessionProgress.percent_complete);
                    switch (sessionProgress.status) {
                        case "not_yet_taken":
                            break;
                        case "incomplete":
                            session.markStarted();
                            session.markAbandoned();
                            break;
                        case "completed":
                            session.markCompleted();
                            break;
                        case "missed":
                            session.markMissed();
                            break;
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean onFailure(CallbackChain chain, RestResponse response) {
            return false;
        }
    };

    // upload queue --------------------------------------------------------------------------------

    public void popQueue() {
        if(uploadQueue.size()>0){
            Object object = uploadQueue.get(0);
            JsonObject json = serialize(object);
            String deviceId = Device.getId();
            Call<ResponseBody> call = null;

            Class key = object.getClass();
            if(uploadBehaviorMap.containsKey(key)) {
               call = uploadBehaviorMap.get(key).callRequested(object,json);
            }

            if(call!=null) {

                markUploadStarted();
                if(CachedObject.class.isAssignableFrom(object.getClass())){
                    call.enqueue(createCachedCallback((CachedObject) object));
                } else {
                    call.enqueue(createDataCallback(object));
                }

            } else {
                uploadQueue.remove(object);
                saveUploadQueue();
                popQueue();
            }
        } else {
            markUploadStopped();
        }
    }

    protected JsonObject serialize(Object object){
        JsonElement element = gson.toJsonTree(object);

        return element.getAsJsonObject();
    }

    protected void saveUploadQueue(){

        PreferencesManager.getInstance().putObject("uploadQueue",uploadQueue.toArray());

    }

    protected void markUploadStarted(){
        if(!uploading) {

            uploading = true;
            for(UploadListener uploadListener : uploadListeners){
                if (uploadListener != null) {
                    uploadListener.onStart();
                }
            }
        }
    }

    private void markUploadStopped(){
        if(uploading) {

            uploading = false;
            for(UploadListener uploadListener : uploadListeners){
                if (uploadListener != null) {
                    uploadListener.onStop();
                }
            }
        }
    }

    public boolean isUploadQueueEmpty(){
        return uploadQueue.size()==0;
    }

    public boolean isUploading(){
        return uploading;
    }

    public void addUploadListener(UploadListener listener){
        uploadListeners.add(listener);
    }

    public void removeUploadListener(UploadListener listener){
        uploadListeners.remove(listener);
    }

    // listener interfaces -------------------------------------------------------------------------

    public interface Listener{
        void onSuccess(RestResponse response);
        void onFailure(RestResponse response);
    }

    public interface UploadListener{
        void onStart();
        void onStop();
    }

    protected interface UploadBehavior {
        Call callRequested(Object object, JsonObject json);
    }
}
