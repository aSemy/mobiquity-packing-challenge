package test.com.mobiquityinc.packer.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import com.mobiquityinc.packer.model.Parcel;
import com.mobiquityinc.packer.validation.ParcelValidator;
import com.mobiquityinc.packer.validation.ValidationError;

public class ParcelValidatorTest {

	@Test
	public void testParcelNull() {
		ParcelValidator pv = new ParcelValidator();

		List<ValidationError> errors = pv.validateParcel(null);

		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("Parcel cannot be null"));
	}

	@Test
	public void testParcelTooHeavy() {
		ParcelValidator pv = new ParcelValidator();

		Parcel p = new Parcel(1, ParcelValidator.MAX_WEIGHT.add(BigDecimal.valueOf(0.01d)));

		List<ValidationError> errors = pv.validateParcel(p);

		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("Parcel weight"));
	}

	@Test
	public void testParcelWeightZero() {
		ParcelValidator pv = new ParcelValidator();

		Parcel p = new Parcel(1, BigDecimal.ZERO);

		List<ValidationError> errors = pv.validateParcel(p);

		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("non-positive max weight"));
	}

	@Test
	public void testParcelWeightNegative() {
		ParcelValidator pv = new ParcelValidator();

		Parcel p = new Parcel(1, BigDecimal.valueOf(-1));

		List<ValidationError> errors = pv.validateParcel(p);

		assertEquals(1, errors.size());
		assertTrue(errors.get(0).getMessage().contains("non-positive max weight"));
	}
}
