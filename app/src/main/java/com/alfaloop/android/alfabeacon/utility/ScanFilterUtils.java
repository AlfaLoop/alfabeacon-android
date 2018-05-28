package com.alfaloop.android.alfabeacon.utility;

import android.util.Log;
import android.util.Pair;

import com.alfaloop.android.alfabeacon.models.AppleBeacon;
import com.polidea.rxandroidble2.scan.ScanResult;

import java.util.Arrays;
import java.util.UUID;

public class ScanFilterUtils {

    /*
    *  from package mobi.inthepocket.android.beacons.ibeaconscanner.utils;
    * */
    public static Pair<Boolean, AppleBeacon> isBeaconPattern(final ScanResult scanResult) {
        boolean isCorrect = false;
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
}
