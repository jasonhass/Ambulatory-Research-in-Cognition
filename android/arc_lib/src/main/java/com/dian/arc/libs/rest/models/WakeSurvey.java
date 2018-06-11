package com.dian.arc.libs.rest.models;

public class WakeSurvey {

    private String bedTime;
    private String getUpTime;
    private int numWakes;
    private float sleepQuality;
    private String sleepTime;
    private String wakeTime;
    private String cpuLoad;
    private String deviceMemory;

    public void setCpuLoad(String cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public void setMemory(String memory) {
        this.deviceMemory = memory;
    }

    public String getBedTime() {
        return bedTime;
    }

    public void setBedTime(String bedTime) {
        this.bedTime = bedTime;
    }

    public String getGetUpTime() {
        return getUpTime;
    }

    public void setGetUpTime(String getUpTime) {
        this.getUpTime = getUpTime;
    }

    public Integer getNumWakes() {
        return numWakes;
    }

    public void setNumWakes(Integer numWakes) {
        this.numWakes = numWakes;
    }

    public Float getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(Float sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public String getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getWakeTime() {
        return wakeTime;
    }

    public void setWakeTime(String wakeTime) {
        this.wakeTime = wakeTime;
    }
}
