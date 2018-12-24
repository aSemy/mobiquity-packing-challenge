package com.mobiquityinc.packer.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParcelSolution {

	public static final String INVALID_INPUT_RESPONSE = "-";

	private List<Item> items = new ArrayList<Item>();

	private BigDecimal weightLimit;

	public ParcelSolution(BigDecimal weightLimit) {
		this.weightLimit = weightLimit;
	}

	public ParcelSolution(final ParcelSolution ps) {
		this.items.addAll(ps.getItems());
		this.weightLimit = ps.getWeightLimit();
	}

	public List<Item> getItems() {
		return items;
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public BigDecimal getCurrentWeight() {
		if (items == null)
			return BigDecimal.ZERO;
		else
			return items.stream().map(i -> i != null ? i.getWeight() : BigDecimal.ZERO).reduce(BigDecimal.ZERO,
					BigDecimal::add);
	}

	public BigDecimal getRemainingWeightLimit() {
		return getWeightLimit().subtract(getCurrentWeight());
	}

	public long getTotalCostInCents() {
		if (items == null)
			return 0;
		else
			return items.stream().mapToLong(i -> i != null ? i.getCostInCents() : 0).sum();
	}

	public BigDecimal getWeightLimit() {
		return weightLimit;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public String getStringOutput() {
		Collections.sort(items);
		return items.stream().map(i -> Long.toString(i.getIndex())).reduce((s1, s2) -> s1 + "," + s2)
				.orElse(INVALID_INPUT_RESPONSE);
	}

	@Override
	public String toString() {

		final String itemsSize = items != null ? Integer.toString(items.size()) : INVALID_INPUT_RESPONSE;
		final String itemsString = items != null
				? items.stream().map(i -> (i != null ? i.toString() : "null")).collect(Collectors.joining(","))
				: "null";

		return "ParcelSolution [items[" + itemsSize + "]={" + itemsString + "}, weightLimit=" + weightLimit
				+ ", getCurrentWeight()=" + getCurrentWeight() + ", getRemainingWeightLimit()="
				+ getRemainingWeightLimit() + ", getTotalCostInCents()=" + getTotalCostInCents() + "]";
	}
}
