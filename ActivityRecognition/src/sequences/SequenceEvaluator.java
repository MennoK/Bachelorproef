package sequences;

import grizzled.sys;
import helpers.HelperFunctions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import play.api.libs.iteratee.internal;


import com.opencsv.CSVWriter;

import weka.classifiers.Classifier;

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

	/* public static void main(String[] args) throws Exception {
		SequenceEvaluator sequenceEvaluator = new SequenceEvaluator("./Sequenties/Models/RandomForest.all-features.model", "./sequentie1_a_l_label.csv", "./sequentie1_a_l_20150210131904.log",
				4.0, 0.25, "./Settings/settingssettings_hmm_10_120.json", 0.1);
		sequenceEvaluator.makePredictionsCsv2();
	}*/
	
	public SequenceEvaluator(String pathToModel, String pathToLabelCsv, String pathToLogFile, double windowSize, double overlap, String pathToSettingsFile, double noiseCutoff) throws Exception {
		this.classifier = (Classifier) weka.core.SerializationHelper.read(pathToModel);
		this.pathToLabelCsv = pathToLabelCsv;
		this.pathToLogFile = pathToLogFile;
		
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(pathToLabelCsv));
		this.instancesFromLabelCsv = loader.getDataSet();
		
		this.sequence = new Sequence(pathToLogFile, windowSize, overlap, pathToSettingsFile);
		this.noiseCutoff = noiseCutoff;
		
	}

	/**
	 * @throws IOException 
	 * 
	 */
	@Deprecated
	public String makePredictionsCsv() throws IOException{
		
		//create csv file and writer with header
		CSVWriter csvOutput = new CSVWriter(new FileWriter("./predictioncsv.csv"));
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
			newLine[2] = getLabel(i, i+5);
			newLine[3] = "";
			
			csvOutput.writeNext(newLine);
		}
		
		csvOutput.close();
		
		return "Prediction csv made!";
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String makePredictionsCsv2() throws Exception{
		
		//create csv file and writer with header
		CSVWriter csvOutput = new CSVWriter(new FileWriter("./predictioncsv.csv"));
		String[] record = FILE_HEADER.split(",");
		csvOutput.writeNext(record);
				
		double startTime = HelperFunctions.getStartTime(pathToLogFile);
		double endTime = HelperFunctions.getEndTime(pathToLogFile);
		double step = 0.5;
		
		for(double i = startTime; i <= endTime; i+=step){
			String[] newLine = new String[4];
			newLine[0] = String.valueOf(i);
			newLine[1] = String.valueOf((i+step));
			newLine[2] = getLabel(i, i+step);
			newLine[3] = sequence.getPrediction(i, classifier, noiseCutoff);
			
			csvOutput.writeNext(newLine);
		}
		
		csvOutput.close();
		
		return "Prediction csv made!";
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
	private String getLabel(double startTime, double endTime){
		for(int i = 0; i < instancesFromLabelCsv.numInstances(); i++){
			if(startTime >= instancesFromLabelCsv.instance(i).value(0)*10 && endTime <= instancesFromLabelCsv.instance(i).value(1)*10){
				return instancesFromLabelCsv.instance(i).toString(2);
				
			}
		}
		return "Ruis";
	}
}
