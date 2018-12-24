package test.com.mobiquityinc.packer.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.model.Parcel;
import com.mobiquityinc.packer.service.ParsingService;
import com.mobiquityinc.packer.validation.ItemValidator;
import com.mobiquityinc.packer.validation.ParcelValidator;
import com.mobiquityinc.packer.validation.ValidationError;

public class ParsingServiceTest {

	private static File file;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void beforeEachTest() throws IOException {
		file = File.createTempFile("test", ".dat");
		file.deleteOnExit();
	}

	@After
	public void afterEachTest() {
		file.delete();
	}

	@Test()
	public void testUnclosedBracket_first() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("bracket");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// missing bracket
		// -----------------------\/------------------
		writer.write("10 : (1,1,€10 (2,1,€€11) (3,1,€€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test()
	public void testNoOpenBracket_first() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("bracket");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// missing bracket
		// ---------------\/-------------------------
		writer.write("10 : 1,1,€€10) (2,1,€€11) (3,1,€€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testUnclosedBracket_middle() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("bracket");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// missing bracket
		// ---------------------------------\/--------
		writer.write("10 : (1,1,€€10) (2,1,€11 (3,1,€€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test()
	public void testNoOpenBracket_middle() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("bracket");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// missing bracket
		// --------------------------\/----------------
		writer.write("10 : (1,1,€€10) 2,1,€€11) (3,1,€€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testUnclosedBracket_last() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("bracket");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// missing bracket
		// -------------------------------------------\/--
		writer.write("10 : (1,1,€10) (2,1,€11) (3,1,€11");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testNoOpenBracket_last() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("bracket");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// missing bracket
		// -----------------------------------\/-------
		writer.write("10 : (1,1,€10) (2,1,€11) 3,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testNoColon() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Expected one ' :'");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// missing colon
		// -------------\/---------------------
		writer.write("10  (1,1,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testTwoColons() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Couldn't fully parse items");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong colons
		// -------------\/---------------------
		writer.write("10 :: (1,1,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testTwoColons2() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Expected one ' :' ");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong space
		// --------------------------------\/---
		writer.write("10 : (1,1,€10) (2,1,€11) : ");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testNoSpaceBetweenBrackets() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Couldn't fully parse items");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong colons
		// -----------------------\/-----------
		writer.write("10 : (1,1,€10)(2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testNoSpaceBetweenColonAndWeight() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Expected one ' :'");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong space
		// ------------\/----------------------
		writer.write("10: (1,1,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testNoSpaceBetweenColonAndItems() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Couldn't fully parse items");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong space
		// --------------\/-------------------
		writer.write("10 :(1,1,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testInvalidParcelWeight_NAN() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Could not parse parcelWeight");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong parcel weight
		// -----------\/-------------------
		writer.write("aa : (1,1,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testInvalidParcelWeight_Missing() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("no parcel weight");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong parcel weight
		// -----------\/-------------------
		writer.write("   : (1,1,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testInvalidItems_Missing() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("no items");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// missing items
		// ----------------------\/-------------------
		writer.write("13 :                       ");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testInvalidParcelWeight_negative() throws IOException {

		// negative parcel weight is invalid, but it should still parse
		// but later it'll fail validation

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong parcel weight
		// -----------\/-------------------
		writer.write("-1 : (1,1,€10) (2,1,€11)");

		writer.close();

		TreeMap<Parcel, ArrayList<Item>> parsed = ps.parseFile(file);

		assertEquals(1, parsed.keySet().size());
		Parcel parcel = parsed.keySet().iterator().next();
		assertTrue(parcel.getMaxWeight().compareTo(BigDecimal.valueOf(-1)) == 0);

		ParcelValidator pv = new ParcelValidator();
		List<ValidationError> errors = pv.validateParcel(parcel);

		assertEquals(1, errors.size());
		assertEquals("Cannot have non-positive max weight", errors.get(0).getMessage());
	}

	@Test
	public void testInvalidParcelWeight_zero() throws IOException {

		// this will parse, but later fail validation

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong parcel weight
		// -----------\/-------------------
		writer.write("0 : (1,1,€10) (2,1,€11)");

		writer.close();

		TreeMap<Parcel, ArrayList<Item>> parsed = ps.parseFile(file);

		assertEquals(1, parsed.keySet().size());
		Parcel parcel = parsed.keySet().iterator().next();
		assertTrue(parcel.getMaxWeight().compareTo(BigDecimal.valueOf(0)) == 0);

		ParcelValidator pv = new ParcelValidator();
		List<ValidationError> errors = pv.validateParcel(parcel);

		assertEquals(1, errors.size());
		assertEquals("Cannot have non-positive max weight", errors.get(0).getMessage());
	}

	@Test
	public void testInvalidParcelWeight_empty() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Found no parcel weight");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong parcel weight
		// ----------\/-------------------
		writer.write(" : (1,1,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testInvalidIndex_nan() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Couldn't fully parse items");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong index
		// ----------------\/-------------------
		writer.write("10 : (aa,1,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testInvalidIndex_negative() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Couldn't fully parse items");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong index
		// ----------------\/-------------------
		writer.write("10 : (-1,1,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testInvalidIndex_empty() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Couldn't fully parse items");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong index
		// ----------------\/-------------------
		writer.write("10 : (,1,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testInvalidItemWeight_negative() throws IOException {

		// this will pass, but later fail validation

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item weight
		// -------------------\/-------------------
		writer.write("10 : (1,-1,€10) (2,1,€11)");

		writer.close();

		TreeMap<Parcel, ArrayList<Item>> parsed = ps.parseFile(file);

		assertEquals(1, parsed.keySet().size());
		Parcel parcel = parsed.keySet().iterator().next();
		assertTrue(parcel.getMaxWeight().compareTo(BigDecimal.valueOf(10)) == 0);

		assertEquals(2, parsed.get(parcel).size());
		// get item of index 1
		Item item1 = parsed.get(parcel).stream().filter(i -> i.getIndex() == 1).findFirst().get();
		assertTrue(item1.getWeight().compareTo(BigDecimal.valueOf(-1)) == 0);

		ItemValidator iv = new ItemValidator();
		List<ValidationError> errors = iv.validateItem(item1);

		assertEquals(1, errors.size());
		assertEquals("Cannot have non-positive weight", errors.get(0).getMessage());
	}

	@Test
	public void testInvalidItemWeight_nan() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Couldn't fully parse items");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item weight
		// -------------------\/-------------------
		writer.write("10 : (1,aa,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testInvalidItemWeight_zero() throws IOException {

		// item weight of 0 is invalid, but it should still parse
		// but later it'll fail validation

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item weight
		// ------------------\/-------------------
		writer.write("10 : (1,0,€10) (2,1,€11)");

		writer.close();

		TreeMap<Parcel, ArrayList<Item>> parsed = ps.parseFile(file);

		assertEquals(1, parsed.keySet().size());
		Parcel parcel = parsed.keySet().iterator().next();
		assertTrue(parcel.getMaxWeight().compareTo(BigDecimal.valueOf(10)) == 0);

		assertEquals(2, parsed.get(parcel).size());
		// get item of index 1
		Item item1 = parsed.get(parcel).stream().filter(i -> i.getIndex() == 1).findFirst().get();
		assertTrue(item1.getWeight().compareTo(BigDecimal.ZERO) == 0);

		ItemValidator iv = new ItemValidator();
		List<ValidationError> errors = iv.validateItem(item1);

		assertEquals(1, errors.size());
		assertEquals("Cannot have non-positive weight", errors.get(0).getMessage());
	}

	@Test
	public void testInvalidItemWeight_empty() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Couldn't fully parse items");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item weight
		// ------------------\/-------------------
		writer.write("10 : (1,,€10) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testItemCost_zero() throws IOException {

		// in a surprising turn, don't expect an exception
		// the cost is valid, so it should fail in validation

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item cost
		// ---------------------\/-------------------
		writer.write("10 : (1,1,€0) (2,1,€11)");

		writer.close();

		TreeMap<Parcel, ArrayList<Item>> parsed = ps.parseFile(file);

		assertEquals(1, parsed.keySet().size());
		Parcel parcel = parsed.keySet().iterator().next();
		assertTrue(parcel.getMaxWeight().compareTo(BigDecimal.valueOf(10)) == 0);

		assertEquals(2, parsed.get(parcel).size());
		// get item of index 1
		Item item1 = parsed.get(parcel).stream().filter(i -> i.getIndex() == 1).findFirst().get();
		assertEquals(0, item1.getCostInCents());

		ItemValidator iv = new ItemValidator();
		List<ValidationError> errors = iv.validateItem(item1);

		assertEquals(1, errors.size());
		assertEquals("Cannot have non-positive cost", errors.get(0).getMessage());
	}

	@Test
	public void testInvalidItemCost_negative() throws IOException {

		// this will parse, but later fail validation

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item cost
		// ----------------------\/-------------------
		writer.write("10 : (1,1,€-1) (2,1,€11)");

		writer.close();

		TreeMap<Parcel, ArrayList<Item>> parsed = ps.parseFile(file);

		assertEquals(1, parsed.keySet().size());
		Parcel parcel = parsed.keySet().iterator().next();
		assertTrue(parcel.getMaxWeight().compareTo(BigDecimal.valueOf(10)) == 0);

		assertEquals(2, parsed.get(parcel).size());
		// get item of index 1
		Item item1 = parsed.get(parcel).stream().filter(i -> i.getIndex() == 1).findFirst().get();
		assertEquals(-100, item1.getCostInCents());

		ItemValidator iv = new ItemValidator();
		List<ValidationError> errors = iv.validateItem(item1);

		assertEquals(1, errors.size());
		assertEquals("Cannot have non-positive cost", errors.get(0).getMessage());
	}

	@Test
	public void testInvalidItemCost_NAN() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Couldn't fully parse items");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item cost
		// ----------------------\/-------------------
		writer.write("10 : (1,1,€aa) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test
	public void testInvalidItemCost_empty() throws IOException {

		// expect exception
		thrown.expect(APIException.class);
		thrown.expectMessage("Couldn't fully parse items");

		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item cost
		// ---------------------\/-------------------
		writer.write("10 : (1,1,€) (2,1,€11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

}
