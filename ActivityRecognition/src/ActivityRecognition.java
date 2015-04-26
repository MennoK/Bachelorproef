import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.ShellFactory;
import au.com.bytecode.opencsv.CSVReader;
import helpers.Classify;
import helpers.Features;
import helpers.Files;
import helpers.HelperFunctions;
import helpers.DataSet;
import helpers.MotionFingerprint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import sequences.SequenceEvaluator;

import com.opencsv.CSVWriter;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


public class ActivityRecognition {
	
	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			if (args[0].equals("plot") && args.length == 2) {
				String print = plot(args[1]);
				System.out.println(print);
			}
			else if (args[0].equals("plot") && args.length == 3) {
				String print = plot(Double.parseDouble(args[1]),args[2]);
				System.out.println(print);
			}
			else if (args[0].equals("longplot") && args.length == 2) {
				String print = longplot(args[1]);
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
			else if (args[0].equals("combineTraining") && args.length == 3) {
				String print = combineTraining(args[1],args[2]);
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
			else if (args[0].equals("makehmmsettings") && args.length == 4) {
				String print = makehmmsettings(args[1],Integer.parseInt(args[2]),Integer.parseInt(args[3]));
				System.out.println(print);
			}
			else if (args[0].equals("expsettings")) {
				String[] args2 = { };
				ExpSettings.main(args2);
			}
			/*else if (args[0]. equals("expsettingshmms")) {
				String[] args2 = { };
				ExpSettingsHMMs.main(args2);
			}*/
			else if (args[0].equals("expfeatureset")) {
				String[] args2 = { };
				ExpFeatureSet.main(args2);
			}
			else if (args[0].equals("expmethod")) {
				String[] args2 = { };
				ExpMethod.main(args2);
			}
			else if (args[0].equals("csvsort") && args.length == 3) {
				String print = csvsort(args[1], args[2]);
				System.out.println(print);
			}
			else if (args[0].equals("eval") && args.length == 8) {
				String print = evaluateSequence(args[1], args[2], args[3], Double.parseDouble(args[4]), Double.parseDouble(args[5]), args[6], Double.parseDouble(args[7]), "");
				System.out.println(print);
			}
			else if (args[0].equals("eval") && args.length == 9) {
				String print = evaluateSequence(args[1], args[2], args[3], Double.parseDouble(args[4]), Double.parseDouble(args[5]), args[6], Double.parseDouble(args[7]), args[8]);
				System.out.println(print);
			}
			else if (args[0].equals("accuracy") && args.length == 3) {
				String print = accuracy(args[1], args[2]);
				System.out.println(print);
			}
			else if (args[0].equals("shorterlog") && args.length == 2) {
				String print = shorterlog(args[1]);
				System.out.println(print);
			}
			else if (args[0].equals("generatedat") && args.length == 2) {
				String print = generateDatFile((args[1]));
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
		Files.writeFile("HMMs/"+states+"-"+iterations+"/settings-"+activity+".json",settings);
		
		// bereken HMM model
		MotionFingerprint.command("--settings HMMs/"+states+"-"+iterations+"/settings-"+activity+".json --hmm HMMs/"+states+"-"+iterations+"/model-"+activity+".jahmm "+Files.logFilesValFromActivity(activity));
		
		// HMM in de juiste vorm zetten
		System.out.println("HMM in de juiste vorm zetten...");
		HelperFunctions.hmm("HMMs/"+states+"-"+iterations+"/model-"+activity+".jahmm");
		return "HMM model voor "+activity+" gemaakt: HMMs/"+states+"-"+iterations+"/model-"+activity+".jahmm";
	}
		
	/**
	 * Maak settings-bestand voor alle HMM bestanden in een opgegeven map.
	 * 
	 * @param	directory
	 * 			Opgegeven map
	 * @param	states
	 * @param	iterations
	 */
	@Command(description="Maak settings-bestand voor alle HMM bestanden in een opgegeven map.")
	public static String makehmmsettings(@Param(name="directory", description="Opgegeven map") String directory, int states, int iterations) {
		
		List<File> hmms = Files.getAllFilesWithExtensionInDirectory(directory, "jahmm");
		
		JSONArray hmm_files = new JSONArray();
		for (File file : hmms) {
			String hmm_file = directory + "/" + file.getName();
			hmm_files.add(hmm_file);
		}
		
		String settingsfile = HelperFunctions.settings2(-1, 12, 0.41666666666666663, 4, 0.8333333333333333, "haar", 10, 10, 0.5, 1.0, true, false, 4.0, states, iterations, hmm_files);
		
		String filename = "Experimenten/Settings-HMMs/"+states+"-"+iterations+"/settings.json";
		
		Files.writeFile(filename, settingsfile.replace("\\", ""));
		
		return "";
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
	public static String combineTraining(@Param(name="path", description="Pad naar folder met trainingsets") String path, @Param(name="path", description="Pad naar output") String output) throws IOException{
	
		//Haal alle csvs-bestanden op 
		File csvfolder = new File(path);
		File[] listOfCsv = Files.getCsvFromFolder(csvfolder);
		
		
		
		
		return Features.combineTrainingSets2(listOfCsv,output);
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
	 * Plot de eerste X seconden een opgegeven log-bestand in een PDF-bestand
	 * @param	seconds
	 * 			Het aantal seconden dat moet geplot worden
	 * @param 	path
	 * 			Pad naar log-bestand
	 */
	@Command(description="Plot de eerste X seconden een opgegeven log-bestand in een PDF-bestand")
	public static String plot(@Param(name="seconds", description="Het aantal seconden dat moet geplot worden") double seconds,
			@Param(name="path", description="Pad naar log-bestand") String path) {
		//System.out.println(cut(path,0.0,startTime+seconds));
		String pathNEW = path.substring(0, path.length() - 4) + ".cut.log";
		try {
			HelperFunctions.makeShorterLogFileFromStart(path, pathNEW, (int) seconds*50);
		} catch (Exception e) {
			return "Fout: kon log-bestand niet korter maken";
		}
		return plot(pathNEW);
	}
	
	/**
	 * Plot een opgegeven log-bestand in een lange plot
	 * @param 	path
	 * 			Pad naar log-bestand
	 * @throws IOException 
	 */
	@Command(description="Plot een opgegeven log-bestand in een lange plot")
	public static String longplot(@Param(name="path", description="Pad naar log-bestand") String path) throws IOException {
		// zet de inhoud van het logbestand in een string
		String s = new String(readAllBytes(get(path)));
		
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		
		// maak arrays van alle gegevens
		int NUM_VALUES = measurements.size();
		double[][] xValues = new double[2][NUM_VALUES];
		double[][] yValues = new double[2][NUM_VALUES];
		double[][] zValues = new double[2][NUM_VALUES];
		Iterator<JSONObject> iterator = measurements.iterator();
		int i=0;
		while(iterator.hasNext()) {
			JSONObject m = iterator.next();
			double x = (double) m.get("x");
			double y = (double) m.get("y");
			double z = (double) m.get("z");
			xValues[0][i] = (long) m.get("timestamp");
			xValues[1][i] = x;
			yValues[0][i] = (long) m.get("timestamp");
			yValues[1][i] = y;
			zValues[0][i] = (long) m.get("timestamp");
			zValues[1][i] = z;
			i++;
		}
		
		// timestamps in seconden...
		double timestampfactor = HelperFunctions.timestampfactor((long) zValues[0][1], (long) zValues[0][zValues[0].length-1], NUM_VALUES);
		for (int j=0; j < zValues[0].length; j++) {
			xValues[0][j] *= timestampfactor;
			yValues[0][j] *= timestampfactor;
			zValues[0][j] *= timestampfactor;
		}
		
		int minuut = 1;
		for (int n=0; n <= zValues[0].length; n+=60*50) { // per minuut een afbeelding maken
			
			double[][] xValues2 = new double[2][zValues[0].length];
			xValues2[0] = Arrays.copyOfRange(xValues[0], n, Math.min(xValues[0].length, n+60*50));
			xValues2[1] = Arrays.copyOfRange(xValues[1], n, Math.min(xValues[1].length, n+60*50));
			double[][] yValues2 = new double[2][zValues[0].length];
			yValues2[0] = Arrays.copyOfRange(yValues[0], n, Math.min(yValues[0].length, n+60*50));
			yValues2[1] = Arrays.copyOfRange(yValues[1], n, Math.min(yValues[1].length, n+60*50));
			double[][] zValues2 = new double[2][zValues[0].length];
			zValues2[0] = Arrays.copyOfRange(zValues[0], n, Math.min(zValues[0].length, n+60*50));
			zValues2[1] = Arrays.copyOfRange(zValues[1], n, Math.min(zValues[1].length, n+60*50));
			
		
			// gebruik die arrays voor een grafiek te maken met jFreeChart
			DefaultXYDataset dataset = new DefaultXYDataset();
			dataset.addSeries("x", xValues2);
			dataset.addSeries("y", yValues2);
			dataset.addSeries("z", zValues2);
		    JFreeChart chart = ChartFactory.createXYLineChart(
		         "Accelerometer data (" + path + ")", // The chart title
		         "Tijd (s)", // x axis label
		         "Versnelling (m.s-2)", // y axis label
		         dataset, // The dataset for the chart
		         PlotOrientation.VERTICAL,
		         true, // Is a legend required?
		         false, // Use tooltips
		         false // Configure chart to generate URLs?
		    );
	
		    // opslaan als afbeelding
		    File imageFile = new File(path.substring(0, path.indexOf(".log")) + "-minuut"+minuut+".png");
		    int width = (int) (xValues2[0].length*2.5);
		    ChartUtilities.saveChartAsPNG(imageFile, chart, width, 400);
	
		    // tonen in venster
		    //JFrame frame = new XYChartExample();
		    //frame.getContentPane().add(new ChartPanel(chart));
		    //frame.setSize(NUM_VALUES, 350);
		    //frame.setVisible(true);
		    //frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		    
		    minuut++;
	    
		}
	    
	    return "";
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
		return HelperFunctions.setLabel(path, label);
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
	
	/**
	 * Sorteer de kolommen van een csv-bestand in alfabetische volgorde (met "label" als laatste kolom).
	 * 
	 * @param 	path
	 * 			Pad naar csv-bestand.
	 * 			Er wordt verwacht dat in dit de laatste kolom de "label" kolom is.
	 * @param 	newPath
	 * 			Pad naar nieuw csv-bestand
	 */
	@Command(description="Sorteer de kolommen van een csv-bestand in alfabetische volgorde.")
	public static String csvsort(@Param(name="path", description="Pad naar csv-bestand") String path, @Param(name="path", description="Pad naar nieuw csv-bestand") String newPath) throws IOException {
		
		HelperFunctions.sortCsv(path, newPath);
		
		return "De kolommen van " + path + " werden gesorteerd en geschreven naar " + newPath;
	}
	
	/**
	 * Evalueer een sequentie met:
	 * @param pathToModel			Pad naar gebruikt model
	 * @param pathToLabelCsv		Pad naar csv-bestand met de echte labels
	 * @param pathToLogFile			Pad naar log-bestand (=metingen)
	 * @param windowSize			Lente van tijdsvensters in seconden
	 * @param overlap				Relatieve overlap van tijdsvensters (vb: 0.5 => overlap in seconden = 0.5*windowSize)
	 * @param pathToSettingsFile	Pad naar settings-bestand om de features te berekenen
	 * @param noiseCutoff			Minimum kans van een label. Indien de kans < noiseCutoff voor elk mogelijk label => wordt als ruis beschoud
	 * @throws Exception 
	 */
	@Command
	public static String evaluateSequence(String pathToModel, String pathToLabelCsv, String pathToLogFile, double windowSize, double overlap, String pathToSettingsFile, double noiseCutoff, String pattern) throws Exception {
		SequenceEvaluator evaluator = new SequenceEvaluator(pathToModel, pathToLabelCsv, pathToLogFile, windowSize, overlap, pathToSettingsFile, noiseCutoff, pattern);
		return evaluator.makePredictionsCsv2();
		
	}
	
	/**
	 * Bereken het percentage van juiste labels van een prediction csv
	 * @param pathToPredictionCsv   Pad naar prediction csv
	 * @param pathToLabelCsv		Pad naar csv-bestand met de echte labels
	 * @throws Exception 
	 */
	@Command
	public static String accuracy(String pathToPredictionCsv, String pathToLabelCsv) throws Exception {
		SequenceEvaluator evaluator = new SequenceEvaluator(pathToLabelCsv);
		return evaluator.getAccuracy(pathToPredictionCsv);		
	}
	
	@Command
	public static String shorterlog(String path) throws IOException {
		double startTime = HelperFunctions.getStartTime(path);
		double endTime = HelperFunctions.getEndTime(path);
		double newEndTime = startTime+5;
		String pathNEW = path.replace("Data/","Data2/");
		if (newEndTime < endTime) {
			cut2(path,startTime,newEndTime);
		}
		else {
			String content = Files.readFile(path);
			Files.writeFile(pathNEW, content);
		}
		return "New (shorter) log file made in Data2 - yeeeey!";
	}
	
	/**
	 * met pathNEW = Data2/[zelfde pad]
	 */
	private static String cut2(@Param(name="path", description="Pad naar log-bestand") String path,
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
		String pathNEW = path.replace("Data/","Data2/");
		System.out.println("Log-bestand knippen...");
		try {
			HelperFunctions.makeShorterLogFile(path, pathNEW, start, end);
		} catch (IOException e) {
			return "Fout met knippen";
		}
		return path+" werd geknipt en weggeschreven in ander log-bestand: "+pathNEW;
	}
	
	@Command
	public static String generateDatFile(@Param(name="path", description = "pad naar folder") String path) throws IOException{
		File folder = new File(path);
		File[] listOfAccuracyFiles = Files.getAccuracyFile(folder);
		File outputdatfile = new File(path + "plotfile.dat");
		FileWriter writer = new FileWriter(outputdatfile);

		
		for(File file : listOfAccuracyFiles){
			String[] split = file.toString().split("-");
			String windowssize = split[3];
			String overlapperc = split[4];
			BufferedReader buffer = new BufferedReader(new FileReader(file));
			String accuracy = buffer.readLine();
			System.out.println("add: "+ windowssize + " " + overlapperc + " " + accuracy );
			buffer.close();
			writer.write(windowssize + " " + overlapperc + " " + accuracy + "\n");
		}
		
		writer.close();
	
		return ".dat file generated";
		
	}
	
		
}