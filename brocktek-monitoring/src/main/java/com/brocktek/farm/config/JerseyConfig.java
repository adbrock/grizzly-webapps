package com.brocktek.farm.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;

public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(FreemarkerMvcFeature.class);
		this.property(FreemarkerMvcFeature.TEMPLATES_BASE_PATH, "/WEB-INF/ftl");

		packages("com.brocktek.farm.server.resources");
	}
}
