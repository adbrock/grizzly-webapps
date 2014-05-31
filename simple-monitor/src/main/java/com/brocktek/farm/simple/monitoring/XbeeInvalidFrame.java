package com.brocktek.farm.simple.monitoring;

public class XbeeInvalidFrame extends XbeeFrame {

	public XbeeInvalidFrame(byte[] frameData, long timestamp) {
		super(frameData, timestamp);
	}

	@Override
	public boolean verify() {
		return false;
	}
}
