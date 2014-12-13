import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class Classify {
	
	/**
	 * Maak een model met een gegeven methode voor de trainingset en voer cross-validatie uit.
	 * En maak een bestand met samenvatting: Resultaten/Crossvalidatie/[trainingset]/[methode].[folds]folds.txt
	 * en een bestand met alleen de confusion matrix: Resultaten/Crossvalidatie/[trainingset]/[methode].[folds]folds.confusionmatrix.txt
	 * Ook het csv-bestand Resultaten/crossvalidatie.csv wordt geupdate.
	 * Opmerking: dit wordt alleen uitgevoerd als deze bestanden nog niet bestaan!
	 * 
	 * @param 	method
	 * 			De methode die moet gebruikt worden
	 * @param 	trainingSet
	 * 			De training-set gebruikt voor het model EN voor de cross-validatie.
	 * @param 	folds
	 * 			Het aantal folds voor cross-validatie
	 * @return	De accuracy van het model, of -1 als er een fout opgetreden is, of -2 als de bestanden al bestaan.
	 */
	public double classify_crossvalidation(Method method, DataSet trainingSet, int folds) {
		// controleer of deze validatie al eens gedaan is
		if (Files.exists("Resultaten/Crossvalidatie/"+trainingSet.name+"/"+method.getName()+"."+folds+"folds.txt")
				&& Files.exists("Resultaten/Crossvalidatie/"+trainingSet.name+"/"+method.getName()+"."+folds+"folds.confusionmatrix.txt"))
			return -2;
		// haal de instances op
		Instances training = trainingSet.instances;
		Classifier cls = method.classifier;
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
			Files.writeFile("Resultaten/Crossvalidatie/"+trainingSet.name+"/"+method.getName()+"."+folds+"folds.txt", summary);
			// schrijf confusion matrix weg
			Files.writeFile("Resultaten/Crossvalidatie/"+trainingSet.name+"/"+method.getName()+"."+folds+"folds.confusionmatrix.txt", confusionMatrix);
			// update Resultaten/crossvalidatie.csv
			Files.appendToFile("Resultaten/crossvalidatie.csv",trainingSet.name+","+method.getName()+","+folds+","+accuracy);
			// geeft accuracy terug
			return accuracy;
		} catch (Exception e) {
			return -1;
		}
	}
	
}
