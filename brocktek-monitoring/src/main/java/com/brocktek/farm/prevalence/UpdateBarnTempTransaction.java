package com.brocktek.farm.prevalence;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.prevayler.Transaction;

import com.brocktek.farm.model.Barn;

public class UpdateBarnTempTransaction implements Transaction<ConcurrentHashMap<Long, Barn>> {
	private static final long serialVersionUID = 4593408605990412805L;
	private Barn barn;
	private Instant timestamp;
	private double wetBulbTemp;
	private double dryBulbTemp;

	public UpdateBarnTempTransaction(Barn barn, Instant timestamp, double wetBulbTemp, double dryBulbTemp) {
		this.barn = barn;
		this.timestamp = timestamp;
		this.wetBulbTemp = wetBulbTemp;
		this.dryBulbTemp = dryBulbTemp;
	}

	@Override
	public void executeOn(ConcurrentHashMap<Long, Barn> prevalentSystem, Date executionTime) {
		prevalentSystem.get(barn.getAddress64()).updateTemperature(timestamp, wetBulbTemp, dryBulbTemp);
	}
}
