package test.com.mobiquityinc.packer.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.Packer;
import com.mobiquityinc.packer.service.ParsingService;

public class ParsingServiceTest {

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

	@Test(expected = APIException.class)
	public void testUnclosedBracket() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// missing bracket
		// ----------------------\/-------------
		writer.write("10 : (1,1,10 (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testNoOpenBracket() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// missing bracket
		// ---------------\/---------------------
		writer.write("10 : 1,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testNoColon() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// missing colon
		// -------------\/---------------------
		writer.write("10  (1,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testTwoColons() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong colons
		// -------------\/---------------------
		writer.write("10 :: (1,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testTwoColons2() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong space
		// --------------------------------\/---
		writer.write("10 : (1,1,10) (2,1,11) : ");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testNoSpaceBetweenBrackets() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong colons
		// -----------------------\/-----------
		writer.write("10 : (1,1,10)(2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testNoSpaceBetweenColonAndWeight() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong space
		// ------------\/----------------------
		writer.write("10: (1,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testNoSpaceBetweenColonAndItems() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong space
		// --------------\/-------------------
		writer.write("10 :(1,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidParcelWeight_NAN() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong parcel weight
		// -----------\/-------------------
		writer.write("aa : (1,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidParcelWeight_negative() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong parcel weight
		// -----------\/-------------------
		writer.write("-1 : (1,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidParcelWeight_zero() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong parcel weight
		// -----------\/-------------------
		writer.write("0 : (1,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidParcelWeight_empty() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong parcel weight
		// ----------\/-------------------
		writer.write(" : (1,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidIndex_nan() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong index
		// ----------------\/-------------------
		writer.write("10 : (aa,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidIndex_negative() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong index
		// ----------------\/-------------------
		writer.write("10 : (-1,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidIndex_empty() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong index
		// ----------------\/-------------------
		writer.write("10 : (,1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidItemWeight_negative() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item weight
		// -------------------\/-------------------
		writer.write("10 : (1,-1,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidItemWeight_nan() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item weight
		// -------------------\/-------------------
		writer.write("10 : (1,aa,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidItemWeight_zero() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item weight
		// ------------------\/-------------------
		writer.write("10 : (1,0,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidItemWeight_empty() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item weight
		// ------------------\/-------------------
		writer.write("10 : (1,,10) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidItemCost_zero() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item cost
		// --------------------\/-------------------
		writer.write("10 : (1,1,0) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidItemCost_negative() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item cost
		// --------------------\/-------------------
		writer.write("10 : (1,1,-1) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidItemCost_NAN() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item cost
		// ---------------------\/-------------------
		writer.write("10 : (1,1,aa) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

	@Test(expected = APIException.class)
	public void testInvalidItemCost_empty() throws IOException {
		ParsingService ps = new ParsingService();

		FileWriter writer = new FileWriter(file);

		// wrong item cost
		// --------------------\/-------------------
		writer.write("10 : (1,1,) (2,1,11)");

		writer.close();

		ps.parseFile(file);

		fail("exception should have been thrown");
	}

}
