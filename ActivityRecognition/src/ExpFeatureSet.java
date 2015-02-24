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
	
	public static String exp = "./Experimenten/FeatureSet/InfoGain/RandomForest"; // map waaronder alle resultaten bewaard worden
	public static String data = "./Experimenten/FeatureSet/data-allfeatures.csv"; // pad naar dataset (.csv)

	public static void main(String[] args) throws Exception {
		
		// importeer data
		DataSet dataset = new DataSet(data);
		
		// kies een methode
		// String method = "weka.classifiers.trees.J48 -- -C 0.25 -M 2";
		String method = "weka.classifiers.trees.RandomForest -- -I 10 -K 0 -S 1";
		
		// evalueer methode voor verschillende aantallen features met 10-fold cross-validatie
		String output = "";
		for (int numFeatures = 1; numFeatures <= 134; numFeatures += 1) {
			
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
				  String[] options = weka.core.Utils.splitOptions("-x 2 -E \"weka.attributeSelection.InfoGainAttributeEval\" -S \"weka.attributeSelection.Ranker -N "+numFeatures+"\" -W "+method);
				  cls.setOptions(options);
				  cls.buildClassifier(train);
				  
				  Evaluation eval = new Evaluation(train);
				  eval.evaluateModel(cls, test);
				  
				  double accuracy_fold = eval.pctCorrect();
				  accuracy += accuracy_fold;
				  
			}
			
			accuracy /= folds;
			output += numFeatures + " " + accuracy + "\n";
		    
		}
		
		Files.writeFile(exp + "/results.dat", output);
		
	}
	
}
