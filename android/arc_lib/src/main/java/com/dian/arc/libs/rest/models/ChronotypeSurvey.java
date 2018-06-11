package com.dian.arc.libs.rest.models;

public class ChronotypeSurvey {

    public int numWorkDays;
    public int wasShiftWorker;
    public String workFreeSleepTime;
    public String workFreeWakeTime;
    public String workSleepTime;
    public String workWakeTime;

    public String cpuLoad;
    public String deviceMemory;

    public ChronotypeSurvey(){
        workFreeSleepTime = new String();
        workFreeWakeTime = new String();
        workSleepTime = new String();
        workWakeTime = new String();
        cpuLoad = new String();
        deviceMemory = new String();
    }
}
