package com.dian.arc.libs.rest.models;

import com.google.gson.annotations.SerializedName;

public class Registration {

    @SerializedName("subject_id")
    private String arcId;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("registrar_code")
    private int registrarCode;

    public String getArcId() {
        return arcId;
    }

    public void setArcId(String arcId) {
        this.arcId = arcId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setRegistrarCode(int registrarCode) {
        this.registrarCode = registrarCode;
    }

}
