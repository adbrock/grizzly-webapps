package com.brocktek.farm.simple.monitoring;

public class XbeeFrameFactory {

	public static XbeeFrame build(byte[] frameData, long timestamp) {

		if (frameData.length < 4)
			return new XbeeInvalidFrame(frameData, timestamp);

		switch (frameData[3] & 0xFF) {
		case (0x92):
			return new XbeeDataSampleRxIndicatorFrame(frameData, timestamp);
		default:
			return new XbeeInvalidFrame(frameData, timestamp);
		}
	}
}
