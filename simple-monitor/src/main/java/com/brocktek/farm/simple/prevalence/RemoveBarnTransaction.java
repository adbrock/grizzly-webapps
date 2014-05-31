package com.brocktek.farm.simple.prevalence;

import java.util.Date;
import java.util.Map;

import org.prevayler.Transaction;

import com.brocktek.farm.simple.model.Barn;

public class RemoveBarnTransaction implements Transaction<Map<Long, Barn>> {
	private static final long serialVersionUID = 3544171989797063369L;
	private Barn barn;
	
	public RemoveBarnTransaction(Barn barn) {
		this.barn = barn;
	}

	@Override
	public void executeOn(Map<Long, Barn> prevalentSystem, Date executionTime) {
		prevalentSystem.remove(barn.getAddress());		
	}
}
