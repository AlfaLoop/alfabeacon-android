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

public class AppleBeacon {
    private String uuid;
    private int major;
    private int minor;
    private int txInMeter;

    public AppleBeacon(String uuid, int major, int minor, int txInMeter) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.txInMeter = txInMeter;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getTxInMeter() {
        return txInMeter;
    }

    public void setTxInMeter(int txInMeter) {
        this.txInMeter = txInMeter;
    }

    @Override
    public String toString() {
        return "AppleBeacon{" +
                "uuid='" + uuid + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", txInMeter=" + txInMeter +
                '}';
    }
}