package com.dian.arc.libs.rest;

import com.dian.arc.libs.rest.models.Registration;
import com.dian.arc.libs.rest.models.RegistrationTwoFactor;
import com.dian.arc.libs.rest.models.VerificationCodeRequest;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RestAPI {

    @POST("deviceRegistration")
    Call<ResponseBody> registerDevice(@Body Registration body);

    @POST("deviceRegistration")
    Call<ResponseBody> registerDevice(@Body RegistrationTwoFactor body);

    @POST("devicesubmit/{deviceId}")
    Call<ResponseBody> submitData(@Path("deviceId") String deviceId, @Body RequestBody file);

    @POST("verificationCode")
    Call<ResponseBody> requestVerificationCode(@Body VerificationCodeRequest body);

    @PUT("deviceheartbeat/{deviceId}")
    Call<ResponseBody> sendHeartbeat(@Path("deviceId") String deviceId);

}
