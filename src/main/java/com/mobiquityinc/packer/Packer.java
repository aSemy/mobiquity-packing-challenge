package com.mobiquityinc.packer;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.model.ParcelSolution;
import com.mobiquityinc.packer.service.PackingService;

public class Packer {


	public static String pack(String filePath) throws APIException {

		PackingService packingService = new PackingService();

		if (filePath == null || filePath.isEmpty())
			throw new APIException("Invalid file");

		File file = new File(filePath);

		if (!file.exists()) {
			throw new APIException("Invalid file");
		} else {
			List<ParcelSolution> solutions = packingService.optimise(file);

			return solutions.stream().map(s -> s.getStringOutput()).collect(Collectors.joining("\n"));
		}
	}
}
