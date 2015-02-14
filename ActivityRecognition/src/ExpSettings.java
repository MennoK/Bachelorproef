import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class ExpSettings {
	
	public static String exp = "./Experimenten/Settings"; // map waaronder alle resultaten bewaard worden
	public static String data = "./Data2"; // map waaronder alle metingen staan

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
		
		// activiteiten waarvoor features moeten berekend worden
		String[] activities = { "Fietsen", 
								"LiftAD",
								"LiftAU", 
							//	"Lopen", 
								"Nietsdoen",
								"Springen", 
							//	"Tandenpoetsen", 
								"Trapaf", 
								"Trapop", 
								"Wandelen"
								};
		
		// maak de settings bestanden		
		Iterator it = settingss.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String i = (String) pair.getKey();
	        String settings = (String) pair.getValue();

	        Files.writeFile(exp + "/Settings"+i+"/settings.json", settings);
	        Files.writeFile("./Settings/"+"settings"+i+".json",settings);
	        
	    }
	    
	    // bereken features voor elke activiteit
	    it = settingss.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String i = (String) pair.getKey();
	        String settings = (String) pair.getValue();
	        
	        for (String activity : activities) {
	        	
	        	long start_time = System.currentTimeMillis();
	        	ActivityRecognition.featuresAll(data+"/"+activity+"/", exp + "/Settings"+i+"/");
	        	long end_time = System.currentTimeMillis();
	        	
	        	long time = end_time - start_time;
	        	Files.writeFile("./TrainingSets/"+activity+"_berekeningstijd_ms.txt", Long.toString(time));
	        	
	        }
	        
	        File source = new File("./TrainingSets/");
        	File dest = new File(exp + "/Settings"+i+"/TrainingSets/");
        	try {
        	    FileUtils.copyDirectory(source, dest);
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
	        
	        
	    }
		
	}

}
