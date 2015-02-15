import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class ExpFeatureSet {
	
	public static String exp = "./Experimenten/FeatureSet"; // map waaronder alle resultaten bewaard worden
	public static String data = "./Data2"; // map waaronder alle metingen staan

	public static void main(String[] args) throws IOException {
		
		// feature sets die moeten geëvalueerd worden
		// = lijst van training set csv-bestanden in de map Experimenten/FeatureSets/Training-sets
		String[] trainingSets = { 
									// "set1.csv", ...
								};
		
		// kies een methode
		Method method = Method.RandomForest_1;
		
		// evalueer methode voor elke training-set
		for (String set : trainingSets) {
			DataSet data = new DataSet(exp + "/Training-sets/"+set);
		    double accuracy = Classify.classify_crossvalidation2(method, data, 10,
		    									exp + "/Results/"+set+".summary.txt",
		    									exp + "/Results/"+set+".confusionMatrix.txt");
		    Files.writeFile(exp + "/Results/"+set+".accuracy.txt", Double.toString(accuracy));
		}
	    
		
	}

}
