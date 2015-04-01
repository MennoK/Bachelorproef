package sequences;

import grizzled.sys;
import helpers.Files;
import helpers.HelperFunctions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import play.api.libs.iteratee.internal;


import au.com.bytecode.opencsv.CSVReader;

import com.opencsv.CSVWriter;

import weka.classifiers.Classifier;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class SequenceEvaluator {
	

	private Classifier classifier;
	private String pathToLabelCsv;
	private String pathToLogFile;
	private Instances instancesFromLabelCsv;
	
	private Sequence sequence;
	private double noiseCutoff;
	
	private final static String FILE_HEADER = "Start,End,Label,Prediction";

	public static void main(String[] args) throws Exception {
		SequenceEvaluator sequenceEvaluator = new SequenceEvaluator("./Sequenties/Models/RandomForest.all-features.model", "./sequentie1_a_l_label.csv", "./sequentie1_a_l_20150210131904.log",
				4.0, 0.25, "./Settings/settingssettings_hmm_10_120.json", 0.1, "");
		sequenceEvaluator.makePredictionsCsv2();
	}
	
	public SequenceEvaluator(String pathToModel, String pathToLabelCsv, String pathToLogFile, double windowSize, double overlap, String pathToSettingsFile, double noiseCutoff, String pattern) throws Exception {
		this.classifier = (Classifier) weka.core.SerializationHelper.read(pathToModel);
		this.pathToLabelCsv = pathToLabelCsv;
		this.pathToLogFile = pathToLogFile;
		
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(pathToLabelCsv));
		this.instancesFromLabelCsv = loader.getDataSet();
		
		this.sequence = new Sequence(pathToLogFile, windowSize, overlap, pathToSettingsFile, pattern, instancesFromLabelCsv);
		this.noiseCutoff = noiseCutoff;
		
	}

	/**
	 * @throws IOException 
	 * 
	 */
	@Deprecated
	public String makePredictionsCsv() throws IOException{
		
		String path = "./predictioncsv.csv";
		//create csv file and writer with header
		CSVWriter csvOutput = new CSVWriter(new FileWriter(path));
		String[] record = FILE_HEADER.split(",");
		csvOutput.writeNext(record);
				
		long startTime = HelperFunctions.getStartTimestamp(pathToLogFile);
		String startTimeToString = Long.toString(startTime);
		
		long endTime = HelperFunctions.getEndTimestamp(pathToLogFile);
		String endTimeToString = Long.toString(endTime);
	
		int lengthOfTime = String.valueOf((long)instancesFromLabelCsv.firstInstance().value(0)*10).length();
		
		String startTimeOfLogFileString = startTimeToString.substring(0, lengthOfTime);
		long startTimeOfLogFile = Long.parseLong(startTimeOfLogFileString);
		
		String endTimeOfLogFileString = endTimeToString.substring(0, lengthOfTime);
		long endTimeOfLogFile = Long.parseLong(endTimeOfLogFileString);
	
		for(long i = startTimeOfLogFile; i <= endTimeOfLogFile; i+=5){
			String[] newLine = new String[4];
			newLine[0] = String.valueOf(i);
			newLine[1] = String.valueOf((i+5));
			newLine[2] = sequence.getLabel(i, i+5);
			newLine[3] = "";
			
			csvOutput.writeNext(newLine);
		}
		
		csvOutput.close();
		
		getAccuracy(path);
		
		return "Prediction csv made!";
	}
	
	public String getAccuracy(String path) throws IOException{
		CSVReader csvReader = new CSVReader(new FileReader(new File(path)));

		String[] nextLine;
		int totalCounter = 0;
		int correctCounter = 0;
		while((nextLine = csvReader.readNext()) != null){
			totalCounter++;
			if(nextLine[2].equalsIgnoreCase(nextLine[3])){
				correctCounter++;
			}
		}
		
		double accuracy = (double) correctCounter/((double)totalCounter);
		
		Files.writeFile("./predictionAccuracy", "Accuracy: " + accuracy);
		
		return "accuracy calculated";
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String makePredictionsCsv2() throws Exception{
		String path = "./predictioncsv.csv";

		
		//create csv file and writer with header
		CSVWriter csvOutput = new CSVWriter(new FileWriter(path));
		String[] record = FILE_HEADER.split(",");
		csvOutput.writeNext(record);
				
		double startTime = HelperFunctions.getStartTime(pathToLogFile);
		double endTime = HelperFunctions.getEndTime(pathToLogFile);
		double step = 0.5;
		
		for(double i = startTime; i <= endTime; i+=step){
			String[] newLine = new String[4];
			newLine[0] = String.valueOf(i);
			newLine[1] = String.valueOf((i+step));
			newLine[2] = sequence.getLabel(i, i+step);
			newLine[3] = sequence.getPrediction(i, classifier, noiseCutoff);
			
			csvOutput.writeNext(newLine);
		}
		
		csvOutput.close();
		
		getAccuracy(path);

		
		return "Prediction csv made!";
	}
	
	
	
	
}
