package com.brocktek.farm.monitoring;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.prevayler.Transaction;

import com.brocktek.farm.model.Barn;

public class RemoveBarnTransaction implements Transaction<ConcurrentHashMap<Long, Barn>> {
	private static final long serialVersionUID = -5635994338388468722L;
	private Barn barn;

	public RemoveBarnTransaction(Barn barn) {
		this.barn = barn;
	}

	@Override
	public void executeOn(ConcurrentHashMap<Long, Barn> prevalentSystem, Date executionTime) {
		prevalentSystem.remove(barn.getAddress64());
	}
}
