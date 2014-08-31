package com.brocktek.util;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.net.ssl.HttpsURLConnection;

public class DnsService {
	private static final Logger LOGGER = Logger.getLogger(DnsService.class.getName());
	private static final Preferences PREFS = Preferences.systemNodeForPackage(DnsService.class);
	private static final String API_PATH = "http://system-ns.com/api?type=dynamic&domain=%s.system-ns.net&command=set&token=%s";
	private final ScheduledExecutorService executor;
	private String apiToken = PREFS.get("system-ns/token", "");
	private String domainName = PREFS.get("system-ns/domain", "");
	private boolean enabled = PREFS.getBoolean("system-ns/enabled", false);

	public DnsService() {
		executor = Executors.newScheduledThreadPool(1);
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		PREFS.put("system-ns/token", apiToken);
		this.apiToken = apiToken;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		PREFS.put("system-ns/domain", domainName);
		this.domainName = domainName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		PREFS.putBoolean("system-ns/enabled", enabled);
		this.enabled = enabled;
	}

	public void start() {
		Runnable updateDns = new Runnable() {
			@Override
			public void run() {
				if (enabled) {
					try {
						URL url = new URL(String.format(API_PATH, domainName, apiToken));
						HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						LOGGER.info("Updated System-Ns @ " + url.toString());
					} catch (IOException e) {
						LOGGER.warning(e.getMessage());
					}
				}
			}
		};
		executor.scheduleAtFixedRate(updateDns, 30, 300, TimeUnit.SECONDS);
	}

	public void shutdown() {
		executor.shutdown();
	}
}
