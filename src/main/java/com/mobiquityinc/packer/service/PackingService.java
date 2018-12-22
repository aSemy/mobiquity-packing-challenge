package com.mobiquityinc.packer.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.model.Parcel;
import com.mobiquityinc.packer.model.ParcelSolution;
import com.mobiquityinc.packer.validation.ItemValidator;
import com.mobiquityinc.packer.validation.ParcelValidator;

public class PackingService {

	private static final ParsingService PARSING_SERVICE = new ParsingService();

	private static final ItemValidator itemValidator = new ItemValidator();
	private static final ParcelValidator parcelValidator = new ParcelValidator();

	public List<ParcelSolution> optimise(File file) throws FileNotFoundException {
		HashMap<Parcel, ArrayList<Item>> input = PARSING_SERVICE.parseFile(file);

		return this.optimise(input);
	}

	public List<ParcelSolution> optimise(HashMap<Parcel, ArrayList<Item>> input) {

		// validate
		input.keySet().forEach(p -> parcelValidator.validateParcel(p));
		input.values().forEach(items -> itemValidator.validateItems(items));

		// get crunching on solutions
		List<ParcelSolution> solutions = new ArrayList<>();

		for (Parcel p : input.keySet()) {

			ParcelSolution solution = optimise(p, input.get(p));

			solutions.add(solution);
		}

		return solutions;
	}

	public ParcelSolution optimise(final Parcel parcel, final ArrayList<Item> potentialItems) {

		return recursive(new ParcelSolution(parcel.getMaxWeight()), new ArrayList<>(potentialItems));

	}

	private ParcelSolution recursive(ParcelSolution solution, ArrayList<Item> potentialItems) {
		// make a copy of current items, so other branches are not affected
		potentialItems = new ArrayList<>(potentialItems);

		//System.out.println("Solution: " + solution);
		System.out.println("potentialItems: {"
				+ potentialItems.stream().map(i -> Long.toString(i.getIndex())).collect(Collectors.joining(",")) + "}");

		// end of recursion
		if (potentialItems.isEmpty() || solution.getRemainingWeightLimit().compareTo(BigDecimal.ZERO) == 0) {

			System.out.println("Potential solution: " + solution.getItems().stream()
					.map(i -> Long.toString(i.getIndex())).collect(Collectors.joining(",")));

			return solution;
		}

		Item currentItem = potentialItems.iterator().next();
		potentialItems.remove(currentItem);

		// if currentItem is too large, then ignore it
		if (currentItem.getWeight().compareTo(solution.getRemainingWeightLimit()) > 0) {
			System.out.println("Item [" + currentItem.getIndex() + "] is too heavy. " + currentItem.getWeight() + " vs "
					+ solution.getRemainingWeightLimit());
			return recursive(solution, potentialItems);
		} else {

			// consider the cost of this item
			// and the cost of the remaining items
			ParcelSolution solution1 = new ParcelSolution(solution);
			solution1.addItem(currentItem);
			System.out.println("Consider current item[" + currentItem.getIndex() + "]");
			ParcelSolution s1 = recursive(solution1, potentialItems);

			// don't consider the cost of the current item
			// just consider the cost of the previous and remaining items
			ParcelSolution solution2 = new ParcelSolution(solution);
			System.out.println("Ignore current item[" + currentItem.getIndex() + "]");
			ParcelSolution s2 = recursive(solution2, potentialItems);

			if (s1.getTotalCostInCents() == s2.getTotalCostInCents())
				return s1.getCurrentWeight().compareTo(s2.getCurrentWeight()) < 0 ? s1 : s2;
			else
				return s1.getTotalCostInCents() > s2.getTotalCostInCents() ? s1 : s2;
		}

	}

}
