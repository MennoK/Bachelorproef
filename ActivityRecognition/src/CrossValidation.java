import helpers.Method;

import java.util.Random;

import weka.core.Instances;

public class CrossValidation {
	
	public static void crossValidation(Method method, DataSet data, int folds, int seed) {
		
		Random rand = new Random(seed);
		Instances randData = new Instances(data.instances);
		randData.randomize(rand);
		
		//randData.stratify(folds);
		
		for (int n = 0; n < folds; n++) {
			  Instances train = randData.trainCV(folds, n);
			  Instances test = randData.testCV(folds, n);
			 
			  // further processing, classification, etc.
		}
		
	}

}
