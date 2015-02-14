import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Features {

	/**
	 * Berekend de features van een log-bestand met settings.json bestand
	 */
	static String settingsFeatures(String pathLog, String pathSettings){
		String settingsname = pathSettings.substring(pathSettings.lastIndexOf("/")+1,pathSettings.lastIndexOf("."));
		String csvPath = pathLog.substring(0,pathLog.lastIndexOf(".log")) + "." + settingsname + ".features.csv";
		MotionFingerprint.command("--settings " + pathSettings + " --features "+ csvPath + " " + pathLog);
		return "Features van " + pathLog + " werden berekend met " + pathSettings;
	}

	/**
	 * Berekend de features van logs files uit een folder adhv verschillende
	 * settings bestanden
	 * 
	 * @param logs		: lijst met logs files
	 * @param settings  : lijst met settings  files
	 */
	static String features(File[] logs, File[] settings) {
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
	static String combineDataSets(File[] csvs, File[] settings) throws IOException{
		for(File setting: settings){			
		
			String settingname = setting.getName().substring(0,setting.getName().indexOf(".json"));
			String[] parts = csvs[0].toString().split("/");
			String activity = parts[2];
			String outputname = /*"trainingset_" + */ activity /* + "_" + settingname */ + ".csv";
			
			List<Path> pathsToCsv = new ArrayList<>();

			for(File csv: csvs){
				if(csv.toString().toLowerCase().contains(settingname.toLowerCase() + ".")){
					pathsToCsv.add(csv.toPath());
				}
			}
			Path output = Paths.get("./TrainingSets/" + outputname);
			List<String> mergedLines = mergeFiles(pathsToCsv);
			Files.write(output, mergedLines, Charset.forName("UTF-8"));
			
		}
		return "Csv files zijn gecombineerd";
	}
	
	/**
	 * 
	 * @param trainingcsvs
	 */
	static String combineTrainingSets(File[] trainingcsvs) throws IOException{
		
		for(int i = 1; i <= 10; i++){
			
			String settingname = "settings" + i + ".";
			List<Path> pathsToCsv = new ArrayList<>();

			for(File csv: trainingcsvs){
				if(csv.toString().toLowerCase().contains(settingname.toLowerCase())){
					pathsToCsv.add(csv.toPath());
				}
			}
			
			String outputname = "Trainingset" + i + ".csv";
			
			Path output = Paths.get("./TrainingSets/" + outputname);
			List<String> mergedLines = mergeFiles(pathsToCsv);
			Files.write(output, mergedLines, Charset.forName("UTF-8"));
		}
				
		return "Trainingsets zijn gecombineerd";
				
	}
	
	/**
	 * Combineert de lijnen van de csv-files
	 * @param csvfiles
	 */
	static List<String> mergeFiles(List<Path> csvfiles) throws IOException {
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



