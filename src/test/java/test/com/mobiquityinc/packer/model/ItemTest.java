package test.com.mobiquityinc.packer.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mobiquityinc.packer.model.Item;

public class ItemTest {

	@Test
	public void testCompare_OtherNull() {
		Item anItem = new Item(1, 2.2d, 100);

		assertTrue(anItem.compareTo(null) < 0);
	}

	@Test
	public void testCompare_OtherSame() {
		Item anItem = new Item(1, 2.2d, 100);

		assertTrue(anItem.compareTo(anItem) == 0);
	}

	@Test
	public void testCompare_OtherSameIndex() {
		Item anItem = new Item(1, 11.1d, 111);
		Item otherItem = new Item(1, 22.2d, 222);

		assertTrue(anItem.compareTo(otherItem) == 0);
		assertTrue(otherItem.compareTo(anItem) == 0);
	}

	@Test
	public void testCompare_OtherDifferentIndex() {
		Item anItem = new Item(1, 11.1d, 111);
		Item otherItem = new Item(2, 22.2d, 222);

		assertTrue(anItem.compareTo(otherItem) < 0);
		assertTrue(otherItem.compareTo(anItem) > 0);
	}

	@Test
	public void testToString_doubleWeightConstructor() {
		Item anItem = new Item(1, 11.0461d, 111);

		assertTrue(anItem.toString().contains("index=1"));
		assertTrue(anItem.toString().contains("weight=11.0461"));
		assertTrue(anItem.toString().contains("costInCents=111"));
	}

	@Test
	public void testToString_longWeightConstructor() {
		Item anItem = new Item(1, 123413, 111);

		assertTrue(anItem.toString().contains("index=1"));
		assertTrue(anItem.toString().contains("weight=123413"));
		assertTrue(anItem.toString().contains("costInCents=111"));
	}

}
