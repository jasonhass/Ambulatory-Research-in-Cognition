package com.dian.arc.libs.rest.models;

import com.google.gson.annotations.SerializedName;

public class VerificationCodeRequest {

    @SerializedName("subject_id")
    private String arcId;

    public String getArcId() {
        return arcId;
    }

    public void setArcId(String arcId) {
        this.arcId = arcId;
    }
}
