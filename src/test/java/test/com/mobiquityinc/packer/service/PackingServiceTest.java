package test.com.mobiquityinc.packer.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.model.Parcel;
import com.mobiquityinc.packer.model.ParcelSolution;
import com.mobiquityinc.packer.service.PackingService;

public class PackingServiceTest {

	@Test
	public void test() {
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
	}

}
