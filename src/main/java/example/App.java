package example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class App {
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

    // Convert the Classroom object to JSON and write it to a file
    private static void serializeJson(Classroom classroom, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Write the JSON directly to a file
            objectMapper.writeValue(new File(filePath), classroom);

            // Print confirmation message
            System.out.println("JSON file created: " + filePath);

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

    // Method to write students from a Classroom object to a text file
    private static void writeStudentsToTextFile(Classroom classroom, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            long count = 0; // Counter for numbering students
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

    public static void main(String[] args) {
        Classroom classroom = new Classroom();

        // Get the student list from the file
        classroom = readStudentsIntoClassroom("input/students_10.dat");

        // Serialize into Json
        serializeJson(classroom, "files/serialized_10.json");

        // Deserialize
        Classroom result = new Classroom();
        result = deserializeJson("files/serialized_10.json");
        writeStudentsToTextFile(result, "files/deserialized_10.txt");
        

        /* Student s1 = new Student("Alberto", 21, "201134441110");
        Student s2 = new Student("Patricia", 22, "201134441116");
        Student s3 = new Student("Luis", 21, "201134441210");

        List<Student> students = new ArrayList<>();
        students.add(s1);
        students.add(s2);
        students.add(s3);

        // --------------------------------------
        // UTILIZANDO A BIBLIOTECA GSON
        Gson gson = new Gson();

        long startSerializationTime = System.nanoTime();
        String json = gson.toJson(students); // Serialização
        long endSerializationTime = System.nanoTime();
        long serializationDuration = endSerializationTime - startSerializationTime;

        System.out.println("JSON Serializado:");
        System.out.println(json);
        System.out.println("Tempo de Serialização (ns): " + serializationDuration);

        Type studentListType = new TypeToken<ArrayList<Student>>() {
        }.getType();
        long startDeserializationTime = System.nanoTime();
        List<Student> deserializedStudents = gson.fromJson(json, studentListType); // Deserialização
        long endDeserializationTime = System.nanoTime();
        long deserializationDuration = endDeserializationTime - startDeserializationTime;

        System.out.println("\nObjetos Deserializados:");
        for (Student student : deserializedStudents) {
            System.out.println(student.getName() + " - " + student.getAge() + " - " + student.getId());
        }

        System.out.println("Tempo de Deserialização (ns): " + deserializationDuration);
        */
    }
}