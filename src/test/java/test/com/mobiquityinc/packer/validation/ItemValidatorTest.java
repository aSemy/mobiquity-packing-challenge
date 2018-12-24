package test.com.mobiquityinc.packer.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.validation.ItemValidator;
import com.mobiquityinc.packer.validation.ValidationError;

import test.com.mobiquityinc.PackerRandomTestUtils;

public class ItemValidatorTest {

	@Test
	public void testNullItems() {
		ItemValidator iv = new ItemValidator();

		List<ValidationError> errors = iv.validateItems(null);

		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("null items"));
	}

	@Test
	public void testEmptyItems() {
		ItemValidator iv = new ItemValidator();

		List<ValidationError> errors = iv.validateItems(new ArrayList<>());

		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("0 items"));
	}

	@Test
	public void testTooManyItems() {
		ItemValidator iv = new ItemValidator();

		ArrayList<Item> items = PackerRandomTestUtils.getRandomItems(ItemValidator.MAX_NUM_OF_ITEMS + 1, 1,
				BigDecimal.valueOf(100));

		List<ValidationError> errors = iv.validateItems(items);

		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("Exceeded max number of items"));
	}

	@Test
	public void testDuplicateItemIndex() {
		ItemValidator iv = new ItemValidator();

		ArrayList<Item> items = Stream.of(new Item(1, 1, 111), new Item(1, 2, 222), new Item(3, 3, 333),
				new Item(55, 55, 5555), new Item(55, 66, 6666)).collect(Collectors.toCollection(ArrayList::new));

		List<ValidationError> errors = iv.validateItems(items);

		assertEquals(2, errors.size());
		assertTrue(errors.get(0).getMessage().contains("duplicated index (1)"));
		assertTrue(errors.get(1).getMessage().contains("duplicated index (55)"));
	}

	@Test
	public void nullItem() {

		ItemValidator iv = new ItemValidator();

		List<ValidationError> errors = iv.validateItem(null);
		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("null item"));
	}

	@Test
	public void itemNegativeWeight() {

		ItemValidator iv = new ItemValidator();

		Item item = new Item(123, -123, 123);

		List<ValidationError> errors = iv.validateItem(item);
		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("non-positive weight"));
	}

	@Test
	public void itemZeroWeight() {

		ItemValidator iv = new ItemValidator();

		Item item = new Item(123, 0, 123);

		List<ValidationError> errors = iv.validateItem(item);
		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("non-positive weight"));
	}

	@Test
	public void itemNegativeCost() {

		ItemValidator iv = new ItemValidator();

		Item item = new Item(123, 99.9, -1);

		List<ValidationError> errors = iv.validateItem(item);
		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("non-positive cost"));
	}

	@Test
	public void itemZeroCost() {

		ItemValidator iv = new ItemValidator();

		Item item = new Item(123, 99.9, 0);

		List<ValidationError> errors = iv.validateItem(item);
		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("non-positive cost"));
	}

	@Test
	public void itemZeroCostAndWeight() {

		ItemValidator iv = new ItemValidator();

		Item item = new Item(123, 0, 0);

		List<ValidationError> errors = iv.validateItem(item);
		assertEquals(2, errors.size());
		assertTrue(errors.stream().filter(e -> e.getMessage().contains("non-positive cost")).findFirst().isPresent());
		assertTrue(errors.stream().filter(e -> e.getMessage().contains("non-positive weight")).findFirst().isPresent());
	}

	@Test
	public void itemNegativeCostAndWeight() {

		ItemValidator iv = new ItemValidator();

		Item item = new Item(123, -123, -123);

		List<ValidationError> errors = iv.validateItem(item);
		assertEquals(2, errors.size());
		assertTrue(errors.stream().filter(e -> e.getMessage().contains("non-positive cost")).findFirst().isPresent());
		assertTrue(errors.stream().filter(e -> e.getMessage().contains("non-positive weight")).findFirst().isPresent());
	}

	@Test
	public void itemAtMaxWeightAndCost() {

		ItemValidator iv = new ItemValidator();

		Item item = new Item(123, ItemValidator.MAX_ITEM_WEIGHT, ItemValidator.MAX_ITEM_COST_IN_CENTS);

		List<ValidationError> errors = iv.validateItem(item);
		assertEquals(0, errors.size());
	}

	@Test
	public void itemTooHeavyAndTooCostly() {

		ItemValidator iv = new ItemValidator();

		Item item = new Item(123, ItemValidator.MAX_ITEM_WEIGHT.add(BigDecimal.valueOf(0.01d)),
				ItemValidator.MAX_ITEM_COST_IN_CENTS + 1);

		List<ValidationError> errors = iv.validateItem(item);
		assertEquals(2, errors.size());
		assertTrue(errors.stream().filter(e -> e.getMessage().contains("Item weight")).findFirst().isPresent());
		assertTrue(errors.stream().filter(e -> e.getMessage().contains("Item cost")).findFirst().isPresent());
	}

	@Test
	public void itemTooHeavy() {

		ItemValidator iv = new ItemValidator();

		Item item = new Item(123, ItemValidator.MAX_ITEM_WEIGHT.add(BigDecimal.valueOf(0.01d)), 1);

		List<ValidationError> errors = iv.validateItem(item);
		assertEquals(1, errors.size());
		assertTrue(errors.stream().filter(e -> e.getMessage().contains("Item weight")).findFirst().isPresent());
	}

	@Test
	public void itemTooCostly() {

		ItemValidator iv = new ItemValidator();

		Item item = new Item(123, ItemValidator.MAX_ITEM_WEIGHT, ItemValidator.MAX_ITEM_COST_IN_CENTS + 1);

		List<ValidationError> errors = iv.validateItem(item);
		assertEquals(1, errors.size());
		assertTrue(errors.stream().filter(e -> e.getMessage().contains("Item cost")).findFirst().isPresent());
	}

}
