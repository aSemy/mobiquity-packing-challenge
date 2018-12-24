package com.mobiquityinc.packer.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.mobiquityinc.packer.model.Item;

public class ItemValidator {

	public static final long MAX_ITEM_WEIGHT = 100;
	public static final long MAX_ITEM_COST_IN_CENTS = 100 * 100;
	public static final int MAX_NUM_OF_ITEMS = 15;

	public List<ValidationError> validateItems(ArrayList<Item> items) {

		List<ValidationError> errors = new ArrayList<>();

		if (items == null) {
			errors.add(new ValidationError("Cannot have null items"));
			return errors;
		}
		if (items.isEmpty()) {
			errors.add(new ValidationError("Cannot have 0 items"));
			return errors;
		}
		if (items.size() > MAX_NUM_OF_ITEMS) {
			errors.add(
					new ValidationError("Exceeded max number of items (" + MAX_NUM_OF_ITEMS + " vs " + items.size() + ")"));
			return errors;
		}

		// find out errors for all items
		List<ValidationError> itemsErrors = items.stream().flatMap(item -> validateItem(item).stream())
				.collect(Collectors.toList());
		errors.addAll(itemsErrors);

		// check indices are unique
		HashMap<Long, List<Item>> mapIndexToItems = new HashMap<>();
		for (Item i : items) {

			if (!mapIndexToItems.containsKey(i.getIndex()))
				mapIndexToItems.put(i.getIndex(), new ArrayList<>());

			mapIndexToItems.get(i.getIndex()).add(i);
		}

		for (long index : mapIndexToItems.keySet()) {
			if (mapIndexToItems.get(index).size() != 1)
				errors.add(new ValidationError("Cannot have duplicated index (" + index + ")"));
		}

		return errors;
	}

	public List<ValidationError> validateItem(Item item) {

		List<ValidationError> errors = new ArrayList<>();

		assert item != null;
		if (item == null) {
			errors.add(new ValidationError("Cannot have null item"));
			return errors;
		}

		if (item.getWeight().compareTo(BigDecimal.ZERO) <= 0)
			errors.add(new ValidationError("Cannot have non-positive weight"));

		if (item.getCostInCents() <= 0)
			errors.add(new ValidationError("Cannot have non-positive cost"));

		if (item.getWeight().compareTo(BigDecimal.valueOf(MAX_ITEM_WEIGHT)) > 0)
			errors.add(new ValidationError(
					"Item weight cannot exceed " + MAX_ITEM_WEIGHT + ", but was " + item.getWeight().toString()));

		if (item.getCostInCents() > MAX_ITEM_COST_IN_CENTS)
			errors.add(new ValidationError("Item cost (in cents) cannot exceed " + MAX_ITEM_COST_IN_CENTS + ", but was "
					+ item.getCostInCents()));

		return errors;
	}
}
