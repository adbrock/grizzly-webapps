package com.brocktek.farm.prevalence;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.prevayler.Transaction;

import com.brocktek.farm.model.Barn;

public class AddBarnTransaction implements Transaction<ConcurrentHashMap<String, Barn>> {
	private static final long serialVersionUID = 8103619424935626806L;
	private Barn barn;

	public AddBarnTransaction(Barn barn) {
		this.barn = barn;
	}

	@Override
	public void executeOn(ConcurrentHashMap<String, Barn> prevalentSystem, Date executionTime) {
		prevalentSystem.put(barn.getAddress64(), barn);
	}
}
