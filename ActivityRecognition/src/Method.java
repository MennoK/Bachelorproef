import org.apache.commons.lang3.StringUtils;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;

/**
 * Alle methodes met verschillende opties opgesomd in een 'enum'
 */
public enum Method {
	
	/**
	 * J48: default = pruned met confidence factor 0.25, min. 2 obj. per blad
	 */
	J48_1("-C 0.25 -M 2"),
	
	/**
	 * J48: hogere confidence factor: 0.5 (http://ww.samdrazin.com/classes/een548/project2report.pdf, figuur 4), min. 2 obj. per blad
	 */
	J48_2("-C 0.5 -M 2"),
	
	/** 
	 * J48: ...
	 */
	J48_3(""),
	/**
	 * J48: unpruned, min. 2 obj. per blad
	 */
	J48_4("-U -M 2");
	
	/**
	 * Opties voor de methode
	 */
	public final String[] options;
	
	/**
	 * Classifier-object (weka.classifiers.Classifier) voor de methode
	 */
	public final Classifier classifier;

	Method(String options) {
		String[] optionsArray = null;
		try {
			optionsArray = weka.core.Utils.splitOptions(options);
		} catch (Exception e) {
			System.out.println("Probleem met het parsen van de opties");
			optionsArray = null;
		}
		this.options = optionsArray;
		// Maak een Classifier object aan voor de methode (weka.classifiers.Classifier).
		// en stel ook de opties in.
		this.classifier = getClassifier();
	}
	
	/**
	 * Geef de naam van de methode terug.
	 * Voorbeeld: J48_2.getName() = "J48"
	 */
	public String getName() {
		String name = this.name();
		return StringUtils.substringBefore(name, "_");
	}
	
	private Classifier getClassifier() {
		Object classifier = null;
		try {
			System.out.println("proberen classifier object aan te maken..."); // debug
			// classifier = Class.forName(this.getName()).newInstance(); TODO: dit werkt niet, maar de lijn hieronder wel...
			classifier = new J48();
			System.out.println("gelukt, maar toch nog eens naar de code kijken (Method.java, bij 'TODO')"); // debug
			// stel de opties in
			((Classifier) classifier).setOptions(this.options);
		}
		catch (Exception e) {
			System.out.println("Fout met het maken van de classifier van "+this.name());
		}
		return (Classifier) classifier;
	}
	
}
