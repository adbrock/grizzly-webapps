package com.brocktek.farm;

import java.net.URI;
import java.util.Scanner;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.CompressionConfig;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import com.brocktek.farm.config.JerseyConfig;
import com.brocktek.farm.model.Barn;
import com.brocktek.farm.monitoring.MonitoringService;
import com.brocktek.util.DnsService;
import com.brocktek.zigbee.ZigbeeFrame;
import com.brocktek.zigbee.ZigbeeFrameListener;

public class BrocktekMonitoring implements ZigbeeFrameListener {
	public static MonitoringService monitoringService;
	private static final HttpServer httpServer;
	private static final int MB = 1024 * 1024;
	public static DnsService dns;
	private int frameCount = 0;
	static {
		URI baseUri = UriBuilder.fromUri("http://0.0.0.0/api").port(80).build();
		httpServer = GrizzlyHttpServerFactory.createHttpServer(baseUri, new JerseyConfig(), false);
		HttpHandler handler = new StaticHttpHandler("WEB-DATA/");
		httpServer.getServerConfiguration().addHttpHandler(handler, "/");
		CompressionConfig compressionConfig = httpServer.getListener("grizzly").getCompressionConfig();
		compressionConfig.setCompressionMode(CompressionConfig.CompressionMode.ON);
		compressionConfig.setCompressableMimeTypes("text/plain", "text/html", "text/css", "application/json", "application/xml");
		compressionConfig.setCompressionMinSize(1);

		dns = new DnsService();
	}

	public static void main(String[] args) throws Exception {
		BrocktekMonitoring app = new BrocktekMonitoring();
		app.init(args[0]);
		app.stop();
	}

	public void init(String portName) throws Exception {
		monitoringService = new MonitoringService();
		monitoringService.start(portName);
		httpServer.start();
		dns.start();

		System.out.println("----------------------------------------------------------------------------------------------------------------------------");
		System.out.println("     _/_/_/      _/_/    _/_/_/    _/      _/      _/      _/    _/_/    _/      _/  _/_/_/  _/_/_/_/_/    _/_/    _/_/_/   ");
		System.out.println("    _/    _/  _/    _/  _/    _/  _/_/    _/      _/_/  _/_/  _/    _/  _/_/    _/    _/        _/      _/    _/  _/    _/  ");
		System.out.println("   _/_/_/    _/_/_/_/  _/_/_/    _/  _/  _/      _/  _/  _/  _/    _/  _/  _/  _/    _/        _/      _/    _/  _/_/_/     ");
		System.out.println("  _/    _/  _/    _/  _/    _/  _/    _/_/      _/      _/  _/    _/  _/    _/_/    _/        _/      _/    _/  _/    _/    ");
		System.out.println(" _/_/_/    _/    _/  _/    _/  _/      _/      _/      _/    _/_/    _/      _/  _/_/_/      _/        _/_/    _/    _/     ");
		System.out.println("----------------------------------------------------------------------------------------------------------------------------");
		System.out.println();

		Scanner scanner = new Scanner(System.in);
		String input;
		do {
			System.out.print(">>");
			input = scanner.nextLine();
			switch (input) {
			case "?":
				System.out.println("zigbee\t\tPrints incoming zigbee frames.");
				System.out.println("barns\t\tPrints current barn status.");
				System.out.println("mem\t\tPrints current memory usage.");
				System.out.println("quit\t\tExits the program.");
				break;
			case "clear":
				monitoringService.clearBarnHistory();
				System.out.println("Barn history has been cleared.");
				break;
			case "zigbee":
				System.out.println("Listening...");
				monitoringService.zigbeeService.addFrameListener(this);
				Thread.sleep(30000L);
				monitoringService.zigbeeService.removeFrameListener(this);
				frameCount = 0;
				break;
			case "barns":
				if (monitoringService.getBarnsAsList().size() > 0) {
					for (Barn barn : monitoringService.getBarnsAsList()) {
						System.out.println(barn);
					}
				} else {
					System.out.println("No barns found.");
				}
				break;
			case "mem":
				Runtime runtime = Runtime.getRuntime();
				StringBuilder builder = new StringBuilder();
				builder.append(String.format("Used Memory:\t%8.3f MB\n", (double) (runtime.totalMemory() - runtime.freeMemory()) / MB));
				builder.append(String.format("Free Memory:\t%8.3f MB\n", (double) runtime.freeMemory() / MB));
				builder.append(String.format("Total Memory:\t%8.3f MB\n", (double) runtime.totalMemory() / MB));
				builder.append(String.format("Max Memory:\t%8.3f MB", (double) runtime.maxMemory() / MB));
				System.out.println(builder);
				break;
			case "quit":
				System.out.println("Goodbye!");
				break;
			default:
				System.out.println(input + " is not a recognized command.");
			}
		} while (!input.equals("quit"));
		scanner.close();
	}

	public void stop() throws Exception {
		dns.shutdown();
		httpServer.shutdownNow();
		monitoringService.shutdown();
	}

	@Override
	public void frameReceived(ZigbeeFrame frame) {
		System.out.println(String.format("Frame %03d: ", frameCount++) + frame);
	}
}
