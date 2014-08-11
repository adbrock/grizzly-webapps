package com.brocktek.util;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import com.brocktek.problem.Problem;
import com.brocktek.problem.ProblemService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DNS {
	@Inject ScheduledExecutorService executor;
	@Inject ProblemService problemService;

	public void start() {
		executor.scheduleAtFixedRate(new Runnable() {
			private Problem dnsProblem;

			@Override
			public void run() {
				try {
					URL url = new URL(
							"http://system-ns.com/api?type=dynamic&domain=allenfarms.system-ns.net&command=set&token=a6228ee5a1240067b83ae38161631925");
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
		}, 30, 300, TimeUnit.SECONDS);
	}
}
