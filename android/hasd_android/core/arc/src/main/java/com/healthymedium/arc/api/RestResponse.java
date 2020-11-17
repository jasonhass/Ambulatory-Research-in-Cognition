//
// RestResponse.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.library.R;

import java.io.IOException;

import okhttp3.ResponseBody;

public class RestResponse {

    public int code;
    public boolean successful;
    public JsonObject optional = new JsonObject();
    public JsonObject errors = new JsonObject();

    public <T> T getOptionalAs(String key, Class<T> tClass){
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setLenient()
                .create();
        return gson.fromJson(optional.get(key), tClass);
    }

    public static RestResponse fromRetrofitResponse(retrofit2.Response<ResponseBody> retrofitResponse){
        RestResponse response = new RestResponse();

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
                response.errors.addProperty("show", Application.getInstance().getResources().getString(R.string.login_error3));
                response.errors.addProperty("format","Invalid response format received");
                response.successful = retrofitResponse.isSuccessful();
                return response;
            }

            JsonObject json;
            try {
                json = new JsonParser().parse(responseData).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                response.errors.addProperty("show", Application.getInstance().getResources().getString(R.string.login_error3));
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

    public static RestResponse fromRetrofitFailure(@Nullable Throwable throwable) {
        RestResponse response = new RestResponse();
        response.successful = false;
        response.code = -1;
        if(throwable!=null) {
            response.errors.addProperty("show", Application.getInstance().getResources().getString(R.string.login_error3));
            response.errors.addProperty(throwable.getClass().getSimpleName(),throwable.getMessage());
        }
        return response;
    }
}
