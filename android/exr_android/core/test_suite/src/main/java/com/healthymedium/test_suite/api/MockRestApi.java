//
// MockRestApi.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.api;

import android.support.annotation.RawRes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.healthymedium.test_suite.R;
import com.healthymedium.arc.api.RestAPI;
import com.healthymedium.arc.core.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

public abstract class MockRestApi implements RestAPI {

    private final BehaviorDelegate<RestAPI> delegate;
    MediaType mediaType = MediaType.parse("application/json");
    Gson gson;

    public MockRestApi(BehaviorDelegate<RestAPI> delegate, Gson gson) {
        this.delegate = delegate;
        this.gson = gson;
    }

    @Override
    public Call<ResponseBody> sendHeartbeat(String deviceId, JsonObject body) {
        ResponseBody response = getResponse(R.raw.json_heartbeat);
        return delegate.returningResponse(response).sendHeartbeat(deviceId,body);
    }

    @Override
    public Call<ResponseBody> registerDevice(JsonObject body) {
        ResponseBody response = getResponse(R.raw.json_heartbeat);
        return delegate.returningResponse(response).registerDevice(body);
    }

    @Override
    public Call<ResponseBody> getContactInfo(String deviceId) {
        ResponseBody response = getResponse(R.raw.json_contact_info);
        return delegate.returningResponse(response).getContactInfo(deviceId);
    }

    @Override
    public Call<ResponseBody> getSessionInfo(String deviceId) {
        JsonObject json = getJson(R.raw.json_session_info);

        ResponseBody response = getResponse(gson.toJson(json));
        return delegate.returningResponse(response).getSessionInfo(deviceId);
    }

    @Override
    public Call<ResponseBody> getTestSchedule(String deviceId) {
        return null;
    }

    @Override
    public Call<ResponseBody> getWakeSleepSchedule(String deviceId) {
        return null;
    }

    @Override
    public Call<ResponseBody> submitSignature(RequestBody singatureData, String deviceId) {
        ResponseBody response = getResponse(R.raw.json_okay);
        return delegate.returningResponse(response).submitSignature(singatureData,deviceId);
    }

    @Override
    public Call<ResponseBody> submitTest(String deviceId, JsonObject test) {
        ResponseBody response = getResponse(R.raw.json_okay);
        return delegate.returningResponse(response).submitTest(deviceId,test);
    }

    @Override
    public Call<ResponseBody> submitTestSchedule(String deviceId, JsonObject body) {
        ResponseBody response = getResponse(R.raw.json_okay);
        return delegate.returningResponse(response).submitTestSchedule(deviceId,body);
    }

    @Override
    public Call<ResponseBody> submitWakeSleepSchedule(String deviceId, JsonObject body) {
        ResponseBody response = getResponse(R.raw.json_okay);
        return delegate.returningResponse(response).submitWakeSleepSchedule(deviceId,body);
    }


    private ResponseBody getResponse(String string){
        return ResponseBody.create(mediaType,string);
    }

    private ResponseBody getResponse(@RawRes int id){
        return ResponseBody.create(mediaType,getString(id));
    }

    private JsonObject getJson(@RawRes int id){
        return gson.fromJson(getString(id), JsonObject.class);
    }

    private String getString(@RawRes int id){
        InputStream is = Application.getInstance().getResources().openRawResource(id);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            is.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }
}
