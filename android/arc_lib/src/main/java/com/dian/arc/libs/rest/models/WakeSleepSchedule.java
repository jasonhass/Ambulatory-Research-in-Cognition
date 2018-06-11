package com.dian.arc.libs.rest.models;

import android.content.Context;

import com.dian.arc.libs.R;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.Config;

import java.util.ArrayList;
import java.util.List;

public class WakeSleepSchedule {

    private int version = Config.REST_API_VERSION;

    private String arcId;
    private String deviceId;
    private Double createdOn = new Double(0);
    private List<WakeSleepData> wakeSleepData = new ArrayList<>();

    public WakeSleepSchedule(Context context){
        arcId = ArcManager.getInstance().getArcId();
        deviceId = ArcManager.getInstance().getDeviceId();
        wakeSleepData.add(new WakeSleepData(context.getString(R.string.weekday_sunday)));
        wakeSleepData.add(new WakeSleepData(context.getString(R.string.weekday_monday)));
        wakeSleepData.add(new WakeSleepData(context.getString(R.string.weekday_tuesday)));
        wakeSleepData.add(new WakeSleepData(context.getString(R.string.weekday_wednesday)));
        wakeSleepData.add(new WakeSleepData(context.getString(R.string.weekday_thursday)));
        wakeSleepData.add(new WakeSleepData(context.getString(R.string.weekday_friday)));
        wakeSleepData.add(new WakeSleepData(context.getString(R.string.weekday_saturday)));
    }

    public void setCreatedOn(double createdOn){
        this.createdOn = createdOn;
    }

    public String getArcId() {
        return arcId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public List<WakeSleepData> getWakeSleepData() {
        return wakeSleepData;
    }

    public WakeSleepData get(int index){
        return wakeSleepData.get(index);
    }

    public WakeSleepData getSunnday() {
        return wakeSleepData.get(0);
    }

    public WakeSleepData getMonday() {
        return wakeSleepData.get(1);
    }

    public WakeSleepData getTuesday() {
        return wakeSleepData.get(2);
    }

    public WakeSleepData getWednesday() {
        return wakeSleepData.get(3);
    }

    public WakeSleepData getThursday() {
        return wakeSleepData.get(4);
    }

    public WakeSleepData getFriday() {
        return wakeSleepData.get(5);
    }

    public WakeSleepData getSaturday() {
        return wakeSleepData.get(6);
    }

}
