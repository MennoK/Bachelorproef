package helpers;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.typesafe.config.ConfigException.Parse;


public class HelperFunctions {
	
	public static void makeShorterLogFile(String pathIN, String pathOUT, double start, double end) throws IOException {
		
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
	
	public static void makeShorterLogFileFromStart(String pathIN, String pathOUT, int nbMeasurements) throws IOException {
		
		// zet de inhoud van het logbestand in een string
		String s = new String(readAllBytes(get(pathIN)));
		
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
	
		// itereer over alle measurements en bouw ondertussen het nieuwe json object op
		Map preprocessed = new LinkedHashMap();
		JSONArray measurementsPre = new JSONArray();
		preprocessed.put("measurements", measurementsPre);
		Iterator<JSONObject> iterator = measurements.iterator();
		int count = 0;
		while(iterator.hasNext() && count < nbMeasurements) {
			JSONObject m = iterator.next();
			measurementsPre.add(m);
			count++;
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
	
	/**
	 * Vraag de accuracy (van de cross-validatie) in een gegeven output txt-bestand op.
	 * 
	 * @param	filename
	 * 			Pad naar output txt-bestand.
	 * @return	Accuracy
	 * 			(0 <= accuracy <= 1)
	 * @throws 	IOException
	 */
	public static double getAccuracy(String filename) throws IOException {
		String content = Files.readFile(filename);
		String[] parts = content.split("=== Stratified cross-validation ===\n\nCorrectly Classified Instances");
		content = parts[1].trim();
		System.out.println(content);
		Scanner scanner = new Scanner(content);
		int instances = scanner.nextInt();
		double accuracy = scanner.nextDouble() / 100;
		return accuracy;
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
	public static void hmm(String path) {
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
	public static String hmmsettings(int states, int iterations) {
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
	
	/**
	 * Geef de inhoud van een settings.json bestand met opgegeven parameters.
	 */
	public static String settings(int window_seconds,
							int nb_fft_features,
							double step_fft_features,
							int nb_fft_peaks,
							double window_fft_features,
							String wavelet_type,
							int nb_dwt_features,
							int nb_wpd_features,
							double peak_wss,
							double peak_mindev,
							boolean geo_correct,
							boolean ignore_q,
							double f_co,
							int hmm_states,
							int hmm_learn_iterations) {
		
		Map settings = new LinkedHashMap();
		
		settings.put("window_seconds",window_seconds);
		settings.put("nb_fft_features",nb_fft_features);
		settings.put("step_fft_features",step_fft_features);
		settings.put("nb_fft_peaks",nb_fft_peaks);
		settings.put("window_fft_features",window_fft_features);
		settings.put("wavelet_type",wavelet_type);
		settings.put("nb_dwt_features",nb_dwt_features);
		settings.put("nb_wpd_features",nb_wpd_features);
		settings.put("peak_wss",peak_wss);
		settings.put("peak_mindev",peak_mindev);
		settings.put("geo_correct",geo_correct);
		settings.put("ignore_q",ignore_q);
		settings.put("f_co",f_co);
		settings.put("hmm_states",hmm_states);
		settings.put("hmm_learn_iterations",hmm_learn_iterations);
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
	
	/**
	 * Geef de inhoud van een settings.json bestand met opgegeven parameters met lijst van HMM files.
	 */
	public static String settings2(int window_seconds,
							int nb_fft_features,
							double step_fft_features,
							int nb_fft_peaks,
							double window_fft_features,
							String wavelet_type,
							int nb_dwt_features,
							int nb_wpd_features,
							double peak_wss,
							double peak_mindev,
							boolean geo_correct,
							boolean ignore_q,
							double f_co,
							int hmm_states,
							int hmm_learn_iterations,
							JSONArray hmm_files) {
		
		Map settings = new LinkedHashMap();
		
		settings.put("window_seconds",window_seconds);
		settings.put("nb_fft_features",nb_fft_features);
		settings.put("step_fft_features",step_fft_features);
		settings.put("nb_fft_peaks",nb_fft_peaks);
		settings.put("window_fft_features",window_fft_features);
		settings.put("wavelet_type",wavelet_type);
		settings.put("nb_dwt_features",nb_dwt_features);
		settings.put("nb_wpd_features",nb_wpd_features);
		settings.put("peak_wss",peak_wss);
		settings.put("peak_mindev",peak_mindev);
		settings.put("geo_correct",geo_correct);
		settings.put("ignore_q",ignore_q);
		settings.put("f_co",f_co);
		settings.put("hmm_states",hmm_states);
		settings.put("hmm_learn_iterations",hmm_learn_iterations);
		settings.put("hmm_files", hmm_files);
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
	
	
	
	public static String doubleFormat(String number) {
		double x = Double.parseDouble(number);
		String withComma = String.format("%.20f", x);
		return withComma.replace(",",".");
	}
	
	public static long getStartTimestamp(String path) throws IOException {
		
		// zet de inhoud van het logbestand in een string
		String s = new String(readAllBytes(get(path)));
		
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		
		return (long) ((Map) ((ArrayList) obj.get("measurements")).get(0)).get("timestamp");
				
	}
	
	public double getStartTime(String path) throws IOException {
		return timestampfactor(getStartTimestamp(path),getEndTimestamp(path),getNumMeasurements(path)) * getStartTimestamp(path);
	}
	
	
	public static double timestampfactor(long start, long end, int numValues) {
		long diff = end - start;
		double duration = numValues/50;
		double factor = duration / diff;
		int exp = 1;
		while (! (factor > 0.5*Math.pow(10, exp))) {
			exp--;
		}
		//System.out.println("Factor: "+Math.pow(10, exp));
		return Math.pow(10, exp);
	}

	public static long getEndTimestamp(String path) throws IOException {
		
		// zet de inhoud van het logbestand in een string
		String s = new String(readAllBytes(get(path)));
		
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		
		ArrayList list = (ArrayList) obj.get("measurements");
		return (long) ((Map) list.get(list.size()-1)).get("timestamp");
				
	}
	
	public  double getEndTime(String path) throws IOException {
		return timestampfactor(getStartTimestamp(path),getEndTimestamp(path),getNumMeasurements(path)) * getEndTimestamp(path);
	}
	
	public int getNumMeasurements(String path) throws IOException {
		
		// zet de inhoud van het logbestand in een string
		String s = new String(readAllBytes(get(path)));
		
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		
		ArrayList list = (ArrayList) obj.get("measurements");
		return list.size();
				
	}

}
