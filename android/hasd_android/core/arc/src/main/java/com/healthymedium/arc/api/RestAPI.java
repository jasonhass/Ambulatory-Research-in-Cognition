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

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestAPI {

    @POST("device-heartbeat")
    Call<ResponseBody> sendHeartbeat(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("device-registration")
    Call<ResponseBody> registerDevice(@Body JsonObject body);

    @POST("request-confirmation-code")
    Call<ResponseBody> requestVerificationCode(@Body JsonObject body);

    @GET("get-contact-info")
    Call<ResponseBody> getContactInfo(@Query("device_id") String deviceId);

    @GET("get-session-info")
    Call<ResponseBody> getSessionInfo(@Query("device_id") String deviceId);

    @GET("get-test-schedule")
    Call<ResponseBody> getTestSchedule(@Query("device_id") String deviceId);

    @GET("get-wake-sleep-schedule")
    Call<ResponseBody> getWakeSleepSchedule(@Query("device_id") String deviceId);

    @POST("signature-data")
    Call<ResponseBody> submitSignature(@Body RequestBody singatureData, @Query("device_id") String deviceId);

    @POST("submit-test")
    Call<ResponseBody> submitTest(@Query("device_id") String deviceId, @Body JsonObject test);

    @POST("submit-test-schedule")
    Call<ResponseBody> submitTestSchedule(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("submit-wake-sleep-schedule")
    Call<ResponseBody> submitWakeSleepSchedule(@Query("device_id") String deviceId, @Body JsonObject body);

    // progress ------------------------------------------------------------------------------------

    @GET("study-summary")
    Call<ResponseBody> getStudySummary(@Query("device_id") String deviceId);

    @GET("study-progress")
    Call<ResponseBody> getStudyProgress(@Query("device_id") String deviceId);

    @GET("cycle-progress")
    Call<ResponseBody> getCycleProgress(@Query("device_id") String deviceId, @Query("cycle") Integer cycle);

    @GET("day-progress")
    Call<ResponseBody> getDayProgress(@Query("device_id") String deviceId, @Query("cycle") Integer cycle, @Query("day") Integer day);

    // earnings ------------------------------------------------------------------------------------

    @GET("earning-overview")
    Call<ResponseBody> getEarningOverview(@Query("device_id") String deviceId, @Query("cycle") Integer cycle, @Query("day") Integer day);

    @GET("earning-details")
    Call<ResponseBody> getEarningDetails(@Query("device_id") String deviceId);

}
