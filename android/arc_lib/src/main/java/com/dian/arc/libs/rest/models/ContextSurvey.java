package com.dian.arc.libs.rest.models;

public class ContextSurvey {

    private Float alertness;
    private String location;
    private Float mood;
    private String recentActivity;
    private String whoIsWith;
    private String cpuLoad;
    private String deviceMemory;

    public void setCpuLoad(String cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public void setMemory(String memory) {
        this.deviceMemory = memory;
    }

    public Float getAlertness() {
        return alertness;
    }

    public void setAlertness(Float alertness) {
        this.alertness = alertness;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Float getMood() {
        return mood;
    }

    public void setMood(Float mood) {
        this.mood = mood;
    }

    public String getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(String recentActivity) {
        this.recentActivity = recentActivity;
    }

    public String getWhoIsWith() {
        return whoIsWith;
    }

    public void setWhoIsWith(String whoIsWith) {
        this.whoIsWith = whoIsWith;
    }
}
