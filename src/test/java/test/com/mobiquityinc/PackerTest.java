package test.com.mobiquityinc;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;

import org.junit.Test;

import com.mobiquityinc.packer.Packer;

public class PackerTest {

	@Test
	public void test() {
		String answer = Packer.pack("src\\resources\\test-data\\data1");
		
		Scanner scanner = new Scanner(answer);
		scanner.useDelimiter("\n");

		assertEquals("4", scanner.next());
		assertEquals("-", scanner.next());
		assertEquals("2,7", scanner.next());
		assertEquals("8,9", scanner.next());
		
		scanner.close();
	}
	
}
