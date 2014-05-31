package com.brocktek.farm.simple.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.glassfish.jersey.server.mvc.Template;

import com.brocktek.farm.simple.PrevalentSystemInjector;
import com.brocktek.farm.simple.prevalence.PrevalentSystem;

@Path("/barns")
public class BarnResource {
	PrevalentSystem prevalentSystem = PrevalentSystemInjector.getPrevalentSystem();

	@GET
	@Template
	public Map<String, Object> getBarns() {
		Map<String, Object> objectMap = new HashMap<String, Object>();
		objectMap.put("barns", prevalentSystem.getBarnsAsList());
		List<String> columnHeaders = new ArrayList<String>();
		columnHeaders.add("Id");
		columnHeaders.add("Status");
		columnHeaders.add("Wet Bulb Temperature");
		columnHeaders.add("Dry Bulb Temperature");
		objectMap.put("headers", columnHeaders);
		objectMap.put("time", System.currentTimeMillis());
		return objectMap;
	}
}
