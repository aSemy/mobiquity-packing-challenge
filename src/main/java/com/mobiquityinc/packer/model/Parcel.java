package com.mobiquityinc.packer.model;

import java.math.BigDecimal;

/**
 * A Parcel (AKA a Package) stores a set of {@link Item}s, not exceeding the
 * {@link Parcel#getMaxWeight()}.
 * 
 * @author Adam
 *
 */
public class Parcel implements Comparable<Parcel> {

	private BigDecimal maxWeight;
	/**
	 * The line number of the input file upon which this parcel was defined. Used as
	 * a unique ID and for sorting.
	 */
	private final long lineNumber;

	public Parcel(long lineNumber, BigDecimal maxWeight) {
		super();
		this.lineNumber = lineNumber;
		this.maxWeight = maxWeight;
	}

	public BigDecimal getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(BigDecimal maxWeight) {
		this.maxWeight = maxWeight;
	}

	@Override
	public String toString() {
		return "Parcel [maxWeight=" + maxWeight + ", lineNumber=" + lineNumber + "]";
	}

	public long getLineNumber() {
		return lineNumber;
	}

	@Override
	public int compareTo(Parcel other) {
		final int BEFORE = -1;
		final int EQUAL = 0;

		if (this == other)
			return EQUAL;

		if (other == null)
			return BEFORE;

		return Long.compare(this.getLineNumber(), other.getLineNumber());
	}

}
