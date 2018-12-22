package com.mobiquityinc.packer.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mobiquityinc.packer.model.Item;

public class ItemValidator {

	private static final long MAX_ITEM_WEIGHT = 100;
	private static final int MAX_NUM_OF_ITEMS = 15;

	public void validateItems(ArrayList<Item> items) {
		assert items != null : "Cannot have null items";
		assert items.size() == 0 : "Cannot have 0 items";
		assert items.size() <= MAX_NUM_OF_ITEMS : "Max number of items is " + MAX_NUM_OF_ITEMS;

		items.forEach(i -> validateItem(i));

		// check indices are unique
		HashMap<Long, List<Item>> mapIndexToItems = new HashMap<>();
		for (Item i : items) {

			if (mapIndexToItems.containsKey(i.getIndex()))
				mapIndexToItems.put(i.getIndex(), new ArrayList<>());

			mapIndexToItems.get(i.getIndex()).add(i);
		}

		for (long index : mapIndexToItems.keySet()) {
			assert mapIndexToItems.get(index).size() == 1 : "Duplicated index " + index;
		}
	}

	public void validateItem(Item item) {

		assert item != null;
		assert item.getWeight().compareTo(
				BigDecimal.valueOf(MAX_ITEM_WEIGHT)) <= 0 : "Cannot have item weight more than " + MAX_ITEM_WEIGHT;
		assert item.getWeight().compareTo(BigDecimal.ZERO) > 0 : "Cannot have negative or 0 weight";
		
		assert item.getCostInCents() >= 0 : "Cannot have negative cost";

	}

}
