/**
 * © Copyright AlfaLoop Technology Co., Ltd. 2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package com.alfaloop.android.alfabeacon.models;


import java.util.Date;

public class LeBeacon {
    public static final int LEBEACON_TYPE_AA = 0;
    public static final int LEBEACON_TYPE_USB = 1;
    public static final int LEBEACON_TYPE_2477 = 2;
    public static final int LEBEACON_TYPE_2477S = 3;

    private int type;
    private String deviceName;
    private String macAddress;
    private int rssi;
    private int batteryLevel;
    private Date datetime;
    private AppleBeacon iBeacon = null;

    public LeBeacon(int type, String deviceName, String macAddress, int rssi, int battery, Date datetime) {
        this.type = type;
        this.deviceName = deviceName;
        this.macAddress = macAddress;
        this.rssi = rssi;
        this.batteryLevel = battery;
        this.datetime = datetime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public AppleBeacon getiBeacon() {
        return iBeacon;
    }

    public void setiBeacon(AppleBeacon iBeacon) {
        this.iBeacon = iBeacon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeBeacon leBeacon = (LeBeacon) o;

        if (type != leBeacon.type) return false;
        return macAddress.equals(leBeacon.macAddress);
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + macAddress.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LeBeacon{" +
                "type=" + type +
                ", deviceName='" + deviceName + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", rssi=" + rssi +
                ", batteryLevel=" + batteryLevel +
                ", datetime=" + datetime +
                ", iBeacon=" + iBeacon +
                '}';
    }
}