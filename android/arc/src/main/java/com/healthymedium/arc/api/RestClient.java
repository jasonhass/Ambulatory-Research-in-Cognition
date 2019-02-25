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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.healthymedium.arc.api.models.DeviceRegistration;
import com.healthymedium.arc.api.models.Heartbeat;
import com.healthymedium.arc.api.models.Response;
import com.healthymedium.arc.api.models.TestSchedule;
import com.healthymedium.arc.api.models.TestScheduleSession;
import com.healthymedium.arc.api.models.TestSubmission;
import com.healthymedium.arc.api.models.WakeSleepData;
import com.healthymedium.arc.api.models.WakeSleepSchedule;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.Device;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.CircadianRhythm;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.Visit;
import com.healthymedium.arc.utilities.VersionUtil;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.time.JodaUtil;
import com.healthymedium.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient <Api>{

    private Class<Api> type;
    private RestAPI service;
    private Api serviceExtension;
    protected Gson gson;

    private List<Object> uploadQueue = new ArrayList<>();
    private UploadListener uploadListener = null;
    private boolean uploading = false;
    private Handler handler;

    public RestClient(Class<Api> type) {
        this.type = type;
        initialize();
    }

    protected synchronized void initialize() {
        Log.i("RestClient","initialize");

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl httpUrl = request.url().newBuilder().build();
                request = request.newBuilder().url(httpUrl).build();
                return chain.proceed(request);
            }
        }).build();

        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .registerTypeAdapter(Double.class,new DoubleTypeAdapter())
                .setPrettyPrinting()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
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
            uploadQueue = new ArrayList<>(uploadData);
            Log.i("RestClient", "uploadQueue="+uploadQueue.toString());
        } else {
            uploadQueue = new ArrayList<>();
        }
    }

    public RestAPI getService() {
        return service;
    }

    public Api getServiceExtension() {
        return serviceExtension;
    }

    // public calls --------------------------------------------------------------------------------

    public void registerDevice(DeviceRegistration registration, final Listener listener){
        if(Config.REST_BLACKHOLE) {
            return;
        }
        Call<ResponseBody> call = getService().registerDevice(serialize(registration));
        call.enqueue(createCallback(listener));
    }

    public void registerDevice(String participantId, String authorizationCode, boolean existingUser, final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }
        Log.i("RestClient","registerDevice(existingUser="+existingUser+")");

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

    public void getSessionInfo(final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }
        Log.i("RestClient","getSessionInfo()");
        Call<ResponseBody> call = getService().getSessionInfo(Device.getId());
        call.enqueue(createCallback(listener));
    }

    public void sendHeartbeat(String participantId, final Listener listener ){
        if(Config.REST_BLACKHOLE || !Config.REST_HEARTBEAT) {
            return;
        }
        Log.i("RestClient","sendHeartbeat()");

        Heartbeat heartbeat = new Heartbeat();
        heartbeat.app_version = VersionUtil.getAppVersionName();
        heartbeat.device_id = Device.getId();
        heartbeat.device_info = Device.getInfo();
        heartbeat.participant_id = participantId;

        JsonObject json = serialize(heartbeat);
        Call<ResponseBody> call = getService().sendHeartbeat(Device.getId(), json);
        call.enqueue(createCallback(listener));
    }

    public void submitWakeSleepSchedule(){
        if(Config.REST_BLACKHOLE) {
            return;
        }
        Log.i("RestClient","submitWakeSleepSchedule()");

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
        Log.i("RestClient","submitTestSchedule()");

        Participant participant = Study.getParticipant();
        ParticipantState state = participant.getState();

        TestSchedule schedule = new TestSchedule();
        schedule.app_version = VersionUtil.getAppVersionName();
        schedule.device_id = Device.getId();
        schedule.device_info = Device.getInfo();
        schedule.participant_id = participant.getId();
        schedule.sessions = new ArrayList<>();

        for(Visit visit : state.visits){
            for(TestSession session : visit.getTestSessions()){
                TestScheduleSession scheduleSession = new TestScheduleSession();
                scheduleSession.session = session.getIndex()%visit.getNumberOfTests(session.getDayIndex());
                scheduleSession.session_id = String.valueOf(session.getId());
                scheduleSession.session_date = JodaUtil.toUtcDouble(session.getScheduledTime());
                scheduleSession.week = Weeks.weeksBetween(state.visits.get(0).getScheduledStartDate(),visit.getScheduledStartDate()).getWeeks();
                scheduleSession.day = session.getDayIndex();
                scheduleSession.types = new ArrayList<>();
                scheduleSession.types.add("cognitive");
                schedule.sessions.add(scheduleSession);
            }
        }

        if(uploading){
            uploadQueue.add(schedule);
            saveUploadQueue();
        } else {
            markUploadStarted();
            JsonObject json = serialize(schedule);
            Call<ResponseBody> call = getService().submitTestSchedule(Device.getId(), json);
            call.enqueue(createDataCallback(schedule));
        }
    }

    public void submitTest(TestSession session) {
        if(Config.REST_BLACKHOLE) {
            return;
        }
        TestSubmission test  = createTestSubmission(session);
        submitTest(test);
    }

    public void submitTest(TestSubmission test) {
        if(Config.REST_BLACKHOLE) {
            return;
        }
        Log.i("RestClient","submitTest(id="+test.session_id+")");
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

    public void enqueueTest(TestSession session)
    {
        if(Config.REST_BLACKHOLE) {
            return;
        }

        TestSubmission test = createTestSubmission(session);
        uploadQueue.add(test);
        saveUploadQueue();
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
        test.session_date = JodaUtil.toUtcDouble(session.getScheduledTime());
        if(session.getStartTime()!=null){
            test.start_time = JodaUtil.toUtcDouble(session.getStartTime());
        }
        test.day = session.getDayIndex();

        DateTime scheduledTime = session.getScheduledTime();
        List<Visit> visits =  Study.getParticipant().getState().visits;

        for(Visit visit : visits) {
            if (scheduledTime.isBefore(visit.getActualEndDate()) && scheduledTime.isAfter(visit.getActualStartDate())) {
                test.week = Weeks.weeksBetween(visits.get(0).getScheduledStartDate(),visit.getScheduledStartDate()).getWeeks();
                test.session = session.getIndex()%visit.getNumberOfTests(session.getDayIndex());
                break;
            }
        }

        test.missed_session = session.wasMissed() ? 1 : 0;
        test.finished_session = session.wasFinished() ? 1 : 0;
        test.interrupted = session.wasInterrupted() ? 1 : 0;
        test.tests = session.getTestData();

        return test;
    }

    protected Response parseResponse(retrofit2.Response<ResponseBody> retrofitResponse){
        Response response = new Response();

        if (response != null) {
            response.code = retrofitResponse.code();
            String responseData = "{}";
            try {
                if(retrofitResponse.isSuccessful()){
                    responseData = retrofitResponse.body().string();
                } else {
                    responseData = retrofitResponse.errorBody().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
                response.errors.addProperty("show","Sorry, our app is currently experiencing issues. Please try again later.");
                response.errors.addProperty("format","Invalid response format received");
                response.successful = retrofitResponse.isSuccessful();
                return response;
            }

            JsonObject json;
            try {
                json = new JsonParser().parse(responseData).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                response.errors.addProperty("show","Sorry, our app is currently experiencing issues. Please try again later.");
                response.errors.addProperty("unknown","Server Error "+response.code);
                return response;
            }

            JsonObject jsonResponse = json.getAsJsonObject("response");
            response.successful = jsonResponse.get("success").getAsBoolean();

            jsonResponse.remove("success");
            response.optional = jsonResponse;

            JsonObject jsonErrors = json.getAsJsonObject("errors");
            for(String key : jsonErrors.keySet()) {
                response.errors.add(key,jsonErrors.get(key));
            }
        }

        return response;
    }

    protected Response parseFailure(@Nullable Throwable throwable) {
        Response response = new Response();
        response.successful = false;
        response.code = -1;
        if(!isNetworkConnected()){
            response.errors.addProperty("network","No Network Connection");
        }
        if(throwable!=null) {
            response.errors.addProperty("show","Sorry, our app is currently experiencing issues. Please try again later.");
            response.errors.addProperty(throwable.getClass().getSimpleName(),throwable.getMessage());
        }
        return response;
    }

    protected Callback createCallback(@Nullable final Listener listener) {
        Log.i("RestClient","createCallback");
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> retrofitResponse) {
                if(listener==null){
                    return;
                }

                Response response = parseResponse(retrofitResponse);
                Log.i("RestClient",gson.toJson(response));
                if(response.successful){
                    Log.i("RestClient","onSuccess");
                    listener.onSuccess(response);
                } else {
                    Log.i("RestClient","onFailure");
                    listener.onFailure(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                if(listener==null){
                    return;
                }

                Response response = parseFailure(throwable);
                Log.i("RestClient",gson.toJson(response));
                Log.i("RestClient","onFailure");
                listener.onFailure(response);
            }
        };
    }

    protected Callback createDataCallback(final Object object) {
        Log.i("RestClient","createDataCallback");
        return createCallback(new Listener() {
            @Override
            public void onSuccess(Response response) {
                Log.i("RestClient","data callback success");
                if(uploadQueue.contains(object)) {
                    uploadQueue.remove(object);
                    saveUploadQueue();
                }
                popQueue();
            }

            @Override
            public void onFailure(Response response) {
                Log.i("RestClient","data callback failure");
                if(!uploadQueue.contains(object)){
                    uploadQueue.add(object);
                    saveUploadQueue();
                }
                markUploadStopped();
            }
        });
    }

    public void popQueue() {
        if(uploadQueue.size()>0){
            Object object = uploadQueue.get(0);
            JsonObject json = serialize(object);
            String deviceId = Device.getId();
            Call<ResponseBody> call = null;

            if(TestSchedule.class.isInstance(object)) {
                call = getService().submitTestSchedule(deviceId,json);
            } else if(WakeSleepSchedule.class.isInstance(object)) {
                call = getService().submitWakeSleepSchedule(deviceId,json);
            } else if(TestSubmission.class.isInstance(object)) {
                call = getService().submitTest(deviceId,json);
            }

            if(call!=null) {
                Log.i("RestClient", "popQueue("+object.getClass().getName()+")");
                markUploadStarted();
                call.enqueue(createDataCallback(object));
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
        Log.v("RestClient",gson.toJson(object));
        return element.getAsJsonObject();
    }

    protected void saveUploadQueue(){
        Log.i("RestClient","saveUploadQueue");
        PreferencesManager.getInstance().putObject("uploadQueue",uploadQueue.toArray());
        Log.i("RestClient",uploadQueue.toString());
    }

    public boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) Application.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void checkIfServerReachable(final ServerListener listener){
        if(listener==null){
            return;
        }
        if(!isNetworkConnected()) {
            listener.onFailed();
        }
        if(handler==null){
            handler = new Handler();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection urlConnection = new URL(Config.REST_ENDPOINT).openConnection();
                    urlConnection.setConnectTimeout(400);
                    urlConnection.connect();
                    listener.onReached();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    listener.onFailed();
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onFailed();
                }
            }
        });
    }

    private void markUploadStarted(){
        if(!uploading) {
            Log.i("RestClient","upload started");
            uploading = true;
            if (uploadListener != null) {
                uploadListener.onStart();
            }
        }
    }

    private void markUploadStopped(){
        if(uploading) {
            Log.i("RestClient","upload stopped");
            uploading = false;
            if (uploadListener != null) {
                uploadListener.onStop();
            }
        }
    }

    public boolean isUploadQueueEmpty(){
        return uploadQueue.size()==0;
    }

    public void setUploadListener(UploadListener listener){
        uploadListener = listener;
    }

    public void removeUploadListener(){
        uploadListener = null;
    }

    // listener interfaces -------------------------------------------------------------------------

    public interface Listener{
        void onSuccess(Response response);
        void onFailure(Response response);
    }

    public interface ServerListener{
        void onReached();
        void onFailed();
    }

    public interface UploadListener{
        void onStart();
        void onStop();
    }
}
