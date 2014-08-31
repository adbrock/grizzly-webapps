package com.brocktek.farm.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.SortedSet;
import java.util.TreeSet;

import com.brocktek.xbee.XbeeNode;

public class Barn extends XbeeNode implements Serializable, Comparable<Barn> {
	private static final long serialVersionUID = -901361627723043662L;
	public static boolean ONLINE = true;
	public static boolean OFFLINE = false;

	private SortedSet<BarnUpdate> updateSet = new TreeSet<BarnUpdate>();

	private Instant timestamp;
	private double wetBulbTemp;
	private double dryBulbTemp;
	private boolean online;
	private String id;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public Instant getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isOnline() {
		return this.online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public double getWetBulbTemp() {
		return this.wetBulbTemp;
	}

	public void setWetBulbTemp(double wetBulbTemp) {
		this.wetBulbTemp = wetBulbTemp;
	}

	public double getDryBulbTemp() {
		return this.dryBulbTemp;
	}

	public void setDryBulbTemp(double dryBulbTemp) {
		this.dryBulbTemp = dryBulbTemp;
	}

	public SortedSet<BarnUpdate> getUpdateSet() {
		return updateSet;
	}

	public void updateStatus(boolean online) {
		this.online = online;
	}

	@Override
	public String toString() {
		if (id != null) {
			return id;
		}
		return ("[id: " + id + "address: " + String.format("%16s", Long.toHexString(this.getAddress64()).replace(" ", "0").toUpperCase()) + "]");
	}

	@Override
	public int compareTo(Barn other) {
		return this.id.compareTo(other.id);
	}
}
