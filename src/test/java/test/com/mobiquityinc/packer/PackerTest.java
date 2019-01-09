package test.com.mobiquityinc.packer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.Packer;
import com.mobiquityinc.packer.model.ParcelSolution;

public class PackerTest {

	private static File file;

	@Before
	public void beforeEachTest() throws IOException {
		file = File.createTempFile("test", ".dat");
		file.deleteOnExit();
	}

	@After
	public void afterEachTest() {
		file.delete();
	}

	@Test
	public void testFromExampleDataFile() {

		String answer = Packer.pack("src" + File.separatorChar + "resources" + File.separatorChar + "test-data"
				+ File.separatorChar + "data1");

		try (Scanner scanner = new Scanner(answer)) {
			scanner.useDelimiter("\n");

			assertEquals("4", scanner.next());
			assertEquals(ParcelSolution.INVALID_INPUT_RESPONSE, scanner.next());
			assertEquals("2,7", scanner.next());
			assertEquals("8,9", scanner.next());

			scanner.close();
		}
	}

	@Test
	public void testMultipleLines() throws IOException {

		FileWriter writer = new FileWriter(file);

		// all should fit
		// solution: 1,2,3,4
		writer.write("10 : (1,1,€10) (2,1,€10) (3,1,€10) (4,1,€10)\n");
		// Test recurring numbers. 3 will fit exactly, one won't
		// solution: 1,2,3
		writer.write("10 : (1,3.33333,€10) (2,3.33333,€10) (3,3.33333,€10) (4,9,€10)\n");
		// none fit
		// solution: -
		writer.write("0.9999999999 : (1,2.22,€10) (2,3.41,€10) (3,3.512,€10) (4,4.5151,€10)\n");
		// test same weight, different costs
		// solution: 2
		writer.write("20.05 : (1,20.05,€10) (2,20.05,€11)\n");

		writer.close();

		String solutionString = Packer.pack(file.getAbsolutePath());

		assertNotNull(solutionString);
		assertFalse(solutionString.trim().isEmpty());

		String[] sol = solutionString.split("\n");

		assertNotNull(sol);
		assertEquals("There are 4 inputs, so 4 solutions expected", 4, sol.length);

		assertEquals("1,2,3,4", sol[0]);
		assertEquals("1,2,3", sol[1]);
		assertEquals(ParcelSolution.INVALID_INPUT_RESPONSE, sol[2]);
		assertEquals("2", sol[3]);

	}

	@Test
	public void testAllFit() throws IOException {

		FileWriter writer = new FileWriter(file);

		// all should fit
		// solution: 1,2,3,4
		writer.write("10 : (1,1,€10) (2,1,€10) (3,1,€10) (4,1,€10)\n");

		writer.close();

		String solutionString = Packer.pack(file.getAbsolutePath());

		assertNotNull(solutionString);
		assertFalse(solutionString.trim().isEmpty());

		String[] sol = solutionString.split("\n");

		assertNotNull(sol);

		assertEquals("1,2,3,4", sol[0]);
	}

	@Test
	public void testDecimalPlaces() throws IOException {

		FileWriter writer = new FileWriter(file);

		// Test recurring numbers. 3 will fit exactly, one won't
		// solution: 1,2,3
		writer.write("10 : (1,3.33333,€10) (2,3.33333,€10) (3,3.33333,€10) (4,9,€10)\n");

		writer.close();

		String solutionString = Packer.pack(file.getAbsolutePath());

		assertNotNull(solutionString);
		assertFalse(solutionString.trim().isEmpty());

		assertEquals("1,2,3", solutionString);

	}

	@Test
	public void testNoneFit() throws IOException {

		FileWriter writer = new FileWriter(file);

		// none fit
		// solution: -
		writer.write("1 : (1,2.22,€10) (2,3.41,€10) (3,3.512,€10) (4,4.5151,€10)\n");

		writer.close();

		String solutionString = Packer.pack(file.getAbsolutePath());

		assertNotNull(solutionString);
		assertFalse(solutionString.trim().isEmpty());

		assertEquals(ParcelSolution.INVALID_INPUT_RESPONSE, solutionString);

	}

	@Test
	public void testSameWeightDifferentCosts() throws IOException {

		FileWriter writer = new FileWriter(file);

		// test same weight, different costs
		// solution: 2
		writer.write("20.05 : (1,20.05,€10) (2,20.05,€11)\n");

		writer.close();

		String solutionString = Packer.pack(file.getAbsolutePath());

		assertNotNull(solutionString);
		assertFalse(solutionString.trim().isEmpty());

		assertEquals("2", solutionString);
	}

	@Test
	public void testExactFitDecimals() throws IOException {

		FileWriter writer = new FileWriter(file);

		writer.write("23.41 : (1,11.705,€10) (2,11.705,€11)\n");

		writer.close();

		String solutionString = Packer.pack(file.getAbsolutePath());

		assertNotNull(solutionString);
		assertFalse(solutionString.trim().isEmpty());

		assertEquals("1,2", solutionString);
	}

	@Test(expected = APIException.class)
	public void testEmptyInput() throws IOException {

		FileWriter writer = new FileWriter(file);

		writer.write(" \n ");

		writer.close();

		String solutionString = Packer.pack(file.getAbsolutePath());

		assert false : "Exception should be thrown, as input file is blank. Got output: '" + solutionString + "'";
	}

	@Test(expected = APIException.class)
	public void testInvalidInput_endsEarly() throws IOException {

		FileWriter writer = new FileWriter(file);

		writer.write("23.41 : (1,11.70");

		writer.close();

		String solutionString = Packer.pack(file.getAbsolutePath());

		assert false : "Exception should be thrown, as input file is blank. Got output: '" + solutionString + "'";
	}
}
