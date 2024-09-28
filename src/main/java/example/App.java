package example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class App {
    private static final String TIMES_FILE = "files/Times.txt"; // Path to the times file
    private static DecimalFormat df = new DecimalFormat("#.####"); // Decimal format for the timings

    // Method for reading the students from the file
    @SuppressWarnings("unchecked")
    private static Classroom readStudentsIntoClassroom(String filePath) {
        Classroom classroom = new Classroom();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            List<Student> students = (List<Student>) ois.readObject();
            classroom.setStudents(new ArrayList<>(students));
            System.out.println("Students read from object file and added to Classroom: " + filePath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classroom;
    }

    // Convert the Classroom object to pretty-printed JSON and write it to a file
    private static void serializeJson(Classroom classroom, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Write the pretty-printed JSON directly to a file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), classroom);

            // Print confirmation message
            System.out.println("Formatted JSON file created: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Deserialize JSON file
    private static Classroom deserializeJson(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        Classroom classroom = null;
        try {
            // Read the JSON file and convert it to a Classroom object
            classroom = objectMapper.readValue(new File(filePath), Classroom.class);
            System.out.println("Classroom object deserialized from JSON file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classroom;
    }

    // Convert the Classroom object to XML with formatting and write it to a file
    private static void serializeXML(Classroom classroom, String filePath) {
        XmlMapper xmlMapper = new XmlMapper();
        // Enable pretty-printing for better formatting
        xmlMapper.writerWithDefaultPrettyPrinter();
        
        try {
            // Create a file object to write the output
            File file = new File(filePath);
            
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            
            // Serialize the Classroom object to XML and append it to the file
            fileWriter.write(xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(classroom));
            
            fileWriter.close();

            System.out.println("Formatted XML file with declaration created: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Deserialize XML file
    private static Classroom deserializeXML(String filePath) {
        XmlMapper xmlMapper = new XmlMapper();
        Classroom classroom = null;
        try {
            // Read the XML file and convert it to a Classroom object
            classroom = xmlMapper.readValue(new File(filePath), Classroom.class);
            System.out.println("Classroom object deserialized from XML file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classroom;
    }

    // Append serialization/deserialization times
    private static void appendTimesToFile(String type, int iteration, long serializeTime, long deserializeTime,
            long totalTime) {
        try {
            // Read the existing content of the Times file
            StringBuilder content = new StringBuilder();
            String jsonSection = "";
            String xmlSection = "";

            // Check if the file exists and read its content
            if (Files.exists(Paths.get(TIMES_FILE))) {
                String fileContent = new String(Files.readAllBytes(Paths.get(TIMES_FILE)));

                // Split the content into JSON and XML sections if they exist
                if (fileContent.contains("JSON:")) {
                    jsonSection = fileContent.substring(fileContent.indexOf("JSON:"),
                            fileContent.contains("XML:") ? fileContent.indexOf("XML:") : fileContent.length());
                }
                if (fileContent.contains("XML:")) {
                    xmlSection = fileContent.substring(fileContent.indexOf("XML:"));
                }
            }

            StringBuilder updatedSection = new StringBuilder();
            if (type.equals("JSON")) {
                if (jsonSection.isEmpty()) {
                    updatedSection.append("JSON:\n");
                } else {
                    updatedSection.append(jsonSection);
                }
                updatedSection.append("\t")
                        .append(iteration)
                        .append(": serialization: ")
                        .append(df.format(serializeTime / 1_000_000.0))
                        .append("ms | deserialization: ")
                        .append(df.format(deserializeTime / 1_000_000.0))
                        .append("ms | total: ")
                        .append(df.format(totalTime / 1_000_000.0))
                        .append("ms\n");
                jsonSection = updatedSection.toString();
            } else if (type.equals("XML")) {
                if (xmlSection.isEmpty()) {
                    updatedSection.append("XML:\n");
                } else {
                    updatedSection.append(xmlSection);
                }
                updatedSection.append("\t")
                        .append(iteration)
                        .append(": serialization: ")
                        .append(df.format(serializeTime / 1_000_000.0))
                        .append("ms | deserialization: ")
                        .append(df.format(deserializeTime / 1_000_000.0))
                        .append("ms | total: ")
                        .append(df.format(totalTime / 1_000_000.0))
                        .append("ms\n");
                xmlSection = updatedSection.toString();
            }

            // Rebuild the entire content with updated sections
            if (!jsonSection.isEmpty()) {
                content.append(jsonSection);
            }
            if (!xmlSection.isEmpty()) {
                content.append(xmlSection);
            }

            // Write the updated content back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(TIMES_FILE))) {
                writer.write(content.toString());
            }

            System.out.println("Times appended to Times file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to write students from a Classroom object to a text file
    private static void writeStudentsToTextFile(Classroom classroom, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            int count = 0; // Counter for numbering students
            for (Student student : classroom.getStudents()) {
                count++;
                // Writing student details in a comma-separated format with a count prefix
                writer.write(count + ": " + student.getName() + "," + student.getAge() + "," + student.getId());
                writer.newLine(); // Move to the next line for each student
            }
            System.out.println("Students written to text file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void JSON(int n) {
        Classroom classroom = new Classroom();

        // Get the student list from the file
        classroom = readStudentsIntoClassroom("input/students_" + n + ".dat");

        // Serialize into Json
        long startTime = System.nanoTime();// Measure the time for serialization
        serializeJson(classroom, "files/ser_" + n + ".json");
        long serializeTime = System.nanoTime() - startTime;

        // Deserialize
        Classroom result = new Classroom();
        long startDeserializeTime = System.nanoTime(); // Measure the time for deserialization
        result = deserializeJson("files/ser_" + n + ".json");
        long deserializeTime = System.nanoTime() - startDeserializeTime;

        long totalTime = serializeTime + deserializeTime;

        writeStudentsToTextFile(result, "files/deser_" + n + "_json.txt");
        appendTimesToFile("JSON", n, serializeTime, deserializeTime, totalTime);
    }

    private static void XML(int n) {
        Classroom classroom = new Classroom();

        // Get the student list from the file
        classroom = readStudentsIntoClassroom("input/students_" + n + ".dat");

        // Serialize into XML
        long startTime = System.nanoTime();// Measure the time for serialization
        serializeXML(classroom, "files/ser_" + n + ".xml");
        long serializeTime = System.nanoTime() - startTime;

        // Deserialize
        Classroom result = new Classroom();
        long startDeserializeTime = System.nanoTime(); // Measure the time for deserialization
        result = deserializeXML("files/ser_" + n + ".xml");
        long deserializeTime = System.nanoTime() - startDeserializeTime;

        long totalTime = serializeTime + deserializeTime;

        writeStudentsToTextFile(result, "files/deser_" + n + "_xml.txt");
        appendTimesToFile("XML", n, serializeTime, deserializeTime, totalTime);
    }

    public static void main(String[] args) {
        System.out.println("1: JSON | 2: XML");
        int option = Integer.parseInt(System.console().readLine());
        System.out.println("Number of students: ");
        int n = Integer.parseInt(System.console().readLine());

        switch (option) {
            case 1:
                JSON(n);
                break;

            case 2:
                XML(n);
                break;

            default:
                break;
        }
    }
}