package com.brocktek.farm.simple.monitoring;

public interface MonitoringServiceListener {
	
	public void barnTempUpdated(long address, double wetBulbTemp, double dryBulbTemp);
}
