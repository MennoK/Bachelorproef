package sequences;

import helpers.DataSet;
import helpers.HelperFunctions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class Sequence {
	
	public final String pathToLogFile;
	public double windowSeconds, overlap;
	private Instances instancesFromLabelCsv;
	
	public final String name;
	
	public List<Window> windows;
	
	public Sequence(String pathToLogFile, double windowSeconds, double overlap, String pathToSettingsFile, String pattern, Instances instancesFromLabelCsv) throws Exception {
		this.pathToLogFile = pathToLogFile;
		this.windowSeconds = windowSeconds;
		this.overlap = overlap;
		this.name = FilenameUtils.removeExtension(FilenameUtils.getName(pathToLogFile));
		this.windows = split(pathToLogFile, windowSeconds, overlap, pathToSettingsFile, pattern);
		this.instancesFromLabelCsv = instancesFromLabelCsv;
	}
	
	/**
	 * Geeft het voorspelde label terug in de sequentie van (startTimeStamp) tot (startTimeStamp+0.5)
	 * of "Ruis" indien grootste kans voor de labels <= noiseCutoff
	 * @param startTimeStamp		De starttijd in seconden
	 * @param classifier			Classifier om de labels te voorspellen
	 * @param noiseCutoff			Cut-off kans voor ruis: als grootste kans voor de labels <= noiseCutoff, geef dan "Ruis" terug
	 */
	public String getPrediction(double startTimeStamp, Classifier classifier, double noiseCutoff) throws Exception {
		
		double endTimeStamp = startTimeStamp+0.5;
		
		System.out.println("*** Voorspelling maken voor " + startTimeStamp + " tot " + endTimeStamp + " ***");
		
		// hashmap met (label,kans) tupels met kans = max. over windows indien zelfde label meerdere keren voorkomt
		HashMap<String,Double> labels = new HashMap<>();
		for (Window window : windows) {
			if (window.containsTimestamp(startTimeStamp) && window.containsTimestamp(endTimeStamp)) {
				String label = window.getPredictedLabel(classifier);
				double probability = window.getProbability(classifier);
				
				System.out.println("Voorspelling voor tijdsvenster van "+window.startSeconds+" tot "+window.endSeconds+": "
						+ label + " (" + probability + ")");
				
				if (! labels.containsKey(label) || labels.get(label) < probability ) {
					labels.put(label, probability);
				}
			}
		}
		
		// label met grootste kans in de hashmap teruggeven OF "Ruis" indien grootste kans voor de labels <= noiseCutoff
		String prediction = "Ruis";
		double probability = noiseCutoff;
		for (String label : labels.keySet()) {
			if (labels.get(label) > probability) {
				prediction = label;
				probability = labels.get(label);
			}
		}
		
		System.out.println("==> voorspelde label is: " + prediction);
		System.out.println("    echte label is: " + this.getLabel(startTimeStamp,endTimeStamp));
		
		
		
		return prediction;
	}
	
	/**
	 * Returns the label of the given start start time and end time
	 * 
	 * Checks if the given start and end time is between an interval
	 * of the label csv, and returns the label
	 * 
	 * else returns label: ruis
	 * 
	 */
	public String getLabel(double startTime, double endTime){
		for(int i = 0; i < instancesFromLabelCsv.numInstances(); i++){
			if(startTime >= instancesFromLabelCsv.instance(i).value(0) && endTime <= instancesFromLabelCsv.instance(i).value(1)){
				return instancesFromLabelCsv.instance(i).toString(2);
				
			}
		}
		return "Ruis";
	}
	
	/**
	 * Knipt de sequentie in een lijst van tijdsvensters.
	 */
	private List<Window> split(String pathToLogFile, double windowSeconds, double overlap, String pathToSettingsFile, String pattern) throws Exception {
		List<Window> windows = new ArrayList<>();
		double startTime = HelperFunctions.getStartTime(pathToLogFile);
		double endTime = startTime + windowSeconds;
		// voeg alle windows tot en met de voorlaatste
		while (endTime < HelperFunctions.getEndTime(pathToLogFile)) {
			// voeg het window toe
			windows.add(new Window(pathToLogFile, startTime, endTime, pathToSettingsFile, pattern));
			// update startSeconds en endSeconds
			startTime = endTime - overlap * windowSeconds;
			endTime = startTime + windowSeconds;
		}
		// voeg laatste window toe, is waarschijnlijk korter
		endTime = HelperFunctions.getEndTime(pathToLogFile);
		windows.add(new Window(pathToLogFile, startTime, endTime, pathToSettingsFile, pattern));
		return windows;
	}

}
