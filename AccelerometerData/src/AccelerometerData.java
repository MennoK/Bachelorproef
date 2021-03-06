import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class AccelerometerData extends JFrame {

	public static void main(String[] args) throws IOException {
		
		String filename = "zonder-g/lopen.txt";

		// zet de inhoud van het logbestand in een string
		String s = new String(readAllBytes(get("src/data/"+filename)));
		
		// maak hiervan een JSON object in Java
		JSONObject obj = (JSONObject) JSONValue.parse(s);
		JSONArray measurements = (JSONArray) obj.get("measurements");
		
		// maak arrays van alle gegevens
		int MAX_VALUES = 50 * 600; // maximaal 20 seconden plotten (anders is grafiek niet meer goed leesbaar)
		int NUM_VALUES = Math.min(measurements.size(), MAX_VALUES);
		double[][] xValues = new double[2][NUM_VALUES];
		double[][] yValues = new double[2][NUM_VALUES];
		double[][] zValues = new double[2][NUM_VALUES];
		Iterator<JSONObject> iterator = measurements.iterator();
		int i=0;
		while(iterator.hasNext() && i < MAX_VALUES) {
			JSONObject m = iterator.next();
			double x = (double) m.get("x");
			double y = (double) m.get("y");
			double z = (double) m.get("z");
			xValues[0][i] = i/50.0;
			yValues[0][i] = i/50.0;
			zValues[0][i] = i/50.0;
			xValues[1][i] = x;
			yValues[1][i] = y;
			zValues[1][i] = z;
			i++;
		}
		
		// gebruik die arrays voor een grafiek te maken met jFreeChart
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("x", xValues);
		dataset.addSeries("y", yValues);
		dataset.addSeries("z", zValues);
	    JFreeChart chart = ChartFactory.createXYLineChart(
	         "Accelerometer data (" + filename + ")", // The chart title
	         "Tijd (s)", // x axis label
	         "Versnelling (m.s-2)", // y axis label
	         dataset, // The dataset for the chart
	         PlotOrientation.VERTICAL,
	         true, // Is a legend required?
	         false, // Use tooltips
	         false // Configure chart to generate URLs?
	    );

	    // opslaan als afbeelding
	    File imageFile = new File("lopen-zonder-g.png");
	    ChartUtilities.saveChartAsPNG(imageFile, chart, 1000, 350);

	    // tonen in venster
	    JFrame frame = new XYChartExample();
	    frame.getContentPane().add(new ChartPanel(chart));
	    frame.setSize(1000, 350);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	      
	}

}
