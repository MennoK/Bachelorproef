import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class HelperFunctions {
	
	static void makeShorterLogFile(String pathIN, String pathOUT, double start, double end) throws IOException {
		
		// zet de inhoud van het logbestand in een string
		String s = new String(readAllBytes(get(pathIN)));
		
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		
		// zet nullen achter start en end zodat lengte van timestamp = lengte van start = lengte van end
		long firstTimestamp = (long) ((Map) ((ArrayList) obj.get("measurements")).get(0)).get("timestamp");
		while (HelperFunctions.length((long) start) < HelperFunctions.length(firstTimestamp))
			start = start * 10;
		while (HelperFunctions.length((long) end) < HelperFunctions.length(firstTimestamp))
			end = end * 10;
	
		// itereer over alle measurements en bouw ondertussen het nieuwe json object op
		Map preprocessed = new LinkedHashMap();
		JSONArray measurementsPre = new JSONArray();
		preprocessed.put("measurements", measurementsPre);
		Iterator<JSONObject> iterator = measurements.iterator();
		while(iterator.hasNext()) {
			JSONObject m = iterator.next();
			if ((long) m.get("timestamp") >= start && (long) m.get("timestamp") <= end)
				measurementsPre.add(m);
		}
		preprocessed.put("has_gravity", obj.get("has_gravity"));
		preprocessed.put("user", obj.get("user"));
		preprocessed.put("notes", obj.get("notes"));
		StringWriter out = new StringWriter();
		JSONValue.writeJSONString(preprocessed, out);
		String jsonText = out.toString();
		
		// schrijf weg naar nieuw bestand
		PrintWriter writer = new PrintWriter(pathOUT, "UTF-8");
		writer.println(jsonText);
		writer.close();
	}

	/** @pre n > 0 */
	static int length(long n) {
		return (int) (Math.log10(n)+1);
	}
	
	/**
	 * Zet een .jahmm-bestand in de juiste vorm.
	 * @param 	path
	 * 			Pad naar bestand
	 */
	static void hmm(String path) {
		try {
			
			// lees bestand in
			String hmmfile = Files.readFile(path);
			
			// vervang alle getallen 1.23456E-7 naar de juiste vorm
			
			Pattern p = Pattern.compile("([0-9]+.[0-9]+E(\\+|\\-)*[0-9]+)");
		    Matcher m = p.matcher(hmmfile);

		    StringBuffer sb = new StringBuffer();

		    while(m.find()) {
		        m.appendReplacement(sb, doubleFormat(m.group(0)));
		    }

		    m.appendTail(sb);
			
			// schrijf weg
			Files.writeFile(path,sb.toString());
			
		} catch (IOException e) {
			System.out.println("Fout: .jahmm-bestand niet gevonden.");
		}
	}
	
	/**
	 * Geef de inhoud van een settings.json bestand om HMM's te maken met een gegeven
	 * aantal states en iterations.
	 */
	static String hmmsettings(int states, int iterations) {
		Map settings = new LinkedHashMap();
		
		settings.put("window_seconds",-1);
		settings.put("nb_fft_features",20);
		settings.put("step_fft_features",0.25);
		settings.put("nb_fft_peaks",4);
		settings.put("window_fft_features",0.5);
		settings.put("wavelet_type","haar");
		settings.put("nb_dwt_features",10);
		settings.put("nb_wpd_features",10);
		settings.put("peak_wss",0.5);
		settings.put("peak_mindev",1);
		settings.put("geo_correct",true);
		settings.put("ignore_q",false);
		settings.put("f_co",4);
		settings.put("hmm_states",states);
		settings.put("hmm_learn_iterations",iterations);
		settings.put("hmm_files", new JSONArray());
		settings.put("cli_args", new JSONArray());
		
		StringWriter out = new StringWriter();
		try {
			JSONValue.writeJSONString(settings, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String jsonText = out.toString();
	
		return jsonText;
	}
	
	static String doubleFormat(String number) {
		double x = Double.parseDouble(number);
		String withComma = String.format("%.20f", x);
		return withComma.replace(",",".");
	}

}
