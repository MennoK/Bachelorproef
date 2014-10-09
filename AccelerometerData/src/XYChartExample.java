import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

public class XYChartExample extends JFrame
{

   // how many values are there in the chart
   public static final int NUM_VALUES = 60;

   public static void main (String[] args) throws IOException
   {
      double[][] sineValues = new double[2][NUM_VALUES];
      double[][] cosineValues = new double[2][NUM_VALUES];

      // X values
      for (int i=0; i<NUM_VALUES; i++)
      {
         sineValues[0][i] = i / 10.0;
         cosineValues[0][i] = i / 10.0;
      }

      // Y values
      for (int i=0; i<NUM_VALUES; i++)
      {
         sineValues[1][i] = Math.sin(sineValues[0][i]);
         cosineValues[1][i] = Math.cos(cosineValues[0][i]);
      }

      DefaultXYDataset dataset = new DefaultXYDataset();
      dataset.addSeries("Sine", sineValues);
      dataset.addSeries("Cosine", cosineValues);

      // Create the chart
      JFreeChart chart = ChartFactory.createXYLineChart(
         "Sine / Cosine Curves", // The chart title
         "X", // x axis label
         "Y", // y axis label
         dataset, // The dataset for the chart
         PlotOrientation.VERTICAL,
         true, // Is a legend required?
         false, // Use tooltips
         false // Configure chart to generate URLs?
      );

/*
      DefaultCategoryDataset catDataset = new DefaultCategoryDataset();
      for (int i=0; i<NUM_VALUES; i++)
      {
         catDataset.addValue(sineValues[1][i], "Sine", ""+sineValues[0][i]);
         catDataset.addValue(cosineValues[1][i], "Cosine", ""+cosineValues[0][i]);
      }

      // Create the chart
      JFreeChart chart = ChartFactory.createBarChart(
         "Sine / Cosine Curves", // The chart title
         "X", // x axis label
         "Y", // y axis label
         catDataset, // The dataset for the chart
         PlotOrientation.VERTICAL,
         true, // Is a legend required?
         false, // Use tooltips
         false // Configure chart to generate URLs?
      );
*/

      // save it to a file
      File imageFile = new File("sine-cosine.png");
      ChartUtilities.saveChartAsPNG(imageFile, chart, 500, 300);

      // display it on the screen
      JFrame frame = new XYChartExample();
      frame.getContentPane().add(new ChartPanel(chart));
      frame.setSize(700, 500);
      frame.setVisible(true);
      frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
   }
}