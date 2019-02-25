//
// RestAPI.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestAPI {

    @POST("device-registration")
    Call<ResponseBody> registerDevice(@Body JsonObject body);

    @POST("device-heartbeat")
    Call<ResponseBody> sendHeartbeat(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("submit-wake-sleep-schedule")
    Call<ResponseBody> submitWakeSleepSchedule(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("submit-test-schedule")
    Call<ResponseBody> submitTestSchedule(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("submit-test")
    Call<ResponseBody> submitTest(@Query("device_id") String deviceId, @Body JsonObject test);

    @GET("get-session-info")
    Call<ResponseBody> getSessionInfo(@Query("device_id") String deviceId);

}
