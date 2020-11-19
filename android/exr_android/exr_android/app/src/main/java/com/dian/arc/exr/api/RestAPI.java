//
// RestAPI.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.api;

import com.dian.arc.exr.api.models.DeviceIdUpdate;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestAPI {

    @POST("update-device-id")
    Call<ResponseBody> updateDeviceId(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("submit-zip")
    Call<ResponseBody> submitZipFile(@Query("device_id") String deviceId, @Body RequestBody file);

}
