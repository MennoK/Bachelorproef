package helpers;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.*;
import weka.classifiers.functions.*;
import weka.classifiers.lazy.*;
import weka.classifiers.rules.*;
import weka.classifiers.trees.*;

/**
 * Alle methodes met verschillende opties opgesomd in een 'enum'
 */
public enum Method {
	
	/**
	 * Beslissingsbomen: default
	 */
	J48_1("-C 0.25 -M 2"),
	
	/**
	 * Beslissingsbomen: hogere confidence factor: 0.2 (http://ww.samdrazin.com/classes/een548/project2report.pdf, figuur 4)
	 */
	J48_2A("-C 0.2 -M 2"),
	
	/**
	 * Beslissingsbomen: hogere confidence factor: 0.5 (http://ww.samdrazin.com/classes/een548/project2report.pdf, figuur 4)
	 */
	J48_2B("-C 0.5 -M 2"),
	
	/**
	 * Beslissingsbomen: unpruned, min. 2 obj. per blad
	 */
	J48_3("-U -M 2"),
	
	/**
	 * Naive Bayes
	 */
	NaiveBayes(""),
	
	/**
	 * k-Nearest Neighbours: default (k=1 buren)
	 */
	IBk_1("-K 1 -W 0 -A \"weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"\""),
	
	/**
	 * k-Nearest Neighbours: k=2 buren
	 */
	IBk_2A("-K 2 -W 0 -A \"weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"\""),
	
	/**
	 * k-Nearest Neighbours: k=5 buren
	 */
	IBk_2B("-K 5 -W 0 -A \"weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"\""),
	
	/**
	 * Support Vector Machines: default
	 */
	LibSVM_1("-S 0 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -seed 1"),
	
	/**
	 * Support Vector Machines: hogere waarde voor -G: 0.01 (gamma parameter van RBF-kernel, wat dat ook zou mogen betekenen?? http://weka.wikispaces.com/Optimizing+parameters)
	 */
	LibSVM_2("-S 0 -K 2 -D 3 -G 0.01 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -seed 1"),
	
	/**
	 * Random forest: default (10 bomen)
	 */
	RandomForest_1("-I 10 -K 0 -S 1"),
	
	/**
	 * Random forest: 20 bomen
	 */
	RandomForest_2("-I 20 -K 0 -S 1"),
	
	/**
	 * K star: default
	 */
	KStar("-B 20");

	
	/**
	 * Opties voor de methode
	 */
	public final String[] options;

	Method(String options) {
		String[] optionsArray = null;
		try {
			optionsArray = weka.core.Utils.splitOptions(options);
		} catch (Exception e) {
			System.out.println("Probleem met het parsen van de opties");
			optionsArray = null;
		}
		this.options = optionsArray;
	}
	
	/**
	 * Geef de naam van de methode terug.
	 * Voorbeeld: J48_2.getName() = "J48"
	 */
	public String getName() {
		String name = this.name();
		return StringUtils.substringBefore(name, "_");
	}
	
	public Classifier getClassifier() {
		Classifier classifier = null;
		try {
			classifier = classifier(this.getName());
			// stel de opties in
			//  System.out.print("Opties instellen: ");
			//  System.out.println(StringUtils.join(this.options, ","));
			classifier.setOptions(Arrays.copyOf(this.options, this.options.length));
		}
		catch (Exception e) {
			System.out.println("Fout met het maken van de classifier van "+this.name());
		}
		return classifier;
	}
	
	private static Classifier classifier(String name) {
		switch (name) {
			case "J48":
				return new J48();
			case "NaiveBayes":
				return new NaiveBayes();
			case "IBk":
				return new IBk();
			case "LibSVM":
				return new LibSVM();
			case "DecisionTable":
				return new DecisionTable();
			case "RandomForest":
				return new RandomForest();
		}
		System.out.println("Classifier "+name+" kon niet aangemaakt worden. Nog toevoegen bij methode classifier in Method.java?");
		return null;
	}
	
}
