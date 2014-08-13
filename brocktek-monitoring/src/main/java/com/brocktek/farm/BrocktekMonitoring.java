package com.brocktek.farm;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;
import java.util.prefs.Preferences;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.grizzly2.httpserver.internal.LocalizationMessages;

import com.brocktek.farm.config.BrocktekMonitoringModule;
import com.brocktek.farm.config.JerseyConfig;
import com.brocktek.farm.prevalence.PrevaylerService;
import com.brocktek.problem.ProblemService;
import com.brocktek.util.DNS;
import com.brocktek.zigbee.ZigbeeService;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class BrocktekMonitoring {
	public static final Preferences prefs = Preferences.systemNodeForPackage(BrocktekMonitoring.class);
	public static final Injector injector = Guice.createInjector(new BrocktekMonitoringModule());
	public static final ScheduledExecutorService executorService = injector.getInstance(ScheduledExecutorService.class);
	public static final PrevaylerService prevaylerService = injector.getInstance(PrevaylerService.class);
	public static final ZigbeeService zigbeeService = injector.getInstance(ZigbeeService.class);
	public static final ProblemService problemService = injector.getInstance(ProblemService.class);
	public static final DNS dns = injector.getInstance(DNS.class);

	private static HttpServer httpServer;

	public static void main(String[] args) throws Exception {
		/* Start Error Service */
		problemService.start();

		/* Start Zigbee Service */
		zigbeeService.start(prefs.get("port", "COM1"));

		/* Start Prevayler Service */
		prevaylerService.start();

		/* Start Jersey Server */
		URI baseUri = UriBuilder.fromUri("http://0.0.0.0/api").port(80).build();
		httpServer = GrizzlyHttpServerFactory.createHttpServer(baseUri, new JerseyConfig(), false);

		HttpHandler handler = new StaticHttpHandler("src/main/resources/WEB-DATA");
		httpServer.getServerConfiguration().addHttpHandler(handler, "/");
		try {
			httpServer.start();
		} catch (IOException e) {
			httpServer.shutdownNow();
			throw new ProcessingException(LocalizationMessages.FAILED_TO_START_SERVER(e.getMessage()), e);
		}

		/* Start DNS Service */
		dns.start();

		System.in.read();
		zigbeeService.shutdown();
		prevaylerService.shutdown();
		httpServer.shutdown();
	}
}
