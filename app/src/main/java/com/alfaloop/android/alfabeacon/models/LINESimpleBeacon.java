package com.alfaloop.android.alfabeacon.models;

public class LINESimpleBeacon {
    private String hwid;
    private String deviceMessage;

    public LINESimpleBeacon(String hwid, String deviceMessage) {
        this.hwid = hwid;
        this.deviceMessage = deviceMessage;
    }

    public String getHwid() {
        return hwid;
    }

    public void setHwid(String hwid) {
        this.hwid = hwid;
    }

    public String getDeviceMessage() {
        return deviceMessage;
    }

    public void setDeviceMessage(String deviceMessage) {
        this.deviceMessage = deviceMessage;
    }

    @Override
    public String toString() {
        return "LINESimpleBeacon{" +
                "hwid='" + hwid + '\'' +
                ", deviceMessage='" + deviceMessage + '\'' +
                '}';
    }
}
