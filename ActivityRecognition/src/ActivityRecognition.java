import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import model.Activity;
import model.Measurement;

public class ActivityRecognition {

	public static void main(String[] args) throws IOException {
		
		if (args[0].equalsIgnoreCase("single")) {
			/* afzonderlijke activiteiten */
			singleActivities();
		}
		
		else if (args[0].equalsIgnoreCase("sequence")){
			/* sequenties van activiteiten */
			// TODO
		}
		
	}
	
	public static void singleActivities() throws IOException {
		
		/* STAP 1: lees alle .log-bestanden in, met bijhorende .txt-bestanden */
		
		String[] logFiles = findFileNames("Data", "log");
		
		System.out.println("Ingelezen logs: (activiteit, nummer, persoon, kant, datum+tijd, start, einde, set, bestandsnaam)");
		
		ArrayList<File> csvFilesTraining = new ArrayList<>();
		ArrayList<File> csvFilesTest = new ArrayList<>();
		
		for (String logFile : logFiles) {
			
						
			// lees alle measurements in .log-bestand in
			Measurement[] allMeasurements = getMeasurements("Data/" + logFile);
			
			// verder zullen we alleen die measurements bijhouden die tussen start en end liggen
			LinkedList<Measurement> measurements = new LinkedList<>();
			
			// haal activiteit naam, persoon, kant en datum+tijd uit bestandsnaam
			Pattern p = Pattern.compile("([a-zA-Z ]+)/([a-z]*)([0-9]*)_([a-z]*)_(l|r)_([0-9]*).log");
			Matcher m = p.matcher(logFile);
			if(m.matches()) {
				
				String folder = m.group(1);
				String activityName = m.group(2);
				String number = m.group(3);
				String person = m.group(4);
				String side = m.group(5);
				String datetime = m.group(6);
				Activity activity = Activity.valueOf(activityName.toUpperCase());
				
				// haal start en einde uit bijhorende .txt-bestand
				String start = "";
				String end = "";
				String set = "";
				try {
					String meta = readFile("Data/"+folder+"/"+activityName+number+"_"+person+"_"+side+"_"+datetime+".txt").replace("\n", "").replace("\r", "");
					Pattern q = Pattern.compile("start:([0-9]+)end:([0-9]+)set:(training|test)");
					Matcher l = q.matcher(meta);
					if(l.matches()) {
						start = l.group(1);
						end = l.group(2);
						set = l.group(3);
					}
				}
				catch (IOException e) {
					// meta-bestand bestaat niet, maak er dan automatisch één:
					start = Long.toString(allMeasurements[0].timestamp);
					end = Long.toString(allMeasurements[allMeasurements.length-1].timestamp);
					set = "training";
					String text = "start:"+start+"\n"+"end:"+end+"\n"+"set:"+set;
					writeToFile("Data/"+folder+"/"+activityName+number+"_"+person+"_"+side+"_"+datetime+".txt", text);
				}
				
				// laat weten welke .log-bestanden zijn ingelezen
				System.out.println("(" + activity + ", " + number + ", " + person + ", " + side + ", " + datetime + ", " + start + ", " + end + ", " + set + ", " + logFile + ")");
				
				// houd alleen de measurements tussen start en end bij
				for (Measurement measurement : allMeasurements) {
					measurements.add(measurement);
				}
				
				// maak nieuw .log-bestand zonder overbodige measurements
				makeShorterLogFile("Data/"+logFile,
						"Data/"+logFile+"2",
						start, end, activity.toString());
				
				// STAP 2: visualiseer + maak .csv bestand met features
				
				try {
					Runtime.getRuntime().exec("java -jar MotionFingerprint.jar --plot Pre-processing/Grafieken/"+activityName+number+"_"+person+"_"+side+"_"+datetime+".pdf Data/"+logFile+"2");
					Runtime.getRuntime().exec("java -jar MotionFingerprint.jar --features Pre-processing/CSV/"+activityName+number+"_"+person+"_"+side+"_"+datetime+".csv Data/"+logFile+"2");
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (set.equals("training"))
					csvFilesTraining.add(new File("Pre-processing/CSV/"+activityName+number+"_"+person+"_"+side+"_"+datetime+".csv"));
				else
					csvFilesTest.add(new File("Pre-processing/CSV/"+activityName+number+"_"+person+"_"+side+"_"+datetime+".csv"));
				
			}
			
			else {
				System.out.println("Fout met bestandsna(a)m(en) van logs!");
			}
			
		}
		
		// STAP 3: maak training-set.csv en test-set.csv
	
		if (csvFilesTraining.size() > 0)
			mergeCsvFiles(csvFilesTraining, "training-set.csv");
		
		if (csvFilesTest.size() > 0)
			mergeCsvFiles(csvFilesTest, "test-set.csv");

			
	}
	
	public static Measurement[] getMeasurements(String logfile) throws IOException {
		
		// zet de inhoud van het logbestand in een string
		String s = readFile(logfile);
		
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		
		// maak arrays van alle gegevens
		int NUM_VALUES = measurements.size();
		Measurement[] result = new Measurement[NUM_VALUES];
		Iterator<JSONObject> iterator = measurements.iterator();
		int i=0;
		while(iterator.hasNext()) {
			JSONObject m = iterator.next();
			long timestamp = (long) m.get("timestamp");
			double x = (double) m.get("x");
			double y = (double) m.get("y");
			double z = (double) m.get("z");
			Measurement measurement = new Measurement(timestamp, x, y, z);
			result[i] = measurement;
			i++;
		}
		
		return result;
		
	}
	
	public static void makeShorterLogFile(String pathIN, String pathOUT, String start, String end, String label) throws IOException {
		// zet de inhoud van het logbestand in een string
		String s = new String(readAllBytes(get(pathIN)));
		
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		
		// itereer over alle measurements en bouw ondertussen het nieuwe json object op
		Map preprocessed = new LinkedHashMap();
		JSONArray measurementsPre = new JSONArray();
		preprocessed.put("measurements", measurementsPre);
		Iterator<JSONObject> iterator = measurements.iterator();
		while(iterator.hasNext()) {
			JSONObject m = iterator.next();
			if ((Long) m.get("timestamp") >= Long.parseLong(start) && (Long) m.get("timestamp") <= Long.parseLong(end))
				measurementsPre.add(m);
		}
		preprocessed.put("has_gravity", obj.get("has_gravity"));
		preprocessed.put("user", label);
		preprocessed.put("notes", obj.get("notes"));
		StringWriter out = new StringWriter();
		JSONValue.writeJSONString(preprocessed, out);
		String jsonText = out.toString();
		
		// schrijf weg naar nieuw bestand
		PrintWriter writer = new PrintWriter(pathOUT, "UTF-8");
		writer.println(jsonText);
		writer.close();
	}
	
	public static String readFile(String path) throws IOException {
		String content = readFile(path, Charset.defaultCharset());
		return content;
	}
	
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static String[] findFileNames(String dirName, final String extension){
    	File dir = new File(dirName);

    	File[] files = dir.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename) { return filename.endsWith("."+extension); }
    	});
    	
    	String[] names = dir.list();

    	
    	String[] fileNames = new String[files.length];
    	
    	for (int i = 0; i < files.length; i++)
    		fileNames[i] = files[i].getName();
    	
    	for(String name : names)
    	{
    	    if (new File(dirName + "/" + name).isDirectory())
    	    {
    	        String[] subFiles = findFileNames(dirName + "/" + name, extension);
    	        for (int i=0; i < subFiles.length; i++)
    	        	subFiles[i] = name + "/" + subFiles[i];
    	        String[] fileNamesCopy = Arrays.copyOf(fileNames, fileNames.length);
    	        fileNames = concat(fileNamesCopy, subFiles);
    	    }
    	}
    	
    	
    	return fileNames;
    }
	
	public static String[] concat(String[] A, String[] B) {
		   int aLen = A.length;
		   int bLen = B.length;
		   String[] C= new String[aLen+bLen];
		   System.arraycopy(A, 0, C, 0, aLen);
		   System.arraycopy(B, 0, C, aLen, bLen);
		   return C;
		}
	
	public static void writeToFile(String path, String text) throws IOException {
		File file = new File(path);
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        output.write(text);
        output.close();
	}
	
	public static void mergeCsvFiles(List<File> csvFiles, String filename) throws IOException {
		
		File fileTemp = new File(filename);
        if (fileTemp.exists()){
           fileTemp.delete();
        }  
		
		String header;
		File firstFile = csvFiles.get(0);
		Scanner scanner = new Scanner(firstFile);
		header = scanner.nextLine();
		scanner.close();

		Iterator<File> iterFiles = csvFiles.iterator();
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename), true));
		writer.write(header);
		writer.newLine();
		while (iterFiles.hasNext()) {
		  File nextFile = iterFiles.next();
		  BufferedReader reader = new BufferedReader(new FileReader(nextFile));
		  String line = null;
		  line = reader.readLine();
		  while ((line = reader.readLine()) != null) {
		    writer.write(line);
		    writer.newLine();
		  }

		  reader.close();
		}
		writer.close();
		
	}
	
}
