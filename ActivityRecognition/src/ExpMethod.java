import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class ExpMethod {
	
	public static String exp = "./Experimenten/Method"; // map waaronder alle resultaten bewaard worden
	public static String data = "./Data2"; // map waaronder alle metingen staan

	public static void main(String[] args) throws IOException {
		
		// feature methodes die moeten geëvalueerd worden
		// = lijst van training set csv-bestanden in de map Experimenten/FeatureSets/Training-sets
		Method[] methods = { 
								Method.RandomForest_1,
								Method.J48_1,
								Method.LibSVM_1,
								Method.NaiveBayes,
								Method.IBk_1
							};
		
		// gebruik 1 training set
		String trainingSet = exp + "/Training-set.csv";
		
		// evalueer methode voor elke training-set
		for (Method method : methods) {
			DataSet data = new DataSet(trainingSet);
		    double accuracy = Classify.classify_crossvalidation2(method, data, 10,
		    									exp + "/Results/"+method.getName()+".summary.txt",
		    									exp + "/Results/"+method.getName()+".confusionMatrix.txt");
		    Files.writeFile(exp + "/Results/"+method.getName()+".accuracy.txt", Double.toString(accuracy));
		}
	    
		
	}

}
