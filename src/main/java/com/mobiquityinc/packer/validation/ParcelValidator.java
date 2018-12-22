package com.mobiquityinc.packer.validation;

import java.math.BigDecimal;

import com.mobiquityinc.packer.model.Parcel;

public class ParcelValidator {

	private static final long MAX_WEIGHT = 100;

	public void validateParcel(Parcel parcel) {

		assert parcel != null;
		assert parcel.getMaxWeight()
				.compareTo(BigDecimal.valueOf(MAX_WEIGHT)) <= 0 : "Parcel cannot have weight more than " + MAX_WEIGHT;
		assert parcel.getMaxWeight().compareTo(BigDecimal.ZERO) > 0 : "Parcel max weight must be greater than 0";

	}

}
