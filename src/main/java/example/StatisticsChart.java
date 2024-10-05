package example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class StatisticsChart extends JFrame {

    private static final String TIMES_FILE = "files/Times_JVM_reset.txt";
    private JTabbedPane tabbedPane;

    public StatisticsChart(String title) {
        super(title);
        this.tabbedPane = new JTabbedPane();
        setContentPane(tabbedPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static DefaultCategoryDataset createDatasetForIteration(int iteration, Map<Integer, double[]> jsonMap,
            Map<Integer, double[]> xmlMap) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (jsonMap.containsKey(iteration)) {
            double[] jsonSums = jsonMap.get(iteration);
            dataset.addValue(jsonSums[0] / jsonSums[2], "Serialization (JSON)", "Objects " + iteration);
            dataset.addValue(jsonSums[1] / jsonSums[2], "Deserialization (JSON)", "Objects " + iteration);
        }

        if (xmlMap.containsKey(iteration)) {
            double[] xmlSums = xmlMap.get(iteration);
            dataset.addValue(xmlSums[0] / xmlSums[2], "Serialization (XML)", "Objects " + iteration);
            dataset.addValue(xmlSums[1] / xmlSums[2], "Deserialization (XML)", "Objects " + iteration);
        }

        return dataset;
    }

    private void addChartTabForIteration(int iteration, Map<Integer, double[]> jsonMap, Map<Integer, double[]> xmlMap) {
        DefaultCategoryDataset dataset = createDatasetForIteration(iteration, jsonMap, xmlMap);

        JFreeChart barChart = ChartFactory.createBarChart(
                "Number of objects: " + iteration + " - Time Comparison",
                "Type",
                "Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        tabbedPane.add("Objects " + iteration, chartPanel);
    }

    // New method to create the file size dataset
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

    // Add file size tab
    private void addFileSizeTab() {
        DefaultCategoryDataset dataset = createSizeDataset();

        JFreeChart sizeChart = ChartFactory.createBarChart(
                "File Size Statistics",
                "Objects",
                "Size (bytes)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(sizeChart);
        tabbedPane.add("File Size Statistics", chartPanel);
    }

    public static void main(String[] args) {
        StatisticsChart chartFrame = new StatisticsChart("Serialization & Deserialization Time Statistics");

        Map<Integer, double[]> jsonAggregationMap = new TreeMap<>();
        Map<Integer, double[]> xmlAggregationMap = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(TIMES_FILE))) {
            String line;
            String currentType = "";
            int currentIteration;

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
                    currentIteration = Integer.parseInt(parts[0].trim());
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

                    aggregationMap.putIfAbsent(currentIteration, new double[4]);
                    double[] sums = aggregationMap.get(currentIteration);
                    sums[0] += serializationTime;
                    sums[1] += deserializationTime;
                    sums[2] += 1;
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing numbers from line: " + line);
                }
            }

            // Adiciona cada gráfico de tempo em um separador (tab)
            for (int iteration : jsonAggregationMap.keySet()) {
                chartFrame.addChartTabForIteration(iteration, jsonAggregationMap, xmlAggregationMap);
            }

            // Adiciona o gráfico de tamanho de arquivo
            chartFrame.addFileSizeTab();

        } catch (IOException e) {
            e.printStackTrace();
        }

        chartFrame.pack();
        chartFrame.setVisible(true);
        chartFrame.setSize(800, 600);
    }
}
