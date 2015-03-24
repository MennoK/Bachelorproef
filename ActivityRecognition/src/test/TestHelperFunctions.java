package test;
import static org.junit.Assert.*;

import helpers.HelperFunctions;

import java.io.IOException;

import org.junit.Test;


public class TestHelperFunctions {

	@Test
	public void getAccuracy() {
		try {
			double accuracy = HelperFunctions.getAccuracy("test.txt");
			System.out.println(accuracy);
			assertTrue(accuracy == 0.925);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
