package com.alfaloop.android.alfabeacon.utility;

import android.os.ParcelUuid;
import android.util.Log;
import android.util.Pair;

import com.alfaloop.android.alfabeacon.fragment.ScannerFragment;
import com.alfaloop.android.alfabeacon.models.AppleBeacon;
import com.alfaloop.android.alfabeacon.models.LINESimpleBeacon;
import com.polidea.rxandroidble2.scan.ScanRecord;
import com.polidea.rxandroidble2.scan.ScanResult;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ScanFilterUtils {
    public static final String TAG = ScanFilterUtils.class.getSimpleName();

    private static UUID LSB_UUID = UUID.fromString("0000fe6f-0000-1000-8000-00805f9b34fb");

    /*
    *  from package mobi.inthepocket.android.beacons.ibeaconscanner.utils;
    * */
    public static Pair<Boolean, AppleBeacon> isAppleBeaconPattern(final ScanResult scanResult) {
        final byte[] bytes = scanResult.getScanRecord().getManufacturerSpecificData(76); // Apple iBeacon 0x004C
        if (bytes != null) {
            if (bytes.length == 23) {
                if ((int)bytes[0] == 2 && (int)bytes[1] == 21) {
                    // identifies an iBeacon
                    final byte[] uuidBytes = new byte[16];
                    System.arraycopy(bytes, 2, uuidBytes, 0, 16);
                    final UUID uuid = UuidUtils.asUuidFromByteArray(uuidBytes);
                    final String uuidString = uuid.toString();
                    final int major = ParserUtils.byteArrayToInteger(Arrays.copyOfRange(bytes, 18, 20));
                    final int minor = ParserUtils.byteArrayToInteger(Arrays.copyOfRange(bytes, 20, 22));
                    final int txMeter = (int)bytes[22];
                    AppleBeacon iBeacon = new AppleBeacon(uuidString, major, minor, txMeter);
                    return new Pair<>(true, iBeacon);
                }
            }
        }
        return new Pair<>(false, null);
    }

    public static Pair<Boolean, LINESimpleBeacon> isLineSimpleBeaconPattern(final ScanResult scanResult) {
        ScanRecord record = scanResult.getScanRecord();
        if (record == null)
            return new Pair<>(false, null);
        List<ParcelUuid> advUuids = record.getServiceUuids();
        if (advUuids == null)
            return new Pair<>(false, null);

        for (ParcelUuid u : advUuids) {
            if (u.getUuid().equals(LSB_UUID)){
                byte[] sdata = record.getServiceData(u);
                if (sdata.length == 20) {
                    Log.d(TAG, String.format("LSB Data %s", ParserUtils.bytesToHex(sdata)));
                    final byte[] hwid = new byte[5];
                    System.arraycopy(sdata, 1, hwid, 0, 5);
                    final byte[] dm = new byte[13];
                    System.arraycopy(sdata, 7, dm, 0, 13);
                    LINESimpleBeacon beacon = new LINESimpleBeacon(ParserUtils.bytesToHex(hwid), ParserUtils.bytesToHex(dm));
                    return new Pair<>(true, beacon);
                }
            }
        }
        return new Pair<>(false, null);
    }
}
