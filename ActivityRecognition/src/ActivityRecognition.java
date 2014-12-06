import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.ShellFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ActivityRecognition {
	
	public String path = "";
	
	@Command(description="Kies een log-bestand waaraan je wil werken")
	public String log(@Param(name="path", description="Pad naar log-bestand") String path) {
		File logFile = new File(path);
		if (! logFile.exists())
			return "Log-bestand niet gevonden";
		this.path = path;
		return "Huidig log-bestand: "+path;
	}
	
	@Command(description="Plot een opgegeven log-bestand in een PDF-bestand")
	public String plot(@Param(name="path", description="Pad naar log-bestand") String path) {
		File logFile = new File(path);
		if (! logFile.exists())
			return "Log-bestand niet gevonden";
		String pathPDF = path.substring(0, path.length() - 4) + ".pdf";
		System.out.println("PDF maken...");
		try {
			Process p = Runtime.getRuntime().exec("java -jar MotionFingerprint.jar --plot "+pathPDF+" "+path);
			p.waitFor();
			Runtime.getRuntime().exec("gnome-open "+pathPDF);
		} catch (Exception e) {
			return path+" kon niet geplot worden";
		}
		return path+" werd geplot in: "+pathPDF;
	}
	
	@Command(description="Plot het huidige log-bestand in een PDF-bestand")
	public String plot() {
		if (path.equals(""))
			return "Geef eerst een log-bestand met het commando: log [pad-naar-log-bestand]";
		String pathPDF = path.substring(0, path.length() - 4) + ".pdf";
		System.out.println("PDF maken...");
		try {
			Process p = Runtime.getRuntime().exec("java -jar MotionFingerprint.jar --plot "+pathPDF+" "+path);
			p.waitFor();
			Runtime.getRuntime().exec("gnome-open "+pathPDF);
		} catch (Exception e) {
			return path+" kon niet geplot worden";
		}
		return path+" werd geplot in: "+pathPDF;
	}

	@Command(description="Knip het opgegeven log-bestand van [start] tot [end]")
	public String cut(@Param(name="path", description="Pad naar log-bestand") String path,
						@Param(name="start", description="Starttijd in seconden") double start,
						@Param(name="end", description="Eindtijd in seconden") double end) {
		File logFile = new File(path);
		if (! logFile.exists())
			return "Log-bestand niet gevonden";
		String pathNEW = path.substring(0, path.length() - 4) + ".cut.log";
		System.out.println("Log-bestand knippen...");
		try {
			makeShorterLogFile(path, pathNEW, start, end);
		} catch (IOException e) {
			return "Fout met knippen";
		}
		plot(pathNEW);
		return path+" werd geknipt en weggeschreven in ander log-bestand: "+pathNEW;
	}
	
	@Command(description="Knip het huidige log-bestand van [start] tot [end]")
	public String cut(@Param(name="start", description="Starttijd in seconden") double start,
						@Param(name="end", description="Eindtijd in seconden") double end) {
		if (path.equals(""))
			return "Geef eerst een log-bestand met het commando: log [pad-naar-log-bestand]";
		String pathNEW = path.substring(0, path.length() - 4) + ".cut.log";
		System.out.println("Log-bestand knippen...");
		try {
			makeShorterLogFile(path, pathNEW, start, end);
		} catch (IOException e) {
			return "Fout met knippen";
		}
		plot(pathNEW);
		return path+" werd geknipt en weggeschreven in ander log-bestand: "+pathNEW;
	}
	
	public static void main(String[] args) throws IOException {
    	ShellFactory.createConsoleShell("ActivityRecognition",
    									"ActivityRecognition",
    									new ActivityRecognition()).commandLoop();
	}
    
	public static void makeShorterLogFile(String pathIN, String pathOUT, double start, double end) throws IOException {
		
		// zet de inhoud van het logbestand in een string
		String s = new String(readAllBytes(get(pathIN)));
		
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		
		// zet nullen achter start en end zodat lengte van timestamp = lengte van start = lengte van end
		long firstTimestamp = (long) ((Map) ((ArrayList) obj.get("measurements")).get(0)).get("timestamp");
		while (length((long) start) < length(firstTimestamp))
			start = start * 10;
		while (length((long) end) < length(firstTimestamp))
			end = end * 10;

		// itereer over alle measurements en bouw ondertussen het nieuwe json object op
		Map preprocessed = new LinkedHashMap();
		JSONArray measurementsPre = new JSONArray();
		preprocessed.put("measurements", measurementsPre);
		Iterator<JSONObject> iterator = measurements.iterator();
		while(iterator.hasNext()) {
			JSONObject m = iterator.next();
			if ((long) m.get("timestamp") >= start && (long) m.get("timestamp") <= end)
				measurementsPre.add(m);
		}
		preprocessed.put("has_gravity", obj.get("has_gravity"));
		preprocessed.put("user", obj.get("user"));
		preprocessed.put("notes", obj.get("notes"));
		StringWriter out = new StringWriter();
		JSONValue.writeJSONString(preprocessed, out);
		String jsonText = out.toString();
		
		// schrijf weg naar nieuw bestand
		PrintWriter writer = new PrintWriter(pathOUT, "UTF-8");
		writer.println(jsonText);
		writer.close();
	}
	
	@Command(description="Toon het label van het opgegeven log-bestand")
	public String getlabel(@Param(name="path", description="Pad naar log-bestand") String path) {
		// zet de inhoud van het logbestand in een string
		String s;
		try {
			s = new String(readAllBytes(get(path)));
		} catch (IOException e) {
			return "Log-bestand niet gevonden";
		}
		// maak hiervan een JSON object in Java en geef het label terug
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		return (String) obj.get("user");
	}
	
	@Command(description="Toon het label van het huidige log-bestand")
	public String getlabel() {
		if (path.equals(""))
			return "Geef eerst een log-bestand met het commando: log [pad-naar-log-bestand]";
		// zet de inhoud van het logbestand in een string
		String s;
		try {
			s = new String(readAllBytes(get(path)));
		} catch (IOException e) {
			return "Log-bestand niet gevonden";
		}
		// maak hiervan een JSON object in Java en geef het label terug
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		return (String) obj.get("user");
	}
	
	@Command(description="Verander het label van het opgegeven log-bestand")
	public String setlabel(@Param(name="path", description="Pad naar log-bestand") String path,
							@Param(name="label", description="Nieuw label") String label) {
		// zet de inhoud van het logbestand in een string
		String s;
		try {
			s = new String(readAllBytes(get(path)));
		} catch (IOException e) {
			return "Log-bestand niet gevonden";
		}
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		// bouw het nieuwe json object op
		Map preprocessed = new LinkedHashMap();
		JSONArray measurementsPre = new JSONArray();
		preprocessed.put("measurements", measurements);
		preprocessed.put("has_gravity", obj.get("has_gravity"));
		preprocessed.put("user", label);
		preprocessed.put("notes", obj.get("notes"));
		StringWriter out = new StringWriter();
		try {
			JSONValue.writeJSONString(preprocessed, out);
		} catch (IOException e) {
			return "Niet gelukt";
		}
		String jsonText = out.toString();
		// schrijf weg naar bestand
		PrintWriter writer;
		try {
			writer = new PrintWriter(path, "UTF-8");
		} catch (Exception e) {
			return "Niet gelukt";
		}
		writer.println(jsonText);
		writer.close();
		return "Label van "+path+" veranderd naar: "+label;
	}
	
	@Command(description="Verander het label van het huidige log-bestand")
	public String setlabel(@Param(name="label", description="Nieuw label") String label) {
		if (path.equals(""))
			return "Geef eerst een log-bestand met het commando: log [pad-naar-log-bestand]";
		// zet de inhoud van het logbestand in een string
		String s;
		try {
			s = new String(readAllBytes(get(path)));
		} catch (IOException e) {
			return "Log-bestand niet gevonden";
		}
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		// bouw het nieuwe json object op
		Map preprocessed = new LinkedHashMap();
		JSONArray measurementsPre = new JSONArray();
		preprocessed.put("measurements", measurements);
		preprocessed.put("has_gravity", obj.get("has_gravity"));
		preprocessed.put("user", label);
		preprocessed.put("notes", obj.get("notes"));
		StringWriter out = new StringWriter();
		try {
			JSONValue.writeJSONString(preprocessed, out);
		} catch (IOException e) {
			return "Niet gelukt";
		}
		String jsonText = out.toString();
		// schrijf weg naar bestand
		PrintWriter writer;
		try {
			writer = new PrintWriter(path, "UTF-8");
		} catch (Exception e) {
			return "Niet gelukt";
		}
		writer.println(jsonText);
		writer.close();
		return "Label van "+path+" veranderd naar: "+label;
	}
	
	/** @pre n > 0 */
	public static int length(long n) {
		return (int) (Math.log10(n)+1);
	}
	
}