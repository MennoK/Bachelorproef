package test;
import static org.junit.Assert.*;

import helpers.Features;
import helpers.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;


public class TestMergeFiles {

	@Test
	public void test1() throws IOException {
		String csv1 = "x,y,z,label\n1,2,3,TEST1";
		String csv2 = "x,label,z,\n4,TEST2,5";
		
		Files.writeFile("Temp/test1.csv", csv1);
		Files.writeFile("Temp/test2.csv", csv2);
		
		File[] files = { new File("Temp/test1.csv"), new File("Temp/test2.csv") };
		
		List<String> mergedList = Features.mergeFiles(files);
		
		String merged = "";
		for (String line : mergedList) {
			merged = merged + line + "\n";
		}
		
		System.out.println(merged);
	}

}
