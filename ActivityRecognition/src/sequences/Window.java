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
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

public class Window {
	
	public Instance instance;
	public double startSeconds, endSeconds;
	
	public ArrayList<String> allActivities;
	
	public Window(String pathToLogFile, double startSeconds, double endSeconds, String pathToSettingsFile, String pattern) throws Exception {
		// berekend de features
		String pathToArff = calculateFeatures(pathToLogFile, startSeconds, endSeconds, pathToSettingsFile, pattern);
		// maak een weka.core.Instance object aan
		DataSource source = new DataSource(pathToArff);
		Instances instances = source.getDataSet();
		//instances.firstInstance().setValue(instances.numAttributes() - 1, "Nietsdoen");
		instances.setClassIndex(instances.numAttributes() - 1);
		this.instance = instances.firstInstance();
		// zet andere variabelen
		this.startSeconds = startSeconds;
		this.endSeconds = endSeconds;
		
		

	}
	
	/**
	 * opm: en geeft de bestandsnaam van het .arff-bestand met de features
	 * @throws IOException 
	 */
	private String calculateFeatures(String pathToLogFile, double startSeconds, double endSeconds, String pathToSettingsFile, String pattern) throws IOException {
		String pathToShorterLogFile = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut.log";
		String pathToSortedCsv = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut-sorted.csv";
		String pathToSortedArff = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut-sorted.arff";
		if (! pattern.isEmpty())
			pathToSortedCsv = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut-"+pattern+"-sorted.csv";
		if (! pattern.isEmpty())
			pathToSortedArff = pathToLogFile.substring(0,pathToLogFile.indexOf(".log")) + "/" + startSeconds + "-" + endSeconds + "-cut-"+pattern+"-sorted.arff";
			
		if (! Files.exists(pathToSortedArff)) {
			
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
			
			// maak er een arff bestand van
			if (pattern.equals("hmm")) {
				csvToArffHMMs(pathToSortedCsv,pathToSortedArff);
			}
			else {
				csvToArff(pathToSortedCsv,pathToSortedArff);
			}
			
		}
		
		return pathToSortedArff;
	}
	
	private void csvToArff(String pathCsv, String pathArff) {
		String csvContent;
		try {
			csvContent = Files.readFile(pathCsv);
			String[] csvLines = csvContent.split("\n");
			String values = csvLines[1];
			String arffContent = "@relation window\n\n@attribute angle_mean numeric\n@attribute angle_stddev numeric\n@attribute corr_z_e_xy_e numeric\n@attribute corr_z_xy_e numeric\n@attribute mcr_t numeric\n@attribute mcr_z numeric\n@attribute t_freq_e_0.00 numeric\n@attribute t_freq_e_0.42 numeric\n@attribute t_freq_e_0.83 numeric\n@attribute t_freq_e_1.25 numeric\n@attribute t_freq_e_1.67 numeric\n@attribute t_freq_e_2.08 numeric\n@attribute t_freq_e_2.50 numeric\n@attribute t_freq_e_2.92 numeric\n@attribute t_freq_e_3.33 numeric\n@attribute t_freq_e_3.75 numeric\n@attribute t_freq_e_4.17 numeric\n@attribute t_freq_e_4.58 numeric\n@attribute t_freq_m_0.00 numeric\n@attribute t_freq_m_0.42 numeric\n@attribute t_freq_m_0.83 numeric\n@attribute t_freq_m_1.25 numeric\n@attribute t_freq_m_1.67 numeric\n@attribute t_freq_m_2.08 numeric\n@attribute t_freq_m_2.50 numeric\n@attribute t_freq_m_2.92 numeric\n@attribute t_freq_m_3.33 numeric\n@attribute t_freq_m_3.75 numeric\n@attribute t_freq_m_4.17 numeric\n@attribute t_freq_m_4.58 numeric\n@attribute t_freq_p1 numeric\n@attribute t_freq_p2 numeric\n@attribute t_freq_p3 numeric\n@attribute t_freq_p4 numeric\n@attribute t_mean numeric\n@attribute t_stddev numeric\n@attribute xy_e_freq_e_0.00 numeric\n@attribute xy_e_freq_e_0.42 numeric\n@attribute xy_e_freq_e_0.83 numeric\n@attribute xy_e_freq_e_1.25 numeric\n@attribute xy_e_freq_e_1.67 numeric\n@attribute xy_e_freq_e_2.08 numeric\n@attribute xy_e_freq_e_2.50 numeric\n@attribute xy_e_freq_e_2.92 numeric\n@attribute xy_e_freq_e_3.33 numeric\n@attribute xy_e_freq_e_3.75 numeric\n@attribute xy_e_freq_e_4.17 numeric\n@attribute xy_e_freq_e_4.58 numeric\n@attribute xy_e_freq_m_0.00 numeric\n@attribute xy_e_freq_m_0.42 numeric\n@attribute xy_e_freq_m_0.83 numeric\n@attribute xy_e_freq_m_1.25 numeric\n@attribute xy_e_freq_m_1.67 numeric\n@attribute xy_e_freq_m_2.08 numeric\n@attribute xy_e_freq_m_2.50 numeric\n@attribute xy_e_freq_m_2.92 numeric\n@attribute xy_e_freq_m_3.33 numeric\n@attribute xy_e_freq_m_3.75 numeric\n@attribute xy_e_freq_m_4.17 numeric\n@attribute xy_e_freq_m_4.58 numeric\n@attribute xy_e_freq_p1 numeric\n@attribute xy_e_freq_p2 numeric\n@attribute xy_e_freq_p3 numeric\n@attribute xy_e_freq_p4 numeric\n@attribute xy_e_mean numeric\n@attribute xy_e_stddev numeric\n@attribute z_dwt_ar_-0 numeric\n@attribute z_dwt_ar_-1 numeric\n@attribute z_dwt_ar_-2 numeric\n@attribute z_dwt_ar_-3 numeric\n@attribute z_dwt_ar_-4 numeric\n@attribute z_dwt_ar_-5 numeric\n@attribute z_dwt_ar_-6 numeric\n@attribute z_dwt_ar_-7 numeric\n@attribute z_dwt_ar_-8 numeric\n@attribute z_dwt_ar_-9 numeric\n@attribute z_dwt_avg_-0 numeric\n@attribute z_dwt_avg_-1 numeric\n@attribute z_dwt_avg_-2 numeric\n@attribute z_dwt_avg_-3 numeric\n@attribute z_dwt_avg_-4 numeric\n@attribute z_dwt_avg_-5 numeric\n@attribute z_dwt_avg_-6 numeric\n@attribute z_dwt_avg_-7 numeric\n@attribute z_dwt_avg_-8 numeric\n@attribute z_dwt_avg_-9 numeric\n@attribute z_dwt_norm_-0 numeric\n@attribute z_dwt_norm_-1 numeric\n@attribute z_dwt_norm_-2 numeric\n@attribute z_dwt_norm_-3 numeric\n@attribute z_dwt_norm_-4 numeric\n@attribute z_dwt_norm_-5 numeric\n@attribute z_dwt_norm_-6 numeric\n@attribute z_dwt_norm_-7 numeric\n@attribute z_dwt_norm_-8 numeric\n@attribute z_dwt_norm_-9 numeric\n@attribute z_e_mean numeric\n@attribute z_e_stddev numeric\n@attribute z_freq_e_0.00 numeric\n@attribute z_freq_e_0.42 numeric\n@attribute z_freq_e_0.83 numeric\n@attribute z_freq_e_1.25 numeric\n@attribute z_freq_e_1.67 numeric\n@attribute z_freq_e_2.08 numeric\n@attribute z_freq_e_2.50 numeric\n@attribute z_freq_e_2.92 numeric\n@attribute z_freq_e_3.33 numeric\n@attribute z_freq_e_3.75 numeric\n@attribute z_freq_e_4.17 numeric\n@attribute z_freq_e_4.58 numeric\n@attribute z_freq_m_0.00 numeric\n@attribute z_freq_m_0.42 numeric\n@attribute z_freq_m_0.83 numeric\n@attribute z_freq_m_1.25 numeric\n@attribute z_freq_m_1.67 numeric\n@attribute z_freq_m_2.08 numeric\n@attribute z_freq_m_2.50 numeric\n@attribute z_freq_m_2.92 numeric\n@attribute z_freq_m_3.33 numeric\n@attribute z_freq_m_3.75 numeric\n@attribute z_freq_m_4.17 numeric\n@attribute z_freq_m_4.58 numeric\n@attribute z_freq_p1 numeric\n@attribute z_freq_p2 numeric\n@attribute z_freq_p3 numeric\n@attribute z_freq_p4 numeric\n@attribute z_hmm_model-Fietsen numeric\n@attribute z_hmm_model-LiftAD numeric\n@attribute z_hmm_model-LiftAU numeric\n@attribute z_hmm_model-Lopen numeric\n@attribute z_hmm_model-Nietsdoen numeric\n@attribute z_hmm_model-Springen numeric\n@attribute z_hmm_model-Tandenpoetsen numeric\n@attribute z_hmm_model-Trapaf numeric\n@attribute z_hmm_model-Trapop numeric\n@attribute z_hmm_model-Wandelen numeric\n@attribute z_kurt numeric\n@attribute z_mean numeric\n@attribute z_peak_d_mean numeric\n@attribute z_peak_d_stddev numeric\n@attribute z_peak_mean numeric\n@attribute z_peak_stddev numeric\n@attribute z_stddev numeric\n@attribute zcr_z numeric\n@attribute label {Trapaf,Lopen,Trapop,Tandenpoetsen,LiftAD,Fietsen,Wandelen,LiftAU,Nietsdoen,Springen}\n\n@data";
			arffContent += "\n"+values;
			Files.writeFile(pathArff, arffContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void csvToArffHMMs(String pathCsv, String pathArff) {
		String csvContent;
		try {
			csvContent = Files.readFile(pathCsv);
			String[] csvLines = csvContent.split("\n");
			String values = csvLines[1];
			String arffContent = "@relation data-hmm-sorted\n\n@attribute z_hmm_model-Fietsen numeric\n@attribute z_hmm_model-LiftAD numeric\n@attribute z_hmm_model-LiftAU numeric\n@attribute z_hmm_model-Lopen numeric\n@attribute z_hmm_model-Nietsdoen numeric\n@attribute z_hmm_model-Springen numeric\n@attribute z_hmm_model-Tandenpoetsen numeric\n@attribute z_hmm_model-Trapaf numeric\n@attribute z_hmm_model-Trapop numeric\n@attribute z_hmm_model-Wandelen numeric\n@attribute label {Trapaf,Lopen,Trapop,Tandenpoetsen,LiftAD,Fietsen,Wandelen,LiftAU,Nietsdoen,Springen}\n\n@data";
			arffContent += "\n"+values;
			Files.writeFile(pathArff, arffContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
