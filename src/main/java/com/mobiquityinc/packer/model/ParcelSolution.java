package com.mobiquityinc.packer.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ParcelSolution {

	private List<Item> items = new ArrayList<Item>();

	private BigDecimal weightLimit;

	public ParcelSolution(BigDecimal weightLimit) {
		this.weightLimit = weightLimit;
	}

	public List<Item> getItems() {
		return items;
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public BigDecimal getCurrentWeight() {
		return items.stream().map(i -> i.getWeight()).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	public BigDecimal getRemainingWeightLimit() {
		return getWeightLimit().subtract(getCurrentWeight());
	}

	public long getTotalCostInCents() {
		return items.stream().mapToLong(i -> i.getCostInCents()).sum();
	}

	public BigDecimal getWeightLimit() {
		return weightLimit;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
}
