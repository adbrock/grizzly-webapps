package com.brocktek.farm.simple;

import com.brocktek.farm.simple.monitoring.MonitoringService;
import com.brocktek.farm.simple.monitoring.XbeeService;
import com.brocktek.farm.simple.prevalence.PrevalentSystem;
import com.google.inject.AbstractModule;

public class SimpleMonitorModule extends AbstractModule {

	@Override
	protected void configure() {
		
		/* Systems */
		bind(MonitoringService.class).to(XbeeService.class);
		bind(PrevalentSystem.class);
		
		/* Controllers */
		bind(SimpleMonitorController.class);
	}

}
