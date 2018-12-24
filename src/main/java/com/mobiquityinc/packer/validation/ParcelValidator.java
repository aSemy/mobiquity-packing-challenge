package com.mobiquityinc.packer.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.mobiquityinc.packer.model.Parcel;

public class ParcelValidator {

	private static final long MAX_WEIGHT = 100;

	public List<ValidationError> validateParcel(Parcel parcel) {

		List<ValidationError> errors = new ArrayList<>();

		if (parcel == null) {
			errors.add(new ValidationError("Parcel cannot be null"));
			return errors;
		}

		if (parcel.getMaxWeight().compareTo(BigDecimal.valueOf(MAX_WEIGHT)) > 0)
			errors.add(new ValidationError(
					"Parcel weight cannot exceed " + MAX_WEIGHT + ", but was " + parcel.getMaxWeight()));

		if (parcel.getMaxWeight().compareTo(BigDecimal.ZERO) <= 0)
			errors.add(new ValidationError("Cannot have non-positive max weight"));

		return errors;
	}
}
