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
			else if (args[0].equals("features") && args.length == 3) {
				String print = features(args[1],args[2]);
				System.out.println(print);
			}
			else if (args[0].equals("featuresAll") && args.length == 3) {
				String print = featuresAll(args[1], args[2]);
				System.out.println(print);
			}
			else if (args[0].equals("combineTraining") && args.length == 2) {
				String print = combineTraining(args[1]);
				System.out.println(print);
			}
			else if (args[0].equals("evaluate") && args.length == 2) {
				String print = evaluate(args[1]);
				System.out.println(print);
			}
			else if (args[0].equals("makehmm") && args.length == 2) {
				String print = makehmm(args[1]);
				System.out.println(print);
			}
			else if (args[0].equals("makehmm") && args.length == 4) {
				String print = makehmm(args[1],Integer.parseInt(args[2]),Integer.parseInt(args[3]));
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
	 * Maak HMM model voor een activiteit voor de logs in de map <b>Validatie-set</b> met de default instellingen.
	 * 
	 * @param	activity
	 * 			De activiteit naam
	 */
	@Command(description="Maak HMM model voor een activiteit voor de logs in de map Validatie-set met de default instellingen.")
	public static String makehmm(@Param(name="activitity", description="Activiteit naam") String activity) {
		return makehmm(activity,10,100);
	}
	
	/**
	 * Maak HMM model voor een activiteit voor de logs in de map <b>Validatie-set</b>
	 * met opgegeven aantal states en iteraties.
	 * 
	 * @param	activity
	 * 			De activiteit naam
	 * @param	states
	 * 			Aantal states
	 * @param	iterations
	 * 			Aantal iteraties
	 */
	@Command(description="Maak HMM model voor een activiteit voor de logs in de map Validatie-set met opgegeven aantal states en iteraties.")
	public static String makehmm(@Param(name="activitity", description="Activiteit naam") String activity,
			@Param(name="states", description="Aantal states") int states,
			@Param(name="iterations", description="Aantal iteraties") int iterations) {
		System.out.println("HMM maken met MotionFingerprint...");
		
		// maak settings.json bestand
		String settings = HelperFunctions.hmmsettings(states,iterations);
		Files.writeFile("Temp/settings.json",settings);
		
		// bereken HMM model
		MotionFingerprint.command("--settings Temp/settings.json --hmm HMMs/"+states+"-"+iterations+"/"+activity+".jahmm "+Files.logFilesValFromActivity(activity));
		
		// HMM in de juiste vorm zetten
		System.out.println("HMM in de juiste vorm zetten...");
		HelperFunctions.hmm("HMMs/"+states+"-"+iterations+"/"+activity+".jahmm");
		return "HMM model voor "+activity+" gemaakt: HMMs/"+states+"-"+iterations+"/"+activity+".jahmm";
	}
	
	/**
	 * Evalueer de modellen voor de verchillende training sets met cross-validatie of training/test set.
	 * <br><b>Cross-validatie:</b> met k=10 en k=20 folds
	 * 
	 * @param 	type
	 * 			Type validatie: 'cross' of 'training-test'
	 */
	@Command(description="Evalueer de modellen voor de verchillende training sets met cross-validatie of training/test set.")
	public static String evaluate(@Param(name="type", description="Type validatie: 'cross' of 'training-test'") String type) {
		if (type.equals("cross")) {
			// cross-validatie
			List<File> trainingSets = Files.getAllFilesWithExtensionInDirectory("TrainingSets", "csv");
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
	
	/**
	 * Berekend de features van een opgegeven log-bestand met settings.json bestand
	 * 
	 * @param 	path
	 * 			Pad naar log-bestand (voorbeeld: ./Data/LiftAD/Training-set/liftad1_a_l_20141124173425.cut.log)
	 * @param 	settings
	 * 			Pad naar settings.json bestand (voorbeeld: ./Settings/settings1.json)
	 */

	@Command(description="Berekend de features van een opgegeven log-bestand met settings.json bestand")
	public static String features(@Param(name="path", description="Pad naar log-bestand (begint met ./)") String path,
			@Param(name="settings", description="Pad naar settings.json bestand (begint met ./)") String settings) { 

		File logFile = new File(path);
		File settingsFile = new File(settings);

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

		if (! settingsFile.exists()) {
			String folder = Files.folder(settings);
			String start2 = Files.file(settings);
			File[] matches = Files.startsWith(folder, start2);
			if (matches.length > 1)
				return "Meerdere setting-bestanden beginnen met '"+start2+"' in de map '"+folder+"'";
			else if (matches.length == 1) {
				settingsFile = matches[0];
				settings = settingsFile.getPath();
			}
			else
				return "Settings-bestand niet gevonden";
		}


		String folder = Files.folder(path);
		String[] parts = folder.split("/"); 
		String label = parts[2];
		setlabel(path,label);

		return Features.settingsFeatures(path,settings);
	}

	/**
	 * Berekend de features van alle log-bestanden in een meegegeven folder voor alle settings
	 * in de meegegeven settings-folder, vervolgens worden alle csv-bestanden van de bijhorende settings
	 * gecombineerd.
	 * 
	 * @param 	path
	 * 			Pad naar log-folder (voorbeeld: ./Data/LiftAD/Training-set/)
	 * @param 	settings
	 * 			Pad naar settings-folder (voorbeeld: ./Settings/)
	 */
	
	@Command(description="Berekend de features van alle log-bestanden in een meegegeven folder voor alle settings in de meegegeven settings-folder, vervolgens worden alle csv-bestanden van de bijhorende settings gecombineerd.")
	public static String featuresAll(@Param(name="path", description="Pad naar log-folder (begint met ./ en eindigt met /)") String path,
			@Param(name="settings", description="Pad naar settings-folder (begint met ./ en eindigt met /)") String settings) throws IOException{
		
		File logfolder = new File(path);
		File settingsfolder = new File(settings);
		File csvfolder = new File(path);
		
		// haal alle logfiles van de meegegeven folder op + zet juiste label
		File[] listOfLogs = Files.getLogsFromFolder(logfolder);
		if (listOfLogs.length == 0)
			return "Er zijn geen log-files in de meegegeven folder.";
		
		for(File log : listOfLogs){
			String folder = Files.folder(log.toString());
			String[] parts = folder.split("/"); 
			String label = parts[2];
			setlabel(log.toString(),label);
		}
		
		// haal alle settings op
		File[] listOfSettings = Files.getSettingsFromFolder(settingsfolder);
		if (listOfSettings.length == 0)
			return "De meegegeven settings-folder is leeg";
		
		//Bereken de features en exporteer de csv-bestanden naar dezelfde folder als log-bestanden
		System.out.println("Features worden berekend...");
		System.out.println(Features.features(listOfLogs, listOfSettings));

		//Haal alle csvs op
		File[] listOfCsv = Files.getCsvFromFolder(csvfolder);
		
		//Combineer csvs per activiteit
		System.out.println("Csv bestanden worden gecombineerd");
		return Features.combineDataSets(listOfCsv, listOfSettings);		
	}
	
	/**
	 * Combineer alle CSV files in een folder
	 * 
	 * @param 	path
	 * 			Pad naar folder met trainingsets
	 */
	@Command(description="Combineer alle CSV files in een folder")
	public static String combineTraining(@Param(name="path", description="Pad naar folder met trainingsets") String path) throws IOException{
	
		//Haal alle csvs-bestanden op 
		File csvfolder = new File(path);
		File[] listOfCsv = Files.getCsvFromFolder(csvfolder);
		
		return Features.combineTrainingSets(listOfCsv);
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