package com.brocktek.farm.simple.monitoring;

import java.util.Arrays;

public class XbeeFrame {
	public static final int DELIMITER = 0x7E;
	public static final int ESCAPE = 0x7D;

	private byte[] frameData;
	private long timestamp;

	public XbeeFrame(byte[] frameData, long timestamp) {
		this.frameData = frameData;
		this.timestamp = timestamp;
	}

	public byte[] getFrameData() {
		return this.frameData;
	}

	public int getLength() {
		if (frameData.length > 2) {
			int msb = (frameData[1] & 0xFF) * 256;
			int lsb = (frameData[2] & 0xFF);
			return msb + lsb;
		} else {
			return 0;
		}
	}

	public boolean verify() {
		if (frameData.length - 4 != getLength())
			return false;

		int sum = 0;
		for (int i = 3; i < frameData.length; i++) {
			sum += frameData[i] & 0xFF;
		}
		return ((sum << -8 >>> -8) == 0xFF) ? true : false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (byte dataByte : frameData) {
			builder.append(String.format("%2s", Integer.toHexString(dataByte & 0xFF)).replace(" ", "0") + " ");
		}
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(frameData);
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XbeeFrame other = (XbeeFrame) obj;
		if (!Arrays.equals(frameData, other.frameData))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	public Class<? extends XbeeFrame> getType() {
		return this.getClass();
	}
}
