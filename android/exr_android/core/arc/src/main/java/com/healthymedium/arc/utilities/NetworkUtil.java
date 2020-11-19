//
// NetworkUtil.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.healthymedium.arc.core.Application;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class NetworkUtil {

    public static boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) Application.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void checkIfServerReachable(final URL url, final ServerListener listener){
        if(listener==null){
            return;
        }
        if(!isNetworkConnected()) {
            listener.onFailed();
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection urlConnection = url.openConnection();
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

    public interface ServerListener{
        void onReached();
        void onFailed();
    }

}
