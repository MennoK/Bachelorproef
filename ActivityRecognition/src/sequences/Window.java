package sequences;

import helpers.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class Window {
	
	public Instance instance;
	public double startSeconds, endSeconds;
	
	public ArrayList<String> allActivities;
	
	public Window(String pathToLogFile, double startSeconds, double endSeconds, String pathToSettingsFile, String pattern) throws Exception {
		// berekend de features
		String pathToCsv = calculateFeatures(pathToLogFile, startSeconds, endSeconds, pathToSettingsFile, pattern);
		// maak een weka.core.Instance object aan
		CSVLoader csv = new CSVLoader();
		csv.setFile(new File(pathToCsv));
		Instances instances = csv.getDataSet();
		//instances.firstInstance().setValue(instances.numAttributes() - 1, "Nietsdoen");
		instances.setClassIndex(instances.numAttributes() - 1);
		this.instance = instances.firstInstance();
		// zet andere variabelen
		this.startSeconds = startSeconds;
		this.endSeconds = endSeconds;
		
		

	}
	
	/**
	 * opm: en geeft de bestandsnaam van het .csv-bestand met de features
	 * @throws IOException 
	 */
	private String calculateFeatures(String pathToLogFile, double startSeconds, double endSeconds, String pathToSettingsFile, String pattern) throws IOException {
		String pathToShorterLogFile = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut.log";
		String pathToSortedCsv = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut-sorted.csv";
		if (! pattern.isEmpty())
			pathToSortedCsv = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut-"+pattern+"-sorted.csv";
			
		if (! Files.exists(pathToSortedCsv)) {
			
			// knip tijdsvenster ==> korter log-bestand
			HelperFunctions.makeShorterLogFile(pathToLogFile, pathToShorterLogFile, startSeconds, endSeconds);
			
			// verander label naar Nietsdoen
			HelperFunctions.setLabel(pathToShorterLogFile, "Nietsdoen");
		
			// bereken features ==> csv
			String pathToCsv = Features.calculateFeaturesWithSettings(pathToShorterLogFile, pathToSettingsFile);
			allActivities = new ArrayList<String>();
			allActivities.add("Fietsen");
			allActivities.add("LiftAD");
			allActivities.add("LiftAU");
			allActivities.add("Lopen");
			allActivities.add("Nietsdoen");
			allActivities.add("Springen");
			allActivities.add("Tandenpoetsen");
			allActivities.add("Trapaf");
			allActivities.add("Trapop");
			allActivities.add("Wandelen");
			// verander alle NaN waarden door -1000 en voeg voor elke activiteit een dummy lijn toe
			String csvContent = Files.readFile(pathToCsv);
			csvContent = csvContent.replace("NaN", "-1000");
			int nbFeatures = csvContent.split("\n")[0].split(",").length - 1;
			for (int j=0; j < allActivities.size(); j++) {
				String line = "";
				for (int i=0; i < nbFeatures; i++) {
					line += "0,";
				}
				line += allActivities.get(j);
				csvContent += line + "\n";
			}
			Files.writeFile(pathToCsv, csvContent);
			
			// select columns
			HelperFunctions.selectColumns(pathToCsv, pathToCsv, pattern);
			
			// sorteer features alfabetisch
			HelperFunctions.sortCsv(pathToCsv, pathToSortedCsv);
			
		}
		
		return pathToSortedCsv;
	}
	
	/**
	 * Geeft het voorspelde label van dit tijdsvenster voor een gegeven classifier.
	 */
	public String getPredictedLabel(Classifier classifier) throws Exception {
		double pred = classifier.classifyInstance(this.instance);
		return this.instance.classAttribute().value((int) pred);
	}
	
	/**
	 * Geeft de waarschijnlijkheid dat het voorspelde label correct is voor een gegeven classifier.
	 */
	public double getProbability(Classifier classifier) throws Exception {
		double[] distribution = classifier.distributionForInstance(this.instance);
		List<Double> distributionList = Arrays.asList(ArrayUtils.toObject(distribution));
        return Collections.max(distributionList);
	}
	
	/**
	 * Controleert of een gegeven timestamp in dit tijdsvenster ligt.
	 */
	public boolean containsTimestamp(double timestamp) {
		return this.startSeconds <= timestamp && timestamp <= this.endSeconds;
	}
	

}
