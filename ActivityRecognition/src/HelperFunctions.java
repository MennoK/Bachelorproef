import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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

}