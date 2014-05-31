package com.brocktek.farm.simple.prevalence;

import java.util.Date;
import java.util.Map;

import org.prevayler.Transaction;

import com.brocktek.farm.simple.model.Barn;

public class AddBarnTransaction implements Transaction<Map<Long, Barn>> {
	private static final long serialVersionUID = -8012103224313197513L;
	private Barn barn;
	
	protected AddBarnTransaction(Barn barn) {
		this.barn = barn;
	}

	@Override
	public void executeOn(Map<Long, Barn> prevalentSystem, Date executionTime) {
		prevalentSystem.put(barn.getAddress(), barn);		
	}
}


