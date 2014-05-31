package com.brocktek.farm.simple.monitoring;

public class XbeeDataSampleRxIndicatorFrame extends XbeeFrame {

	public XbeeDataSampleRxIndicatorFrame(byte[] frameData, long timestamp) {
		super(frameData, timestamp);
	}

	public long getAddress() {
		long l = 0;
		l |= getFrameData()[4] & 0xFF;
		l <<= 8;
		l |= getFrameData()[5] & 0xFF;
		l <<= 8;
		l |= getFrameData()[6] & 0xFF;
		l <<= 8;
		l |= getFrameData()[7] & 0xFF;
		l <<= 8;
		l |= getFrameData()[8] & 0xFF;
		l <<= 8;
		l |= getFrameData()[9] & 0xFF;
		l <<= 8;
		l |= getFrameData()[10] & 0xFF;
		l <<= 8;
		l |= getFrameData()[11] & 0xFF;
		return l;
	}

	public double getWetBulbTemp() {
		int i = 0;
		i |= getFrameData()[19] & 0xFF;
		i <<= 8;
		i |= getFrameData()[20] & 0xFF;
		return ((i * .00117)/(.24812))*(100);
	}
	
	public double getDryBulbTemp() {
		int i = 0;
		i |= getFrameData()[21] & 0xFF;
		i <<= 8;
		i |= getFrameData()[22] & 0xFF;
		return ((i * .00117)/(.24812))*(100);
	}
}
