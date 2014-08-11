package com.brocktek.util;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import com.google.inject.Injector;

public class GuiceUtil {

	public static Parent loadFXML(Injector injector, URL url, Class<? extends Object> clazz) throws IOException {
		FXMLLoader loader = new FXMLLoader(url);
		loader.setController(injector.getInstance(clazz));
		return loader.load();
	}

}
