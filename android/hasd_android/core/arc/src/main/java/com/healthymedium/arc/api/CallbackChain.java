//
// CallbackChain.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api;



import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class CallbackChain {

    private static final String tag = "CallbackChain";

    AtomicBoolean stopped = new AtomicBoolean(false);

    
    List<Link> links = Collections.synchronizedList(new ArrayList<Link>());
    RestClient.Listener clientListener;

    AtomicReference<Object> cache = new AtomicReference<>();
    Object cachedObject = new Object();

    Gson gson;

    public CallbackChain(Gson gson){
        this.gson = gson;
        cache.set(cachedObject);
    }

    public Object getCachedObject(){
        return cache.get();
    }

    public void setCachedObject(Object object){
        cache.compareAndSet(cachedObject,object);
    }

    public boolean addLink(Call call){
        return addLink(call,null);
    }

    public boolean addLink(Call call, Listener listener){
        if(call==null){
            return false;
        }
        Link link = new Link();
        link.call = call;
        link.listener = listener;
        links.add(link);
        return true;
    }

    public boolean addLink(ShallowListener listener){
        ShallowLink link = new ShallowLink();
        link.listener = listener;
        links.add(link);
        return true;
    }

    public void execute(RestClient.Listener clientListener){
        this.clientListener = clientListener;
        RestResponse response = new RestResponse();
        response.successful = true;
        response.code = 200;
        handleTail(response);
    }

    public void stop() {
        if(links.size()>0){
            stopped.compareAndSet(false,true);
        }
    }

    private Callback callback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> retrofitResponse) {

            RestResponse response = RestResponse.fromRetrofitResponse(retrofitResponse);


            if(links.size()==0){

                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            Link link = links.remove(0);

            boolean proceed;
            if(link.listener!=null) {
                if(response.successful) {
                    proceed = link.listener.onResponse(CallbackChain.this,response);
                } else {
                    proceed = link.listener.onFailure(CallbackChain.this,response);
                }
            } else {
                proceed = response.successful;
            }

            if(!proceed){

                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            handleTail(response);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            RestResponse response = RestResponse.fromRetrofitFailure(throwable);


            if(links.size()==0){

                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            Link link = links.remove(0);

            boolean proceed;
            if(link.listener!=null) {
                if(response.successful) {
                    proceed = link.listener.onResponse(CallbackChain.this,response);
                } else {
                    proceed = link.listener.onFailure(CallbackChain.this,response);
                }
            } else {
                proceed = response.successful;
            }

            if(!proceed){

                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            handleTail(response);
        }
    };

    private void handleTail(RestResponse response) {


        if(links.size()==0) {

            if(clientListener!=null) {
                clientListener.onSuccess(response);
            }
            return;
        }

        if(stopped.get()){

            stopped.compareAndSet(true,false);
            return;
        }

        if(links.get(0) instanceof ShallowLink) {

            ((ShallowLink)links.remove(0)).listener.onExecute(CallbackChain.this);
            handleTail(response);
            return;
        }

        if(links.get(0).call==null) {

            if(clientListener!=null) {
                clientListener.onFailure(response);
            }
            return;
        }


        links.get(0).call.enqueue(callback);
    }

    private class Link {
        Call call;
        Listener listener;
    }

    public interface Listener {
        boolean onResponse(CallbackChain chain, RestResponse response);
        boolean onFailure(CallbackChain chain, RestResponse response);
    }

    private class ShallowLink extends Link {
        ShallowListener listener;
    }

    public interface ShallowListener {
        void onExecute(CallbackChain chain);
    }
}
