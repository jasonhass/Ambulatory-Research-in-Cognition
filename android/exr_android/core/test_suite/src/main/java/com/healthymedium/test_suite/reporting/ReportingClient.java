//
// ReportingClient.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.reporting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.healthymedium.test_suite.core.TestReport;
import com.healthymedium.arc.api.DoubleTypeAdapter;
import com.healthymedium.arc.api.ItemTypeAdapterFactory;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.api.models.CachedImage;
import com.healthymedium.arc.api.models.CachedObject;
import com.healthymedium.arc.api.models.CachedSignature;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.utilities.CacheManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

@SuppressWarnings("unchecked")
public class ReportingClient{

    private static final String tag = "ReportingClient";

    private ReportingAPI service;
    protected Retrofit retrofit;
    protected Gson gson;

    protected List<Object> uploadQueue = Collections.synchronizedList(new ArrayList<>());
    private UploadListener uploadListener = null;
    protected boolean uploading = false;
    private Handler handler;

    public ReportingClient() {
        initialize();
    }

    protected synchronized void initialize() {
        Log.i(tag,"initialize");

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

        service = retrofit.create(ReportingAPI.class);

    }

    public ReportingAPI getService() {
        return service;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    // public calls --------------------------------------------------------------------------------

    public void submitReport(TestReport report){
        if(uploading){
            uploadQueue.add(report);
        } else {
            markUploadStarted();
            Call<ResponseBody> call = getService().submitReport(report);
            call.enqueue(createDataCallback(report));
        }
    }

    public void submitScreenshot(/*String reportId, String locale, */String name, Bitmap bitmap) {

        CacheManager.getInstance().putBitmap(name,bitmap,100);

        CachedImage cachedImage = new CachedSignature();
        cachedImage.filename = name;

        if(uploading) {
            Log.i(tag,"adding signature to upload queue");
            uploadQueue.add(cachedImage);
        } else {
            Log.i(tag,"uploading signature now");
            markUploadStarted();
            Call<ResponseBody> call = getService().submitScreenshot(cachedImage.getRequestBody());
            call.enqueue(createCachedCallback(cachedImage));
        }
    }

    // callback creation ---------------------------------------------------------------------------

    protected Callback createCallback(@Nullable final Listener listener) {
        Log.i(tag,"createCallback");
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> retrofitResponse) {
                if(listener==null){
                    return;
                }

                RestResponse response = RestResponse.fromRetrofitResponse(retrofitResponse);
                Log.i(tag,gson.toJson(response));
                if(response.successful){
                    Log.i(tag,"onSuccess");
                    listener.onSuccess(response);
                } else {
                    Log.i(tag,"onFailure");
                    listener.onFailure(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                if(listener==null){
                    return;
                }

                RestResponse response = RestResponse.fromRetrofitFailure(throwable);
                Log.i(tag,gson.toJson(response));
                Log.i(tag,"onFailure");
                listener.onFailure(response);
            }
        };
    }

    protected Callback createDataCallback(final Object object) {
        Log.i(tag,"createDataCallback");
        return createCallback(new Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                Log.i(tag,"data callback success");
                if(uploadQueue.contains(object)) {
                    uploadQueue.remove(object);
                }
                popQueue();
            }

            @Override
            public void onFailure(RestResponse response) {
                Log.i(tag,"data callback failure");
                if(!uploadQueue.contains(object)){
                    uploadQueue.add(object);
                }
                markUploadStopped();
            }
        });
    }

    protected Callback createCachedCallback(final CachedObject object) {
        Log.i(tag,"createCachedCallback");
        return createCallback(new Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                Log.i(tag,"cached callback success");
                if(uploadQueue.contains(object)) {
                    uploadQueue.remove(object);
                }
                CacheManager.getInstance().remove(object.filename);
                popQueue();
            }

            @Override
            public void onFailure(RestResponse response) {
                Log.i(tag,"cached callback failure");
                if(!uploadQueue.contains(object)){
                    uploadQueue.add(object);
                }
                markUploadStopped();
            }
        });
    }

    // upload queue --------------------------------------------------------------------------------

    public void popQueue() {
        if(uploadQueue.size()>0){
            Object object = uploadQueue.get(0);
            Call<ResponseBody> call = null;

            if(TestReport.class.isInstance(object)) {
                call = getService().submitReport((TestReport) object);
            } else if(CachedScreenshot.class.isInstance(object)) {
                RequestBody requestBody = ((CachedScreenshot)object).getRequestBody();
                call = getService().submitScreenshot(requestBody);
            }

            if(call!=null) {
                Log.i(tag, "popQueue("+object.getClass().getName()+")");
                markUploadStarted();
                if(CachedObject.class.isAssignableFrom(object.getClass())){
                    call.enqueue(createCachedCallback((CachedObject) object));
                } else {
                    call.enqueue(createDataCallback(object));
                }

            } else {
                uploadQueue.remove(object);
                popQueue();
            }
        } else {
            markUploadStopped();
        }
    }

    protected void markUploadStarted(){
        if(!uploading) {
            Log.i(tag,"upload started");
            uploading = true;
            if (uploadListener != null) {
                uploadListener.onStart();
            }
        }
    }

    private void markUploadStopped(){
        if(uploading) {
            Log.i(tag,"upload stopped");
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

    // connectivity helpers ------------------------------------------------------------------------

    @SuppressLint("MissingPermission")
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

    // listener interfaces -------------------------------------------------------------------------

    public interface Listener{
        void onSuccess(RestResponse response);
        void onFailure(RestResponse response);
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
