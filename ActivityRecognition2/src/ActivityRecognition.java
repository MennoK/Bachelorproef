import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class ActivityRecognition {

	public static void main(String[] args) {

		if(args.length > 0) {
			if(args[0].equals("--help")){
				help();
			}
			else if(args[0].equals("--editlog")){
				try {
					editlog(args[1]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else help();
		}

	}

	public static void help(){
		System.out.println("Dit is de helppagina:");
		System.out.println("--help: opent helppagina:");
		System.out.println("--editlog + input.log: een log bestand moet als inputfile meegegeven worden");
	}

	public static void editlog(String filepath) throws IOException {

		File file = new File(filepath);
		String filename = file.getName();
		String contentFile = readFile(file.getAbsolutePath());
		String path = (String) filepath.subSequence(0, filepath.lastIndexOf("/") + 1);

		// lees alle measurements in .log-bestand in
		Measurement[] measurements = getMeasurements(file);

		// haal activiteit naam, persoon, kant en datum+tijd uit bestandsnaam
		Pattern p = Pattern.compile("([a-z]*)([0-9]*)_([a-z]*)_(l|r)_([0-9]*).log");
		Matcher m = p.matcher(filename);
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
				String meta = readFile(path+activityName+number+"_"+person+"_"+side+"_"+datetime+".txt").replace("\n", "").replace("\r", "");
				Pattern q = Pattern.compile("start:([0-9]+)end:([0-9]+)set:(training|test)");
				Matcher l = q.matcher(meta);
				if(l.matches()) {
					start = l.group(1);
					end = l.group(2);
					set = l.group(3);
				}
				
				System.out.println(filename + " is aangepast");

			}
			catch (IOException e) {
				// meta-bestand bestaat niet, maak er dan automatisch één:
				start = Long.toString(measurements[0].timestamp);
				end = Long.toString(measurements[measurements.length-1].timestamp);
				set = "training";
				String text = "start:"+start+"\n"+"end:"+end+"\n"+"set:"+set;
				writeToFile(path+activityName+number+"_"+person+"_"+side+"_"+datetime+".txt", text);
				System.out.println("Er bestond nog geen " + filename + ".txt, deze is nu aangemaakt om te wijzigen");
			}

			// maak nieuw .log-bestand zonder overbodige measurements
			File outputfile = new File(filepath);
			String outputfilename = outputfile.getName();
			outputfilename = (String) filepath.subSequence(filepath.lastIndexOf("/") + 1,filepath.lastIndexOf("_") +1);
			outputfilename = outputfilename + "2.log";
			String outputpath = (String) filepath.subSequence(0, filepath.lastIndexOf("/") + 1);
			String filepathoutput = path + outputfilename;
			
			makeShorterLogFile(filepath,
			filepathoutput,
			start, end, activity.toString());
			
		}
		else {
			System.out.println("Het bestand: " + filename + " is niet geldig!");
		}			
	}

	public static Measurement[] getMeasurements(File logfile) throws IOException {

		// zet de inhoud van het logbestand in een string
		String s = readFile(logfile.getAbsolutePath());

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
			Double x, y, z;
			try {
				x = (Double) m.get("x");
			}
			catch (Exception e) {
				x = 0.0;
			}
			try {
				y = (Double) m.get("y");
			}
			catch (Exception e) {
				y = 0.0;
			}
			try {
				z = (Double) m.get("z");
			}
			catch (Exception e) {
				z = 0.0;
			}
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
		String content = readFile(path, Charset.defaultCharset());;
		return content;
	}

	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static void writeToFile(String path, String text) throws IOException {

		File file = new File(path);
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		output.write(text);
		output.close();
	}

}
