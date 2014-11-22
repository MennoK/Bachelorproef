import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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
		
		String[] logFiles = findFileNames("data/single", "log");
		
		System.out.println("Ingelezen logs: (activiteit, nummer, persoon, kant, datum+tijd, start, einde, set, bestandsnaam)");
		
		for (String logFile : logFiles) {
			
			String log = readFile("data/single/"+logFile);
			
			// lees alle measurements in .log-bestand in
			Measurement[] measurements = getMeasurements("data/single/"+logFile);
			
			// haal activiteit naam, persoon, kant en datum+tijd uit bestandsnaam
			Pattern p = Pattern.compile("([a-z]*)([0-9]*)_([a-z]*)_(l|r)_([0-9]*).log");
			Matcher m = p.matcher(logFile);
			if(m.matches()) {
				
				String activityName = m.group(1);
				String number = m.group(2);
				String person = m.group(3);
				String side = m.group(4);
				String datetime = m.group(5);
				Activity activity = Activity.valueOf(activityName.toUpperCase());
				
				// haal start en einde uit bijhorende .txt-bestand
				String start = "";
				String end = "";
				String set = "";
				try {
					String meta = readFile("data/single/"+activityName+number+"_"+person+"_"+side+"_"+datetime+".txt").replace("\n", "").replace("\r", "");
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
					start = Long.toString(measurements[0].timestamp);
					end = Long.toString(measurements[measurements.length-1].timestamp);
					set = "training";
					String text = "start:"+start+"\n"+"end:"+end+"\n"+"set:"+set;
					writeToFile("data/single/"+activityName+number+"_"+person+"_"+side+"_"+datetime+".txt", text);
				}
				
				System.out.println("(" + activity + ", " + number + ", " + person + ", " + side + ", " + datetime + ", " + start + ", " + end + ", " + set + ", " + logFile + ")");
				
			}
			else {
				System.out.println("Fout met bestandsna(a)m(en) van logs!");
			}
			
			
		}
			
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
			Measurement measurement = new Measurement(timestamp, x, z, z);
			result[i] = measurement;
			i++;
		}
		
		return result;
		
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
    	
    	String[] fileNames = new String[files.length];
    	
    	for (int i = 0; i < files.length; i++)
    		fileNames[i] = files[i].getName();
    	
    	return fileNames;

    }
	
	public static void writeToFile(String path, String text) throws IOException {
		File file = new File(path);
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        output.write(text);
        output.close();
	}
	
}
