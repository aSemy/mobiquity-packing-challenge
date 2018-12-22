package com.mobiquityinc.packer.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.model.Parcel;

public class ParsingService {

	public Item parseItem(final Scanner lineScanner) {

		long index;
		BigDecimal weight;
		long costInEuros;

		if (lineScanner.hasNextLong()) {
			index = lineScanner.nextLong();
		} else {
			InputMismatchException e = new InputMismatchException("Couldn't find index. Found: " + lineScanner.next());

			lineScanner.close();
			throw e;
		}
		if (lineScanner.hasNextBigDecimal()) {
			weight = lineScanner.nextBigDecimal();
		} else {
			InputMismatchException e = new InputMismatchException("Couldn't find weight. Found: " + lineScanner.next());

			lineScanner.close();
			throw e;
		}
		if (lineScanner.hasNextLong()) {
			costInEuros = lineScanner.nextLong();
		} else {
			InputMismatchException e = new InputMismatchException(
					"Couldn't find costInEuros. Found: " + lineScanner.next());

			lineScanner.close();
			throw e;
		}

		long costInCents = costInEuros * 100;

		return new Item(index, weight, costInCents);
	}

	public HashMap<Parcel, ArrayList<Item>> parseFile(File file) throws FileNotFoundException, InputMismatchException {

		HashMap<Parcel, ArrayList<Item>> input = new HashMap<>();

		Scanner fileScanner = new Scanner(file);

		while (fileScanner.hasNextLine()) {
			Scanner lineScanner = new Scanner(fileScanner.nextLine());
			lineScanner.useDelimiter("[ ,\\(\\):€]+");

			// get the parcel

			BigDecimal bd = lineScanner.nextBigDecimal();

			Parcel parcel = new Parcel(bd);

			input.put(parcel, new ArrayList<>());

			while (lineScanner.hasNext()) {

				Item item = parseItem(lineScanner);

				input.get(parcel).add(item);

			}

			lineScanner.close();
		}
		fileScanner.close();

		return input;
	}

}
