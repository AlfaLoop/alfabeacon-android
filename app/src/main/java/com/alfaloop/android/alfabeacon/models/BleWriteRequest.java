package com.alfaloop.android.alfabeacon.models;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.Arrays;

public class BleWriteRequest {
    private BluetoothGattCharacteristic characteristic;
    private byte[] data;

    public BleWriteRequest(BluetoothGattCharacteristic characteristic, byte[] data) {
        this.characteristic = characteristic;
        this.data = data;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BleWriteRequest{" +
                "characteristic=" + characteristic +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
