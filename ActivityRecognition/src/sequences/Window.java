package sequences;

import helpers.*;

import java.io.File;
import java.io.IOException;
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
	
	public Window(String pathToLogFile, double startSeconds, double endSeconds, String pathToSettingsFile) throws Exception {
		// berekend de features
		String pathToCsv = calculateFeatures(pathToLogFile, startSeconds, endSeconds, pathToSettingsFile);
		// maak een weka.core.Instance object aan
		CSVLoader csv = new CSVLoader();
		csv.setFile(new File(pathToCsv));
		Instances instances = csv.getDataSet();
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
	private String calculateFeatures(String pathToLogFile, double startSeconds, double endSeconds, String pathToSettingsFile) throws IOException {
		String pathToShorterLogFile = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut.log";
		String pathToCsv = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut.csv";
		String pathToSortedCsv = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut-sorted.csv";
		HelperFunctions.makeShorterLogFile(pathToLogFile, pathToShorterLogFile, startSeconds, endSeconds);
		Features.settingsFeatures(pathToShorterLogFile, pathToSettingsFile);
		HelperFunctions.sortCsv(pathToCsv, pathToSortedCsv);
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
