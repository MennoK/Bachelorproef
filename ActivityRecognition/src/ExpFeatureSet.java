
import helpers.DataSet;
import helpers.Files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.core.Instances;

/**
 * Uitvoer: (exp)/results.dat
 */
public class ExpFeatureSet {
	
	/**
	 * Map waaronder alle resultaten bewaard worden.
	 */
	public static String exp = "./Experimenten/FeatureSet/FFT/InfoGain/RandomForest/";
	
	/**
	 * Pad naar dataset (.csv)
	 */
	public static String data = "./Experimenten/FeatureSet/data-fft.csv";
	
	/**
	 * Totaal aantal features in de dataset.
	 */
	// public static int TOTAL_NUM_FEATURES = 134; // data-allfeatures.csv
	// public static int TOTAL_NUM_FEATURES = 10; // data-hmm.csv
	// public static int TOTAL_NUM_FEATURES = 20; // data-statistic.csv
	// public static int TOTAL_NUM_FEATURES = 30; // data-dwt.csv
	public static int TOTAL_NUM_FEATURES = 84; // data-fft.csv
	
	/**
	 * Gebruikte methode (met parameters)
	 */
	// public static String method = "weka.classifiers.trees.J48 -- -C 0.25 -M 2";
	public static String method = "weka.classifiers.trees.RandomForest -- -I 10 -K 0 -S 1";
	
	/**
	 * Opties voor AttributeSelectedClassifier (zonder de parameters -N en -W)
	 */
	public static String options = "-x 2 -E \"weka.attributeSelection.InfoGainAttributeEval\" -S \"weka.attributeSelection.Ranker";
	// public static String options = "-x 2 -E \"weka.attributeSelection.ChiSquaredAttributeEval\" -S \"weka.attributeSelection.Ranker";

	public static void main(String[] args) throws Exception {
		
		// importeer data
		DataSet dataset = new DataSet(data);
		
		// evalueer methode voor verschillende aantallen features met 10-fold cross-validatie
		String output = "";
		for (int numFeatures = 1; numFeatures <= TOTAL_NUM_FEATURES; numFeatures += 1) {
			
		    double accuracy = 0.0;
		    
		    // cross-validatie
			int seed = 5;
			int folds = 10;
			Random rand = new Random(seed);
			Instances randData = new Instances(dataset.instances);
			randData.randomize(rand);
			//randData.stratify(folds);
			
			for (int n = 0; n < folds; n++) {
				  Instances train = randData.trainCV(folds, n);
				  Instances test = randData.testCV(folds, n);
				 
				  // pas attribute selectie toe en classificeer met gekozen methode voor numFeatures = het aantal features
				  Classifier cls = new AttributeSelectedClassifier();
				  String[] optionsArray = weka.core.Utils.splitOptions(options+" -N "+numFeatures+"\" -W "+method);
				  cls.setOptions(optionsArray);
				  cls.buildClassifier(train);
				  
				  Evaluation eval = new Evaluation(train);
				  eval.evaluateModel(cls, test);
				  
				  double accuracy_fold = eval.pctCorrect();
				  accuracy += accuracy_fold;
				  
			}
			
			accuracy /= folds;
			output += numFeatures + " " + accuracy + "\n";
		    
		}
		
		Files.writeFile(exp + "results.dat", output);
		
		String log = "Experiment voor feature set gedaan" + "\n"
				+ "Locatie van resultaten: " + exp + "\n"
				+ "Gebruikte dataset: " + data + "\n"
				+ "Gebruikte methode: " + method + "\n"
				+ "Opties voor AttributeSelectedClassifier: " + options;
		
		Files.writeFile(exp + "log.txt", log);
		
		System.out.println(log);
		
	}
	
}
