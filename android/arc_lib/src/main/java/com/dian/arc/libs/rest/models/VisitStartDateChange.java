package com.dian.arc.libs.rest.models;

import com.dian.arc.libs.utilities.Config;

public class VisitStartDateChange {

    private int version = Config.REST_API_VERSION;

    private String arcId;
    private String deviceId;
    private int visitId;
    private Double arcStartDate= new Double(0);
    private Double arcEndDate= new Double(0);
    private Double userStartDate= new Double(0);
    private Double userEndDate= new Double(0);

    public String getArcId() {
        return arcId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setArcId(String arcId) {
        this.arcId = arcId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setVisitId(int visitId) {
        this.visitId = visitId;
    }

    public void setArcStartDate(double arcStartDate) {
        this.arcStartDate = arcStartDate;
    }

    public void setArcEndDate(double arcEndDate) {
        this.arcEndDate = arcEndDate;
    }

    public void setUserStartDate(double userStartDate) {
        this.userStartDate = userStartDate;
    }

    public void setUserEndDate(double userEndDate) {
        this.userEndDate = userEndDate;
    }

}
