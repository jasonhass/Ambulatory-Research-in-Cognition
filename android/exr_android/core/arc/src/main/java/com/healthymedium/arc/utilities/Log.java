//
// Log.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Log {

    private static FileOutputStream stream;
    private static boolean logSystemOut = false;

    public static void pointToLogcat(){
        logSystemOut = false;
    }

    public static void pointToSystemOut(){
        logSystemOut = true;
    }

    public static String format(String level, String tag, String msg) {
        String time = DateTime.now().toString();
        return time+"/"+level+"/"+tag+": "+msg+"\n";
    }

    public static String filename(){
        return Application.getInstance().getCacheDir()+"/Log";
    }

    private static boolean checkStream(){
        if(stream!=null){
            return true;
        }
        if(Application.getInstance()==null){
           return false;
        }
        File logFile = new File(filename());
        if(!logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            stream = new FileOutputStream(logFile,true);
            String divider = "--------------------\n";
            stream.write(divider.getBytes());
            stream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static void writeToFile(String level, String tag, String msg) {
        String output = format(level,tag,msg);
        if(checkStream()){
            try {
                stream.write(output.getBytes());
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeToSystemOut(String level, String tag, String msg){
        String output = format(level,tag,msg);
        System.out.print(output);
    }

    public static int v(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("V",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("V",tag,msg);
            return 0;
        }
        return android.util.Log.v(tag,msg);
    }

    public static int d(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("D",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("D",tag,msg);
            return 0;
        }
        return android.util.Log.d(tag,msg);
    }

    public static int i(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("I",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("I",tag,msg);
            return 0;
        }
        return android.util.Log.d(tag,msg);

    }

    public static int w(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("W",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("W",tag,msg);
            return 0;
        }
        return android.util.Log.d(tag,msg);
    }

    public static int e(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("E",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("E",tag,msg);
            return 0;
        }
        return android.util.Log.d(tag,msg);
    }

    public static int wtf(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("WTF",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("WTF",tag,msg);
            return 0;
        }
        return android.util.Log.wtf(tag,msg);
    }

    public static int wtf(String tag, Throwable throwable) {
        String msg = getStackTraceString(throwable);
        return wtf(tag,msg);
    }

    public static String getStackTraceString(Throwable throwable) {
        return android.util.Log.getStackTraceString(throwable);
    }

    // add other methods if required...
}
