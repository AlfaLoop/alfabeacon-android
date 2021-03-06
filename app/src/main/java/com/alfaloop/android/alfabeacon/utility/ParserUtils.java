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
package com.alfaloop.android.alfabeacon.utility;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Base64;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ParserUtils {
	final private static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	public static String convertAddressFormat(String address) {
		if (address == null)
			return "";
		if (address.length() != 12)
			return "";

		String macAddr = String.format("%s:%s:%s:%s:%s:%s", address.substring(0 , 2),
				address.substring(2 , 4),
				address.substring(4 , 6),
				address.substring(6 , 8),
				address.substring(8 , 10),
				address.substring(10 , 12));
		return macAddr;
	}

	public static String asHexStringFromAddress(String address) {
		String[] v = address.split(":");
		return  String.format("%s%s%s%s%s%s", v[0], v[1], v[2], v[3], v[4], v[5]);
	}

	public static String convertSemanticVersion(String code) {
		if (code.length() != 4)
			return "";

		String major = code.substring(1,2);
		String minor = code.substring(2,3);
		String patch = code.substring(3,4);
		return String.format("%s.%s.%s", major, minor, patch);
	}

	public static String parse(final BluetoothGattCharacteristic characteristic) {
		return parse(characteristic.getValue());
	}

	public static String parse(final BluetoothGattDescriptor descriptor) {
		return parse(descriptor.getValue());
	}

	public static String parse(final byte[] data) {
		if (data == null || data.length == 0)
			return "";

		final char[] out = new char[data.length * 3 - 1];
		for (int j = 0; j < data.length; j++) {
			int v = data[j] & 0xFF;
			out[j * 3] = HEX_ARRAY[v >>> 4];
			out[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
			if (j != data.length - 1)
				out[j * 3 + 2] = '-';
		}
		return "(0x) " + new String(out);
	}

	public static String parseDebug(final byte[] data) {
		if (data == null || data.length == 0)
			return "";

		final char[] out = new char[data.length * 2];
		for (int j = 0; j < data.length; j++) {
			int v = data[j] & 0xFF;
			out[j * 2] = HEX_ARRAY[v >>> 4];
			out[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return "0x" + new String(out);
	}

	public static byte[] getByteArrayFromGuid(String str)
	{
		UUID uuid = UUID.fromString(str);
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return bb.array();
	}

	public static long byteAsULong(byte b) {
		return ((long)b) & 0x00000000000000FFL;
	}

	public static long getUInt32(byte[] bytes) {
		long value = byteAsULong(bytes[0]) | (byteAsULong(bytes[1]) << 8) | (byteAsULong(bytes[2]) << 16) | (byteAsULong(bytes[3]) << 24);
		return value;
	}

	public static long getUInt32Big(byte[] bytes) {
		long value = byteAsULong(bytes[3]) | (byteAsULong(bytes[2]) << 8) | (byteAsULong(bytes[1]) << 16) | (byteAsULong(bytes[0]) << 24);
		return value;
	}

	public static byte calculateXor(byte[] data, int dataLength) {
		int  XOR = 0;

		for (int i = 0; i < dataLength; i++) {
			XOR = XOR ^ data[i];
		}
		return (byte)XOR;
	}

	public static int byteArrayToInteger(final byte[] byteArray)
	{
		return (byteArray[0] & 0xff) * 0x100 + (byteArray[1] & 0xff);
	}

	public static byte calculateXor(byte xorValue, byte[] data, int dataLength) {
		for (int i = 0; i < dataLength; i++) {
			xorValue = (byte) (xorValue ^ data[i]);
		}
		return (byte)xorValue;
	}

	public static String nativeStringDecode(String text) {
		String str = "";
		byte[] data = Base64.decode(text, Base64.DEFAULT);
		try {
			str = new String(data, "UTF-8");
		} catch (Exception ex) {
			return str;
		}
		return str;
	}
}
