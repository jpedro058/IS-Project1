package example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
   // Method to generate random students
    private static List<Student> generateRandomStudents(int count) {
        List<Student> students = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            String name = "Student" + i;
            int age = random.nextInt(8) + 18;  // Random age between 18 and 25
            String id = "S" + String.format("%04d", i); // ID like S0001, S0002, etc.
            students.add(new Student(name, age, id));
        }

        return students;
    }

    // Method to write students to an object file
    private static void writeStudentsToObjectFile(List<Student> students, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(students);
            System.out.println("Students written to object file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
      List<Student> students = new ArrayList<>();
      System.out.println("Number of students: ");
      int n = Integer.parseInt(System.console().readLine());
      students = generateRandomStudents(n);
      writeStudentsToObjectFile(students, "input/students_" + n + ".dat");
    }
}
