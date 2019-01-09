package com.mobiquityinc.packer.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.model.Parcel;

public class ParsingService {

	private static final Logger logger = LoggerFactory.getLogger(ParsingService.class);

	private static final String regexDouble = "-?\\d*\\.?\\d+";
	private static final String regexLong = "\\d+";

	public TreeMap<Parcel, ArrayList<Item>> parseFile(File file) throws FileNotFoundException, InputMismatchException {

		// debug output
		try (Scanner fileScanner = new Scanner(file)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Reading in file " + file.getName());
				while (fileScanner.hasNextLine()) {
					logger.debug(fileScanner.nextLine());
				}
				fileScanner.close();
			}
		}

		try (Scanner fileScanner = new Scanner(file)) {

			TreeMap<Parcel, ArrayList<Item>> parsedInput = new TreeMap<>();

			long lineNumber = 1;
			while (fileScanner.hasNextLine()) {

				String line = fileScanner.nextLine();

				String[] lineSplit = line.split(" :");

				if (lineSplit.length != 2) {
					fileScanner.close();
					throw new IllegalArgumentException(
							"Invalid data format. Expected one ' :' per line, but got " + (lineSplit.length - 1));
				}
				if (lineSplit[0] == null || lineSplit[0].trim().isEmpty()) {
					fileScanner.close();
					throw new IllegalArgumentException(
							"Invalid data format. Found no parcel weight on line " + lineNumber);
				}

				if (lineSplit[1] == null || lineSplit[1].trim().isEmpty()) {
					fileScanner.close();
					throw new IllegalArgumentException("Invalid data format. Found no items on line " + lineNumber);
				}

				// get the parcel weight from the first part of lineSplit
				BigDecimal parcelWeight;
				try {
					parcelWeight = new BigDecimal(lineSplit[0]);
				} catch (NumberFormatException e) {
					fileScanner.close();
					throw new IllegalArgumentException(
							"Invalid data format. Could not parse parcelWeight on line " + lineNumber);
				}
				// now create the parcel.
				Parcel parcel = new Parcel(lineNumber++, parcelWeight);

				// now use the rest of the split to make items

				String itemsString = lineSplit[1];

				// validate all brackets open and close
				validateItemBrackets(itemsString);

				// match contents of brackets
				// Pattern pattern = Pattern.compile("(
				// \\(\\d+,\\d+\\.?\\d*,€\\d+\\.?\\d*\\))");
				Pattern pattern = Pattern
						.compile("( \\(" + regexLong + "," + regexDouble + ",€" + regexDouble + "\\))");
				Matcher matcher = pattern.matcher(itemsString);

				ArrayList<String> matches = new ArrayList<>();
				while (matcher.find()) {
					String match = matcher.group();
					matches.add(match);
				}

				assert itemsString.equals(
						matches.stream().collect(Collectors.joining())) : "Couldn't fully parse items. Expected '"
								+ itemsString + "' but got '" + matches.stream().collect(Collectors.joining()) + "'";

				ArrayList<Item> items = new ArrayList<>(
						matches.stream().map(m -> parseItem(m)).collect(Collectors.toList()));
				Collections.sort(items);

				parsedInput.put(parcel, items);

			}
			fileScanner.close();

			return parsedInput;

		} catch (FileNotFoundException | IllegalStateException | IllegalArgumentException | AssertionError e) {
			throw new APIException(e);
		}
	}

	public Item parseItem(final String itemString) {

		Pattern itemPattern = Pattern
				.compile(" \\((" + regexLong + "),(" + regexDouble + "),€(" + regexDouble + ")\\)");
		Matcher matcher = itemPattern.matcher(itemString);

		assert matcher.find() : "Couldn't parse item";
		assert matcher.groupCount() == 3 : "Expect each item to have three qualities";

		String indexString = matcher.group(1);
		String weightString = matcher.group(2);
		String costInEurosString = matcher.group(3);

		long index;
		BigDecimal weight;
		long costInEuros;

		try {
			index = Long.parseLong(indexString);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Couldn't parse item index from '" + indexString + "'");
		}
		try {
			weight = new BigDecimal(weightString);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Couldn't parse item weight from '" + weightString + "'");
		}
		try {
			costInEuros = Long.parseLong(costInEurosString);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Couldn't parse item costInEuros from '" + costInEurosString + "'");
		}

		assert !matcher.find() : "There shouldn't be anything further to match";

		long costInCents = costInEuros * 100;

		return new Item(index, weight, costInCents);
	}

	/**
	 * Check there's the right amount of open and close brackets
	 * 
	 * @param itemsString
	 */
	private void validateItemBrackets(final String itemsString) {

		boolean bracketsOpen = false;
		for (char c : itemsString.toCharArray()) {
			if (c == '(') {
				if (bracketsOpen)
					throw new IllegalStateException("Invalid brackets, brackets opened twice");

				bracketsOpen = true;
			} else if (c == ')') {
				if (bracketsOpen == false)
					throw new IllegalStateException("Invalid brackets, brackets already closed");

				bracketsOpen = false;
			}
		}
		if (bracketsOpen == true)
			throw new IllegalStateException("Invalid brackets, bracket never closed");

	}

}
