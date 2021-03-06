package helpers;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class Classify {
	
	/**
	 * Maak een model met een gegeven methode voor de trainingset en voer cross-validatie uit.
	 * <br>Maak ook een bestand met de samenvatting: <b>Resultaten/Crossvalidatie/[trainingset]-[methode-[folds]folds.txt</b>
	 * <br>en een bestand met alleen de confusion matrix: <b>Resultaten/Crossvalidatie/[trainingset]-[methode]-[folds]folds.confusionmatrix.txt</b>
	 * <br>Ook het csv-bestand <b>Resultaten/crossvalidatie.csv</b> wordt geupdatet.
	 * <br><i>Opmerking: dit wordt alleen uitgevoerd als deze bestanden nog niet bestaan!</i>
	 * 
	 * @param 	method
	 * 			De methode die moet gebruikt worden
	 * @param 	trainingSet
	 * 			De training-set gebruikt voor het model EN voor de cross-validatie
	 * @param 	folds
	 * 			Het aantal folds voor cross-validatie
	 * @return	De accuracy van het model,
	 * 			<br>of <b>-1</b> als er een fout opgetreden is,
	 * 			<br>of <b>-2</b> als de bestanden al bestaan
	 */
	public static double classify_crossvalidation(Method method, DataSet trainingSet, int folds) {
		
		// controleer of deze validatie al eens gedaan is
		if (Files.exists("Resultaten/Crossvalidatie/"+trainingSet.name+"-"+method.name()+"-"+folds+"folds.txt")
				&& Files.exists("Resultaten/Crossvalidatie/"+trainingSet.name+"-"+method.name()+"-"+folds+"folds.confusionmatrix.txt"))
			return -2;
		
		// haal de instances op
		Instances training = trainingSet.instances;
		Classifier cls = method.getClassifier();
		
		try {
			
			// classify + evaluate
			cls.buildClassifier(training);
			Evaluation eval = new Evaluation(training);
			eval.crossValidateModel(cls, training, folds, new Random(1));
			
			// haal de resultaten van de evaluatie op
			double accuracy = eval.pctCorrect();
			String confusionMatrix = eval.toMatrixString();
			String summary = eval.toSummaryString();
			
			// schrijf samenvatting weg
			Files.writeFile("Resultaten/Crossvalidatie/"+trainingSet.name+"-"+method.name()+"-"+folds+"folds.txt", summary);
			// schrijf confusion matrix weg
			Files.writeFile("Resultaten/Crossvalidatie/"+trainingSet.name+"-"+method.name()+"-"+folds+"folds.confusionmatrix.txt", confusionMatrix);
			// update Resultaten/crossvalidatie.csv
			Files.appendToFile("Resultaten/crossvalidatie.csv",trainingSet.name+","+method.name()+","+folds+","+accuracy);
			
			// geeft accuracy terug
			return accuracy;
			
		} catch (Exception e) {
			System.out.println("Fout met crossvalidatie van "+trainingSet.name+" met methode "+method.name());
			e.printStackTrace(System.out);
			return -1;
		}
	}
	
	/**
	 * Maak een model met een gegeven methode voor de trainingset en voer cross-validatie uit.
	 * 
	 * @param 	method
	 * 			De methode die moet gebruikt worden
	 * @param 	trainingSet
	 * 			De training-set gebruikt voor het model EN voor de cross-validatie
	 * @param 	folds
	 * 			Het aantal folds voor cross-validatie
	 * @param	summaryOutputFile
	 * 			Pad naar bestand met samenvatting van resultaten
	 * @param	confMatrixOutputFile
	 * 			Pad naar bestand met confusion matrix
	 * @return	De accuracy van het model,
	 * 			<br>of <b>-1</b> als er een fout opgetreden is.
	 */
	public static double classify_crossvalidation2(Method method, DataSet trainingSet, int folds,
			String summaryOutputFile, String confMatrixOutputFile) {
		
		// haal de instances op
		Instances training = trainingSet.instances;
		Classifier cls = method.getClassifier();
		
		try {
			
			// classify + evaluate
			cls.buildClassifier(training);
			Evaluation eval = new Evaluation(training);
			eval.crossValidateModel(cls, training, folds, new Random(1));
			
			// haal de resultaten van de evaluatie op
			double accuracy = eval.pctCorrect();
			String confusionMatrix = eval.toMatrixString();
			String summary = eval.toSummaryString();
			
			// schrijf samenvatting weg
			Files.writeFile(summaryOutputFile, summary);
			// schrijf confusion matrix weg
			Files.writeFile(confMatrixOutputFile, confusionMatrix);
			
			// geeft accuracy terug
			return accuracy;
			
		} catch (Exception e) {
			System.out.println("Fout met crossvalidatie van "+trainingSet.name+" met methode "+method.name());
			e.printStackTrace(System.out);
			return -1;
		}
	}
	
	/**
	 * Voer voor elke methode in [enum] Method cross-validatie uit op de gegeven training-set.
	 * 
	 * @param 	trainingSet
	 * 			De training-set gebruikt voor het model EN voor de cross-validatie
	 * @param 	folds
	 * 			Het aantal folds voor cross-validatie
	 */
	public static void classifyAll_crossvalidation(DataSet trainingSet, int folds) {
		
		Method[] methods = Method.values();
		
		for(Method method : methods) {
			classify_crossvalidation(method, trainingSet, folds);
		}
		
	}
	
}
