package com.brocktek.farm.model;

import java.io.Serializable;
import java.time.Instant;

public class BarnUpdate implements Serializable, Comparable<BarnUpdate> {
	private static final long serialVersionUID = -5173549260692319275L;
	private final Instant timestamp;
	private final double wetBulbTemp;
	private final double dryBulbTemp;

	public BarnUpdate(Instant timestamp, double wetBulbTemp, double dryBulbTemp) {
		this.timestamp = timestamp;
		this.wetBulbTemp = wetBulbTemp;
		this.dryBulbTemp = dryBulbTemp;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public double getWetBulbTemp() {
		return wetBulbTemp;
	}

	public double getDryBulbTemp() {
		return dryBulbTemp;
	}

	@Override
	public int compareTo(BarnUpdate other) {
		return this.timestamp.compareTo(other.timestamp);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(dryBulbTemp);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		temp = Double.doubleToLongBits(wetBulbTemp);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BarnUpdate other = (BarnUpdate) obj;
		if (Double.doubleToLongBits(dryBulbTemp) != Double.doubleToLongBits(other.dryBulbTemp))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (Double.doubleToLongBits(wetBulbTemp) != Double.doubleToLongBits(other.wetBulbTemp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BarnUpdate [timestamp=" + timestamp + ", wetBulbTemp=" + wetBulbTemp + ", dryBulbTemp=" + dryBulbTemp + "]";
	}
}
