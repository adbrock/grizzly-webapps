package com.brocktek.farm.prevalence;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.prevayler.Transaction;

import com.brocktek.farm.model.Barn;

public class RemoveBarnTransaction implements Transaction<ConcurrentHashMap<String, Barn>> {
	private static final long serialVersionUID = -5635994338388468722L;
	private Barn barn;

	public RemoveBarnTransaction(Barn barn) {
		this.barn = barn;
	}

	@Override
	public void executeOn(ConcurrentHashMap<String, Barn> prevalentSystem, Date executionTime) {
		prevalentSystem.remove(barn.getAddress64());
	}
}
