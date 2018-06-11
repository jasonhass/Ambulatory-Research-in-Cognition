package com.dian.arc.libs.rest.models;

public class WakeSleepData {

    private String bed;
    private String wake;
    private String weekday;

    WakeSleepData(String weekday){
        this.weekday = weekday;
    }

    public String getBedTime() {
        return bed;
    }

    public void setBedTime(String bed) {
        this.bed = bed;
    }

    public String getWakeTime() {
        return wake;
    }

    public void setWakeTime(String wake) {
        this.wake = wake;
    }

    public String getWeekday() {
        return weekday;
    }

}
