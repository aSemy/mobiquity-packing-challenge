package com.mobiquityinc.packer.model;

import java.math.BigDecimal;

/**
 * A Parcel (AKA a Package) stores a set of {@link Item}s, not exceeding the
 * {@link Parcel#getMaxWeight()}.
 * 
 * @author Adam
 *
 */
public class Parcel {

	private BigDecimal maxWeight;

	public Parcel(BigDecimal maxWeight) {
		super();
		this.maxWeight = maxWeight;
	}

	public BigDecimal getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(BigDecimal maxWeight) {
		this.maxWeight = maxWeight;
	}

}
