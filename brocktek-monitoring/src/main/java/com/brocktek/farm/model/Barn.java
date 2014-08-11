package com.brocktek.farm.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import com.brocktek.zigbee.ZigbeeNode;

public class Barn implements Serializable, Comparable<Barn>, ZigbeeNode {
	private static final long serialVersionUID = -901361627723043662L;

	private SortedSet<BarnUpdate> updateSet = new TreeSet<BarnUpdate>();
	private Instant timestamp;
	private double wetBulbTemp;
	private double dryBulbTemp;
	private boolean online;
	private short address16;
	private String address64;
	private String id;

	@Override
	public short getAddress16() {
		return this.address16;
	}

	@Override
	public void setAddress16(short address16) {
		this.address16 = address16;
	}

	@Override
	public String getAddress64() {
		return address64;
	}

	@Override
	public void setAddress64(String address64) {
		this.address64 = address64;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public Instant getTimestamp() {
		return this.timestamp;
	}

	public boolean isOnline() {
		return this.online;
	}

	public double getWetBulbTemp() {
		return this.wetBulbTemp;
	}

	public double getDryBulbTemp() {
		return this.dryBulbTemp;
	}

	public SortedSet<BarnUpdate> getUpdateSet() {
		return updateSet;
	}

	public void updateStatus(Instant timestamp, boolean online) {
		this.timestamp = timestamp;
		this.online = online;

		try {
			if (timestamp.compareTo(updateSet.last().getTimestamp().plusSeconds(60)) > 0)
				updateSet.add(new BarnUpdate(timestamp, 0, 0));
			if (updateSet.size() > 10080)
				updateSet.remove(updateSet.first());
		} catch (NoSuchElementException e) {
			updateSet.add(new BarnUpdate(timestamp, 0, 0));
		}
	}

	public void updateTemperature(Instant timestamp, double wetBulbTemp, double dryBulbTemp) {
		this.timestamp = timestamp;
		this.online = true;
		this.wetBulbTemp = wetBulbTemp;
		this.dryBulbTemp = dryBulbTemp;

		try {
			if (timestamp.compareTo(updateSet.last().getTimestamp().plusSeconds(60)) > 0)
				updateSet.add(new BarnUpdate(timestamp, wetBulbTemp, dryBulbTemp));
			if (updateSet.size() > 10080)
				updateSet.remove(updateSet.first());
		} catch (NoSuchElementException e) {
			updateSet.add(new BarnUpdate(timestamp, wetBulbTemp, dryBulbTemp));
		}
	}

	@Override
	public String toString() {
		if (id != null) {
			return id;
		}
		return this.address64;
	}

	@Override
	public int compareTo(Barn other) {
		return this.id.compareTo(other.id);
	}
}
