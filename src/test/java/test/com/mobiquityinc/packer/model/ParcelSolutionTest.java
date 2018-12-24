package test.com.mobiquityinc.packer.model;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.Test;

import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.model.ParcelSolution;
import com.mobiquityinc.packer.validation.ItemValidator;

public class ParcelSolutionTest {

	@Test
	public void testToStringNoItems() {
		ParcelSolution solution = new ParcelSolution(BigDecimal.valueOf(50));

		String string = solution.toString();

		assertTrue(string.contains("items[0]={}"));
		assertTrue(string.contains("weightLimit=50"));
	}

	@Test
	public void testToStringMaxItems() {
		ParcelSolution solution = new ParcelSolution(BigDecimal.valueOf(50));

		// generate items
		ArrayList<Item> items = LongStream.range(0, ItemValidator.MAX_NUM_OF_ITEMS)
				.mapToObj(index -> new Item(index, index, index * 10)).collect(Collectors.toCollection(ArrayList::new));
		solution.setItems(items);

		String string = solution.toString();

		assertTrue(string.contains("items[" + ItemValidator.MAX_NUM_OF_ITEMS + "]="));
		assertTrue(string.contains("weightLimit=50"));
	}

	@Test
	public void testToString_ItemsButNull() {
		ParcelSolution solution = new ParcelSolution(BigDecimal.valueOf(50));

		// null item in solution
		ArrayList<Item> items = new ArrayList<>(10);
		items.add(new Item(1, 10, 100));
		items.add((Item) null);
		solution.setItems(items);

		String string = solution.toString();

		assertTrue(string.contains("items[" + items.size() + "]="));
		assertTrue(string.contains("weightLimit=50"));
	}

	@Test
	public void testToStringNullItems() {
		ParcelSolution solution = new ParcelSolution(BigDecimal.valueOf(50));
		solution.setItems(null);

		String string = solution.toString();

		assertTrue(string.contains("items[-]={null}"));
		assertTrue(string.contains("weightLimit=50"));
	}

}
