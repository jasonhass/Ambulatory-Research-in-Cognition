package com.dian.arc.libs.rest.models;

import com.dian.arc.libs.utilities.Config;

import java.util.ArrayList;
import java.util.List;

public class TestSessionSchedule {

    private int version = Config.REST_API_VERSION;

    private String arcId;
    private String deviceId;
    private Integer visitId;
    private List<Session> sessions = new ArrayList<>();

    public String getArcId() {
        return arcId;
    }

    public void setArcId(String arcId) {
        this.arcId = arcId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getVisitId() {
        return visitId;
    }

    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

}
