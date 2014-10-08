import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class AccelerometerData {

	public static void main(String[] args) throws IOException {
		
		String filename = "log1.txt";

		// zet de inhoud van het logbestand in een string
		String s = new String(readAllBytes(get("src/data/"+filename)));
		
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		
		// itereer over alle measurements
		Iterator<JSONObject> iterator = measurements.iterator();
		while(iterator.hasNext()) {
			JSONObject m = iterator.next();
			long timestamp = (long) m.get("timestamp");
			double r = (double) m.get("r");
			double x = (double) m.get("x");
			double y = (double) m.get("y");
			double z = (double) m.get("z");
			System.out.println(timestamp + "	" + r + "	" + x + "	" + y + "	" + z);
		}
	}

}
