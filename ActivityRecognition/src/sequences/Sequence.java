package sequences;

import helpers.HelperFunctions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class Sequence {
	
	public final String pathToLogFile;
	
	public final String name;
	
	public Instances instances;
	
	public Sequence(String pathToLogFile, double windowSeconds, double overlap) {
		this.pathToLogFile = pathToLogFile;
		this.name = FilenameUtils.removeExtension(FilenameUtils.getName(pathToLogFile));
		
	}
	
	/**
	 * opm: en geeft de bestandsnaam van het .csv-bestand met de features
	 * @throws IOException 
	 */
	private String calculateFeatures(String pathToLogFile, double startSeconds, double endSeconds, String pathToSettingsFile) throws IOException {
		String pathToShorterLogFile = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut.log";
		String pathToCsv = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut.csv";
		HelperFunctions.makeShorterLogFile(this.pathToLogFile, pathToShorterLogFile, startSeconds, endSeconds);
		helpers.Features.settingsFeatures(pathToShorterLogFile, pathToSettingsFile);
		return pathToCsv;
	}
	
	private Instances getInstances(String pathToCsvFile, double windowSeconds, double overlap) {
		try {
			CSVLoader csv = new CSVLoader();
			csv.setFile(new File(pathToLogFile));
			this.instances = csv.getDataSet();
			this.instances.setClassIndex(this.instances.numAttributes() - 1);
		} catch (Exception e) {
			System.out.println("Log-bestand niet gevonden: "+pathToLogFile);
		}
		return null; // TODO
	}

}
