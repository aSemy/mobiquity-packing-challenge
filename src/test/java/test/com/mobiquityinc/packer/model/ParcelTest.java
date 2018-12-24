package test.com.mobiquityinc.packer.model;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.mobiquityinc.packer.model.Parcel;

public class ParcelTest {

	@Test
	public void testCompare_OtherNull() {
		Parcel aParcel = new Parcel(1, BigDecimal.valueOf(2.22d));

		assertTrue(aParcel.compareTo(null) < 0);
	}

	@Test
	public void testCompare_OtherSame() {
		Parcel aParcel = new Parcel(1, BigDecimal.valueOf(2.22d));

		assertTrue(aParcel.compareTo(aParcel) == 0);
	}

	@Test
	public void testCompare_OtherSameLineNumber() {
		Parcel aParcel = new Parcel(1, BigDecimal.valueOf(1.11d));
		Parcel bParcel = new Parcel(1, BigDecimal.valueOf(2.22d));

		assertTrue(aParcel.compareTo(bParcel) == 0);
		assertTrue(bParcel.compareTo(aParcel) == 0);
	}

	@Test
	public void testCompare_OtherDifferent() {
		Parcel aParcel = new Parcel(1, BigDecimal.valueOf(1.11d));
		Parcel bParcel = new Parcel(2, BigDecimal.valueOf(2.22d));

		assertTrue(aParcel.compareTo(bParcel) < 0);
		assertTrue(bParcel.compareTo(aParcel) > 0);
	}

	@Test
	public void testToString() {
		Parcel aParcel = new Parcel(14123, BigDecimal.valueOf(2.22d));

		assertTrue(aParcel.toString().contains("lineNumber=14123"));
		assertTrue(aParcel.toString().contains("maxWeight=2.22"));
	}
}
