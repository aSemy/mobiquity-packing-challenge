package com.mobiquityinc.packer.model;

import java.math.BigDecimal;

/**
 * An item that is stored in a {@link Package}.
 * 
 * @author Adam
 *
 */
public class Item implements Comparable<Item> {

	private long index;
	private BigDecimal weight;
	private long costInCents;

	public Item() {
	}

	public Item(long index, long weight, long costInCents) {
		this(index, BigDecimal.valueOf(weight), costInCents);
	}

	public Item(long index, double weight, long costInCents) {
		this(index, BigDecimal.valueOf(weight), costInCents);
	}

	public Item(long index, BigDecimal weight, long costInCents) {
		super();
		this.index = index;
		this.weight = weight;
		this.costInCents = costInCents;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public long getCostInCents() {
		return costInCents;
	}

	public void setCostInCents(long costInCents) {
		this.costInCents = costInCents;
	}

	@Override
	public int compareTo(Item other) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		
		if (this == other)
			return EQUAL;
		
		if (other == null)
			return BEFORE;
		
		return Long.compare(this.index, other.index);
	}
}
