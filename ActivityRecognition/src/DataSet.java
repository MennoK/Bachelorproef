
import java.io.File;

import org.apache.commons.io.FilenameUtils;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Een DataSet is een verzameling objecten met waardes voor features en een activiteit als label
 */
public class DataSet {
	
	/**
	 * Pad naar het CSV-bestand
	 */
	public final String path;
	
	/**
	 * Naam van de DataSet = bestandsnaam
	 */
	public final String name;
	
	/**
	 * Instances (weka.core.Instances) van de DataSet
	 */
	public Instances instances;
	
	public DataSet(String path) {
		this.path = path;
		this.name = FilenameUtils.removeExtension(FilenameUtils.getName(path));
		try {
			CSVLoader csv = new CSVLoader();
			csv.setFile(new File(path));
			this.instances = csv.getDataSet();
			this.instances.setClassIndex(this.instances.numAttributes() - 1);
		} catch (Exception e) {
			System.out.println("CSV-bestand niet gevonden: "+path);
		}
	}

}
