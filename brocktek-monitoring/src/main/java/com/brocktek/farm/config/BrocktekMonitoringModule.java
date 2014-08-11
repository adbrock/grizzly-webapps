package com.brocktek.farm.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.brocktek.farm.errors.SimpleProblemService;
import com.brocktek.farm.prevalence.PrevaylerService;
import com.brocktek.problem.ProblemService;
import com.brocktek.util.DNS;
import com.brocktek.xbee.XbeeService;
import com.brocktek.zigbee.ZigbeeService;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;

public class BrocktekMonitoringModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ZigbeeService.class).to(XbeeService.class);
		bind(PrevaylerService.class);
		bind(ProblemService.class).to(SimpleProblemService.class);
		bind(ExecutorService.class).toProvider(ScheduledExecutorServiceProvider.class);
		bind(ScheduledExecutorService.class).toProvider(ScheduledExecutorServiceProvider.class);
		bind(DNS.class);
	}

	private static class ScheduledExecutorServiceProvider implements Provider<ScheduledExecutorService> {
		private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

		@Override
		public ScheduledExecutorService get() {
			return executor;
		}
	}
}
