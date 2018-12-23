package test.com.mobiquityinc;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.validation.ItemValidator;

public class PackerRandomTestUtils {

	private static final Random random = new SecureRandom("seed".getBytes());

	public static ArrayList<Item> getRandomItems(final long count, final long startIndex, final BigDecimal totalWeight) {
		
		assert totalWeight.longValue() <= ItemValidator.MAX_ITEM_WEIGHT;

		ArrayList<Item> items = new ArrayList<>();

		long index = startIndex;
		BigDecimal remainingWeight = new BigDecimal(totalWeight.toString());

		for (int i = 0; i < count; i++, index++) {

			BigDecimal itemWeight;
			
			if (i + 1 == count) {
				// it's the last element, use the remaining weight
				itemWeight = new BigDecimal(remainingWeight.toString());
			}
			else {
				// get a random weight between 0.01 and remainingWeight/2
				itemWeight = BigDecimal.valueOf(randomDouble(0.01d, remainingWeight.divide(BigDecimal.valueOf(2)).doubleValue()));
			}
			
			Item item = new Item(index, itemWeight, random.longs(1, ItemValidator.MAX_ITEM_COST_IN_CENTS).findFirst().getAsLong());
			items.add(item);
			
			remainingWeight = remainingWeight.subtract(item.getWeight());

		}

		BigDecimal createdWeight = items.stream().map(i -> i.getWeight()).reduce(BigDecimal.ZERO, BigDecimal::add);
		assert totalWeight.compareTo(createdWeight) == 0;

		return items;

	}

	private static double randomDouble(double origin, double bound) {

		double r = random.nextDouble();
		r = r * (bound - origin) + origin;
		if (r >= bound) // correct for rounding
			r = Math.nextDown(bound);
		return r;
	}
}
