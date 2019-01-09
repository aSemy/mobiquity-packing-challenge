package com.mobiquityinc.packer.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.model.Parcel;
import com.mobiquityinc.packer.model.ParcelSolution;
import com.mobiquityinc.packer.validation.ItemValidator;
import com.mobiquityinc.packer.validation.ParcelValidator;
import com.mobiquityinc.packer.validation.ValidationError;

public class PackingService {

	private static final Logger logger = LoggerFactory.getLogger(PackingService.class);

	private static final ParsingService PARSING_SERVICE = new ParsingService();

	private static final ItemValidator itemValidator = new ItemValidator();
	private static final ParcelValidator parcelValidator = new ParcelValidator();

	public List<ParcelSolution> optimise(File file) throws FileNotFoundException {
		TreeMap<Parcel, ArrayList<Item>> input = PARSING_SERVICE.parseFile(file);

		return this.optimise(input);
	}

	public ParcelSolution optimise(final Parcel parcel, final ArrayList<Item> potentialItems) {

		TreeMap<Parcel, ArrayList<Item>> input = new TreeMap<>();
		input.put(parcel, potentialItems);
		List<ParcelSolution> result = optimise(input);

		assert result != null;
		assert result.size() == 1;

		return result.iterator().next();
	}

	public List<ParcelSolution> optimise(final TreeMap<Parcel, ArrayList<Item>> input) {

		// validate
		List<ValidationError> errors = new ArrayList<>();
		errors.addAll(input.keySet().stream().map(p -> parcelValidator.validateParcel(p)).flatMap(List::stream)
				.collect(Collectors.toList()));
		errors.addAll(input.values().stream().map(items -> itemValidator.validateItems(items)).flatMap(List::stream)
				.collect(Collectors.toList()));

		if (!errors.isEmpty()) {
			String errorsJoinedString = errors.stream().map(e -> e.getMessage()).collect(Collectors.joining(", "));
			throw new APIException("Validation error(s): [" + errorsJoinedString + "]");
		}

		// get crunching on solutions
		List<ParcelSolution> solutions = new ArrayList<>();

		for (final Parcel p : input.keySet()) {
			ParcelSolution solution = recursive(p, input.get(p));

			solutions.add(solution);
		}

		return solutions;
	}

	private ParcelSolution recursive(final Parcel parcel, final ArrayList<Item> potentialItems) {
		return recursive(new ParcelSolution(parcel.getMaxWeight()), potentialItems);
	}

	private ParcelSolution recursive(final ParcelSolution solution, ArrayList<Item> potentialItems) {
		// make a copy of current items, so other branches are not affected
		potentialItems = new ArrayList<>(potentialItems);

		if (logger.isDebugEnabled())
			logger.debug("potentialItems: <{}>",
					potentialItems.stream().map(i -> Long.toString(i.getIndex())).collect(Collectors.joining(",")));

		// end of recursion
		if (potentialItems.isEmpty() || solution.getRemainingWeightLimit().compareTo(BigDecimal.ZERO) == 0) {

			if (logger.isDebugEnabled())
				logger.debug("Potential solution: <{}>", solution.getItems().stream()
						.map(i -> Long.toString(i.getIndex())).collect(Collectors.joining(",")));

			return solution;
		}

		Item currentItem = potentialItems.iterator().next();
		potentialItems.remove(currentItem);

		// if currentItem is too large, then ignore it
		if (currentItem.getWeight().compareTo(solution.getRemainingWeightLimit()) > 0) {

			if (logger.isDebugEnabled())
				logger.debug("Item [{}] is too heavy. {} vs {}", currentItem.getIndex(), currentItem.getWeight(),
						solution.getRemainingWeightLimit());

			return recursive(solution, potentialItems);
		} else {

			// consider the cost of this item
			// and the cost of the remaining items
			ParcelSolution solution1 = new ParcelSolution(solution);
			solution1.addItem(currentItem);
			logger.debug("Branch: Consider current item[{}]", currentItem.getIndex());

			ParcelSolution s1 = recursive(solution1, potentialItems);

			// don't consider the cost of the current item
			// just consider the cost of the previous and remaining items
			ParcelSolution solution2 = new ParcelSolution(solution);
			logger.debug("Branch: Ignore current item[{}]", currentItem.getIndex());
			ParcelSolution s2 = recursive(solution2, potentialItems);

			if (s1.getTotalCostInCents() == s2.getTotalCostInCents())
				return s1.getCurrentWeight().compareTo(s2.getCurrentWeight()) < 0 ? s1 : s2;
			else
				return s1.getTotalCostInCents() > s2.getTotalCostInCents() ? s1 : s2;
		}

	}

}
