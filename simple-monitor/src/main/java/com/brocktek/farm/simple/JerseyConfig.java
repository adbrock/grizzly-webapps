package com.brocktek.farm.simple;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;

public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(FreemarkerMvcFeature.class);
		packages("com.brocktek.farm.simple.resources");
		this.property(FreemarkerMvcFeature.TEMPLATES_BASE_PATH, "/WEB-INF/ftl");
	}
}
