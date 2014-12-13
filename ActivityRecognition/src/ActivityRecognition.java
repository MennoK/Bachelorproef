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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ActivityRecognition {
	
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			if (args[0].equals("plot") && args.length == 2) {
				String print = plot(args[1]);
				System.out.println(print);
			}
			else if (args[0].equals("cut") && args.length == 4) {
				String print = cut(args[1], Double.parseDouble(args[2]), Double.parseDouble(args[3]));
				System.out.println(print);
			}
			else if (args[0].equals("getlabel") && args.length == 2) {
				String print = getlabel(args[1]);
				System.out.println(print);
			}
			else if (args[0].equals("setlabel") && args.length == 3) {
				String print = setlabel(args[1], args[2]);
				System.out.println(print);
			}
			else {
				System.out.println("Commando niet begrepen...");
			}
		}
		else {
			ShellFactory.createConsoleShell("ActivityRecognition",
    										"ActivityRecognition",
    										new ActivityRecognition()).commandLoop();
		}
	}
	
	/**
	 * Evalueer de modellen voor de verchillende training sets met cross-validatie of training/test set.
	 * <br><b>Cross-validatie:</b> met k=10 en k=20 folds
	 * 
	 * @param 	type
	 * 			Type validatie: 'cross' of 'training-test'
	 */
	@Command(description="Evalueer de modellen voor de verchillende training sets met cross-validatie of training/test set.")
	public String evaluate(@Param(name="type", description="Type validatie: 'cross' of 'training-test'") String type) {
		if (type.equals("cross")) {
			// cross-validatie
			List<File> trainingSets = Files.getAllFilesWithExtensionInDirectory("Trainingsets", "csv");
			for (File file : trainingSets) {
				DataSet trainingSet = new DataSet(file.getPath());
				System.out.println("Bezig met cross-validatie voor "+trainingSet.name+" ...");
				// k = 10
				Classify.classifyAll_crossvalidation(trainingSet, 10);
				// k = 20
				Classify.classifyAll_crossvalidation(trainingSet, 20);
			}
			return "Done";
		}
		else if (type.equals("training-test")) {
			// TODO (Arne)
			// training/test set validatie
			return "";
		}
		return "Type van validatie moet ofwel 'cross' of 'training-test' zijn.";
	}
	
	public String features() { // TODO   door Menno
		// voor alle setting bestanden: berekend features van training set + test set
		// (dus voor elk log-bestand: (naam).setting1.features.csv (1 keer gemaakt worden) en voor elke setting: trainingset.setting1.csv (altijd veranderen) enz
		return "";
	}
	
	/** PRE-PROCESSING */
	
	private String path = "";
	
	/**
	 * Kies een log-bestand waaraan je wil werken
	 * @param 	path
	 * 			Pad naar log-bestand
	 */
	@Command(description="Kies een log-bestand waaraan je wil werken")
	public String log(@Param(name="path", description="Pad naar log-bestand") String path) {
		File logFile = new File(path);
		if (! logFile.exists()) {
			String folder = Files.folder(path);
			String start = Files.file(path);
			File[] matches = Files.startsWith(folder, start);
			if (matches.length > 1)
				return "Meerdere log-bestanden beginnen met '"+start+"' in de map '"+folder+"'";
			else if (matches.length == 1) {
				logFile = matches[0];
				path = logFile.getPath();
			}
			else
				return "Log-bestand niet gevonden";
		}
		this.path = path;
		return "Huidig log-bestand: "+path;
	}
	
	/**
	 * Plot een opgegeven log-bestand in een PDF-bestand
	 * @param 	path
	 * 			Pad naar log-bestand
	 */
	@Command(description="Plot een opgegeven log-bestand in een PDF-bestand")
	public static String plot(@Param(name="path", description="Pad naar log-bestand") String path) {
		File logFile = new File(path);
		if (! logFile.exists()) {
			String folder = Files.folder(path);
			String start = Files.file(path);
			File[] matches = Files.startsWith(folder, start);
			if (matches.length > 1)
				return "Meerdere log-bestanden beginnen met '"+start+"' in de map '"+folder+"'";
			else if (matches.length == 1) {
				logFile = matches[0];
				path = logFile.getPath();
			}
			else
				return "Log-bestand niet gevonden";
		}
		String pathPDF = path.substring(0, path.length() - 4) + ".pdf";
		System.out.println("PDF maken...");
		try {
			MotionFingerprint.command("--plot "+pathPDF+" "+path);
			Runtime.getRuntime().exec("gnome-open "+pathPDF);
		} catch (Exception e) {
			return path+" kon niet geplot worden";
		}
		return path+" werd geplot in: "+pathPDF;
	}
	
	/**
	 * Plot het huidige log-bestand in een PDF-bestand
	 */
	@Command(description="Plot het huidige log-bestand in een PDF-bestand")
	public String plot() {
		if (path.equals(""))
			return "Geef eerst een log-bestand met het commando: log [pad-naar-log-bestand]";
		String pathPDF = path.substring(0, path.length() - 4) + ".pdf";
		System.out.println("PDF maken...");
		try {
			MotionFingerprint.command("--plot "+pathPDF+" "+path);
			Runtime.getRuntime().exec("gnome-open "+pathPDF);
		} catch (Exception e) {
			return path+" kon niet geplot worden";
		}
		return path+" werd geplot in: "+pathPDF;
	}

	/**
	 * Knip het opgegeven log-bestand van [start] tot [end]
	 * @param 	path
	 * 			Pad naar log-bestand
	 * @param	start
	 * 			Starttijd in seconden
	 * @param 	end
	 * 			Eindtijd in seconden
	 */
	@Command(description="Knip het opgegeven log-bestand van [start] tot [end]")
	public static String cut(@Param(name="path", description="Pad naar log-bestand") String path,
						@Param(name="start", description="Starttijd in seconden") double start,
						@Param(name="end", description="Eindtijd in seconden") double end) {
		File logFile = new File(path);
		if (! logFile.exists()) {
			String folder = Files.folder(path);
			String start2 = Files.file(path);
			File[] matches = Files.startsWith(folder, start2);
			if (matches.length > 1)
				return "Meerdere log-bestanden beginnen met '"+start2+"' in de map '"+folder+"'";
			else if (matches.length == 1) {
				logFile = matches[0];
				path = logFile.getPath();
			}
			else
				return "Log-bestand niet gevonden";
		}
		String pathNEW = path.substring(0, path.length() - 4) + ".cut.log";
		System.out.println("Log-bestand knippen...");
		try {
			HelperFunctions.makeShorterLogFile(path, pathNEW, start, end);
		} catch (IOException e) {
			return "Fout met knippen";
		}
		plot(pathNEW);
		return path+" werd geknipt en weggeschreven in ander log-bestand: "+pathNEW;
	}
	
	/**
	 * Knip het huidige log-bestand van [start] tot [end]
	 * @param 	start
	 * 			Starttijd in seconden
	 * @param 	end
	 * 			Eindtijd in seconden
	 */
	@Command(description="Knip het huidige log-bestand van [start] tot [end]")
	public String cut(@Param(name="start", description="Starttijd in seconden") double start,
						@Param(name="end", description="Eindtijd in seconden") double end) {
		if (path.equals(""))
			return "Geef eerst een log-bestand met het commando: log [pad-naar-log-bestand]";
		String pathNEW = path.substring(0, path.length() - 4) + ".cut.log";
		System.out.println("Log-bestand knippen...");
		try {
			HelperFunctions.makeShorterLogFile(path, pathNEW, start, end);
		} catch (IOException e) {
			return "Fout met knippen";
		}
		plot(pathNEW);
		return path+" werd geknipt en weggeschreven in ander log-bestand: "+pathNEW;
	}
	
	/**
	 * Toon het label van het opgegeven log-bestand
	 * @param 	path
	 * 			Pad naar log-bestand
	 */
	@Command(description="Toon het label van het opgegeven log-bestand")
	public static String getlabel(@Param(name="path", description="Pad naar log-bestand") String path) {
		File logFile = new File(path);
		if (! logFile.exists()) {
			String folder = Files.folder(path);
			String start2 = Files.file(path);
			File[] matches = Files.startsWith(folder, start2);
			if (matches.length > 1)
				return "Meerdere log-bestanden beginnen met '"+start2+"' in de map '"+folder+"'";
			else if (matches.length == 1) {
				logFile = matches[0];
				path = logFile.getPath();
			}
			else
				return "Log-bestand niet gevonden";
		}
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
	
	/**
	 * Toon het label van het huidige log-bestand
	 */
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
	
	/**
	 * Verander het label van het opgegeven log-bestand
	 * @param 	path
	 * 			Pad naar log-bestand
	 * @param 	label
	 * 			Nieuw label
	 */
	@Command(description="Verander het label van het opgegeven log-bestand")
	public static String setlabel(@Param(name="path", description="Pad naar log-bestand") String path,
							@Param(name="label", description="Nieuw label") String label) {
		File logFile = new File(path);
		if (! logFile.exists()) {
			String folder = Files.folder(path);
			String start2 = Files.file(path);
			File[] matches = Files.startsWith(folder, start2);
			if (matches.length > 1)
				return "Meerdere log-bestanden beginnen met '"+start2+"' in de map '"+folder+"'";
			else if (matches.length == 1) {
				logFile = matches[0];
				path = logFile.getPath();
			}
			else
				return "Log-bestand niet gevonden";
		}
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
	
	/**
	 * Verander het label van het huidige log-bestand
	 * @param 	label
	 * 			Nieuw label
	 */
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
	
}