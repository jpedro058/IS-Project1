package example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import java.util.Arrays;

public class StatisticsChart extends ApplicationFrame {

    private static final String TIMES_FILE = "files/Times.txt";

    public StatisticsChart(String applicationTitle, String chartTitle) {
        super(applicationTitle);

        // Create dataset
        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
                "Iteration",
                "Time (ms)",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
    }

    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Separate maps for JSON and XML
        Map<Integer, double[]> jsonAggregationMap = new HashMap<>();
        Map<Integer, double[]> xmlAggregationMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(TIMES_FILE))) {
            String line;
            String currentType = "";
            int iteration;

            while ((line = br.readLine()) != null) {
                line = line.trim(); // Remove leading/trailing whitespace

                // Check if the line indicates a new section (JSON or XML)
                if (line.startsWith("JSON:")) {
                    currentType = "JSON";
                    continue; // Skip to the next iteration
                } else if (line.startsWith("XML:")) {
                    currentType = "XML";
                    continue; // Skip to the next iteration
                } else if (line.isEmpty()) {
                    continue; // Skip empty lines
                }

                // Parse the timing information
                String[] parts = line.split(":", 2); // Split on the first colon only
                if (parts.length < 2) {
                    System.out.println("Skipping line due to unexpected format: " + line);
                    continue; // Skip if there are not enough parts
                }

                try {
                    iteration = Integer.parseInt(parts[0].trim());
                    System.out.println("iteration: " + iteration); // Print iteration

                    // Get timing details after the first colon
                    String timingDetailsPart = parts[1].trim(); // Everything after the first colon
                    String[] timingDetails = timingDetailsPart.split("\\|"); // Split by "|"
                    System.out.println("timing details: " + Arrays.toString(timingDetails)); // Print timing details

                    // Check if the timingDetails array has enough elements
                    if (timingDetails.length < 2) {
                        System.out.println("Skipping line due to missing timing details: " + line);
                        continue; // Skip if timing details are missing
                    }

                    // Extract serialization and deserialization times
                    double serializationTime = Double
                            .parseDouble(timingDetails[0].split(":")[1].trim().replace("ms", "").trim());
                    double deserializationTime = Double
                            .parseDouble(timingDetails[1].split(":")[1].trim().replace("ms", "").trim());

                    // Choose the appropriate aggregation map based on currentType
                    Map<Integer, double[]> aggregationMap = currentType.equals("JSON") ? jsonAggregationMap
                            : xmlAggregationMap;

                    // Aggregate the times for the same iteration
                    aggregationMap.putIfAbsent(iteration, new double[3]); // [serializationSum, deserializationSum,
                                                                          // count]
                    double[] sums = aggregationMap.get(iteration);
                    sums[0] += serializationTime; // Sum of serialization times
                    sums[1] += deserializationTime; // Sum of deserialization times
                    sums[2] += 1; // Count of occurrences for this iteration

                } catch (NumberFormatException e) {
                    System.out.println("Error parsing numbers from line: " + line);
                }
            }

            // calculate the average and add to the dataset for JSON
            for (Map.Entry<Integer, double[]> entry : jsonAggregationMap.entrySet()) {
                int iter = entry.getKey();
                double[] sums = entry.getValue();
                double avgSerializationTime = sums[0] / sums[2]; // Average serialization time
                double avgDeserializationTime = sums[1] / sums[2]; // Average deserialization time

                // Add values to the dataset
                dataset.addValue(avgSerializationTime, "Serialization (JSON)", "Iteration " + iter);
                dataset.addValue(avgDeserializationTime, "Deserialization (JSON)", "Iteration " + iter);
            }

            // calculate the average and add to the dataset for XML
            for (Map.Entry<Integer, double[]> entry : xmlAggregationMap.entrySet()) {
                int iter = entry.getKey();
                double[] sums = entry.getValue();
                double avgSerializationTime = sums[0] / sums[2]; // Average serialization time
                double avgDeserializationTime = sums[1] / sums[2]; // Average deserialization time

                // Add values to the dataset
                dataset.addValue(avgSerializationTime, "Serialization (XML)", "Iteration " + iter);
                dataset.addValue(avgDeserializationTime, "Deserialization (XML)", "Iteration " + iter);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    public static void main(String[] args) {
        StatisticsChart chart = new StatisticsChart("Serialization/Deserialization Statistics",
                "Serialization & Deserialization Time Comparison");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }
}
