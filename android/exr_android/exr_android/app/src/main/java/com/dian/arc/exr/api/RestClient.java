//
// RestClient.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.api;

import com.dian.arc.exr.api.models.CachedZip;
import com.dian.arc.exr.api.models.DeviceIdUpdate;
import com.google.gson.JsonObject;
import com.healthymedium.arc.api.CallbackChain;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.core.Device;
import com.healthymedium.arc.utilities.CacheManager;

import java.io.File;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class RestClient extends com.healthymedium.arc.api.RestClient<RestAPI> {

    public RestClient() {
        super(RestAPI.class);
    }

    @Override
    protected synchronized void initialize() {
        super.initialize();

        uploadBehaviorMap.put(CachedZip.class, new UploadBehavior() {
            @Override
            public Call callRequested(Object object, JsonObject json) {
                RequestBody requestBody = ((CachedZip)object).getRequestBody();
                return getServiceExtension().submitZipFile(Device.getId(),requestBody);
            }
        });
    }

    public void addUpdateDeviceIdLink(CallbackChain chain, String oldId) {
        DeviceIdUpdate idUpdate = new DeviceIdUpdate();
        idUpdate.new_device_id = Device.getId();
        JsonObject json = serialize(idUpdate);
        Call<ResponseBody> call = getServiceExtension().updateDeviceId(oldId,json);
        chain.addLink(call);
    }

    public void addContactLink(CallbackChain chain) {
        Call<ResponseBody> contactInfo = getService().getContactInfo(Device.getId());
        chain.addLink(contactInfo,contactListener);
    }

    public void addSessionInfoLink(CallbackChain chain, CallbackChain.Listener listener) {
        Call<ResponseBody> sessionInfo = getService().getSessionInfo(Device.getId());
        chain.addLink(sessionInfo, listener);
    }

    public void addZipUploadLink(CallbackChain chain, final File file) {
        CachedZip cachedZip = new CachedZip();
        cachedZip.filename = file.getName();
        Call<ResponseBody> call = getServiceExtension().submitZipFile(Device.getId(),cachedZip.getRequestBody());
        chain.addLink(call, new CallbackChain.Listener() {
            @Override
            public boolean onResponse(CallbackChain chain, RestResponse response) {
                CacheManager.getInstance().remove(file.getName());
                return true;
            }

            @Override
            public boolean onFailure(CallbackChain chain, RestResponse response) {
                return false;
            }
        });
    }

}
