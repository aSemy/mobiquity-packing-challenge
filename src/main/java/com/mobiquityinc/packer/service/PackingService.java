package com.mobiquityinc.packer.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.model.Parcel;
import com.mobiquityinc.packer.model.ParcelSolution;

public class PackingService {

	private static final ParsingService PARSING_SERVICE = new ParsingService();


	public List<ParcelSolution> optimise(File file) {
		HashMap<Parcel, ArrayList<Item>> input = PARSING_SERVICE.parseFile(file);
		
		return this.optimise(input);
	}

	public List<ParcelSolution> optimise(HashMap<Parcel, ArrayList<Item>> input) {
		
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

		// end of recursion
		if (potentialItems.isEmpty() || solution.getRemainingWeightLimit().compareTo(BigDecimal.ZERO) == 0)
			return solution;

		Item currentItem = potentialItems.iterator().next();
		potentialItems.remove(currentItem);
		potentialItems = new ArrayList<>(potentialItems);

		// if currentItem is too large, then ignore it
		if (currentItem.getWeight().compareTo(solution.getRemainingWeightLimit()) > 0) {
			return recursive(solution, potentialItems);
		} else {

			// consider the cost of this item
			// and the cost of the remaining items
			solution.addItem(currentItem);
			ParcelSolution s1 = recursive(solution, potentialItems);

			// don't consider the cost of the current item
			// just consider the cost of the previous and remaining items
			ParcelSolution solution2 = new ParcelSolution(solution.getWeightLimit());
			solution2.setItems(solution.getItems());
			ParcelSolution s2 = recursive(solution2, potentialItems);

			return s1.getTotalCostInCents() > s2.getTotalCostInCents() ? s1 : s2;
		}

	}

}
