package com.brocktek.farm.prevalence;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.prevayler.Transaction;

import com.brocktek.farm.model.Barn;

public class SetBarnStatusOfflineTransaction implements Transaction<ConcurrentHashMap<Long, Barn>> {
	private static final long serialVersionUID = -5560880678744102559L;
	private Barn barn;
	private Instant timestamp;

	public SetBarnStatusOfflineTransaction(Barn barn, Instant timestamp) {
		this.barn = barn;
		this.timestamp = timestamp;
	}

	@Override
	public void executeOn(ConcurrentHashMap<Long, Barn> prevalentSystem, Date executionTime) {
		prevalentSystem.get(barn.getAddress64()).updateStatus(timestamp, false);
	}
}
