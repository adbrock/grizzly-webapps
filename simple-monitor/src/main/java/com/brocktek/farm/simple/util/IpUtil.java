package com.brocktek.farm.simple.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class IpUtil {
	
	
	public static String getExternalIp() throws MalformedURLException {
		URL whatismyip = new URL("http://ipinfo.io/ip");
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
		                whatismyip.openStream()))) {
			String ip = in.readLine(); //you get the IP as a String
			return ip;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
