package com.dian.arc.libs.utilities;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.Context.ACTIVITY_SERVICE;

public class StatUtil {

    static public String getUserLoad() {
        if(!Config.RECORD_PERFORMANCE){
            return "0%";
        }

        java.lang.Process p = null;
        BufferedReader in = null;
        String string = null;
        try {
            p = Runtime.getRuntime().exec("top -n 1");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (string == null || string.contentEquals("")) {
                string = in.readLine();
            }
        } catch (IOException e) {
            Log.e("executeTop", "error in getting first line of top");
            e.printStackTrace();
        } finally {
            try {
                in.close();
                p.destroy();
            } catch (IOException e) {
                Log.e("executeTop","error in closing and destroying top process");
                e.printStackTrace();
            }
        }
        int load = 0;

        if(string == null) {
            string = "0%";
            //String[] strings = string.split("[ ,%]");
            //load = Integer.valueOf(strings[1]);
        }
        return string;
    }

    static public String getDeviceMemory(Context context){
        if(!Config.RECORD_PERFORMANCE){
            return " ";
        }

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return (mi.availMem/0x100000L)+"/"+(mi.totalMem/0x100000L)+" MB";
    }
}
