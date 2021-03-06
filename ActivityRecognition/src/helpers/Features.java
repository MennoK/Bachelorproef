package helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import csvMerge.Record;

public class Features {

	/**
	 * Berekent de features van een log-bestand met settings.json bestand
	 */
	public static String settingsFeatures(String pathLog, String pathSettings){
		String settingsname = pathSettings.substring(pathSettings.lastIndexOf("/")+1,pathSettings.lastIndexOf("."));
		String csvPath = pathLog.substring(0,pathLog.lastIndexOf(".log")) + "." + settingsname + ".features.csv";
		MotionFingerprint.command("--settings " + pathSettings + " --features "+ csvPath + " " + pathLog);
		return "Features van " + pathLog + " werden berekend met " + pathSettings;
	}
	
	/**
	 * Berekent de features van een log-bestand met settings.json bestand en geeft het pad naar het resulterende csv-bestand
	 */
	public static String calculateFeaturesWithSettings(String pathLog, String pathSettings){
		String settingsname = pathSettings.substring(pathSettings.lastIndexOf("/")+1,pathSettings.lastIndexOf("."));
		String csvPath = pathLog.substring(0,pathLog.lastIndexOf(".log")) + "." + settingsname + ".features.csv";
		MotionFingerprint.command("--settings " + pathSettings + " --features "+ csvPath + " " + pathLog);
		return csvPath;
	}

	/**
	 * Berekent de features van logs files uit een folder adhv verschillende
	 * settings bestanden
	 * 
	 * @param logs		: lijst met logs files
	 * @param settings  : lijst met settings  files
	 */
	public static String features(File[] logs, File[] settings) {
		for(File log : logs){
			for(File setting : settings){
				settingsFeatures(log.toString(),setting.toString());
			}
		}
		return "Features zijn berekend";
	}

	/**
	 * Combineert de csv bestanden van dezelfde setting
	 * @throws IOException 
	 */
	public static String combineDataSets(File[] csvs, File[] settings) throws IOException{
		for(File setting: settings){			
		
			String settingname = setting.getName().substring(0,setting.getName().indexOf(".json"));
			String[] parts = csvs[0].toString().split("/");
			String activity = parts[2];
			String outputname = /*"trainingset_" + */ activity /* + "_" + settingname */ + ".csv";
			
			List<File> pathsToCsv = new ArrayList<>();

			for(File csv: csvs){
				if(csv.toString().toLowerCase().contains(settingname.toLowerCase() + ".")){
					pathsToCsv.add(csv);
				}
			}
			Path output = Paths.get("./TrainingSets/" + outputname);
			List<String> mergedLines = mergeFiles(pathsToCsv.toArray(new File[pathsToCsv.size()]));
			Files.write(output, mergedLines, Charset.forName("UTF-8"));
			
		}
		return "Csv files zijn gecombineerd";
	}
	
	/**
	 * 
	 * @param trainingcsvs
	 */
	public static String combineTrainingSets(File[] trainingcsvs) throws IOException{
		
		for(int i = 1; i <= 10; i++){
			
			String settingname = "settings" + i + ".";
			List<File> pathsToCsv = new ArrayList<>();

			for(File csv: trainingcsvs){
				if(csv.toString().toLowerCase().contains(settingname.toLowerCase())){
					pathsToCsv.add(csv);
				}
			}
			
			String outputname = "Trainingset" + i + ".csv";
			
			Path output = Paths.get("./TrainingSets/" + outputname);
			List<String> mergedLines = mergeFiles(pathsToCsv.toArray(new File[pathsToCsv.size()]));
			Files.write(output, mergedLines, Charset.forName("UTF-8"));
		}
				
		return "Trainingsets zijn gecombineerd";
				
	}
	
	/**
	 * voor 1 settings bestand
	 */
	public static String combineTrainingSets2(File[] trainingcsvs, String outputPath) throws IOException{	
		List<Path> pathsToCsv = new ArrayList<>();

		/*for(File csv: trainingcsvs){
			pathsToCsv.add(csv.toPath());
		}*/
		
		Path output = Paths.get(outputPath);
		List<String> mergedLines = mergeFiles(trainingcsvs);
		Files.write(output, mergedLines, Charset.forName("UTF-8"));
				
		return "Trainingsets zijn gecombineerd";
	}
	
	/**
	 * Combineert de lijnen van de csv-files
	 * 
	 * !! Soms niet dezelfde kolommen! (kan afhankelijk zijn van de lengte van de meting...)
	 * Hier wordt daar wel rekening mee gehouden.
	 * 
	 * @param csvfiles
	 */
	public static List<String> mergeFiles(File[] csvFiles) throws IOException {
		List<Record> records = new ArrayList<Record>();
		
		Set<String> keyStore = new HashSet<>();

		for (File file:csvFiles) {
		  List<String> keys = getColumnsHeaders(file);
		  keyStore.addAll(keys);
		  List<String> lines = Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
		  for (int i=1; i < lines.size(); i++) {
			String line = lines.get(i);
		    String[] values = line.split(",");
		    Record record = new Record(file.getName()+i);  // as an example for id
		    for (int j = 0; j < values.length; j++) {
		      record.put(keys.get(j), values[j]);
		    }
		    records.add(record);
		  }
		}
		
		List<String> result = new ArrayList<String>();
		
		String firstLine = "";
		for (String key : keyStore)
			if (! key.equals("label"))
				firstLine = firstLine + key + ",";
		firstLine = firstLine + "label";
		result.add(firstLine);
		
		for (Record record : records) {
			String line = "";
			for (String key : keyStore) {
				if (! key.equals("label")) {
					String value = record.get(key);
					if (value == null)
						line = line + "0,";
					else
						line = line + value + ",";
				}
			}
			line = line + record.get("label");
			result.add(line);
		}
		
		return result;
	}
	
	private static List<String> getColumnsHeaders(File file) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
		String firstLine = lines.get(0);
		String[] headers = firstLine.split(",");
		return new ArrayList<String>(Arrays.asList(headers));
	}

	private static List<String> mergeFilesOld(List<Path> csvfiles) throws IOException {
		List<String> mergedLines = new ArrayList<> ();
		for (Path p : csvfiles){
			List<String> lines = Files.readAllLines(p, Charset.forName("UTF-8"));
			if (!lines.isEmpty()) {
				if (mergedLines.isEmpty()) {
					mergedLines.add(lines.get(0));
				}
				mergedLines.addAll(lines.subList(1, lines.size()));
			}
		}
		return mergedLines;
	}
}



