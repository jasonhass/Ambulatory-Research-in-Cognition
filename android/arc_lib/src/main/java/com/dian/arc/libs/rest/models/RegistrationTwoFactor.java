package com.dian.arc.libs.rest.models;

import com.google.gson.annotations.SerializedName;

public class RegistrationTwoFactor {

    @SerializedName("subject_id")
    private String arcId;

    @SerializedName("verification_code")
    private String verificationCode;

    @SerializedName("device_id")
    private String deviceId;

    public String getArcId() {
        return arcId;
    }

    public void setArcId(String arcId) {
        this.arcId = arcId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

}
