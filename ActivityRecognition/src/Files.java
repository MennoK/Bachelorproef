import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.apache.commons.io.*;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;


public class Files {

	static String folder(String path) {
		if (null != path && path.length() > 0 ) {
			int endIndex = path.lastIndexOf("/");
			if (endIndex != -1) {
				path = path.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
			}
		}
		else
			return "";
		return path;
	}

	static String file(String path) {
		if (! path.contains("/"))
			return path;
		else
			return path.substring(path.lastIndexOf("/") + 1);
	}

	static File[] startsWith(String folder, final String start) {
		File dir = new File(folder);
		File[] foundFiles = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith(start) && name.endsWith(".log") && (! name.endsWith(".cut.log"));
		    }
		});
		return foundFiles;
	}
	
	static void writeFile(String path, String content) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(path, "UTF-8");
			writer.println(content);
			writer.close();
		} catch (Exception e) {
			System.out.println("Fout met schrijven naar bestand");
		}
	}
	
	static void appendToFile(String path, String content) {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
		    out.println("the text");
		}catch (IOException e) {
			System.out.println("Fout met toevoegen aan bestand");
		}
	}
	
	static boolean exists(String filename) {
		File f = new File(filename);
		return f.exists() && !f.isDirectory();
	}

	/*public File[] allFiles(String folder) {
		File dir = new File(folder);
		Collection<File> files = FileUtils.listFiles(
				  dir, 
				  new RegexFileFilter("^(.log)"), 
				  DirectoryFileFilter.DIRECTORY
				);
		String[] fileNames = new String[files.size()];
		for (int i=0; i<fileNames.length; i++)
			fileNames[i] = ((File) files.get(i)).getPath();
	}*/
	
}