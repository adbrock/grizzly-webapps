package com.brocktek.farm.monitoring;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.prevayler.Transaction;

import com.brocktek.farm.model.Barn;

public class AddBarnTransaction implements Transaction<ConcurrentHashMap<Long, Barn>> {
	private static final long serialVersionUID = 8103619424935626806L;
	private Barn barn;

	public AddBarnTransaction(Barn barn) {
		this.barn = barn;
	}

	@Override
	public void executeOn(ConcurrentHashMap<Long, Barn> prevalentSystem, Date executionTime) {
		prevalentSystem.put(barn.getAddress64(), barn);
	}
}
