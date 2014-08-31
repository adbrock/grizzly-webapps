package com.brocktek.farm.config;

import org.glassfish.jersey.server.ResourceConfig;

public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		packages("com.brocktek.farm.server.resources");
	}
}
