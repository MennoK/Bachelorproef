import helpers.DataSet;
import helpers.Classify;
import helpers.Features;
import helpers.Files;
import helpers.HelperFunctions;
import helpers.Method;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class ExpSettings {
	
	public static String exp = "./Experimenten/Settings"; // map waaronder alle resultaten bewaard worden
	public static String data = "./Data"; // map waaronder alle metingen staan

	public static void main(String[] args) throws IOException {
		
		Map<String,String> settingss = new HashMap<>();
		
		settingss.put("_default", HelperFunctions.settings(-1, 20, 0.25, 4, 0.5, "haar", 10, 10, 0.5, 1, true, false, 4, 10, 100));
		
		
		/* Hoe de FFT features berekenen?
		   (aantale FFT bins en breedten en + stapgrootte hangen samen (anders wordt de Fourier-transformatie niet voor dezelfde frequenties berekend)) */
		for (double factor = 0.2; factor < 2; factor += 0.2)
			settingss.put("_fft_" + (int) (Math.round(factor*20)), HelperFunctions.settings(-1, (int) (Math.round(factor*20)), 0.25/factor, 4, 0.5/factor, "haar", 10, 10, 0.5, 1, true, false, 4, 10, 100));
		for (double factor = 2; factor <= 4; factor += 1)
			settingss.put("_fft_" + (int) (Math.round(factor*20)), HelperFunctions.settings(-1, (int) (Math.round(factor*20)), 0.25/factor, 4, 0.5/factor, "haar", 10, 10, 0.5, 1, true, false, 4, 10, 100));
		
		/* Hoe de DWT features berekenen? */
		for (int n = 4; n <= 20; n += 2)
			settingss.put("_dwt_" + n, HelperFunctions.settings(-1, 20, 0.25, 4, 0.5, "haar", n, 10, 0.5, 1, true, false, 4, 10, 100));
		
		settingss.put("_dwt_daubechies4", HelperFunctions.settings(-1, 20, 0.25, 4, 0.5, "daubechies4", 10, 10, 0.5, 1, true, false, 4, 10, 100));
		settingss.put("_dwt_biorthogonal11", HelperFunctions.settings(-1, 20, 0.25, 4, 0.5, "biorthogonal11", 10, 10, 0.5, 1, true, false, 4, 10, 100));
		
		List<File> settingsFiles = Files.getAllFilesWithExtensionInDirectory(exp,"json");
		
		for (File file : settingsFiles) {
			settingss.put(Files.file(file.getName()), Files.readFile(file.getAbsolutePath()));
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
		
		// maak de settings bestanden		
		makeSettingsFiles(settingss);
	    
	    // bereken features voor elke activiteit (training set + validatie set)
		// + evalueer een methode (Random Forest) voor elke settings (m.b.v. training set)
		Method method = Method.RandomForest_1;
	    calculateFeatures(settingss, activities, method);
		
	}

	private static void makeSettingsFiles(Map<String, String> settingss) {
		Iterator it = settingss.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String i = (String) pair.getKey();
	        String settings = (String) pair.getValue();

	        Files.writeFile(exp + "/Settings"+i+"/settings.json", settings);
	        Files.writeFile("./Settings/"+"settings"+i+".json",settings);
	        
	    }
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
	        	ActivityRecognition.featuresAll(data+"/"+activity+"/Training-set/", exp + "/Settings"+i+"/");
	        	long end_time = System.currentTimeMillis();
	        	long time = end_time - start_time;
	        	Files.writeFile("./TrainingSets/"+activity+"_berekeningstijd_ms.txt", Long.toString(time));
	        	
	        	
	        }
	        
	        File source = new File("./TrainingSets/");
        	File dest = new File(exp + "/Settings"+i+"/Training-sets/");
        	try {
        	    FileUtils.copyDirectory(source, dest);
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	
        	// voor Validatie-set
        	
        	for (String activity : activities) {
	        	
        		long start_time = System.currentTimeMillis();
	        	ActivityRecognition.featuresAll(data+"/"+activity+"/Validatie-set/", exp + "/Settings"+i+"/");
	        	long end_time = System.currentTimeMillis();
	        	long time = end_time - start_time;
	        	Files.writeFile("./TrainingSets/"+activity+"_berekeningstijd_ms.txt", Long.toString(time));
	        	
	        }
	        
	        source = new File("./TrainingSets/");
        	dest = new File(exp + "/Settings"+i+"/Validatie-sets/");
        	try {
        	    FileUtils.copyDirectory(source, dest);
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	
        	// evalueer methode
        	evaluateMethod(i, settings, method);
	        
	        
	    }
	}
	
	private static void evaluateMethod(String i, String settings,
			Method method) throws IOException {
        // eerst de training sets van verschillende activiteiten mergen
        List<File> trainingSetsList = Files.getAllFilesWithExtensionInDirectory(exp+"/Settings"+i+"/Training-sets/", "csv");
        File[] trainingSets = trainingSetsList.toArray(new File[trainingSetsList.size()]);
        Features.combineTrainingSets2(trainingSets, exp + "/Settings"+i+"/Training-set.csv");
        
        // dan de methode toepassen en resulaten bijhouden
	    DataSet data = new DataSet(exp + "/Settings"+i+"/Training-set.csv");
	    double accuracy = Classify.classify_crossvalidation2(method, data, 10,
	    									exp + "/Settings"+i+"/Summary.txt",
	    									exp + "/Settings"+i+"/ConfusionMatrix.txt");
	    
	    Files.writeFile(exp + "/Settings"+i+"/Accuracy.txt", Double.toString(accuracy));
	}

}

