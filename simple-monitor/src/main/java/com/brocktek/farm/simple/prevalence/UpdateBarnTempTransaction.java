package com.brocktek.farm.simple.prevalence;

import java.util.Date;
import java.util.Map;

import org.prevayler.Transaction;

import com.brocktek.farm.simple.model.Barn;

public class UpdateBarnTempTransaction implements Transaction<Map<Long, Barn>> {
	private static final long serialVersionUID = -8144030124677854629L;
	private long address;
	private double wetBulbTemp;
	private double dryBulbTemp;
	
	public UpdateBarnTempTransaction(long address, double wetBulbTemp, double dryBulbTemp) {
		this.address = address;
		this.wetBulbTemp = wetBulbTemp;
		this.dryBulbTemp = dryBulbTemp;
	}

	@Override
	public void executeOn(Map<Long, Barn> prevalentSystem, Date executionTime) {
		Barn barn = prevalentSystem.get(address);
		barn.setOnline(true);
		barn.setLastUpdate(executionTime.getTime());
		barn.setWetBulbTemp(wetBulbTemp);
		barn.setDryBulbTemp(dryBulbTemp);
	}
}
