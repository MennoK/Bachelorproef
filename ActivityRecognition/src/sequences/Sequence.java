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
	
	public final String name;
	
	public List<Window> windows;
	
	public Sequence(String pathToLogFile, double windowSeconds, double overlap, String pathToSettingsFile) throws Exception {
		this.pathToLogFile = pathToLogFile;
		this.windowSeconds = windowSeconds;
		this.overlap = overlap;
		this.name = FilenameUtils.removeExtension(FilenameUtils.getName(pathToLogFile));
		this.windows = split(pathToLogFile, windowSeconds, overlap, pathToSettingsFile);
	}
	
	/**
	 * Geeft het voorspelde label terug in de sequentie van (startTimeStamp) tot (startTimeStamp+0.5)
	 * of "Ruis" indien grootste kans voor de labels <= noiseCutoff
	 * @param startTimeStamp		De starttijd in seconden
	 * @param classifier			Classifier om de labels te voorspellen
	 * @param noiseCutoff			Cut-off kans voor ruis: als grootste kans voor de labels <= noiseCutoff, geef dan "Ruis" terug
	 */
	public String getPrediction(double startTimeStamp, Classifier classifier, double noiseCutoff) throws Exception {
		
		// hashmap met (label,kans) tupels met kans = max. over windows indien zelfde label meerdere keren voorkomt
		HashMap<String,Double> labels = new HashMap<>();
		for (Window window : windows) {
			if (window.containsTimestamp(startTimeStamp) && window.containsTimestamp(startTimeStamp+0.5)) {
				String label = window.getPredictedLabel(classifier);
				double probability = window.getProbability(classifier);
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
		
		return prediction;
	}
	
	/**
	 * Knipt de sequentie in een lijst van tijdsvensters.
	 */
	private List<Window> split(String pathToLogFile, double windowSeconds, double overlap, String pathToSettingsFile) throws Exception {
		List<Window> windows = new ArrayList<>();
		double startSeconds = HelperFunctions.getStartTimestamp(pathToLogFile);
		double endSeconds = startSeconds + windowSeconds;
		// voeg alle windows tot en met de voorlaatste
		while (endSeconds < HelperFunctions.getEndTimestamp(pathToLogFile)) {
			// voeg het window toe
			windows.add(new Window(pathToLogFile, startSeconds, endSeconds, pathToSettingsFile));
			// update startSeconds en endSeconds
			startSeconds = endSeconds - overlap * windowSeconds;
			endSeconds = startSeconds + windowSeconds;
		}
		// voeg laatste window toe, is waarschijnlijk korter
		endSeconds = HelperFunctions.getEndTimestamp(pathToLogFile);
		windows.add(new Window(pathToLogFile, startSeconds, endSeconds, pathToSettingsFile));
		return windows;
	}

}
