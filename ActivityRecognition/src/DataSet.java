import org.apache.commons.io.FilenameUtils;

import weka.core.Instances;
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
		this.name = FilenameUtils.removeExtension(path);
		DataSource source;
		try {
			source = new DataSource(path);
			this.instances = source.getDataSet();
		} catch (Exception e) {
			System.out.println("CSV-bestand niet gevonden: "+path);
		}
	}

}