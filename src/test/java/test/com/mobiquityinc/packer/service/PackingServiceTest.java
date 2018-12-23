package test.com.mobiquityinc.packer.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.model.Parcel;
import com.mobiquityinc.packer.model.ParcelSolution;
import com.mobiquityinc.packer.service.PackingService;
import com.mobiquityinc.packer.validation.ItemValidator;

import test.com.mobiquityinc.PackerRandomTestUtils;

public class PackingServiceTest {

	@Test
	public void test_allFit() {
		PackingService ps = new PackingService();

		BigDecimal maxWeight = new BigDecimal(30d);

		Parcel p = new Parcel(maxWeight);

		Item i1 = new Item(1, 10, 10);
		Item i2 = new Item(2, 10, 20);
		Item i3 = new Item(3, 10, 30);

		ArrayList<Item> items = new ArrayList<>(Arrays.asList(i1, i2, i3));

		ParcelSolution solution = ps.optimise(p, items);

		assertEquals(items.stream().mapToLong(i -> i.getCostInCents()).sum(), solution.getTotalCostInCents());

		assertEquals(items.stream().map(i -> i.getWeight()).reduce(BigDecimal.ZERO, BigDecimal::add),
				solution.getCurrentWeight());

		assertEquals("1,2,3", solution.getStringOutput());
	}

	@Test
	public void test_oneTooHeavy() {
		PackingService ps = new PackingService();

		BigDecimal maxWeight = new BigDecimal(30d);

		Parcel p = new Parcel(maxWeight);

		Item i1 = new Item(1, 10, 10);
		Item i2 = new Item(2, 10, 20);
		Item i3 = new Item(3, maxWeight.add(BigDecimal.valueOf(1)), 30);

		ArrayList<Item> items = new ArrayList<>(Arrays.asList(i1, i2, i3));

		ParcelSolution solution = ps.optimise(p, items);

		assertEquals(i1.getCostInCents() + i2.getCostInCents(), solution.getTotalCostInCents());

		assertEquals(i1.getWeight().add(i2.getWeight()), solution.getCurrentWeight());

		assertEquals("1,2", solution.getStringOutput());
	}

	@Test
	public void test_allTooHeavy() {
		PackingService ps = new PackingService();

		BigDecimal maxWeight = new BigDecimal(30d);

		Parcel p = new Parcel(maxWeight);

		Item i1 = new Item(1, maxWeight.add(BigDecimal.valueOf(1)), 10);
		Item i2 = new Item(2, maxWeight.add(BigDecimal.valueOf(2)), 20);
		Item i3 = new Item(3, maxWeight.add(BigDecimal.valueOf(3)), 30);

		ArrayList<Item> items = new ArrayList<>(Arrays.asList(i1, i2, i3));

		ParcelSolution solution = ps.optimise(p, items);

		assertEquals(0, solution.getTotalCostInCents());

		assertEquals(BigDecimal.ZERO, solution.getCurrentWeight());

		assertEquals("-", solution.getStringOutput());
	}

	@Test
	public void test_SameCost_DifferentWeights1() {
		PackingService ps = new PackingService();

		BigDecimal maxWeight = new BigDecimal(10d);

		Parcel p = new Parcel(maxWeight);

		Item i1 = new Item(1, BigDecimal.valueOf(1), 100);
		Item i2 = new Item(2, BigDecimal.valueOf(10), 100);
		
		assert i1.getCostInCents() == i2.getCostInCents();

		ArrayList<Item> items = new ArrayList<>(Arrays.asList(i1, i2));

		ParcelSolution solution = ps.optimise(p, items);

		assertEquals(i1.getCostInCents(), solution.getTotalCostInCents());

		assertEquals(i1.getWeight(), solution.getCurrentWeight());

		assertEquals("1", solution.getStringOutput());
	}

	@Test
	public void test_SameCost_DifferentWeights2() {
		PackingService ps = new PackingService();

		BigDecimal maxWeight = new BigDecimal(10d);

		Parcel p = new Parcel(maxWeight);

		Item i1 = new Item(1, BigDecimal.valueOf(10), 100);
		Item i2 = new Item(2, BigDecimal.valueOf(1), 100);
		
		assert i1.getCostInCents() == i2.getCostInCents();

		ArrayList<Item> items = new ArrayList<>(Arrays.asList(i1, i2));

		ParcelSolution solution = ps.optimise(p, items);

		assertEquals(i2.getCostInCents(), solution.getTotalCostInCents());

		assertEquals(i2.getWeight(), solution.getCurrentWeight());

		assertEquals("2", solution.getStringOutput());
	}

	/**
	 * Test data from example, line 1
	 * 
	 * 81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)
	 */
	@Test
	public void testDataLine1() {
		PackingService ps = new PackingService();

		BigDecimal maxWeight = new BigDecimal(81);

		Parcel p = new Parcel(maxWeight);

		Item i1 = new Item(1, BigDecimal.valueOf(53.38), 45 * 100);
		Item i2 = new Item(2, BigDecimal.valueOf(88.62), 98 * 100);
		Item i3 = new Item(3, BigDecimal.valueOf(78.48), 3 * 100);
		Item i4 = new Item(4, BigDecimal.valueOf(72.30), 76 * 100);
		Item i5 = new Item(5, BigDecimal.valueOf(30.18), 9 * 100);
		Item i6 = new Item(6, BigDecimal.valueOf(46.34), 48 * 100);

		ArrayList<Item> items = new ArrayList<>(Arrays.asList(i1, i2, i3, i4, i5, i6));

		ParcelSolution solution = ps.optimise(p, items);

		assertEquals(i4.getCostInCents(), solution.getTotalCostInCents());

		assertEquals(i4.getWeight(), solution.getCurrentWeight());

		assertEquals("4", solution.getStringOutput());
	}

	/**
	 * Test data from example, line 2
	 * 
	 * 8 : (1,15.3,€34)
	 */
	@Test
	public void testDataLine2() {
		PackingService ps = new PackingService();

		BigDecimal maxWeight = new BigDecimal(8);

		Parcel p = new Parcel(maxWeight);

		Item i1 = new Item(1, BigDecimal.valueOf(15.3), 34 * 100);

		ArrayList<Item> items = new ArrayList<>(Arrays.asList(i1));

		ParcelSolution solution = ps.optimise(p, items);

		assertEquals(0, solution.getTotalCostInCents());

		assertEquals(BigDecimal.ZERO, solution.getCurrentWeight());

		assertEquals("-", solution.getStringOutput());
	}

	/**
	 * Test data from example, line 3
	 * 
	 * 75 : (1,85.31,€29) (2,14.55,€74) (3,3.98,€16) (4,26.24,€55) (5,63.69,€52) (6,76.25,€75) (7,60.02,€74) (8,93.18,€35) (9,89.95,€78)
	 */
	@Test
	public void testDataLine3() {
		PackingService ps = new PackingService();

		BigDecimal maxWeight = new BigDecimal(75);

		Parcel p = new Parcel(maxWeight);

		Item i1 = new Item(1, BigDecimal.valueOf(85.31), 29 * 100);
		Item i2 = new Item(2, BigDecimal.valueOf(14.55), 74 * 100);
		Item i3 = new Item(3, BigDecimal.valueOf(3.98), 16 * 100);
		Item i4 = new Item(4, BigDecimal.valueOf(26.24), 55 * 100);
		Item i5 = new Item(5, BigDecimal.valueOf(63.69), 52 * 100);
		Item i6 = new Item(6, BigDecimal.valueOf(76.25), 75 * 100);
		Item i7 = new Item(7, BigDecimal.valueOf(60.02), 74 * 100);
		Item i8 = new Item(8, BigDecimal.valueOf(93.18), 35 * 100);
		Item i9 = new Item(9, BigDecimal.valueOf(89.95), 78 * 100);

		ArrayList<Item> items = new ArrayList<>(Arrays.asList(i1, i2, i3, i4, i5, i6, i7, i8, i9));

		ParcelSolution solution = ps.optimise(p, items);

		assertEquals(i2.getCostInCents() + i7.getCostInCents(), solution.getTotalCostInCents());

		assertEquals(i2.getWeight().add(i7.getWeight()), solution.getCurrentWeight());

		assertEquals("2,7", solution.getStringOutput());
	}

	/**
	 * Test data from example, line 4
	 * 
	 * 56 : (1,90.72,€13) (2,33.80,€40) (3,43.15,€10) (4,37.97,€16) (5,46.81,€36) (6,48.77,€79) (7,81.80,€45) (8,19.36,€79) (9,6.76,€64)
	 */
	@Test
	public void testDataLine4() {
		PackingService ps = new PackingService();

		BigDecimal maxWeight = new BigDecimal(56);

		Parcel p = new Parcel(maxWeight);

		Item i1 = new Item(1, BigDecimal.valueOf(90.72), 13 * 100);
		Item i2 = new Item(2, BigDecimal.valueOf(33.80), 40 * 100);
		Item i3 = new Item(3, BigDecimal.valueOf(43.15), 10 * 100);
		Item i4 = new Item(4, BigDecimal.valueOf(37.97), 16 * 100);
		Item i5 = new Item(5, BigDecimal.valueOf(46.81), 36 * 100);
		Item i6 = new Item(6, BigDecimal.valueOf(48.77), 79 * 100);
		Item i7 = new Item(7, BigDecimal.valueOf(81.80), 45 * 100);
		Item i8 = new Item(8, BigDecimal.valueOf(19.36), 79 * 100);
		Item i9 = new Item(9, BigDecimal.valueOf(6.76), 64 * 100);

		ArrayList<Item> items = new ArrayList<>(Arrays.asList(i1, i2, i3, i4, i5, i6, i7, i8, i9));

		ParcelSolution solution = ps.optimise(p, items);

		assertEquals(i8.getCostInCents() + i9.getCostInCents(), solution.getTotalCostInCents());

		assertEquals(i8.getWeight().add(i9.getWeight()), solution.getCurrentWeight());

		assertEquals("8,9", solution.getStringOutput());
	}
	
	@Test
	public void test15RandomItems_1() {
		
		BigDecimal parcelMaxWeight = BigDecimal.valueOf(50);
		
		Parcel p = new Parcel(parcelMaxWeight);
		
		ArrayList<Item> items = PackerRandomTestUtils.getRandomItems(ItemValidator.MAX_NUM_OF_ITEMS, 1, p.getMaxWeight());
		BigDecimal createdWeight = items.stream().map(i -> i.getWeight()).reduce(BigDecimal.ZERO, BigDecimal::add);
		
		assertEquals(0, parcelMaxWeight.compareTo(createdWeight));
		
		PackingService ps = new PackingService();
		ParcelSolution solution = ps.optimise(p, items);
		

		// all weight should have been used
		assertEquals(0, createdWeight.compareTo(solution.getCurrentWeight()));
		assertEquals(0, BigDecimal.ZERO.compareTo(solution.getRemainingWeightLimit()));
		
		// all items should be in solution
		assertEquals(items.size(), solution.getItems().size());
	}
	
	@Test
	public void test15RandomItems_2() {
		
		BigDecimal parcelMaxWeight = BigDecimal.valueOf(50);
		
		Parcel p = new Parcel(parcelMaxWeight);
		
		ArrayList<Item> items = PackerRandomTestUtils.getRandomItems(ItemValidator.MAX_NUM_OF_ITEMS, 1, p.getMaxWeight());
		BigDecimal createdWeight = items.stream().map(i -> i.getWeight()).reduce(BigDecimal.ZERO, BigDecimal::add);
		
		assertEquals(0, parcelMaxWeight.compareTo(createdWeight));
		
		PackingService ps = new PackingService();
		ParcelSolution solution = ps.optimise(p, items);
		

		// all weight should have been used
		assertEquals(0, createdWeight.compareTo(solution.getCurrentWeight()));
		assertEquals(0, BigDecimal.ZERO.compareTo(solution.getRemainingWeightLimit()));
		
		// all items should be in solution
		assertEquals(items.size(), solution.getItems().size());
	}
}
