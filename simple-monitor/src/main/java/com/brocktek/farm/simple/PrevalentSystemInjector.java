package com.brocktek.farm.simple;

import com.brocktek.farm.simple.prevalence.PrevalentSystem;

public class PrevalentSystemInjector {
	private static PrevalentSystem system;

	public static void setPrevalentSystem(PrevalentSystem sys) {
		system = sys;
	}

	public static PrevalentSystem getPrevalentSystem() {
		return system;
	}
}
