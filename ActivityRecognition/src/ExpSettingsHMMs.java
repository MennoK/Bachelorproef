import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class ExpSettingsHMMs {
	
	public static String exp = "./Experimenten/Settings-HMMs"; // map waaronder alle resultaten bewaard worden
	public static String data = "./Data"; // map waaronder alle metingen staan

	public static void main(String[] args) throws IOException {
		
		Map<String,String> settingss = new HashMap<>();
		
		List<File> settingsFiles = Files.subDirectories(exp);
		
		for (File file : settingsFiles) {
			settingss.put(Files.file(file.getName()), "");
		}		
		
		// activiteiten waarvoor features moeten berekend worden
		String[] activities = { "Fietsen", 
								"LiftAD",
								"LiftAU",
								"Lopen", 
								"Nietsdoen",
								"Springen", 
								"Tandenpoetsen", 
								"Trapaf", 
								"Trapop", 
								"Wandelen"
								};
	    
	    // bereken features voor elke activiteit (training set + validatie set)
		// + evalueer een methode (Random Forest) voor elke settings (m.b.v. training set)
		Method method = Method.RandomForest_1;
	    calculateFeatures(settingss, activities, method);
		
	}

	private static void calculateFeatures(Map<String, String> settingss,
			String[] activities, Method method) throws IOException {
		Iterator it;
		it = settingss.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String i = (String) pair.getKey();
	        String settings = (String) pair.getValue();
	        
	     // voor Training-set
	        
	        for (String activity : activities) {
	        	
	        	long start_time = System.currentTimeMillis();
	        	ActivityRecognition.featuresAll(data+"/"+activity+"/Training-set/", exp+"/"+i+"/");
	        	long end_time = System.currentTimeMillis();
	        	long time = end_time - start_time;
	        	Files.writeFile("./TrainingSets/"+activity+"_berekeningstijd_ms.txt", Long.toString(time));
	        	
	        	
	        }
	        
	        File source = new File("./TrainingSets/");
        	File dest = new File(exp + "/"+i+"/Training-sets/");
        	try {
        	    FileUtils.copyDirectory(source, dest);
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	
        	// voor Validatie-set
        	
        	/*for (String activity : activities) {
	        	
        		long start_time = System.currentTimeMillis();
	        	ActivityRecognition.featuresAll(data+"/"+activity+"/Validatie-set/", exp + "/Settings"+i+"/");
	        	long end_time = System.currentTimeMillis();
	        	long time = end_time - start_time;
	        	Files.writeFile("./TrainingSets/"+activity+"_berekeningstijd_ms.txt", Long.toString(time));
	        	
	        }
	        
	        source = new File("./TrainingSets/");
        	dest = new File(exp + "/"+i+"/Validatie-sets/");
        	try {
        	    FileUtils.copyDirectory(source, dest);
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}*/
        	
        	// evalueer methode
        	evaluateMethod(i, settings, method);
	        
	        
	    }
	}
	
	private static void evaluateMethod(String i, String settings,
			Method method) throws IOException {
        // eerst de training sets van verschillende activiteiten mergen
        List<File> trainingSetsList = Files.getAllFilesWithExtensionInDirectory(exp+"/"+i+"/Training-sets/", "csv");
        File[] trainingSets = trainingSetsList.toArray(new File[trainingSetsList.size()]);
        Features.combineTrainingSets2(trainingSets, exp + "/"+i+"/Training-set.csv");
        
        // dan de methode toepassen en resulaten bijhouden
	    DataSet data = new DataSet(exp + "/"+i+"/Training-set.csv");
	    double accuracy = Classify.classify_crossvalidation2(method, data, 10,
	    									exp + "/"+i+"/Summary.txt",
	    									exp + "/"+i+"/ConfusionMatrix.txt");
	    
	    Files.writeFile(exp + "/"+i+"/Accuracy.txt", Double.toString(accuracy));
	}

}
