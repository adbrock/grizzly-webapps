package com.brocktek.util;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import com.brocktek.farm.BrocktekMonitoring;
import com.brocktek.problem.Problem;
import com.brocktek.problem.ProblemService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DNS {
	@Inject transient ScheduledExecutorService executor;
	@Inject transient ProblemService problemService;

	private String apiToken = BrocktekMonitoring.prefs.get("system-ns/token", "");
	private String domainName = BrocktekMonitoring.prefs.get("system-ns/domain", "");
	private boolean enabled = BrocktekMonitoring.prefs.getBoolean("system-ns/enabled", false);

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		BrocktekMonitoring.prefs.put("system-ns/token", apiToken);
		this.apiToken = apiToken;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		BrocktekMonitoring.prefs.put("system-ns/domain", domainName);
		this.domainName = domainName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		BrocktekMonitoring.prefs.putBoolean("system-ns/enabled", enabled);
		this.enabled = enabled;
	}

	public void start() {
		executor.scheduleAtFixedRate(new Runnable() {
			private Problem dnsProblem;

			@Override
			public void run() {
				if (enabled) {
					try {
						URL url = new URL(String.format("http://system-ns.com/api?type=dynamic&domain=%s.system-ns.net&command=set&token=%s", domainName,
								apiToken));
						HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
					} catch (IOException e) {
						dnsProblem = new Problem() {
							@Override
							public String getMessage() {
								return "DNS Service encountered unexpected error";
							}

							@Override
							public String toString() {
								return "DNS Service encountered unexpected error";
							}
						};
						if (dnsProblem != null)
							problemService.clearProblem(dnsProblem);
						problemService.putProblem(dnsProblem);
					}
				}
			}
		}, 30, 300, TimeUnit.SECONDS);
	}
}
