package helpers;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.*;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;


public class Files {

	public static String folder(String path) {
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

	public static String file(String path) {
		if (! path.contains("/"))
			return path;
		else
			return path.substring(path.lastIndexOf("/") + 1);
	}
	
	public static List<File> getAllFilesWithExtensionInDirectory(String path, String... extensions) {
		File dir = new File(path);
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		return files;
	}
	
	/**
	 * Geef alle log-files van een activiteit in de Validatie-set map.
	 * @result	String van alle padnamen gescheiden door spaties
	 */
	public static String logFilesValFromActivity(String activity) {
		List<File> files = getAllFilesWithExtensionInDirectory("Data/"+activity+"/Validatie-set", "log");
		String result = "";
		for (File file : files) {
			result += file.getPath() + " ";
		}
		return result;
	}

	public static File[] startsWith(String folder, final String start) {
		File dir = new File(folder);
		File[] foundFiles = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith(start) && name.endsWith(".log") && (! name.endsWith(".cut.log"));
		    }
		});
		return foundFiles;
	}
	
	public static void writeFile(String path, String content) {
		File dir = new File(new File(path).getParent());
		dir.mkdirs();
		PrintWriter writer;
		try {
			writer = new PrintWriter(path, "UTF-8");
			writer.println(content);
			writer.close();
		} catch (Exception e) {
			System.out.println("Fout met schrijven naar bestand: "+path);
		}
	}
	
	public static void appendToFile(String path, String content) {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
		    out.println(content);
		}catch (IOException e) {
			System.out.println("Fout met toevoegen aan bestand: "+path);
		}
	}
	
	public static boolean exists(String filename) {
		File f = new File(filename);
		return f.exists() && !f.isDirectory();
	}
	
	public static File[] getLogsFromFolder(File folder){
		File[] listOfLogs;
		FilenameFilter logFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".log");
			}
		};
		return listOfLogs = folder.listFiles(logFilter);
	}
	
	public static File[] getSettingsFromFolder(File folder){
		File[] listofSettings;
		FilenameFilter settingsFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".json");
			}
		};
		return listofSettings = folder.listFiles(settingsFilter);
	}

	public static File[] getCsvFromFolder(File folder){
		File[] listofCsv;
		FilenameFilter csvFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".csv");
			}
		};
		return listofCsv = folder.listFiles(csvFilter);
	}
	
	
	public static File[] getAccuracyFile(File folder){
		File[] listOfAccuracyFiles;
		FilenameFilter accuracyFilter = new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.toLowerCase().contains("accuracy");
			}
		};
		return listOfAccuracyFiles = folder.listFiles(accuracyFilter);
		
	}
	public static String readFile(String path) throws IOException {
		return new Scanner( new File(path) ).useDelimiter("\\A").next();
	}
	
	public static boolean deleteFile(String path) {
		try{
    		File file = new File(path);
    		if(file.delete()){
    			return true;
    		}else{
    			return false;
    		}
    	}catch(Exception e){
    		return false;
    	}
	}
	
	public static List<File> subDirectories(String dir) {
		File file = new File(dir);
		String[] names = file.list();
		
		List<File> result = new ArrayList<File>();

		for(String name : names)
		{
		    if (new File(dir + "/" + name).isDirectory())
		    {
		        result.add(new File(dir + "/" + name));
		    }
		}
		return result;
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
