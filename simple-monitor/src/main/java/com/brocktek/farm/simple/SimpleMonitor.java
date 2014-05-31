package com.brocktek.farm.simple;

import java.net.URI;
import java.net.URL;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import com.brocktek.farm.simple.prevalence.PrevalentSystem;
import com.brocktek.farm.simple.util.GuiceUtil;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class SimpleMonitor extends Application {
	private Injector injector;

	public static void main(String[] args) {
		SimpleMonitor.launch(args);
	}

	public static URI getBaseUri() {
		return UriBuilder.fromUri("http://0.0.0.0/").port(80).build();
	}

	@Override
	public void init() throws Exception {
		injector = Guice.createInjector(new SimpleMonitorModule());
		System.out.println("Grizzly server starting...");
		startServer();
		PrevalentSystemInjector.setPrevalentSystem(injector.getInstance(PrevalentSystem.class));
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		URL url = SimpleMonitor.class.getResource("SimpleMonitorView.fxml");
		Scene scene = new Scene(GuiceUtil.loadFXML(injector, url, SimpleMonitorController.class));

		primaryStage.setTitle("Simple Monitor");
		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(1000);
			}
		});
	}

	public static HttpServer startServer() {
		return GrizzlyHttpServerFactory.createHttpServer(getBaseUri(), new JerseyConfig());
	}
}
