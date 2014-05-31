package com.brocktek.farm.simple.monitoring;

public interface MonitoringService {
	
	public void addMonitoringServiceListener(MonitoringServiceListener listener);

	public void removeMonitoringServiceListener(MonitoringServiceListener listener);
	
	public void addFrameListener(FrameListener listener);
	
	public void setPortName(String port);
	
	public String getPortName();
}
