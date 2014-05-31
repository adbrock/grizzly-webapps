package com.brocktek.farm.simple.model;

import java.io.Serializable;

public class Barn implements Serializable, Comparable<Barn> {
	private static final long serialVersionUID = -3231820682198590018L;
	private String id;
	private long address;
	private long lastUpdate;
	private double wetBulbTemp;
	private double dryBulbTemp;
	private boolean online;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getAddress() {
		return address;
	}

	public void setAddress(long address) {
		this.address = address;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public double getWetBulbTemp() {
		return wetBulbTemp;
	}

	public void setWetBulbTemp(double wetBulbTemp2) {
		this.wetBulbTemp = wetBulbTemp2;
	}

	public double getDryBulbTemp() {
		return dryBulbTemp;
	}

	public void setDryBulbTemp(double dryBulbTemp) {
		this.dryBulbTemp = dryBulbTemp;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@Override
	public int compareTo(Barn other) {
		return this.id.compareTo(other.id);
	}
}
