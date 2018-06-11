package com.dian.arc.libs.rest.models;

import com.dian.arc.libs.utilities.Config;
import com.dian.arc.libs.utilities.JodaUtil;

import org.joda.time.DateTime;

public class MissedTestSession {

    private int version = Config.REST_API_VERSION;

    private String arcId;
    private String deviceId;
    private int visitId;
    private int sessionId;
    private Double sessionDate = new Double(0);
    private Double expirationDate = new Double(0);

    private int finishedSession = 0;
    private int missedSession = 1;
    private int interrupted = 0;
    private int willUpgradePhone = 0;

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    private String deviceInfo;

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

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public void setSessionDate(DateTime sessionDate) {
        this.sessionDate = JodaUtil.toUtcDouble(sessionDate);
    }

}
