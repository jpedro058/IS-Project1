package example;

import javax.swing.*;
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
import java.util.Map;
import java.util.TreeMap;

public class StatisticsChart extends ApplicationFrame {

    private static final String TIMES_FILE = "files/Times.txt";

    // Updated constructor
    public StatisticsChart(String applicationTitle, String chartTitle) {
        super(applicationTitle);

        // Create a tabbed pane to hold the two charts
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create the time dataset and chart
        JFreeChart timeBarChart = ChartFactory.createBarChart(
                chartTitle, // Use the passed chart title
                "Iteration",
                "Time (ms)",
                createTimeDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel timeChartPanel = new ChartPanel(timeBarChart);
        tabbedPane.addTab("Time Statistics", timeChartPanel); // Add time chart to tabbed pane

        // Create the size dataset and chart
        JFreeChart sizeBarChart = ChartFactory.createBarChart(
                "File Size Comparison",
                "Iteration",
                "Size (bytes)",
                createSizeDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel sizeChartPanel = new ChartPanel(sizeBarChart);
        tabbedPane.addTab("File Size Statistics", sizeChartPanel); // Add size chart to tabbed pane

        // Set the tabbed pane as the content pane
        setContentPane(tabbedPane);
    }

    private DefaultCategoryDataset createTimeDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<Integer, double[]> jsonAggregationMap = new TreeMap<>();
        Map<Integer, double[]> xmlAggregationMap = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(TIMES_FILE))) {
            String line;
            String currentType = "";
            int iteration;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("JSON:")) {
                    currentType = "JSON";
                    continue;
                } else if (line.startsWith("XML:")) {
                    currentType = "XML";
                    continue;
                } else if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(":", 2);
                if (parts.length < 2) {
                    System.out.println("Skipping line due to unexpected format: " + line);
                    continue;
                }

                try {
                    iteration = Integer.parseInt(parts[0].trim());
                    String timingDetailsPart = parts[1].trim();
                    String[] timingDetails = timingDetailsPart.split("\\|");

                    if (timingDetails.length < 2) {
                        System.out.println("Skipping line due to missing timing details: " + line);
                        continue;
                    }

                    double serializationTime = Double
                            .parseDouble(timingDetails[0].split(":")[1].trim().replace("ms", "").trim());
                    double deserializationTime = Double
                            .parseDouble(timingDetails[1].split(":")[1].trim().replace("ms", "").trim());

                    Map<Integer, double[]> aggregationMap = currentType.equals("JSON") ? jsonAggregationMap
                            : xmlAggregationMap;

                    aggregationMap.putIfAbsent(iteration, new double[3]);
                    double[] sums = aggregationMap.get(iteration);
                    sums[0] += serializationTime; // Sum serialization time
                    sums[1] += deserializationTime; // Sum deserialization time
                    sums[2] += 1; // Count

                } catch (NumberFormatException e) {
                    System.out.println("Error parsing numbers from line: " + line);
                }
            }

            // Add averages for JSON
            for (Map.Entry<Integer, double[]> entry : jsonAggregationMap.entrySet()) {
                int iter = entry.getKey();
                double[] sums = entry.getValue();
                dataset.addValue(sums[0] / sums[2], "Serialization (JSON)", Integer.toString(iter));
                dataset.addValue(sums[1] / sums[2], "Deserialization (JSON)", Integer.toString(iter));
            }

            // Add averages for XML
            for (Map.Entry<Integer, double[]> entry : xmlAggregationMap.entrySet()) {
                int iter = entry.getKey();
                double[] sums = entry.getValue();
                dataset.addValue(sums[0] / sums[2], "Serialization (XML)", Integer.toString(iter));
                dataset.addValue(sums[1] / sums[2], "Deserialization (XML)", Integer.toString(iter));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private DefaultCategoryDataset createSizeDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<Integer, Double> jsonSizeMap = new TreeMap<>();
        Map<Integer, Double> xmlSizeMap = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(TIMES_FILE))) {
            String line;
            String currentType = "";

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("JSON:")) {
                    currentType = "JSON";
                    continue;
                } else if (line.startsWith("XML:")) {
                    currentType = "XML";
                    continue;
                } else if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(":", 2);
                if (parts.length < 2) {
                    System.out.println("Skipping line due to unexpected format: " + line);
                    continue;
                }

                try {
                    int iteration = Integer.parseInt(parts[0].trim());
                    String timingDetailsPart = parts[1].trim();

                    // Split the timing details and size
                    String[] timingDetails = timingDetailsPart.split("\\|");
                    if (timingDetails.length < 3) {
                        System.out.println("Skipping line due to missing size details: " + line);
                        continue;
                    }

                    // Extract file size from the last element
                    String sizeDetail = timingDetails[timingDetails.length - 1].trim(); // Get the last detail
                    double fileSize = Double.parseDouble(sizeDetail.split(":")[1].trim().replace("bytes", "").trim());

                    // Populate size map based on current type
                    Map<Integer, Double> sizeMap = currentType.equals("JSON") ? jsonSizeMap : xmlSizeMap;
                    sizeMap.put(iteration, fileSize);

                } catch (NumberFormatException e) {
                    System.out.println("Error parsing numbers from line: " + line);
                }
            }

            // Add sizes for JSON
            for (Map.Entry<Integer, Double> entry : jsonSizeMap.entrySet()) {
                int iter = entry.getKey();
                dataset.addValue(entry.getValue(), "Size (JSON)", Integer.toString(iter));
            }

            // Add sizes for XML
            for (Map.Entry<Integer, Double> entry : xmlSizeMap.entrySet()) {
                int iter = entry.getKey();
                dataset.addValue(entry.getValue(), "Size (XML)", Integer.toString(iter));
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
