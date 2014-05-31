package com.brocktek.farm.simple.prevalence;

import java.util.Date;
import java.util.Map;

import org.prevayler.Transaction;

import com.brocktek.farm.simple.model.Barn;

public class SetBarnStatusTransaction implements Transaction<Map<Long, Barn>> {
	private static final long serialVersionUID = 8058526873781775693L;
	private long address;
	private boolean status;
	
	public SetBarnStatusTransaction(long address, boolean status) {
		this.address = address;
		this.status = status;
	}

	@Override
	public void executeOn(Map<Long, Barn> prevalentSystem, Date executionTime) {
		prevalentSystem.get(address).setOnline(status);		
	}
}
